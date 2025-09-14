package dev.bayan_ibrahim.authorization.core

import dev.bayan_ibrahim.authorization.model.privilege.Privilege
import kotlinx.datetime.Instant

interface AuthorizationRepoDBDataSource {
    suspend fun getPrivileges(
        userID: Long,
        action: String? = null,
        resource: String? = null,
        resourceID: Long? = null,
        expireBefore: Instant? = null,
        expireAfter: Instant? = null,
    ): Set<Privilege>


    suspend fun addPrivilege(userID: Long, privilege: Privilege): Boolean
    /**
     * delete privilege permanently
     */
    suspend fun deletePrivilege(userID: Long, privilege: Privilege): Boolean

    /**
     * change the privilege expired at,
     * @return true if the privilege with the specified action, res, resid and user is found
     */
    suspend fun expirePrivilege(userID: Long, privilege: Privilege): Boolean
}