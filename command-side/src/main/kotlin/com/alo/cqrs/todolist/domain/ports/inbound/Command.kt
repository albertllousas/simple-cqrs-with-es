package com.alo.cqrs.todolist.domain.ports.inbound

sealed class Command {
    data class CreateTodoList(val name: String) : Command()
}
