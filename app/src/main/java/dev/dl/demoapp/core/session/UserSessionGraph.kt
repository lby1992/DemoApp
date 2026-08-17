package dev.dl.demoapp.core.session

import javax.inject.Inject

@UserSessionScoped
class UserSessionGraph @Inject constructor(
    val sessionScope: SessionScope,
)