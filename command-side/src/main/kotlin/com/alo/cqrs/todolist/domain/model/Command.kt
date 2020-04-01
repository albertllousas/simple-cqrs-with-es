package com.alo.cqrs.todolist.domain.model

sealed class Command {
    data class CreateTodoList(val name: String) : Command()
}
