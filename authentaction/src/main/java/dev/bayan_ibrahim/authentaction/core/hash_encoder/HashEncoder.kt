package dev.bayan_ibrahim.authentaction.core.hash_encoder

interface HashEncoder {
    fun hash(value: String): String
    fun match(origin: String, hash: String): Boolean
}