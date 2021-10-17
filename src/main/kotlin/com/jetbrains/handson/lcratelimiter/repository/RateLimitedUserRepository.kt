package com.jetbrains.handson.lcratelimiter.repository

data class RateLimitedUser(val limit: Int, val lastRequest: Long)

class RateLimitedUsersRepository(val getCurrentTimeMillis: () -> Long) {
    private val usersRateLimiter: MutableMap<String, RateLimitedUser> = mutableMapOf()

    fun getOrCreate(userId: String, limit: Int): RateLimitedUser {
        return this.usersRateLimiter.getOrPut(userId) { RateLimitedUser( limit, getCurrentTimeMillis()) }
    }

    fun update(userId: String, limit: Int? = null, lastRequest: Long? = null) {
        val rateLimitedUser = this.usersRateLimiter[userId]
        rateLimitedUser!!

        val newRateLimitedUser = RateLimitedUser(
            limit ?: rateLimitedUser.limit, lastRequest ?: rateLimitedUser.lastRequest
        )
        this.usersRateLimiter[userId] = newRateLimitedUser
    }

    fun clear() {
        this.usersRateLimiter.clear()
    }
}