package dev.dl.demoapp.core.session

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SessionContainer @Inject constructor(
    private val provider: Provider<UserSessionGraph>
) {
    private var current: UserSessionGraph? = null

    val userSessionGraph: UserSessionGraph
        get() = current ?: error("No active user session")

    fun startUserSession(): UserSessionGraph {
        return provider.get().also {
            current = it
        }
    }

    fun endUserSession() {
        current?.sessionScope?.cancel()
        current = null
    }
}