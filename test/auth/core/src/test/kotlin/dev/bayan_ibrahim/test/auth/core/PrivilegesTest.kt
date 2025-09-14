package dev.bayan_ibrahim.test.auth.core

import dev.bayan_ibrahim.authentication.model.privilege.Privilege
import dev.bayan_ibrahim.authentication.model.privilege.Privileges
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrivilegesTest {

    @Test
    fun `constructor should create Privileges with given set`() {
        val privilegeSet = setOf(
            Privilege("read", "article", null),
            Privilege("edit", "article", 123L)
        )
        val privileges = Privileges(privilegeSet)
        
        assertEquals(2, privileges.size)
        assertTrue(privileges.containsAll(privilegeSet))
    }

    @Test
    fun `encode should correctly format multiple privileges with semicolon separator`() {
        val privileges = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        val expected = "read:article;edit:comment@123"
        assertEquals(expected, privileges.encode())
        assertEquals(expected, Privileges.encode(privileges))
    }

    @Test
    fun `encode should handle empty set`() {
        val privileges = Privileges(emptySet())
        assertEquals("", privileges.encode())
    }

    @Test
    fun `encode should handle single privilege`() {
        val privileges = Privileges(setOf(Privilege("read", "article", null)))
        assertEquals("read:article", privileges.encode())
    }

    @Test
    fun `decode should correctly parse string with multiple privileges`() {
        val encoded = "read:article;edit:comment@123"
        val expected = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        assertEquals(expected, Privileges.decode(encoded))
    }

    @Test
    fun `decode should handle empty string`() {
        val privileges = Privileges.decode("")
        assertEquals(0, privileges.size)
        assertTrue(privileges.isEmpty())
    }

    @Test
    fun `decode should handle single privilege`() {
        val privileges = Privileges.decode("read:article")
        assertEquals(1, privileges.size)
        assertTrue(privileges.contains(Privilege("read", "article", null)))
    }

    @Test
    fun `decode should handle duplicate privileges by deduplicating`() {
        val privileges = Privileges.decode("read:article;read:article")
        assertEquals(1, privileges.size)
        assertTrue(privileges.contains(Privilege("read", "article", null)))
    }

    @Test
    fun `satisfy should return true when any privilege satisfies the requirement`() {
        val privileges = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        // First privilege satisfies
        assertTrue(privileges.satisfy(Privilege("read", "article", null)))
        assertTrue(Privileges.satisfy(privileges, Privilege("read", "article", null)))
        
        // Second privilege satisfies
        assertTrue(privileges.satisfy(Privilege("edit", "comment", 123L)))
        assertTrue(Privileges.satisfy(privileges, Privilege("edit", "comment", 123L)))
        
        // Wildcard privilege satisfies
        assertFalse(privileges.satisfy(Privilege("", "article", null)))
        assertFalse(Privileges.satisfy(privileges, Privilege("read", "", null)))
    }

    @Test
    fun `satisfy should return false when no privilege satisfies the requirement`() {
        val privileges = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        assertFalse(privileges.satisfy(Privilege("delete", "article", null)))
        assertFalse(Privileges.satisfy(privileges, Privilege("delete", "article", null)))
        
        assertFalse(privileges.satisfy(Privilege("read", "user", null)))
        assertFalse(Privileges.satisfy(privileges, Privilege("read", "user", null)))
        
        assertFalse(privileges.satisfy(Privilege("edit", "comment", 456L)))
        assertFalse(Privileges.satisfy(privileges, Privilege("edit", "comment", 456L)))
    }

    @Test
    fun `satisfy should return false for empty privileges`() {
        val privileges = Privileges(emptySet())
        
        assertFalse(privileges.satisfy(Privilege("read", "article", null)))
        assertFalse(Privileges.satisfy(privileges, Privilege("read", "article", null)))
    }

    @Test
    fun `satisfy should handle wildcard privileges correctly`() {
        val privileges = Privileges(
            setOf(
                Privilege("", "article", null), // Any action on article
                Privilege("read", "", null)     // Read action on any resource
            )
        )
        
        // First privilege (wildcard action) should satisfy
        assertTrue(privileges.satisfy(Privilege("read", "article", null)))
        assertTrue(privileges.satisfy(Privilege("edit", "article", null)))
        
        // Second privilege (wildcard resource) should satisfy
        assertTrue(privileges.satisfy(Privilege("read", "comment", null)))
        assertTrue(privileges.satisfy(Privilege("read", "user", null)))
        
        // Should not satisfy when neither matches
        assertFalse(privileges.satisfy(Privilege("edit", "comment", null)))
    }

    @Test
    fun `infix satisfy should work correctly`() {
        val privileges = Privileges(setOf(Privilege("read", "article", null)))
        val required = Privilege("read", "article", null)
        
        // Test the infix notation
        assertTrue(privileges satisfy required)
    }

    @Test
    fun `set delegation should work correctly`() {
        val privilegeSet = setOf(
            Privilege("read", "article", null),
            Privilege("edit", "article", 123L)
        )
        val privileges = Privileges(privilegeSet)
        
        // Test that Set methods work through delegation
        assertEquals(2, privileges.size)
        assertTrue(privileges.contains(Privilege("read", "article", null)))
        assertFalse(privileges.contains(Privilege("delete", "article", null)))
        
        // Test iteration
        var count = 0
        privileges.forEach { count++ }
        assertEquals(2, count)
    }

    @Test
    fun `equals and hashCode should work correctly`() {
        val privileges1 = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        val privileges2 = Privileges(
            setOf(
                Privilege("edit", "comment", 123L),
                Privilege("read", "article", null) // Same privileges, different order
            )
        )
        
        assertEquals(privileges1, privileges2)
        assertEquals(privileges1.hashCode(), privileges2.hashCode())
        
        val privileges3 = Privileges(
            setOf(
                Privilege("read", "article", null) // Only one privilege
            )
        )
        
        assertFalse(privileges1 == privileges3)
    }

    @Test
    fun `encode should include all privileges`() {
        val privileges = Privileges(
            setOf(
                Privilege("read", "article", null),
                Privilege("edit", "comment", 123L)
            )
        )
        
        val str = privileges.encode()
        assertTrue(str.contains("read:article"))
        assertTrue(str.contains("edit:comment@123"))
    }
}