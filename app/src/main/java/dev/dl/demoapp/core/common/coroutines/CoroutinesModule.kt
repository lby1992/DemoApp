package dev.dl.demoapp.core.common.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Custom qualifier to distinguish the application-wide CoroutineScope
 * from other potential scope implementations, preventing injection conflicts.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

/**
 * Configures Coroutine-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    /**
     * Provides the Default Dispatcher, optimized for CPU-intensive tasks
     */
    @Dispatcher(AppDispatchers.Default)
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides the IO Dispatcher, optimized for blocking IO operations
     */
    @Dispatcher(AppDispatchers.IO)
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides a global application-level CoroutineScope.
     * Use `SupervisorJob()` to ensure that the failure of any child coroutine does not cancel the entire scope or other sibling coroutines.
     */
    @ApplicationScope
    @Singleton
    @Provides
    fun provideCoroutineScope(
        @Dispatcher(AppDispatchers.Default) defaultDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
}