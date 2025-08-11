package com.example.app.plugins.routing.task

import com.example.app.plugins.routing.task.delete.clearAllTasks
import com.example.app.plugins.routing.task.delete.deleteTask
import com.example.app.plugins.routing.task.get.getAllTasks
import com.example.app.plugins.routing.task.get.getTaskByName
import com.example.app.plugins.routing.task.get.getTaskByPriority
import com.example.app.plugins.routing.task.post.addRandomTask
import com.example.app.plugins.routing.task.post.addRandomTasks
import com.example.app.plugins.routing.task.post.addTask
import com.example.app.plugins.routing.task.update.updateTask
import com.example.app.plugins.routing.task.websocket.tasksStream
import com.example.data.repository.TaskRepository
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.taskRouting(repository: TaskRepository) {
    route("/tasks") {
        getAllTasks(repository)
        getTaskByName(repository)
        getTaskByPriority(repository)

        addTask(repository)
        addRandomTask(repository)
        addRandomTasks(repository)

        deleteTask(repository)
        clearAllTasks(repository)

        updateTask(repository)

        tasksStream(repository)
    }
}
