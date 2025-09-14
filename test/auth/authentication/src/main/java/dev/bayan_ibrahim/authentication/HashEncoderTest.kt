package dev.bayan_ibrahim.authentication

import dev.bayan_ibrahim.authentication.core.hash_encoder.HashEncoder
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

abstract class HashEncoderTest {

    abstract fun createEncoder(): HashEncoder

    private lateinit var encoder: HashEncoder

    @Before
    fun init() {
        encoder = createEncoder()
    }

    @Test
    fun `hash should return different string for non-empty input`() {
        val input = "test"
        val hashed = encoder.hash(input)
        assertNotEquals(input, hashed)
    }

    @Test
    fun `hash should handle empty string`() {
        val hashed = encoder.hash("")
        assertNotNull(hashed)
    }

    @Test
    fun `hash should produce consistent output`() {
        val input = "consistent"
        val firstHash = encoder.hash(input)
        val secondHash = encoder.hash(input)
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `hash should handle special characters`() {
        val input = "!@#\$%^&*()_+"
        val hashed = encoder.hash(input)
        assertNotNull(hashed)
    }

    @Test
    fun `match should return true for original value and its hash`() {
        val input = "test"
        val hashed = encoder.hash(input)
        assertTrue(encoder.match(input, hashed))
    }

    @Test
    fun `match should return false for different value`() {
        val input = "test"
        val hashed = encoder.hash(input)
        assertFalse(encoder.match("different", hashed))
    }

    @Test
    fun `match should return false for empty string against non-empty hash`() {
        val hashed = encoder.hash(" ")
        assertFalse(encoder.match("", hashed))
    }

    @Test
    fun `match should handle empty string correctly`() {
        val hashed = encoder.hash("")
        assertTrue(encoder.match("", hashed))
    }

    @Test
    fun `match should return false when origin is incorrect for hash`() {
        val hashed = encoder.hash("password123")
        assertFalse(encoder.match("password124", hashed))
    }

    @Test
    fun `hash should handle long input`() {
        val longInput = "a".repeat(1000)
        val hashed = encoder.hash(longInput)
        assertNotNull(hashed)
    }
}