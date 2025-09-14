package dev.bayan_ibrahim.authentication

import dev.bayan_ibrahim.authentication.model.BaseDeviceSession
import dev.bayan_ibrahim.authentication.model.DeviceType
import kotlinx.datetime.Instant

data class TestDeviceSession(
    override val id: Long,
    override val userID: Long,
    override val loginAt: Instant,
    override val deviceType: DeviceType?,
    override val deviceName: String?,
    override val emailVerified: Boolean,
    override val phoneNumberVerified: Boolean,
) : BaseDeviceSession
