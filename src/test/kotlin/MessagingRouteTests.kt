import com.jetbrains.handson.lcratelimiter.authorization.rateLimiter.RateLimiter
import com.jetbrains.handson.lcratelimiter.repository.RateLimitedUsersRepository
import com.jetbrains.handson.lcratelimiter.routes.registerMessagingRoutes
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MessagingRouteTests {

    private val userId = "b9544bc9-6360-410c-bb48-ca27fb379ee9"

    private var mockTimestamp = 1634508591303

    private fun delayMockTimestamp(delay: Int = 10) {
        this.mockTimestamp = 1634508591303 + (delay * 1000)
    }

    private fun getCurrentTimeMock(): Long {
        return mockTimestamp
    }

    private val rateLimiterInstance = RateLimiter(
        RateLimitedUsersRepository(::getCurrentTimeMock),
        ::getCurrentTimeMock
    )


    @Before
    fun beforeEachTest() {
        rateLimiterInstance.clear()
    }

    @Test
    fun testGetOneMessage() = withTestApplication({ registerMessagingRoutes(rateLimiterInstance) }) {
        with(handleRequest(HttpMethod.Get, "/message") {
            addHeader("userId", userId)
        }) {
            assertEquals("Thanks, fuck you too.", response.content)
        }
    }

    @Test
    fun testGetFiveMessageInTimeWindow() = withTestApplication({ registerMessagingRoutes(rateLimiterInstance) }) {
        fun doRequestAndCheckResponse(path: String, userId: String, expected: String) {
            with(handleRequest(HttpMethod.Get, path) {
                addHeader("userId", userId)
            }) {
                assertEquals(expected, response.content)
            }
        }

        repeat(5) {
            doRequestAndCheckResponse("/message", userId, "Thanks, fuck you too.")
        }
    }

    @Test
    fun testGetFiveMessagesWithinTimeWindowAndTheSixthOneGetRateLimited() = withTestApplication({ registerMessagingRoutes(rateLimiterInstance) }) {
        fun doRequestAndCheckResponse(path: String, userId: String, expected: String) {
            with(handleRequest(HttpMethod.Get, path) {
                addHeader("userId", userId)
            }) {
                assertEquals(expected, response.content)
            }
        }

        repeat(5) {
            doRequestAndCheckResponse("/message", userId, "Thanks, fuck you too.")
        }
        doRequestAndCheckResponse("/message", userId, "Rate limited exceeded")
    }

    @Test
    fun testGetFiveMessagesAndDelayTimeWindowsGetOtherMessage() = withTestApplication({ registerMessagingRoutes(rateLimiterInstance) }) {
        fun doRequestAndCheckResponse(path: String, userId: String, expected: String) {
            with(handleRequest(HttpMethod.Get, path) {
                addHeader("userId", userId)
            }) {
                assertEquals(expected, response.content)
            }
        }

        repeat(5) {
            doRequestAndCheckResponse("/message", userId, "Thanks, fuck you too.")
        }

        delayMockTimestamp()
        doRequestAndCheckResponse("/message", userId, "Thanks, fuck you too.")
    }
}
