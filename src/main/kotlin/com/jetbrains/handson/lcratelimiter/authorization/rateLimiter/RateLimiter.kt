package com.jetbrains.handson.lcratelimiter.authorization.rateLimiter
import com.jetbrains.handson.lcratelimiter.repository.RateLimitedUsersRepository

const val defaultLimit = 5
const val defaultTimeWindow = 10

class RateLimiter(
    private val userRateLimiterRepository: RateLimitedUsersRepository,
    private val getCurrentTimeMillis: () -> Long,
    private val limit: Int = defaultLimit,
    private val timeWindow: Int = defaultTimeWindow,
) {

    private fun checkTimeWindow (timestamp: Long): Boolean {
        val currentTime = getCurrentTimeMillis()
        return currentTime - timestamp < timeWindow * 1000
    }

    private fun checkLimit (limit: Int): Boolean {
        return limit == 0
    }

    fun userCanRequest (userId: String): Boolean {
        val user = this.userRateLimiterRepository.getOrCreate(userId, limit)

        if (checkTimeWindow(user.lastRequest)) {
            if (checkLimit(user.limit)) {
                return false
            }

            userRateLimiterRepository.update(userId, limit = user.limit - 1)
            return true
        }

        userRateLimiterRepository.update(userId, limit, getCurrentTimeMillis())
        return true
    }

    fun clear() {
        userRateLimiterRepository.clear()
    }
}