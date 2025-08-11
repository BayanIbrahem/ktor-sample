package com.example.app.plugins.di

import com.example.data.repository.FakeTaskRepository
import com.example.data.repository.PostgresTaskRepository
import com.example.data.repository.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

/**
 * dependency injection for data
 * using the injected objects should be within an application since we are using [Application.dependencies]
 *
 * providing works with generic injection (providing a child type will enable injecting its parent type
 *
 * to inject dependencies into the application modules we need two things,
 * 1) provide the provider function,
 * 2) mention this provider function in the 'ktor.application.dependencies' of 'application.yaml' (similar to modules mention method
 *
 * life cycle handling,
 * 1) auto closable we can inherit [AutoCloseable] like in [MyAutoClosableDatabase] to be automatically closed when the app closed
 * 2) using the [cleanup] method for a custom cleanup logic
 */
fun Application.di() {
////    using the injected object:
////    Using property delegation
//    val repository: TaskRepository by dependencies
////    Direct resolution (suspend)
//    val repository = dependencies.resolve<TaskRepository>()

    dependencies {
        // Lambda-based
        provide<TaskRepository> {
//            PostgresTaskRepository()
            FakeTaskRepository
        } cleanup {
            // some code to cleanup
        }

        key<TaskRepository>("postgres") {
//          we call it using @Named
//          @Named("postgres")
//          val postgresql;
            provide { PostgresTaskRepository() }
        }

        // Function references
//        provide<TaskRepository>(FakeTaskRepository::class)
//        provide<TaskRepository>(PostgresTaskRepository::class)

        // Registering a lambda as a dependency
//        provide<() -> TaskRepository> { { PostgresTaskRepository() } }
    }
}

private class MyAutoClosableDatabase : AutoCloseable {
    override fun close() {
        // TODO, auto called when the application stops
    }
}
