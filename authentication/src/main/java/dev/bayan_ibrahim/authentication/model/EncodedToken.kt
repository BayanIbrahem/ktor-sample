package dev.bayan_ibrahim.authentication.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

sealed class EncodedToken(
    open val type: TokenType,
) {
    abstract val value: String

    data class Access(override val value: String) : EncodedToken(TokenType.Access)
    data class Refresh(override val value: String) : EncodedToken(TokenType.Refresh)
}

enum class TokenType(
    val expireAt: Duration,
) {
    Access(15.minutes),
    Refresh(15.days)
}