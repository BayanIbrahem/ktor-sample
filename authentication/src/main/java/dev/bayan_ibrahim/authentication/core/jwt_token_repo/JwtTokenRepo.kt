package dev.bayan_ibrahim.authentication.core.jwt_token_repo

import com.auth0.jwt.interfaces.Payload
import dev.bayan_ibrahim.authentication.model.BaseDeviceSession
import dev.bayan_ibrahim.authentication.model.DecodedToken
import dev.bayan_ibrahim.authentication.model.EncodedToken
import dev.bayan_ibrahim.authentication.model.TokenType
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface JwtTokenRepo {
    fun encodeToken(token: DecodedToken): EncodedToken
    fun decodeToken(token: EncodedToken): DecodedToken
    fun decodeToken(payload: Payload): DecodedToken

    fun buildTokenData(
        session: BaseDeviceSession,
        type: TokenType,
        at: Instant = Clock.System.now(),
    ): DecodedToken

    fun JWTAuthenticationProvider.Config.install()


    companion object Companion {
        const val CLAIMS_SESSION_ID = "session_id"
        const val CLAIMS_LOGIN_AT = "login_at"
        const val CLAIMS_DEVICE_TYPE = "device_type"
        const val CLAIMS_DEVICE_NAME = "device_name"
        const val CLAIMS_TOKEN_TYPE = "token_type"
        const val CLAIMS_EMAIL_VERIFIED = "email_verified"
        const val CLAIMS_PHONE_NUMBER_VERIFIED = "phone_number_verified"
    }
}