package dev.bayan_ibrahim.authentaction.core.hash_encoder

import io.ktor.server.application.Application
import io.ktor.util.getDigestFunction

class DigestHashEncoder(s: String) : HashEncoder {
    private val digester = getDigestFunction("SHA-256") { salt ->
        "$s${salt.length}"
    }

    override fun hash(value: String): String {
        val result = digester(value)
        return result.decodeToString()
    }

    override fun match(origin: String, hash: String): Boolean {
        return hash(origin) == hash
    }
}

fun Application.buildDigestHashEncoderRepo() = DigestHashEncoder("QT")