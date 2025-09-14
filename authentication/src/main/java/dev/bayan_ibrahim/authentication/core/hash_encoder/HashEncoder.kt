package dev.bayan_ibrahim.authentication.core.hash_encoder

interface HashEncoder {
    fun hash(value: String): String
    fun match(origin: String, hash: String): Boolean
}