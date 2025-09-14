package dev.bayan_ibrahim.authentication.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

data class AuthConfigurations(
    val accessTokenExpiration: Duration = 10.minutes,
    val refreshTokenExpiration: Duration = 15.days,
    val usernameStatus: EntryStatus = EntryStatus(isRequired = false, isUnique = true),
    val emailStatus: EntryStatus = EntryStatus(isRequired = false, isUnique = true),
    val phoneNumberStatus: EntryStatus = EntryStatus(isRequired = false, isUnique = false),
) {
    /**
     * what is the status of the entry
     */
    data class EntryStatus(
        val isRequired: Boolean,
        val isUnique: Boolean,
    )
}
