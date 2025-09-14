package dev.bayan_ibrahim.authentication.core.jwt_token_repo

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import dev.bayan_ibrahim.authentication.model.BaseDeviceSession
import dev.bayan_ibrahim.authentication.model.DecodedToken
import dev.bayan_ibrahim.authentication.model.DeviceType
import dev.bayan_ibrahim.authentication.model.EncodedToken
import dev.bayan_ibrahim.authentication.model.TokenType
import dev.bayan_ibrahim.authentication.model.toClaims
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date
import java.util.concurrent.TimeUnit

class RS256JwtTokenRepo(
    private val jwkProvider: JwkProvider,
    private val issuer: String,
    private val privateKeyString: String,
    private val publicKey: String,
    private val audience: String,
) : JwtTokenRepo {
    override fun encodeToken(token: DecodedToken): EncodedToken {
        val publicKey = jwkProvider.get(publicKey).publicKey
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString))
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpecPKCS8)
        val encodedToken = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .apply {
                token.toClaims().forEach { (label, value) ->
                    withClaim(label, value)
                }
            }
            .withSubject(token.subject)
            .withIssuedAt(Date(token.issuedAt.toEpochMilliseconds()))
            .withExpiresAt(Date(token.expiredAt.toEpochMilliseconds()))
            .sign(Algorithm.RSA256(publicKey as RSAPublicKey, privateKey as RSAPrivateKey))
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
            subject = payload.subject!!,
            issuedAt = payload.issuedAt.toInstant().toKotlinInstant(),
            expiredAt = payload.expiresAt.toInstant().toKotlinInstant(),
            id = payload.claims[JwtTokenRepo.CLAIMS_SESSION_ID]!!.asLong(),
            loginAt = (payload.claims[JwtTokenRepo.CLAIMS_LOGIN_AT]!!.asString()).let { Instant.parse(it) },
            deviceType = (payload.claims[JwtTokenRepo.CLAIMS_DEVICE_TYPE]?.asString())?.let { DeviceType.valueOf(it) },
            deviceName = payload.claims[JwtTokenRepo.CLAIMS_DEVICE_NAME]?.asString(),
            type = payload.claims[JwtTokenRepo.CLAIMS_TOKEN_TYPE]!!.asString().let { TokenType.valueOf(it) },
            emailVerified = payload.claims[JwtTokenRepo.CLAIMS_EMAIL_VERIFIED]?.asBoolean() ?: false,
            phoneNumberVerified = payload.claims[JwtTokenRepo.CLAIMS_PHONE_NUMBER_VERIFIED]?.asBoolean() ?: false,
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
            expiredAt = at + type.expireAt,
            emailVerified = session.emailVerified,
            phoneNumberVerified = session.phoneNumberVerified,
        )
    }

    /**
     * - install the verifier
     */
    override fun JWTAuthenticationProvider.Config.install() {
        verifier(jwkProvider, issuer) {
            acceptLeeway(3)
        }

        validate { call ->
            val session = call.payload.getClaim(JwtTokenRepo.CLAIMS_SESSION_ID).asLong()
            session?.let {
                decodeToken(call.payload)
            }
        }
    }
}

fun Application.buildRS256JwtTokenRepo(): RS256JwtTokenRepo {
    val privateKeyString = environment.config.property("jwt.privateKey").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    return RS256JwtTokenRepo(
        jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build(),
        issuer = issuer,
        privateKeyString = privateKeyString,
        publicKey = "6f8856ed-9189-488f-9011-0ff4b6c08edc",
        audience = audience,
    )
}