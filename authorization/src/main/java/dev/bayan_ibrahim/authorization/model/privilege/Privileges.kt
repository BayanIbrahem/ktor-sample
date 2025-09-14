package dev.bayan_ibrahim.authorization.model.privilege

/**
 * Represents a collection of [Privilege]s.
 *
 * @property privileges The set of privileges.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
data class Privileges(
    val privileges: Set<Privilege> = emptySet(),
) : Set<Privilege> by privileges {
    /**
     * Checks if this set of privileges satisfies the given [privilege].
     *
     * @param privilege The privilege to check.
     * @return True if the privilege is satisfied, false otherwise.
     */
    infix fun satisfy(privilege: Privilege): Boolean = satisfy(
        privileges = this,
        privilege = privilege
    )

    /**
     * Encodes this set of privileges into a string.
     *
     * @return The encoded string representation of the privileges.
     */
    fun encode(): String = encode(this)

    companion object {

        /**
         * Encodes the given [Privileges] into a string.
         *
         * @param privileges The privileges to encode.
         * @return The encoded string representation of the privileges.
         */
        fun encode(privileges: Privileges): String {
            return privileges.joinToString(
                separator = ";",
                prefix = "",
                postfix = ""
            ) {
                it.encode()
            }
        }

        /**
         * Decodes the given string into a [Privileges] object.
         *
         * @param string The string to decode.
         * @return The decoded [Privileges] object.
         */
        fun decode(string: String): Privileges {
            if(string.isBlank()) return Privileges(emptySet())
            return Privileges(
                string.split(";").map {
                    Privilege.decode(it)
                }.toSet()
            )
        }

        /**
         * Checks if the given set of [privileges] satisfies the given [privilege].
         *
         * @param privileges The set of privileges to check.
         * @param privilege The privilege to check.
         * @return True if the privilege is satisfied, false otherwise.
         */
        fun satisfy(privileges: Privileges, privilege: Privilege): Boolean {
            return privileges.any {
                it.satisfy(privilege)
            }
        }
    }
}
