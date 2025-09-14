package dev.bayan_ibrahim.authentication.model

import kotlinx.datetime.Instant

interface BaseDeviceSessionData {
    val deviceType: DeviceType?
    val deviceName: String?
}

data class DeviceSessionData(
    override val deviceType: DeviceType?,
    override val deviceName: String?,
) : BaseDeviceSessionData {
    constructor(data: BaseDeviceSessionData) : this(
        deviceType = data.deviceType,
        deviceName = data.deviceName
    )
}

interface BaseDeviceSession : BaseDeviceSessionData {
    val id: Long
    val userID: Long
    val loginAt: Instant
    val emailVerified: Boolean
    val phoneNumberVerified: Boolean
}

data class DeviceSession(
    override val id: Long,
    override val userID: Long,
    override val loginAt: Instant,
    override val deviceType: DeviceType?,
    override val deviceName: String?,
    override val emailVerified: Boolean,
    override val phoneNumberVerified: Boolean,
) : BaseDeviceSession {
    constructor(session: BaseDeviceSession) : this(
        id = session.id,
        userID = session.userID,
        loginAt = session.loginAt,
        deviceType = session.deviceType,
        deviceName = session.deviceName,
        emailVerified = session.emailVerified,
        phoneNumberVerified = session.phoneNumberVerified,
    )

    constructor(
        data: BaseDeviceSessionData,
        id: Long,
        userID: Long,
        loginAt: Instant,
        emailVerified: Boolean,
        phoneNumberVerified: Boolean,
    ) : this(
        id = id,
        userID = userID,
        loginAt = loginAt,
        deviceType = data.deviceType,
        deviceName = data.deviceName,
        emailVerified = emailVerified,
        phoneNumberVerified = phoneNumberVerified,
    )
}

enum class DeviceType {
    Mobile, Browser, Desktop
}