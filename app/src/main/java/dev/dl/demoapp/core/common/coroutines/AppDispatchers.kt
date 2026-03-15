package dev.dl.demoapp.core.common.coroutines

import javax.inject.Qualifier

/**
 * A Hilt qualifier used to differentiate between various [kotlinx.coroutines.CoroutineDispatcher] implementations.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatchers)

/**
 * Defines the set of non-main thread dispatchers managed by the application.
 * Using an enum allows for type-safe identification of dispatchers
 * within the [Dispatcher] qualifier.
 */
enum class AppDispatchers {
    Default,
    IO,
}