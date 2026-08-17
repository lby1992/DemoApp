package dev.dl.demoapp.data.infra.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedRetrofit


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptor(val interceptor: NetworkInterceptors)

enum class NetworkInterceptors {
    Auth,
    Logging,
}