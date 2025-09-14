package dev.bayan_ibrahim.authentication.model.privilege

/**
 * Represents a privilege, which is a combination of an action, a resource, and an optional resource ID.
 *
 * @property action The action that can be performed (e.g., "create", "read", "update", "delete").
 * @property resource The resource on which the action can be performed (e.g., "user", "post").
 * @property resID Optional ID of the specific resource. If null, the privilege applies to all resources of that type.
 */
data class Privilege(
    val action: String,
    val resource: String,
    val resID: Long?,
) {
    /**
     * Encodes the privilege into a string representation.
     * The format is "action:resource@resID" or "action:resource" if resID is null.
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
        val regex = Regex("^([a-z]*):([a-z]*)(@\\w*)?$")

        /**
         * Encodes a given privilege into a string representation.
         * The format is "action:resource@resID" or "action:resource" if resID is null.
         *
         * @param privilege The privilege to encode.
         * @return The encoded string representation of the privilege.
         */
        fun encode(privilege: Privilege): String {
            return buildString {
                append(privilege.action)
                append(":")
                append(privilege.resource)
                if (privilege.resID != null) {
                    append("@")
                    append(privilege.resID)
                }
            }
        }

        /**
         * Decodes a string representation into a Privilege object.
         *
         * @param string The string to decode. Must be in the format "action:resource@resID" or "action:resource".
         * @return The decoded Privilege object.
         * @throws IllegalArgumentException if the string format is invalid.
         */
        fun decode(string: String): Privilege {
            val results = regex.find(string)?.groupValues
            require(results != null) { "Invalid privilege string format" }
            require(results.size >= 3) { "Invalid privilege string format" }
            val action = results[1]
            val resource = results[2]
            val resID = results.getOrNull(3)?.trimStart('@')?.let {
                if (it.isBlank()) {
                    null
                } else {
                    it.toLong()
                }
            }
            return Privilege(action = action, resource = resource, resID = resID)
        }

        /**
         * Checks if a given privilege satisfies a required privilege.
         *
         * A privilege satisfies a required privilege if:
         * - The action is empty or matches the required action.
         * - The resource is empty or matches the required resource.
         * - The resource ID is null or matches the required resource ID.
         *
         * @param privilege The privilege to check.
         * @param requiredPrivilege The privilege that is required.
         * @return True if the given privilege satisfies the required privilege, false otherwise.
         */
        fun satisfy(privilege: Privilege, requiredPrivilege: Privilege): Boolean {
            val satisfyAction = privilege.action.isEmpty() || privilege.action == requiredPrivilege.action
            val satisfyRes = privilege.resource.isEmpty() || privilege.resource == requiredPrivilege.resource
            val satisfyResID = privilege.resID == null || privilege.resID == requiredPrivilege.resID
            return satisfyAction && satisfyRes && satisfyResID
        }
    }
}
