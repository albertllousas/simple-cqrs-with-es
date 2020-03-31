package com.alo.cqrs.todolist.domain.model

sealed class Commands {
    data class CreateTodoList(val name: String) : Commands()
}
