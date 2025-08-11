package com.example.app.module.routing.task

import com.example.app.module.routing.task.delete.clearAllTasks
import com.example.app.module.routing.task.delete.deleteTask
import com.example.app.module.routing.task.get.getAllTasks
import com.example.app.module.routing.task.get.getTaskByName
import com.example.app.module.routing.task.get.getTaskByPriority
import com.example.app.module.routing.task.post.addRandomTask
import com.example.app.module.routing.task.post.addRandomTasks
import com.example.app.module.routing.task.post.addTask
import com.example.app.module.routing.task.update.updateTask
import com.example.app.module.routing.task.websocket.tasksStream
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.taskRouting() {
    route("/tasks") {
        getAllTasks()
        getTaskByName()
        getTaskByPriority()

        addTask()
        addRandomTask()
        addRandomTasks()

        deleteTask()
        clearAllTasks()

        updateTask()

        tasksStream()
    }
}
