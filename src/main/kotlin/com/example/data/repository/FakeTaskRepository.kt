package com.example.data.repository

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskListGenerator

object FakeTaskRepository : TaskRepository {
    private val taskListGenerator by lazy {
        TaskListGenerator()
    }
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("painting", "Paint the fence", Priority.Medium)
    )

    override suspend fun clear() {
        tasks.clear()
    }

    override suspend fun random() {
        clear()
        tasks.addAll(taskListGenerator.generate())
    }

    override suspend fun allTasks(): List<Task> = tasks
    override suspend fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    override suspend fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    override suspend fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.name == task.name }
        if (index < 0) throw IllegalStateException("Task not found")
        tasks[index] = task
    }

    override suspend fun removeTask(name: String): Boolean {
        return tasks.removeIf { it.name == name }
    }
}