package dev.dl.demoapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * State of navigation.
 *
 * @param startKey The starting navigation key.
 * @param topLevelStack The top level back stack. It holds only top level keys.
 * @param subStacks The back stacks for each top level key.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    /**
     * The current top level key, which is the last key in the top level stack.
     */
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    /**
     * The set of top level keys, which is the same as the keys of the subStacks map.
     */
    val topLevelKeys
        get() = subStacks.keys

    /**
     * The current substack, which is the substack corresponding to the current top level key.
     * An error will be thrown if there is no substack for the current top level key, which should never happen since a substack is created for each top level key when the NavigationState is created.
     */
    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("No subStack for the top level key: $currentTopLevelKey")

    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }
}

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: Set<NavKey>,
): NavigationState {
    val topLevelStack = rememberNavBackStack(startKey)
    // Create a NavBackStack for each top-level key and store them in a map.
    val subStacks = topLevelKeys.associateWith { key ->
        rememberNavBackStack(key)
    }

    return remember(startKey, topLevelKeys) {
        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

/**
 * Convert NavigationState into NavEntries.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): List<NavEntry<NavKey>> {
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        val decorators = listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}