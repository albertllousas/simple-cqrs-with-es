package com.alo.cqrs.todolist.projection.todolistdetail

import java.util.UUID

data class TodoListDetailDto(
    val id: UUID,
    val name: String,
    val status: Status,
    val tasks: List<TaskDto>
)

data class TaskDto(
    val id: UUID,
    val name: String,
    val status: Status
)

enum class Status {
    TODO, DONE
}
