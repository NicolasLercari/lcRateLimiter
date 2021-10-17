package com.jetbrains.handson.lcratelimiter

import com.jetbrains.handson.lcratelimiter.authorization.rateLimiter.RateLimiter
import com.jetbrains.handson.lcratelimiter.repository.RateLimitedUsersRepository
import com.jetbrains.handson.lcratelimiter.routes.registerMessagingRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // Initialization
    val rateLimiterUserRepository = RateLimitedUsersRepository(::getCurrentTimeMillis)
    val rateLimiterInstance = RateLimiter(rateLimiterUserRepository, ::getCurrentTimeMillis)

    registerMessagingRoutes(rateLimiterInstance)
}
