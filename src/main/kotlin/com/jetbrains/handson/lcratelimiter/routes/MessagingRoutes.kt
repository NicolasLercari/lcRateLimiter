package com.jetbrains.handson.lcratelimiter.routes
import com.jetbrains.handson.lcratelimiter.authorization.rateLimiter.RateLimiter
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import com.jetbrains.handson.lcratelimiter.interceptor.routeWithRateLimiter

fun Route.messagingRouting(rateLimiter :RateLimiter) {
    routeWithRateLimiter(rateLimiter) {
        get("/message") {
            call.respond("Thanks, fuck you too.")
        }
    }
}

fun Application.registerMessagingRoutes(rateLimiter :RateLimiter) {
    routing {
        messagingRouting(rateLimiter)
    }
}


