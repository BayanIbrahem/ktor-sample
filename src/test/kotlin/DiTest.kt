import com.example.data.repository.FakeTaskRepository
import com.example.data.repository.TaskRepository
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import org.junit.Test

class DiTest {
    @Test
    fun test() = testApplication {
        // inject configurations
        configure("root-config.yaml", "test-overrides.yaml")
        application {
            dependencies.provide<TaskRepository> {
                FakeTaskRepository
            }
        }
    }
}