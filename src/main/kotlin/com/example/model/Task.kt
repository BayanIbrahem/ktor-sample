package com.example.model

import com.example.core.random.Generator
import com.example.core.random.ListGenerator
import com.example.core.random.SentenceGenerator
import com.example.core.random.WordGenerator
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val name: String,
    val description: String,
    val priority: Priority,
)

@Serializable
enum class Priority {
    Low, Medium, High, Vital
}

object TaskGenerator : Generator<Task> {
    val descriptionGenerator by lazy {
        SentenceGenerator(3, 7)
    }

    override fun generate(): Task {
        return Task(
            name = WordGenerator.generate(),
            description = descriptionGenerator.generate().joinToString(" ", "", "."),
            priority = Priority.entries.random()
        )
    }
}

class TaskListGenerator(
    minLength: Int = 3,
    maxLength: Int = 10,
) : ListGenerator<Task>(
    minLength = minLength,
    maxLength = maxLength,
    itemGenerator = TaskGenerator
)

