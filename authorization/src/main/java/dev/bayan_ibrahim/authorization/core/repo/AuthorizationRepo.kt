package dev.bayan_ibrahim.authorization.core.repo

import dev.bayan_ibrahim.authorization.model.privilege.Privilege
import dev.bayan_ibrahim.authorization.model.privilege.Privileges
import kotlinx.datetime.Instant

interface AuthorizationRepo {
    suspend fun hasPrivilege(
        userID: Long,
        privilege: Privilege,
        expireBefore: Instant? = null,
    ): Boolean
    suspend fun getPrivileges(userID: Long): Privileges
    suspend fun grandPrivilege(userID: Long, privilege: Privilege): Boolean
    suspend fun deletePrivilege(userID: Long, privilege: Privilege): Boolean
    suspend fun expirePrivilege(userID: Long, privilege: Privilege): Boolean
}