package dev.bayan_ibrahim.authentication.core.authenticaiton_repo

import dev.bayan_ibrahim.authentication.model.BaseDeviceSessionData
import dev.bayan_ibrahim.authentication.model.BaseUser
import dev.bayan_ibrahim.authentication.model.EncodedToken

interface AuthenticationRepo<U : BaseUser> {
    /**
     * create new account
     * */
    suspend fun createAccount(user: U, password: String)

    /**
     * login user by email
     * @return token pair
     * @see loginByUsername
     * @see loginByPhoneNumber
     */
    suspend fun loginByEmail(
        email: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh>

    /**
     * login user by username
     * @return token pair
     * @see loginByPhoneNumber
     * @see loginByEmail
     */
    suspend fun loginByUsername(
        username: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh>

    /**
     * login user by phone number
     * @return token pair
     * @see loginByUsername
     * @see loginByEmail
     */
    suspend fun loginByPhoneNumber(
        phoneNumber: String,
        password: String,
        session: BaseDeviceSessionData,
    ): Pair<EncodedToken.Access, EncodedToken.Refresh>

    // TODO, login by google and other oauth stuff

    suspend fun logoutSession(userID: Long, sessionID: Long): Boolean
    suspend fun logoutAllSession(userID: Long): Boolean
    suspend fun deleteAccount(userID: Long): Boolean
    suspend fun refreshToken(refreshToken: EncodedToken.Refresh): Pair<EncodedToken.Access, EncodedToken.Refresh>

    suspend fun getUser(userID: Long): U
    suspend fun updateUserData(user: U): U
}