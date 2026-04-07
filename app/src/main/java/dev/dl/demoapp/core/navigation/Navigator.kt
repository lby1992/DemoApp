package dev.dl.demoapp.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Navigation management.
 */
class Navigator(
    val state: NavigationState
) {
    /**
     * Navigate to a key.
     *
     * @param key The key of destination.
     * @param keepSingle Whether to keep only a single instance of the key in the substack. This is only applicable when the key is not a top-level key. If true, the existing instance of the key in the substack will be removed before navigating to it, ensuring that there is only one instance of the key in the substack. If false, the new instance of the key will be added to the substack without removing the existing one, allowing multiple instances of the same key in the substack.
     *
     */
    fun navigateTo(
        key: NavKey,
        keepSingle: Boolean = false,
    ) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goTo(key, keepSingle)
        }
    }

    /**
     * Go back to the previous key.
     *
     * If the current key is the start key, an error will be thrown since it's forbidden to go back from the start point.
     *
     * If the current key is a top-level key, the last key in the top-level stack will be removed to go back to the previous top-level key.
     *
     * Otherwise, the last key in the current substack will be removed to go back to the previous key in the same substack.
     */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> error("Forbidden operation to go back from the start point.")
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
            }

            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * Go to a not top-level key.
     *
     * @param key The key of destination.
     * @param keepSingle Whether to keep only a single instance of the key in the substack. This is only applicable when the key is not a top-level key. If true, the existing instance of the key in the substack will be removed before navigating to it, ensuring that there is only one instance of the key in the substack. If false, the new instance of the key will be added to the substack without removing the existing one, allowing multiple instances of the same key in the substack.
     */
    private fun goTo(
        key: NavKey,
        keepSingle: Boolean = false,
    ) {
        with(state.currentSubStack) {
            if (keepSingle) {
                remove(key)
            }
            add(key)
        }
    }

    /**
     * Go to a top-level key.
     *
     * @param key The key of destination.
     */
    private fun goToTopLevel(key: NavKey) {
        with(state.topLevelStack) {
            if (key == state.startKey) {
                // For the starting point, navigating to it means that the top level should be cleared.
                clear()
            } else {
                // Remove the existing target key in the stack, re-add it to keep it on the top.
                remove(key)
            }
            add(key)
        }
    }

    /**
     * Clear the current substack except the root key.
     */
    private fun clearSubStack() {
        with(state.currentSubStack) {
            if (size > 1) { // reserve the root key in this substack
                subList(1, size).clear()
            }
        }
    }
}