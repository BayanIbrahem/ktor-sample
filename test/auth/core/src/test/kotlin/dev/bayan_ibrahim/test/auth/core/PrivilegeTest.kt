package dev.bayan_ibrahim.test.auth.core

import dev.bayan_ibrahim.authentication.model.privilege.Privilege
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrivilegeTest {
    // Test encoding with various scenarios
    @Test
    fun `encode should correctly format string representation`() {
        listOf(
            Privilege("read", "article", null) to "read:article",
            Privilege("edit", "comment", 123L) to "edit:comment@123",
            Privilege("", "", null) to ":",
            Privilege("", "", 7L) to ":@7",
            Privilege("action", "resource", Long.MAX_VALUE) to "action:resource@${Long.MAX_VALUE}"
        ).forEach { (privilege, expected) ->
            assertEquals(expected, privilege.encode())
            assertEquals(expected, Privilege.encode(privilege))
        }
    }

    // Test decoding valid strings
    @Test
    fun `decode should correctly parse valid strings`() {
        listOf(
            "read:article" to Privilege("read", "article", null),
            "edit:comment@123" to Privilege("edit", "comment", 123L),
            ":post" to Privilege("", "post", null),
            "action:" to Privilege("action", "", null),
            ":" to Privilege("", "", null),
            "action:resource@789" to Privilege("action", "resource", 789L),
            "action:resource@" to Privilege("action", "resource", null), // Edge case with trailing @
            "action:resource@0" to Privilege("action", "resource", 0L) // Zero resID
        ).forEach { (string, expected) ->
            assertEquals(expected, Privilege.decode(string))
        }
    }

    // Test decoding invalid strings throws exceptions
    @Test
    fun `decode should throw IllegalArgumentException for invalid strings`() {
        listOf(
            "invalidstring", // No colon
            "action", // No colon or resource
            "action@123", // Missing resource part
            "@123", // Missing action and resource
            "action:resource@invalid", // Non-numeric resID
            "action:resource@123extra", // Extra characters after resID
            "Action:resource", // Uppercase action
            "action:Resource", // Uppercase resource
            "action:resource@123@456" // Multiple @ symbols
        ).forEach { invalidString ->
            assertFailsWith<IllegalArgumentException> {
                Privilege.decode(invalidString)
            }
        }
    }

    // Test round-trip encoding and decoding
    @Test
    fun `encode and decode should be symmetric`() {
        listOf(
            Privilege("read", "article", null),
            Privilege("edit", "comment", 123L),
            Privilege("", "", null),
            Privilege("", "", 7L),
            Privilege("action", "resource", Long.MAX_VALUE)
        ).forEach { original ->
            val encoded = original.encode()
            val decoded = Privilege.decode(encoded)
            assertEquals(original, decoded)
        }
    }

    // Test edge cases for resID parsing
    @Test
    fun `decode should handle resID edge cases`() {
        assertEquals(Privilege("a", "b", null), Privilege.decode("a:b@"))
        assertEquals(Privilege("a", "b", 0L), Privilege.decode("a:b@0"))
        assertEquals(Privilege("a", "b", Long.MAX_VALUE), Privilege.decode("a:b@${Long.MAX_VALUE}"))
    }


    // Test null resID with trailing @
    @Test
    fun `decode with trailing @ should have null resID`() {
        val privilege = Privilege.decode("action:resource@")
        assertEquals("action", privilege.action)
        assertEquals("resource", privilege.resource)
        assertNull(privilege.resID)
    }

    // Add these tests to your existing PrivilegeTest class

    @Test
    fun `satisfy should return true when privilege exactly matches required privilege`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return false when actions don't match`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("write", "article", 123L)
        assertFalse(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return false when resources don't match`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("read", "comment", 123L)
        assertFalse(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return false when resIDs don't match`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("read", "article", 456L)
        assertFalse(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return true when privilege has empty action and matches otherwise`() {
        val privilege = Privilege("", "article", 123L)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return true when privilege has empty resource and matches otherwise`() {
        val privilege = Privilege("read", "", 123L)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return true when privilege has null resID and matches otherwise`() {
        val privilege = Privilege("read", "article", null)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return true when all privilege fields are empty or null`() {
        val privilege = Privilege("", "", null)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return false when required privilege has empty action but privilege doesn't match`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("", "article", 123L)
        // Empty action in required privilege doesn't mean it satisfies any action
        // The method checks if privilege satisfies required, not the other way around
        assertFalse(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should handle combination of empty fields correctly`() {
        val privilege = Privilege("", "article", null)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))

        val privilege2 = Privilege("read", "", null)
        val required2 = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege2, required2))

        val privilege3 = Privilege("read", "article", null)
        val required3 = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege3, required3))
    }

    @Test
    fun `satisfy should return false when privilege has non-null resID but required has different resID`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("read", "article", 456L)
        assertFalse(Privilege.satisfy(privilege, required))
    }

    @Test
    fun `satisfy should return true when privilege has non-null resID and required has same resID`() {
        val privilege = Privilege("read", "article", 123L)
        val required = Privilege("read", "article", 123L)
        assertTrue(Privilege.satisfy(privilege, required))
    }
}
