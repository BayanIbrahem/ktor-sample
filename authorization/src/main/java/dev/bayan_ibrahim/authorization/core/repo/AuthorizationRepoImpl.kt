package dev.bayan_ibrahim.authorization.core.repo

import dev.bayan_ibrahim.authorization.core.AuthorizationRepoDBDataSource
import dev.bayan_ibrahim.authorization.model.privilege.Privilege
import dev.bayan_ibrahim.authorization.model.privilege.Privileges
import kotlinx.datetime.Instant

class AuthorizationRepoImpl(
    private val dbDataSource: AuthorizationRepoDBDataSource,
) : AuthorizationRepo {
    // TODO, need to add revoke privileges to remove a certain privilege
    override suspend fun hasPrivilege(
        userID: Long,
        privilege: Privilege,
        expireBefore: Instant?,
    ): Boolean {
        return dbDataSource.getPrivileges(
            userID = userID,
            action = privilege.action.takeIfNotBlank(),
            resource = privilege.resource.takeIfNotBlank(),
            resourceID = privilege.resID,
            expireAfter = privilege.expireAt,
            expireBefore = expireBefore,
        ).isNotEmpty()
    }

    override suspend fun getPrivileges(userID: Long): Privileges {
        return Privileges(dbDataSource.getPrivileges(userID))
    }

    override suspend fun grandPrivilege(userID: Long, privilege: Privilege): Boolean {
        return dbDataSource.addPrivilege(userID, privilege)
    }

    override suspend fun deletePrivilege(userID: Long, privilege: Privilege): Boolean {
        return dbDataSource.deletePrivilege(userID = userID, privilege = privilege)
    }

    override suspend fun expirePrivilege(userID: Long, privilege: Privilege): Boolean {
        return dbDataSource.expirePrivilege(userID = userID, privilege = privilege)
    }

    private fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }
}