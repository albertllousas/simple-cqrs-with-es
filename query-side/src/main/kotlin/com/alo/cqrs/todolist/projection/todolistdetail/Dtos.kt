package com.alo.cqrs.todolist.projection.todolistdetail

data class TodoListDetailDto(
    val name: String,
    val status: Status,
    val tasks: List<TaskDto>
)

data class TaskDto(
    val name: String,
    val status: Status
)

enum class Status {
    TODO, DONE
}
