package dev.bayan_ibrahim.test.logger.postgres

import dev.bayan_ibrahim.logger_postgresql.core.PostgresqlLogger
import dev.bayan_ibrahim.test.logger.core.BaseLoggerTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import java.util.UUID

class PostgresqlLoggerTest : BaseLoggerTest<PostgresqlLogger>() {

    override val logger: PostgresqlLogger by lazy {
        PostgresqlLogger()
    }

    private var db: Database? = null

    override fun loggerSetup() {
        db = Database.connect(
            url = "jdbc:h2:mem:test${UUID.randomUUID()};DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )

        transaction(db) {
            logger.createScheme()

        }
//        Database.connect(
//            url = "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
//            user = "postgres",
//            password = "subpostfull"
//        )
    }

    override fun loggerAfter() {
        db?.connector()?.close()
    }

    @Test
    fun testnothing() {

    }
}