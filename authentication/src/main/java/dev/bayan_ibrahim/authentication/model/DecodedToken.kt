package dev.bayan_ibrahim.authentication.model

import dev.bayan_ibrahim.authentication.core.jwt_token_repo.JwtTokenRepo
import kotlinx.datetime.Instant

typealias DecodedJWTPrincipal = DecodedToken

data class DecodedToken(
    override val id: Long,
    val subject: String,
    override val loginAt: Instant,
    override val deviceType: DeviceType?,
    override val deviceName: String?,
    val type: TokenType,
    val issuedAt: Instant,
    val expiredAt: Instant,
    override val emailVerified: Boolean,
    override val phoneNumberVerified: Boolean,
) : BaseDeviceSession {
    override val userID: Long = subject.toLong()
}

fun DecodedToken.toClaims(): Map<String, String> = listOfNotNull(
    JwtTokenRepo.CLAIMS_SESSION_ID to id.toString(),
    JwtTokenRepo.CLAIMS_LOGIN_AT to loginAt.toString(),
    if (deviceType != null) {
        JwtTokenRepo.CLAIMS_DEVICE_TYPE to deviceType.toString()
    } else {
        null
    },
    if (deviceName != null) {
        JwtTokenRepo.CLAIMS_DEVICE_NAME to deviceName
    } else {
        null
    },
    JwtTokenRepo.CLAIMS_TOKEN_TYPE to type.toString(),
    JwtTokenRepo.CLAIMS_EMAIL_VERIFIED to emailVerified.toString(),
    JwtTokenRepo.CLAIMS_PHONE_NUMBER_VERIFIED to phoneNumberVerified.toString(),
).toMap()
