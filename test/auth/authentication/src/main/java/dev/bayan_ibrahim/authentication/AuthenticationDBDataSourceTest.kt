@file:Suppress("unused", "FunctionName")

package dev.bayan_ibrahim.authentication

import dev.bayan_ibrahim.authentication.core.AuthenticationDBDataSource
import dev.bayan_ibrahim.authentication.model.BaseUser
import dev.bayan_ibrahim.authentication.model.DeviceType
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

abstract class AuthenticationDBDataSourceTest {

    abstract fun createDataSource(): AuthenticationDBDataSource
    abstract fun getHashedPassword(password: String): String

    fun createMockUser(): TestUser {
        return TestUser(
            id = 1,
            name = BaseUser.Name(
                first = "first",
                middle = "middle",
                last = "last",
                prefix = "prefix",
                suffix = "suffix"
            ),
            identityVerified = true,
            username = "username",
            email = "user@email.com",
            phoneNumber = "+123456789",
            userPicUri = "https://uri/prifile",
            profilePicUri = "https://uri/prifile",
            address = BaseUser.Address(
                street = "street",
                street2 = "street2",
                city = "city",
                state = "state",
                country = "country",
                postalCode = "postal code"
            ),
            metadata = BaseUser.Metadata(),
            contactInfo = emptyList(),
            otherData = emptyMap()
        )
    }

    fun createMockSession(): TestDeviceSession {
        return TestDeviceSession(
            id = 1,
            userID = 1,
            loginAt = Clock.System.now(),
            deviceType = DeviceType.Mobile,
            deviceName = "device name",
            emailVerified = true,
            phoneNumberVerified = true
        )
    }
    private lateinit var dataSource: AuthenticationDBDataSource

    @Before
    fun setup() {
        dataSource = createDataSource()
    }

