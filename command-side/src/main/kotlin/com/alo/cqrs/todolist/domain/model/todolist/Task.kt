package com.alo.cqrs.todolist.domain.model.todolist

import java.util.UUID

data class TaskId(val value: UUID)

data class Task(val id:TaskId, val name: String, val status: Status) {
    fun isCompleted() = this.status == Status.DONE
}
