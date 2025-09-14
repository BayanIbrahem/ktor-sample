package dev.bayan_ibrahim.authentaction.core.authenticaiton_repo

import dev.bayan_ibrahim.authentaction.core.AuthenticationDBDataSource
import dev.bayan_ibrahim.authentaction.core.hash_encoder.HashEncoder
import dev.bayan_ibrahim.authentaction.core.jwt_token_repo.JwtTokenRepo
import dev.bayan_ibrahim.authentaction.model.AuthConfigurations
import dev.bayan_ibrahim.authentaction.model.BaseDeviceSession
import dev.bayan_ibrahim.authentaction.model.BaseDeviceSessionData
import dev.bayan_ibrahim.authentaction.model.BaseUser
import dev.bayan_ibrahim.authentaction.model.DeviceSession
import dev.bayan_ibrahim.authentaction.model.EncodedToken
import dev.bayan_ibrahim.authentaction.model.TokenType
import kotlinx.datetime.Clock

class AuthenticationRepoImpl<U : BaseUser>(
    private val authenticationDBDataSource: AuthenticationDBDataSource,
    private val jwtTokenRepo: JwtTokenRepo,
    private val userMapper: (BaseUser) -> U,
    private val hashEncoder: HashEncoder,
    private val authConfigurations: AuthConfigurations = AuthConfigurations(),
) : AuthenticationRepo<U> {
    /**
     * create new account, require satisfying [authConfigurations] for email, username, and phone number
     */
    override suspend fun createAccount(user: U, password: String) {
        require(user.email != null || !authConfigurations.emailStatus.isRequired)
        require(user.username != null || !authConfigurations.usernameStatus.isRequired)
        require(user.phoneNumber != null || !authConfigurations.phoneNumberStatus.isRequired)
        if (authConfigurations.emailStatus.isUnique) {
            user.email?.let { email ->
                val users = authenticationDBDataSource.getUsersByEmail(email)
                require(users.isEmpty()) { "Duplicate email $email" }
            }
        }

        if (authConfigurations.phoneNumberStatus.isUnique) {
            user.phoneNumber?.let { phoneNumber ->
                val users = authenticationDBDataSource.getUsersByPhoneNumber(phoneNumber)
                require(users.isEmpty()) { "Duplicate phone number $phoneNumber" }
            }
        }

        if (authConfigurations.phoneNumberStatus.isUnique) {
            user.username?.let { username ->
                val users = authenticationDBDataSource.getUsersByUsername(username)
                require(users.isEmpty()) { "Duplicate username $username" }
            }
        }
        val hashedPassword = hashEncoder.hash(password)
        authenticationDBDataSource.saveNewUser(user, hashedPassword)
    }

    override suspend fun loginByEmail(
        email: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh> {
        val hashedPassword = hashEncoder.hash(password)
        val user = authenticationDBDataSource.getUserByEmailAndPassword(
            email = email,
            hashPassword = hashedPassword
        )
        require(user != null) {
            "email or password incorrect"
        }
        val session = DeviceSession(
            data = session,
            id = -1,
            userID = user.id,
            loginAt = Clock.System.now(),
            emailVerified = false,
            phoneNumberVerified = false
        )
        return buildAndStoreTokens(session)
    }


    override suspend fun loginByUsername(
        username: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh> {
        val hashedPassword = hashEncoder.hash(password)
        val user = authenticationDBDataSource.getUserByUsernameAndPassword(
            username = username,
            hashPassword = hashedPassword
        )
        require(user != null) {
            "username or password incorrect"
        }
        val session = DeviceSession(
            data = session,
            id = -1,
            userID = user.id,
            loginAt = Clock.System.now(),
            emailVerified = false,
            phoneNumberVerified = false
        )
        return buildAndStoreTokens(session)
    }

    override suspend fun loginByPhoneNumber(
        phoneNumber: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh> {
        val hashedPassword = hashEncoder.hash(password)
        val user = authenticationDBDataSource.getUserByPhoneNumberAndPassword(
            phoneNumber = phoneNumber,
            hashPassword = hashedPassword
        )
        require(user != null) {
            "phone number or password incorrect"
        }
        val session = DeviceSession(
            data = session,
            id = -1,
            userID = user.id,
            loginAt = Clock.System.now(),
            emailVerified = false,
            phoneNumberVerified = false,
        )
        return buildAndStoreTokens(session)
    }

    override suspend fun logoutSession(userID: Long, sessionID: Long): Boolean {
        return authenticationDBDataSource.deleteSessionOfUser(userID = userID, sessionID = sessionID)
    }

    override suspend fun logoutAllSession(userID: Long): Boolean {
        return authenticationDBDataSource.deleteSessionsOfUser(userID = userID)
    }

    override suspend fun deleteAccount(userID: Long): Boolean {
        return authenticationDBDataSource.deleteUser(userID)
    }

    override suspend fun refreshToken(refreshToken: EncodedToken.Refresh): Pair<EncodedToken.Access, EncodedToken.Refresh> {
        jwtTokenRepo.decodeToken(refreshToken)
        val hashedEncodedRefreshToken = hashEncoder.hash(refreshToken.value)
        val session = authenticationDBDataSource.getSessionOfRefreshToken(hashedEncodedRefreshToken)
        require(session != null) {
            "Valid But Unexisted refresh token"
        }
        return buildAndStoreTokens(session)
    }

    override suspend fun getUser(userID: Long): U {
        val user = authenticationDBDataSource.getUserByID(userID)
        require(user != null) { "user with id $userID not found" }
        return userMapper(user)
    }

    override suspend fun updateUserData(user: U): U {
        val oldUser = getUser(user.id)
        val newUser = authenticationDBDataSource.updateUserData(oldUser.id, user)
        return userMapper(newUser)
    }

    private suspend fun buildAndStoreTokens(
        session: BaseDeviceSession,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh> {
        val session = authenticationDBDataSource.saveNewDeviceSession(session = session)

        val decodedAccessToken = jwtTokenRepo.buildTokenData(
            session = session,
            type = TokenType.Access,
            at = Clock.System.now()
        )
        val decodedRefreshToken = jwtTokenRepo.buildTokenData(
            session = session,
            type = TokenType.Refresh,
            at = Clock.System.now()
        )

        val encodedAccessToken = jwtTokenRepo.encodeToken(decodedAccessToken)
        val encodedRefreshToken = jwtTokenRepo.encodeToken(decodedRefreshToken)

        val hashedEncodedAccessToken = hashEncoder.hash(encodedAccessToken.value)
        val hashedEncodedRefreshToken = hashEncoder.hash(encodedRefreshToken.value)

        authenticationDBDataSource.updateDeviceSessionTokenData(
            sessionID = session.id,
            hashedEncodedAccessToken = hashedEncodedAccessToken,
            hashedEncodedRefreshToken = hashedEncodedRefreshToken
        )
        return encodedAccessToken as EncodedToken.Access to encodedRefreshToken as EncodedToken.Refresh
    }
}