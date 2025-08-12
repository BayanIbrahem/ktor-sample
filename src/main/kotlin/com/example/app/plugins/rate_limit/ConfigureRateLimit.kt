package com.example.app.plugins.rate_limit

import com.example.app.plugins.routing.rateLimitEndpoint
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.response.header
import kotlin.time.Duration.Companion.seconds

val PROTECTED_RATE_LIMIT_NAME = RateLimitName("Protected")

/**
 * ## Overview
 * Ktor uses the token bucket algorithm for rate limiting, which works as follows:
 * 1. In the beginning, we have a bucket defined by its capacity - the number of tokens.
 * 2. Each incoming request tries to consume one token from the bucket:
 * * If there is enough capacity, the server handles a request and sends a response with the following headers:
 *      - X-RateLimit-Limit: a specified bucket capacity.
 *      - X-RateLimit-Remaining: the number of tokens remaining in a bucket.
 *      - X-RateLimit-Reset: a UTC timestamp (in seconds) that specifies the time of refilling a bucket.
 * * If there is insufficient capacity, the server rejects a request using a 429 Too Many Requests response
 * and adds the Retry-After header, indicating how long the client should wait (in seconds) before making a follow-up request.
 *
 * After a specified period of time, a bucket capacity is refilled.
 * @see rateLimitEndpoint for example of rate limit of sub routes
 */
fun Application.configureRateLimit() {
    install(RateLimit) {
        // global limit for everything
        global {
            // 30 requests per minute
            rateLimiter(
                limit = 30,
                refillPeriod = 60.seconds
            )
            // return a key for the request
            // Requests with different keys have independent rate limits
            requestKey { call ->
                call.request.queryParameters["login"]!!
            }
            // how many tokens are consumed per this request
            requestWeight { call, key ->
                when (key) {
                    "jetbrains" -> 1
                    else -> 2
                }
            }
            // edit the default X-RateLimit-* header
            modifyResponse { applicationCall, state ->
                applicationCall.response.header("X-RateLimit-Custom-Header", "Some value")
            }
        }
        // limit can be applied for certain routes
        register(
            PROTECTED_RATE_LIMIT_NAME
        ) {
            // same as global
            // but this can be used in the sub routing
            // see the referred method in the document
        }
    }
}
