package dev.bayan_ibrahim.test.auth.core

import dev.bayan_ibrahim.authorization.model.privilege.Privilege
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

class PrivilegeTest {
    @Test
    fun `encode with only action and resource`() {
        val p = Privilege("read", "user", null, null)
        assertEquals("read:user", p.encode())
    }

    @Test
    fun `encode with action, resource and resID`() {
        val p = Privilege("update", "post", 123L, null)
        assertEquals("update:post@123", p.encode())
    }

    @Test
    fun `encode with action, resource and expireAt`() {
        val now = Instant.parse("2025-09-14T12:00:00Z")
        val p = Privilege("delete", "file", null, now)
        assertEquals("delete:fileT2025-09-14T12:00:00Z", p.encode())
    }

    @Test
    fun `encode with action, resource, resID and expireAt`() {
        val now = Instant.parse("2025-09-14T12:00:00Z")
        val p = Privilege("create", "doc", 456L, now)
        assertEquals("create:doc@456T2025-09-14T12:00:00Z", p.encode())
    }

    @Test
    fun `decode simple privilege`() {
        val p = Privilege.decode("read:user")
        assertEquals("read", p.action)
        assertEquals("user", p.resource)
        assertNull(p.resID)
        assertNull(p.expireAt)
    }

    @Test
    fun `decode with resID`() {
        val p = Privilege.decode("update:post@123")
        assertEquals("update", p.action)
        assertEquals("post", p.resource)
        assertEquals(123L, p.resID)
        assertNull(p.expireAt)
    }

    @Test
    fun `decode with expireAt`() {
        val p = Privilege.decode("delete:fileT2025-09-14T12:00:00Z")
        assertEquals("delete", p.action)
        assertEquals("file", p.resource)
        assertNull(p.resID)
        assertEquals(Instant.parse("2025-09-14T12:00:00Z"), p.expireAt)
    }

    @Test
    fun `decode with resID and expireAt`() {
        val p = Privilege.decode("create:doc@456T2025-09-14T12:00:00Z")
        assertEquals("create", p.action)
        assertEquals("doc", p.resource)
        assertEquals(456L, p.resID)
        assertEquals(Instant.parse("2025-09-14T12:00:00Z"), p.expireAt)
    }

    @Test
    fun `decode invalid string throws`() {
        assertThrows<IllegalArgumentException> {
            Privilege.decode("invalid-format")
        }
    }

    @Test
    fun `satisfy passes when privilege is more general`() {
        val general = Privilege("read", "user", null, null)
        val specific = Privilege("read", "user", 123L, null)
        assertTrue(general satisfy specific)
    }

    @Test
    fun `satisfy fails when action mismatch`() {
        val p1 = Privilege("read", "user", null, null)
        val p2 = Privilege("write", "user", null, null)
        assertFalse(p1 satisfy p2)
    }

    @Test
    fun `satisfy fails when resource mismatch`() {
        val p1 = Privilege("read", "user", null, null)
        val p2 = Privilege("read", "post", null, null)
        assertFalse(p1 satisfy p2)
    }

    @Test
    fun `satisfy passes when privilege has null resID`() {
        val p1 = Privilege("read", "user", null, null)
        val p2 = Privilege("read", "user", 999L, null)
        assertTrue(p1 satisfy p2)
    }

    @Test
    fun `satisfy fails when resID mismatch`() {
        val p1 = Privilege("read", "user", 123L, null)
        val p2 = Privilege("read", "user", 456L, null)
        assertFalse(p1 satisfy p2)
    }

    @Test
    fun `satisfy passes when privilege never expires`() {
        val neverExpire = Privilege("read", "user", null, null)
        val required = Privilege("read", "user", null, Clock.System.now())
        assertTrue(neverExpire satisfy required)
    }

    @Test
    fun `satisfy passes when privilege expiration is after required`() {
        val p1 = Privilege("read", "user", null, Instant.parse("2025-09-15T00:00:00Z"))
        val p2 = Privilege("read", "user", null, Instant.parse("2025-09-14T00:00:00Z"))
        assertTrue(p1 satisfy p2)
    }

    @Test
    fun `satisfy fails when privilege expiration is before required`() {
        val p1 = Privilege("read", "user", null, Instant.parse("2025-09-13T00:00:00Z"))
        val p2 = Privilege("read", "user", null, Instant.parse("2025-09-14T00:00:00Z"))
        assertFalse(p1 satisfy p2)
    }

    @Test
    fun `satisfy passes when required privilege has no expiration`() {
        val p1 = Privilege("read", "user", null, Instant.parse("2025-09-14T00:00:00Z"))
        val p2 = Privilege("read", "user", null, null)
        // rule says: if required has no expire, privilege must NOT have expiration
        assertFalse(p1 satisfy p2)
    }

    @Test
    fun `regex matches valid privilege strings`() {
        assertTrue(Privilege.regex.matches("read:user"))
        assertTrue(Privilege.regex.matches("update:post@123"))
        assertTrue(Privilege.regex.matches("delete:fileT2025-09-14T12:00:00Z"))
        assertTrue(Privilege.regex.matches("create:doc@456T2025-09-14T12:00:00Z"))
    }

    @Test
    fun `regex does not match invalid privilege strings`() {
        assertFalse(Privilege.regex.matches("invalid"))
        assertFalse(Privilege.regex.matches("read"))
        assertFalse(Privilege.regex.matches("read:user:extra"))
    }
}
