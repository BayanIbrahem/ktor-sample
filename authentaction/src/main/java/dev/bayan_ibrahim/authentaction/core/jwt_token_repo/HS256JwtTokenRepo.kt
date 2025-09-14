package dev.bayan_ibrahim.authentaction.core.jwt_token_repo

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import dev.bayan_ibrahim.authentaction.model.BaseDeviceSession
import dev.bayan_ibrahim.authentaction.model.DecodedToken
import dev.bayan_ibrahim.authentaction.model.DeviceType
import dev.bayan_ibrahim.authentaction.model.EncodedToken
import dev.bayan_ibrahim.authentaction.model.TokenType
import dev.bayan_ibrahim.authentaction.model.toClaims
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.util.Date

class HS256JwtTokenRepo(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
) : JwtTokenRepo {
    override fun encodeToken(token: DecodedToken): EncodedToken {
        val encodedToken = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer).apply {
                token.toClaims().forEach { (label, value) ->
                    withClaim(label, value)
                }
            }
            .withSubject(token.subject)
            .withIssuedAt(Date(token.issuedAt.toEpochMilliseconds()))
            .withExpiresAt(Date(token.expiredAt.toEpochMilliseconds()))
            .sign(Algorithm.HMAC256(secret))

        return when (token.type) {
            TokenType.Access -> EncodedToken.Access(encodedToken)
            TokenType.Refresh -> EncodedToken.Refresh(encodedToken)
        }
    }

    override fun decodeToken(token: EncodedToken): DecodedToken {
        val decodedToken = JWT.decode(token.value)
        return decodeToken(decodedToken)
    }

    override fun decodeToken(payload: Payload): DecodedToken {
        return DecodedToken(
            id = payload.claims[JwtTokenRepo.CLAIMS_SESSION_ID]!!.asLong(),
            subject = payload.subject!!,
            loginAt = (payload.claims[JwtTokenRepo.CLAIMS_LOGIN_AT]!!.asString()).let { Instant.parse(it) },
            deviceType = (payload.claims[JwtTokenRepo.CLAIMS_DEVICE_TYPE]?.asString())?.let { DeviceType.valueOf(it) },
            deviceName = payload.claims[JwtTokenRepo.CLAIMS_DEVICE_NAME]?.asString(),
            type = payload.claims[JwtTokenRepo.CLAIMS_TOKEN_TYPE]!!.asString().let { TokenType.valueOf(it) },
            issuedAt = payload.issuedAt.toInstant().toKotlinInstant(),
            expiredAt = payload.expiresAt.toInstant().toKotlinInstant(),
        )
    }

    override fun buildTokenData(
        session: BaseDeviceSession,
        type: TokenType,
        at: Instant,
    ): DecodedToken {
        return DecodedToken(
            id = session.id,
            subject = session.userID.toString(),
            loginAt = session.loginAt,
            deviceType = session.deviceType,
            deviceName = session.deviceName,
            type = type,
            issuedAt = at,
            expiredAt = at + type.expireAt
        )
    }

    /**
     * - install the verifier
     */
    override fun JWTAuthenticationProvider.Config.install() {
        verifier(
            verifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        )

        validate { call ->
            val session = call.payload.getClaim(JwtTokenRepo.CLAIMS_SESSION_ID).asLong()
            session?.let {
                decodeToken(call.payload)
            }
        }
    }
}

fun Application.buildHS256JwtTokenRepo(): HS256JwtTokenRepo {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    return HS256JwtTokenRepo(
        secret = secret,
        issuer = issuer,
        audience = audience,
    )
}