package dev.bayan_ibrahim.authentication.core

import dev.bayan_ibrahim.authentication.model.BaseDeviceSession
import dev.bayan_ibrahim.authentication.model.BaseUser

/**
 * this repo is responsible to save account data and retrieve them
 */
interface AuthenticationDBDataSource {
    suspend fun getUserByID(id: Long): BaseUser?
    suspend fun getUsersByEmail(email: String): List<BaseUser>
    suspend fun getUsersByPhoneNumber(phoneNumber: String): List<BaseUser>
    suspend fun getUsersByUsername(username: String): List<BaseUser>
    suspend fun saveNewUser(user: BaseUser, hashedPassword: String)
    suspend fun getUserByEmailAndPassword(email: String, hashPassword: String): BaseUser?
    suspend fun getUserByPhoneNumberAndPassword(phoneNumber: String, hashPassword: String): BaseUser?
    suspend fun getUserByUsernameAndPassword(username: String, hashPassword: String): BaseUser?
    suspend fun getSessionOfRefreshToken(
        hashedEncodedRefreshToken: String
    ): BaseDeviceSession?

    suspend fun saveNewDeviceSession(
        session: BaseDeviceSession,
    ): BaseDeviceSession

    suspend fun updateDeviceSessionTokenData(
        sessionID: Long,
        hashedEncodedAccessToken: String,
        hashedEncodedRefreshToken: String,
    )
    suspend fun updateUserData(
        userID: Long,
        userData: BaseUser
    ): BaseUser

    suspend fun deleteSessionOfUser(userID: Long, sessionID: Long): Boolean

    suspend fun deleteSessionsOfUser(userID: Long): Boolean
    suspend fun deleteUser(userID: Long): Boolean
}