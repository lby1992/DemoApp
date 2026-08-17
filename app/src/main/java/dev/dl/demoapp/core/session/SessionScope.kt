package dev.dl.demoapp.core.session

import dev.dl.demoapp.core.common.coroutines.AppDispatchers
import dev.dl.demoapp.core.common.coroutines.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@UserSessionScoped
class SessionScope @Inject constructor(
    @get:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineScope {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = CoroutineName(NAME) + ioDispatcher + job

    fun cancel() {
        job.cancel()
    }

    companion object {
        private const val NAME = "SessionScope"
    }
}