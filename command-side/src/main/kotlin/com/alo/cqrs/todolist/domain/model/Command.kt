package com.alo.cqrs.todolist.domain.model

import java.util.UUID

sealed class Command {

    data class CreateTodoList(val aggregateId: UUID, val name: String) : Command()

    data class AddTask(val aggregateId: UUID, val name: String) : Command()

    data class CompleteTask(val aggregateId: UUID, val taskId: UUID) : Command()

}
