package com.example.data.repository

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskListGenerator

object TaskRepository {
    private val taskListGenerator by lazy {
        TaskListGenerator()
    }
    private val tasks = mutableListOf<Task>()
    fun clear() {
        tasks.clear()
    }

    fun random() {
        clear()
        tasks.addAll(taskListGenerator.generate())
    }

    fun allTasks(): List<Task> = tasks
    fun tasksByPriority(priority: Priority) = tasks.filter {
        it.priority == priority
    }

    fun taskByName(name: String) = tasks.find {
        it.name.equals(name, ignoreCase = true)
    }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("Cannot duplicate task names!")
        }
        tasks.add(task)
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.name == task.name }
        if (index < 0) throw IllegalStateException("Task not found")
        tasks[index] = task
    }

    fun removeTask(name: String): Boolean {
        return tasks.removeIf { it.name == name }
    }
}