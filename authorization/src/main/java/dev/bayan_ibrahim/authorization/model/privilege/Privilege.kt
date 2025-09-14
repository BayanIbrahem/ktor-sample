package dev.bayan_ibrahim.authorization.model.privilege

import kotlinx.datetime.Instant


/**
 * Represents a privilege, which is a combination of an action, a resource, an optional resource ID, and an optional expiration time.
 *
 * @property action The action that can be performed (e.g., "create", "read", "update", "delete").
 * @property resource The resource on which the action can be performed (e.g., "user", "post").
 * @property resID Optional ID of the specific resource. If null, the privilege applies to all resources of that type.
 * @property expireAt Optional expiration time for the privilege. If null, the privilege does not expire.
 */
data class Privilege(
    val action: String,
    val resource: String,
    val resID: Long?,
    val expireAt: Instant?,
) {
    /**
     * Encodes the privilege into a string representation.
     * The format is "action:resource@resIDTexpireAt" or "action:resource@resID" if expireAt is null,
     * or "action:resourceTexpireAt" if resID is null, or "action:resource" if both are null.
     *
     * @return The encoded string representation of the privilege.
     */
    fun encode(): String = encode(this)

    /**
     * Checks if this privilege satisfies another (required) privilege.
     *
     * @param privilege The other privilege to check against.
     * @return True if this privilege satisfies the other privilege, false otherwise.
     */
    infix fun satisfy(privilege: Privilege) = satisfy(this, privilege)

    companion object {
        const val ACTION_RES_SEPARATOR = ":"
        const val RES_ID_PREFIX = "@"
        const val EXPIRE_AT_PREFIX = "T"
        val regex = Regex("^([a-z]*)$ACTION_RES_SEPARATOR([a-z]*)($RES_ID_PREFIX\\w*)?($EXPIRE_AT_PREFIX.*)?$")

        /**
         * Encodes a given privilege into a string representation.
         * The format is "action:resource@resIDTexpireAt" or "action:resource@resID" if expireAt is null,
         * or "action:resourceTexpireAt" if resID is null, or "action:resource" if both are null.
         *
         * @param privilege The privilege to encode.
         * @return The encoded string representation of the privilege.
         */
        fun encode(privilege: Privilege): String {
            return buildString {
                append(privilege.action)
                append(ACTION_RES_SEPARATOR)
                append(privilege.resource)
                if (privilege.resID != null) {
                    append(RES_ID_PREFIX)
                    append(privilege.resID)
                }
                if (privilege.expireAt != null) {
                    append(EXPIRE_AT_PREFIX)
                    append(privilege.expireAt.toString())
                }
            }
        }

        /**
         * Decodes a string representation into a Privilege object.
         *
         * @param string The string to decode. Must be in the format "action:resource@resIDTexpireAt", "action:resource@resID", "action:resourceTexpireAt", or "action:resource".
         * @return The decoded Privilege object.
         * @throws IllegalArgumentException if the string format is invalid.
         */
        fun decode(string: String): Privilege {
            val results = regex.find(string)?.groupValues
            require(results != null) { "Invalid privilege string format: $string" }
            require(results.size >= 3) { "Invalid privilege string format (not enough groups): $string" } // Ensure we have at least action and resource
            val action = results[1]
            val resource = results[2]

            val resIDString = results.getOrNull(3)?.trimStart(RES_ID_PREFIX.first())
            val resID = if (resIDString.isNullOrBlank()) null else resIDString.toLong()

            val expireAtString = results.getOrNull(4)?.trimStart(EXPIRE_AT_PREFIX.first())
            val expireAt = if (expireAtString.isNullOrBlank()) null else Instant.parse(expireAtString)

            return Privilege(
                action = action,
                resource = resource,
                resID = resID,
                expireAt = expireAt
            )
        }

        /**
         * Checks if a given privilege satisfies a required privilege.
         *
         * A privilege satisfies a required privilege if:
         * - The action is empty or matches the required action.
         * - The resource is empty or matches the required resource.
         * - The resource ID is null or matches the required resource ID.
         * - The privilege's expiration time is null (never expires) or is after the required privilege's expiration time.
         *   If the required privilege has no expiration time, then this privilege is required not to have expiration date
         *
         * @param privilege The privilege to check.
         * @param requiredPrivilege The privilege that is required.
         * @return True if the given privilege satisfies the required privilege, false otherwise.
         */
        fun satisfy(
            privilege: Privilege,
            requiredPrivilege: Privilege,
        ): Boolean {
            val satisfyAction = privilege.action.isEmpty() || privilege.action == requiredPrivilege.action
            val satisfyRes = privilege.resource.isEmpty() || privilege.resource == requiredPrivilege.resource
            val satisfyResID = privilege.resID == null || privilege.resID == requiredPrivilege.resID
            // If privilege.expireAt is null, it means it never expires, so it satisfies the expiration requirement.
            // If requiredPrivilege.expireAt is null, it means no expiration is required, so any privilege.expireAt is fine.
            // Otherwise, privilege.expireAt must be after requiredPrivilege.expireAt.
            val satisfyExpireAt = privilege.expireAt == null ||
                    (requiredPrivilege.expireAt != null &&
                            privilege.expireAt > requiredPrivilege.expireAt)
            return satisfyAction && satisfyRes && satisfyResID && satisfyExpireAt
        }
    }
}
