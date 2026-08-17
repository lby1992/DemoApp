package dev.dl.demoapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dl.demoapp.core.common.coroutines.AppDispatchers
import dev.dl.demoapp.core.common.coroutines.ApplicationScope
import dev.dl.demoapp.core.common.coroutines.Dispatcher
import dev.dl.demoapp.data.proto.AppPrefs
import dev.dl.demoapp.data.proto.AuthPrefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideAppPreferencesDataStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        serializer: AppPrefsSerializer,
    ): DataStore<AppPrefs> {
        return DataStoreFactory.create(
            serializer = serializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("app_prefs.pb")
        }
    }

    @Singleton
    @Provides
    fun provideAuthPreferencesDataStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        serializer: AuthPrefsSerializer,
    ): DataStore<AuthPrefs> {
        return DataStoreFactory.create(
            serializer = serializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("auth_prefs.pb")
        }
    }
}