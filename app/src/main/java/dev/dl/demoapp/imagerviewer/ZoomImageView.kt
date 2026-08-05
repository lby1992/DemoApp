package dev.dl.demoapp.imagerviewer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.hypot

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0,
) : AppCompatImageView(context, attrs, defaultStyle) {

    private val baseMatrix = Matrix()
    private val suppMatrix = Matrix()
    private val drawMatrix = Matrix()
    private val matrixValues = FloatArray(9)
    private val tempRect = RectF()

    private var viewWidth = 0
    private var viewHeight = 0

    var minScale = DEFAULT_MIN_SCALE
    var maxScale = DEFAULT_MAX_SCALE
    private var midScale = 2f

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val currentDrawable: Drawable? get() = drawable

    val scale: Float
        get() {
            suppMatrix.getValues(matrixValues)
            return matrixValues[Matrix.MSCALE_X]
        }

    private val displayRect: RectF?
        get() {
            val drawable = currentDrawable ?: return null
            tempRect.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            drawMatrix.mapRect(tempRect)
            return tempRect
        }

    val isZoomed: Boolean get() = scale > minScale

    private var isDragging = false
    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var activePointerId = MotionEvent.INVALID_POINTER_ID

    private var matrixAnimator: ValueAnimator? = null
    private val tempStartValues = FloatArray(9)
    private val tempEndValues = FloatArray(9)
    private val tempResultValues = FloatArray(9)
    private val tempMatrix = Matrix()

    private val scroller = OverScroller(context)

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    private var flingRunnable: Runnable? = null

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = drawMatrix
        isClickable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 【修复1】：分别调用，防止 || 的短路导致 gestureDetector 接收不到事件
        val scaleHandled = scaleDetector.onTouchEvent(event)
        val gestureHandled = gestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_POINTER_UP -> onPointerUp(event) // 【新增】处理多指抬起
            MotionEvent.ACTION_MOVE -> onActionMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onActionUp()
        }

        return scaleHandled || gestureHandled || super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == oldw && h == oldh) return
        viewWidth = w
        viewHeight = h
        updateBaseMatrix()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        resetMatrixForNewImage(drawable != null)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        resetMatrixForNewImage(bm != null)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        resetMatrixForNewImage(resId != 0)
    }

    private fun resetMatrixForNewImage(hasImage: Boolean) {
        if (!hasImage) {
            baseMatrix.reset()
            suppMatrix.reset()
            rebuildMatrix()
        } else {
            updateBaseMatrix()
        }
    }

    override fun onDetachedFromWindow() {
        cancelFling()
        matrixAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun onActionDown(event: MotionEvent) {
        cancelFling()
        requestParentDisallowIntercept(true)
        activePointerId = event.getPointerId(0)
        lastTouchX = event.x
        lastTouchY = event.y
        isDragging = false
    }

    private fun onActionUp() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        isDragging = false
    }

    private fun onPointerUp(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        if (pointerId == activePointerId) {
            val newIndex = if (pointerIndex == 0) 1 else 0
            if (newIndex < event.pointerCount) {
                activePointerId = event.getPointerId(newIndex)
                lastTouchX = event.getX(newIndex)
                lastTouchY = event.getY(newIndex)
            }
        }
    }

    private fun onActionMove(event: MotionEvent) {
        // 使用安全的手指索引获取坐标
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex == -1) return
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)

        if (scaleDetector.isInProgress) {
            // 【关键】：如果在双指缩放，要不断更新 lastTouch，这样缩放结束接着单指拖拽时才不会跳跃
            lastTouchX = x
            lastTouchY = y
            return
        }

        val dx = x - lastTouchX
        val dy = y - lastTouchY

        if (!isDragging) {
            isDragging = hypot(dx.toDouble(), dy.toDouble()) >= touchSlop
        }

        if (!isDragging) return

        val canMoveH = canMoveHorizontal(dx)
        requestParentDisallowIntercept(canMoveH)

        if (isZoomed) {
            postTranslate(dx, dy)
        }

        lastTouchX = x
        lastTouchY = y
    }

    private fun requestParentDisallowIntercept(disallow: Boolean) {
        parent?.requestDisallowInterceptTouchEvent(disallow)
    }

    private fun canMoveHorizontal(dx: Float): Boolean {
        val rect = displayRect ?: return false
        if (rect.width() <= width) return false
        if (dx > 0) return rect.left < 0 // 向右滑
        if (dx < 0) return rect.right > width // 向左滑
        return false
    }

    private fun canMoveVertical(dy: Float): Boolean {
        val rect = displayRect ?: return false
        if (rect.height() <= height) return false
        if (dy > 0) return rect.top < 0
        if (dy < 0) return rect.bottom > height
        return false
    }

    // 【修复4】：提供一个预测矩阵边界修正的方法，用于双击放大时防止图片飞出屏幕外
    private fun getBoundedTargetMatrix(targetSuppMatrix: Matrix): Matrix {
        val result = Matrix(targetSuppMatrix)
        val drawable = currentDrawable ?: return result

        val tempDrawMatrix = Matrix(baseMatrix)
        tempDrawMatrix.postConcat(targetSuppMatrix)

        val rect = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        tempDrawMatrix.mapRect(rect)

        var dx = 0f
        var dy = 0f

        if (rect.width() <= viewWidth) {
            dx = viewWidth / 2f - rect.centerX()
        } else {
            if (rect.left > 0f) dx = -rect.left
            else if (rect.right < viewWidth) dx = viewWidth - rect.right
        }

        if (rect.height() <= viewHeight) {
            dy = viewHeight / 2f - rect.centerY()
        } else {
            if (rect.top > 0f) dy = -rect.top
            else if (rect.bottom < viewHeight) dy = viewHeight - rect.bottom
        }

        result.postTranslate(dx, dy)
        return result
    }

    private fun animateMatrix(target: Matrix) {
        matrixAnimator?.cancel()
        val start = Matrix(suppMatrix)

        matrixAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ZOOM_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val fraction = it.animatedValue as Float
                val matrix = interpolateMatrix(start, target, fraction)
                suppMatrix.set(matrix)
                rebuildMatrix()
            }
        }
        matrixAnimator?.start()
    }

    private fun interpolateMatrix(start: Matrix, end: Matrix, fraction: Float): Matrix {
        start.getValues(tempStartValues)
        end.getValues(tempEndValues)
        for (i in 0 until 9) {
            tempResultValues[i] = tempStartValues[i] + (tempEndValues[i] - tempStartValues[i]) * fraction
        }
        tempMatrix.setValues(tempResultValues)
        return tempMatrix
    }

    fun reset(animated: Boolean = false) {
        if (animated) {
            animateMatrix(Matrix())
        } else {
            suppMatrix.reset()
            constrainMatrix()
            rebuildMatrix()
        }
    }

    private fun updateBaseMatrix() {
        val drawable = currentDrawable ?: return
        val dw = drawable.intrinsicWidth.toFloat()
        val dh = drawable.intrinsicHeight.toFloat()

        if (dw <= 0f || dh <= 0f || viewWidth == 0 || viewHeight == 0) return

        baseMatrix.reset()
        val scale = minOf(viewWidth / dw, viewHeight / dh)
        val dx = (viewWidth - dw * scale) / 2f
        val dy = (viewHeight - dh * scale) / 2f

        baseMatrix.postScale(scale, scale)
        baseMatrix.postTranslate(dx, dy)
        rebuildMatrix()
    }

    private fun applyMatrix() {
        drawMatrix.set(baseMatrix)
        drawMatrix.postConcat(suppMatrix)
        imageMatrix = drawMatrix
    }

    private fun postTranslate(dx: Float, dy: Float) {
        suppMatrix.postTranslate(dx, dy)
        constrainMatrix()
        rebuildMatrix()
    }

    private fun postScale(scale: Float, px: Float, py: Float) {
        suppMatrix.postScale(scale, scale, px, py)
        constrainMatrix()
        rebuildMatrix()
    }

    private fun constrainMatrix() {
        applyMatrix()

        val rect = displayRect ?: return
        var dx = 0f
        var dy = 0f

        if (rect.width() <= viewWidth) {
            dx = viewWidth / 2f - rect.centerX()
        } else {
            if (rect.left > 0f) dx = -rect.left
            else if (rect.right < viewWidth) dx = viewWidth - rect.right
        }

        if (rect.height() <= viewHeight) {
            dy = viewHeight / 2f - rect.centerY()
        } else {
            if (rect.top > 0f) dy = -rect.top
            else if (rect.bottom < viewHeight) dy = viewHeight - rect.bottom
        }

        if (dx != 0f || dy != 0f) {
            suppMatrix.postTranslate(dx, dy)
        }
    }

    private fun rebuildMatrix() {
        applyMatrix()
    }

    private fun startFling(velocityX: Float, velocityY: Float) {
        matrixAnimator?.cancel()
        scroller.fling(0, 0, velocityX.toInt(), velocityY.toInt(), Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)

        flingRunnable = object : Runnable {
            private var lastX = 0
            private var lastY = 0

            override fun run() {
                if (scroller.computeScrollOffset()) {
                    val dx = scroller.currX - lastX
                    val dy = scroller.currY - lastY
                    postTranslate(dx.toFloat(), dy.toFloat())
                    lastX = scroller.currX
                    lastY = scroller.currY
                    postOnAnimation(this)
                }
            }
        }
        postOnAnimation(flingRunnable)
    }

    private fun cancelFling() {
        scroller.forceFinished(true)
        flingRunnable?.let { removeCallbacks(it) }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val current = scale
            val target = (current * detector.scaleFactor).coerceIn(minScale, maxScale)
            val realScale = target / current
            postScale(realScale, detector.focusX, detector.focusY)
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val target = Matrix(suppMatrix)
            if (scale < midScale) {
                val factor = midScale / scale
                target.postScale(factor, factor, e.x, e.y)
            } else {
                target.reset()
            }

            // 使用修正后的目标矩阵，防止双击边缘时图片飞出视口
            val finalMatrix = getBoundedTargetMatrix(target)
            animateMatrix(finalMatrix)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (!isZoomed) return false
            startFling(velocityX, velocityY)
            return true
        }
    }

    companion object {
        private const val DEFAULT_MIN_SCALE = 1f
        private const val DEFAULT_MAX_SCALE = 4f
        private const val ZOOM_ANIMATION_DURATION = 250L
    }
}