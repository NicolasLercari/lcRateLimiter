package com.jetbrains.handson.lcratelimiter.interceptor

import com.jetbrains.handson.lcratelimiter.authorization.rateLimiter.RateLimiter
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.routeWithRateLimiter(rateLimiter: RateLimiter, callback: Route.() -> Unit): Route {
    val routeWithRateLimiter = this.createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    routeWithRateLimiter.intercept(ApplicationCallPipeline.Features) {
        val userId = call.request.headers["userId"]

        if (userId == null) {
            call.respondText(
            "Missing or malformed userId",
                status = HttpStatusCode.BadRequest
            )
            return@intercept finish()
        }

        if (!rateLimiter.userCanRequest(userId)) {
            call.respondText(
                "Rate limited exceeded",
                status = HttpStatusCode.BadRequest
            )
            return@intercept finish()
        }

        proceed()
    }

    callback(routeWithRateLimiter)

    return routeWithRateLimiter
}

