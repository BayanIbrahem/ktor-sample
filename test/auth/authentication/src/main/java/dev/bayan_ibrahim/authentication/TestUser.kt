package dev.bayan_ibrahim.authentication

import dev.bayan_ibrahim.authentication.model.BaseUser

// Helper classes for testing
data class TestUser(
    override val id: Long,
    override val name: BaseUser.Name,
    override val identityVerified: Boolean = false,
    override val username: String? = null,
    override val email: String? = null,
    override val phoneNumber: String? = null,
    override val userPicUri: String? = null,
    override val profilePicUri: String? = null,
    override val address: BaseUser.Address? = null,
    override val metadata: BaseUser.Metadata? = null,
    override val contactInfo: List<BaseUser.ContactInfo> = emptyList(),
    override val otherData: Map<String, String> = emptyMap(),
) : BaseUser
