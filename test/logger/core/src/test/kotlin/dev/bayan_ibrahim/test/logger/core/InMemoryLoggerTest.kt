package dev.bayan_ibrahim.test.logger.core

import dev.bayan_ibrahim.logger.core.InMemoryLogger
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryLoggerTest : BaseLoggerTest<InMemoryLogger>() {
    override val logger: InMemoryLogger by lazy {
        InMemoryLogger()
    }

    @Test
    fun testnothing() {

    }
}
