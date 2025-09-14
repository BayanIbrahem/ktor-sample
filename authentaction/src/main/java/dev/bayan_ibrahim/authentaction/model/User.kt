package dev.bayan_ibrahim.authentaction.model

import java.time.Instant

interface BaseUser {
    val id: Long
    val name: Name
    val identityVerified: Boolean
    val username: String?
    val email: String?
    val phoneNumber: String?
    val userPicUri: String?
    val profilePicUri: String?
    val address: Address?
    val metadata: Metadata?
    val contactInfo: List<ContactInfo>
    val otherData: Map<String, String>

    /**
     * User's name components
     * @property first First/given name
     * @property middle Middle name/initial
     * @property last Last/family name
     * @property prefix Title (e.g., Dr., Mr.)
     * @property suffix Generational suffix (e.g., Jr., III)
     */
    data class Name(
        val first: String,
        val middle: String? = null,
        val last: String? = null,
        val prefix: String? = null,
        val suffix: String? = null,
    )

    /**
     * additional contact info for the user,
     * @param accountLabel label of the account, like facebook, whatsapp, etc
     * @param accountUri uri of the account
     */
    data class ContactInfo(
        val accountLabel: String,
        val accountUri: String,
    )

    /**
     * Physical address details
     * @property street Primary street address, it may contains all the data, like:
     * Country, State, City, street, in some cases the address is needed just a human readable text
     * @property street2 Apartment/Suite number
     * @property city Municipality
     * @property state Province/Region
     * @property country ISO country code
     * @property postalCode ZIP/Postal code
     */
    data class Address(
        val street: String,
        val street2: String? = null,
        val city: String? = null,
        val state: String? = null,
        val country: String? = null,
        val postalCode: String? = null,
    )

    /**
     * System-generated metadata
     * @property createdAt Account creation timestamp
     * @property updatedAt Last profile update timestamp
     * @property locale User's preferred locale (e.g., en-US)
     * @property timezone IANA timezone (e.g., America/New_York)
     * @property timeOffset is the offset of the time zone
     */
    data class Metadata(
        val createdAt: Instant = Instant.now(),
        val updatedAt: Instant? = null,
        val locale: String? = null,
        val timezone: String? = null,
        val timeOffset: Int? = null,
    )
}

data class User(
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