    @Test
    fun `getUserByID should return correct user`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val retrievedUser = dataSource.getUserByID(mockUser.id)
        assertNotNull(retrievedUser)
        assertEquals(mockUser.id, retrievedUser?.id)
        assertEquals(mockUser.email, retrievedUser?.email)
    }

    @Test
    fun `getUserByID should return null for non-existent user`() = runBlocking {
        val retrievedUser = dataSource.getUserByID(-1L)
        assertNull(retrievedUser)
    }

    @Test
    fun `getUsersByEmail should return users with matching email`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val users = dataSource.getUsersByEmail(mockUser.email!!)
        assertTrue(users.isNotEmpty())
        assertEquals(mockUser.email, users.first().email)
    }

    @Test
    fun `getUsersByPhoneNumber should return users with matching phone number`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val users = dataSource.getUsersByPhoneNumber(mockUser.phoneNumber!!)
        assertTrue(users.isNotEmpty())
        assertEquals(mockUser.phoneNumber, users.first().phoneNumber)
    }

    @Test
    fun `getUsersByUsername should return users with matching username`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val users = dataSource.getUsersByUsername(mockUser.username!!)
        assertTrue(users.isNotEmpty())
        assertEquals(mockUser.username, users.first().username)
    }

    @Test
    fun `saveNewUser should store user correctly`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val retrievedUser = dataSource.getUserByID(mockUser.id)
        assertNotNull(retrievedUser)
        assertEquals(mockUser.name.first, retrievedUser?.name?.first)
        assertEquals(mockUser.name.last, retrievedUser?.name?.last)
    }

    @Test
    fun `getUserByEmailAndPassword should return user with correct credentials`() = runBlocking {
        val mockUser = createMockUser()
        val password = "password123"
        dataSource.saveNewUser(mockUser, getHashedPassword(password))

        val user = dataSource.getUserByEmailAndPassword(mockUser.email!!, getHashedPassword(password))
        assertNotNull(user)
        assertEquals(mockUser.id, user?.id)
    }

    @Test
    fun `getUserByEmailAndPassword should return null with incorrect password`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val user = dataSource.getUserByEmailAndPassword(
            mockUser.email!!,
            getHashedPassword("wrongpassword")
        )
        assertNull(user)
    }

    @Test
    fun `getUserByPhoneNumberAndPassword should return user with correct credentials`() = runBlocking {
        val mockUser = createMockUser()
        val password = "password123"
        dataSource.saveNewUser(mockUser, getHashedPassword(password))

        val user = dataSource.getUserByPhoneNumberAndPassword(mockUser.phoneNumber!!, getHashedPassword(password))
        assertNotNull(user)
        assertEquals(mockUser.id, user?.id)
    }

    @Test
    fun `getUserByUsernameAndPassword should return user with correct credentials`() = runBlocking {
        val mockUser = createMockUser()
        val password = "password123"
        dataSource.saveNewUser(mockUser, getHashedPassword(password))

        val user = dataSource.getUserByUsernameAndPassword(mockUser.username!!, getHashedPassword(password))
        assertNotNull(user)
        assertEquals(mockUser.id, user?.id)
    }

    @Test
    fun `getSessionOfRefreshToken should return correct session`(): Unit = runBlocking {
        val mockSession = createMockSession()
        dataSource.saveNewDeviceSession(mockSession)

        // This test assumes the implementation stores hashed refresh tokens in a way that can be retrieved
        // The exact mechanism would depend on the implementation
    }

    @Test
    fun `saveNewDeviceSession should store session correctly`() = runBlocking {
        val mockSession = createMockSession()
        val savedSession = dataSource.saveNewDeviceSession(mockSession)

        assertNotNull(savedSession)
        assertEquals(mockSession.userID, savedSession.userID)
    }

    @Test
    fun `updateDeviceSessionTokenData should update tokens correctly`() = runBlocking {
        val mockSession = createMockSession()
        val savedSession = dataSource.saveNewDeviceSession(mockSession)

        val newAccessToken = "new_access_token_hash"
        val newRefreshToken = "new_refresh_token_hash"

        dataSource.updateDeviceSessionTokenData(savedSession.id, newAccessToken, newRefreshToken)

        // This would need to be verified based on implementation details
        // For example, if there's a way to retrieve a session by ID and check its tokens
    }

    @Test
    fun `updateUserData should update user information correctly`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val updatedName = BaseUser.Name("Updated", "Middle", "Name")
        val updatedUser = mockUser.copy(name = updatedName)

        val result = dataSource.updateUserData(mockUser.id, updatedUser)
        assertEquals(updatedName.first, result.name.first)
        assertEquals(updatedName.last, result.name.last)
    }

    @Test
    fun `deleteSessionOfUser should remove specific session`() = runBlocking {
        val mockSession = createMockSession()
        val savedSession = dataSource.saveNewDeviceSession(mockSession)

        val result = dataSource.deleteSessionOfUser(savedSession.userID, savedSession.id)
        assertTrue(result)
    }

    @Test
    fun `deleteSessionsOfUser should remove all user sessions`() = runBlocking {
        val mockSession = createMockSession()
        val savedSession = dataSource.saveNewDeviceSession(mockSession)

        val result = dataSource.deleteSessionsOfUser(savedSession.userID)
        assertTrue(result)
    }

    @Test
    fun `deleteUser should remove user from database`() = runBlocking {
        val mockUser = createMockUser()
        dataSource.saveNewUser(mockUser, getHashedPassword("password123"))

        val result = dataSource.deleteUser(mockUser.id)
        assertTrue(result)

        val retrievedUser = dataSource.getUserByID(mockUser.id)
        assertNull(retrievedUser)
    }

    @Test
    fun `BaseUser Name should handle all components correctly`() {
        val name = BaseUser.Name(
            first = "John", middle = "Michael", last = "Doe", prefix = "Dr.", suffix = "Jr."
        )

        assertEquals("John", name.first)
        assertEquals("Michael", name.middle)
        assertEquals("Doe", name.last)
        assertEquals("Dr.", name.prefix)
        assertEquals("Jr.", name.suffix)
    }

    @Test
    fun `BaseUser ContactInfo should store account details correctly`() {
        val contactInfo = BaseUser.ContactInfo(
            accountLabel = "Facebook", accountUri = "https://facebook.com/johndoe"
        )

        assertEquals("Facebook", contactInfo.accountLabel)
        assertEquals("https://facebook.com/johndoe", contactInfo.accountUri)
    }

    @Test
    fun `BaseUser Address should handle all address components`() {
        val address = BaseUser.Address(
            street = "123 Main St", street2 = "Apt 4B", city = "New York", state = "NY", country = "USA", postalCode = "10001"
        )

        assertEquals("123 Main St", address.street)
        assertEquals("Apt 4B", address.street2)
        assertEquals("New York", address.city)
        assertEquals("NY", address.state)
        assertEquals("USA", address.country)
        assertEquals("10001", address.postalCode)
    }

    @Test
    fun `BaseUser Metadata should store system information correctly`() {
        val now = Clock.System.now()
        val metadata = BaseUser.Metadata(
            createdAt = now,
            updatedAt = now,
            locale = "en-US",
            timezone = "America/New_York",
            timeOffset = -5
        )

        assertEquals(now, metadata.createdAt)
        assertEquals(now, metadata.updatedAt)
        assertEquals("en-US", metadata.locale)
        assertEquals("America/New_York", metadata.timezone)
        assertEquals(-5, metadata.timeOffset)
    }
}


