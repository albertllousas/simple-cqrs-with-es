package com.alo.cqrs.todolist.projection

import java.util.UUID

sealed class Event()

data class TodoListCreated(val id: UUID, val name: String): Event()

data class TaskAdded(val aggregateId: UUID, val id: UUID, val name: String): Event()

data class TaskCompleted(val aggregateId: UUID, val id: UUID): Event()

data class TodoListCompleted(val id: UUID): Event()
