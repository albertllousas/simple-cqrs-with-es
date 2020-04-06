package com.alo.cqrs.todolist.domain.model

import java.util.UUID

sealed class Command {
    data class CreateTodoList(val id: UUID, val name: String) : Command()
}
