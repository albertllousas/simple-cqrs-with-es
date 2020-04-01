package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.DomainEvent
import java.util.UUID

sealed class TodoListEvent: DomainEvent()

data class TodoListCreated(val id: UUID, val name: String): TodoListEvent()
