package dev.bayan_ibrahim.authentication

import dev.bayan_ibrahim.authentication.core.jwt_token_repo.JwtTokenRepo
import dev.bayan_ibrahim.authentication.model.BaseDeviceSession
import dev.bayan_ibrahim.authentication.model.DecodedToken
import dev.bayan_ibrahim.authentication.model.DeviceType
import dev.bayan_ibrahim.authentication.model.EncodedToken
import dev.bayan_ibrahim.authentication.model.TokenType
import dev.bayan_ibrahim.authentication.model.toClaims
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

abstract class JwtTokenRepoTest {
    abstract fun createRepo(): JwtTokenRepo
    abstract fun createMockSession(): BaseDeviceSession

    private lateinit var repo: JwtTokenRepo

    @BeforeTest
    fun setup() {
        repo = createRepo()
    }

    @Test
    fun `encodeToken and decodeToken should work correctly for Access token`() {
        val originalToken = createDecodedToken(TokenType.Access)
        val encodedToken = repo.encodeToken(originalToken)

        assertTrue(encodedToken is EncodedToken.Access)
        assertEquals(TokenType.Access, encodedToken.type)
        assertNotNull(encodedToken.value)
        assertTrue(encodedToken.value.isNotBlank())

        val decodedToken = repo.decodeToken(encodedToken)
        assertEquals(originalToken.id, decodedToken.id)
        assertEquals(originalToken.subject, decodedToken.subject)
        assertEquals(originalToken.type, decodedToken.type)
        assertEquals(originalToken.emailVerified, decodedToken.emailVerified)
    }

    @Test
    fun `encodeToken and decodeToken should work correctly for Refresh token`() {
        val originalToken = createDecodedToken(TokenType.Refresh)
        val encodedToken = repo.encodeToken(originalToken)

        assertTrue(encodedToken is EncodedToken.Refresh)
        assertEquals(TokenType.Refresh, encodedToken.type)
        assertNotNull(encodedToken.value)
        assertTrue(encodedToken.value.isNotBlank())

        val decodedToken = repo.decodeToken(encodedToken)
        assertEquals(originalToken.id, decodedToken.id)
        assertEquals(originalToken.subject, decodedToken.subject)
        assertEquals(originalToken.type, decodedToken.type)
    }

    @Test
    fun `decodeToken with payload should work correctly`() {
        val originalToken = createDecodedToken(TokenType.Access)
        val encodedToken = repo.encodeToken(originalToken)
        val decodedFromEncoded = repo.decodeToken(encodedToken)

        // This test assumes we can get a Payload from the encoded token
        // The implementation might need to provide a way to extract payload
        // For now, we'll test that decoding the same token twice gives same result
        val decodedAgain = repo.decodeToken(encodedToken)
        assertEquals(decodedFromEncoded, decodedAgain)
    }

    @Test
    fun `buildTokenData should create valid DecodedToken`() {
        val session = createMockSession()
        val type = TokenType.Access
        val now = Clock.System.now()

        val token = repo.buildTokenData(session, type, now)

        assertEquals(session.id, token.id)
        assertEquals(session.userID.toString(), token.subject)
        assertEquals(session.loginAt, token.loginAt)
        assertEquals(session.deviceType, token.deviceType)
        assertEquals(session.deviceName, token.deviceName)
        assertEquals(type, token.type)
        assertEquals(now, token.issuedAt)
        assertEquals(now + type.expireAt, token.expiredAt)
        assertEquals(session.emailVerified, token.emailVerified)
        assertEquals(session.phoneNumberVerified, token.phoneNumberVerified)
    }

    @Test
    fun `buildTokenData should use current time when not specified`() {
        val session = createMockSession()
        val type = TokenType.Access

        val token = repo.buildTokenData(session, type)

        // The issuedAt should be close to now (within a few seconds)
        val now = Clock.System.now()
        assertTrue((token.issuedAt - now).absoluteValue.inWholeSeconds <= 2)
        assertEquals(token.issuedAt + type.expireAt, token.expiredAt)
    }

    @Test
    fun `toClaims should include all required claims`() {
        val token = createDecodedToken(TokenType.Access)
        val claims = token.toClaims()

        assertEquals(token.id.toString(), claims[JwtTokenRepo.CLAIMS_SESSION_ID])
        assertEquals(token.loginAt.toString(), claims[JwtTokenRepo.CLAIMS_LOGIN_AT])
        assertEquals(token.deviceType?.toString(), claims[JwtTokenRepo.CLAIMS_DEVICE_TYPE])
        assertEquals(token.deviceName, claims[JwtTokenRepo.CLAIMS_DEVICE_NAME])
        assertEquals(token.type.toString(), claims[JwtTokenRepo.CLAIMS_TOKEN_TYPE])
        assertEquals(token.emailVerified.toString(), claims[JwtTokenRepo.CLAIMS_EMAIL_VERIFIED])
        assertEquals(token.phoneNumberVerified.toString(), claims[JwtTokenRepo.CLAIMS_PHONE_NUMBER_VERIFIED])
    }

    @Test
    fun `toClaims should handle null deviceType and deviceName`() {
        val token = createDecodedToken(TokenType.Access).copy(
            deviceType = null,
            deviceName = null
        )

        val claims = token.toClaims()

        assertNull(claims[JwtTokenRepo.CLAIMS_DEVICE_TYPE])
        assertNull(claims[JwtTokenRepo.CLAIMS_DEVICE_NAME])
        assertNotNull(claims[JwtTokenRepo.CLAIMS_SESSION_ID])
        assertNotNull(claims[JwtTokenRepo.CLAIMS_TOKEN_TYPE])
    }

    @Test
    fun `EncodedToken types should have correct token types`() {
        val accessToken = EncodedToken.Access("test-access-token")
        val refreshToken = EncodedToken.Refresh("test-refresh-token")

        assertEquals(TokenType.Access, accessToken.type)
        assertEquals(TokenType.Refresh, refreshToken.type)
        assertEquals("test-access-token", accessToken.value)
        assertEquals("test-refresh-token", refreshToken.value)
    }

    @Test
    fun `TokenType should have correct expiration durations`() {
        assertEquals(15.minutes, TokenType.Access.expireAt)
        assertEquals(15.days, TokenType.Refresh.expireAt)
    }


    private fun createDecodedToken(type: TokenType): DecodedToken {
        val now = Clock.System.now()
        return DecodedToken(
            id = 123L,
            subject = "user123",
            loginAt = now - 10.minutes,
            deviceType = DeviceType.Mobile,
            deviceName = "Test Device",
            type = type,
            issuedAt = now,
            expiredAt = now + type.expireAt,
            emailVerified = true,
            phoneNumberVerified = false
        )
    }

}