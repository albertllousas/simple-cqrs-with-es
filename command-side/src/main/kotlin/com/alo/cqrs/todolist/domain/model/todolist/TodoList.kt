package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.AggregateId
import com.alo.cqrs.todolist.domain.model.AggregateRoot
import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.UnsupportedEventException
import java.util.UUID

data class TodoListId(override val value: UUID) : AggregateId()

class TodoList(history: List<DomainEvent>) : AggregateRoot(history) {

    override lateinit var id: TodoListId
        private set

    lateinit var name: String
        private set

    override fun applyChange(event: DomainEvent) {
        if(event is TodoListEvent) {
            when (event) {
                is TodoListCreated -> apply(event)
            }.exhaustive
        } else {
            throw UnsupportedEventException(aggregateClass = this::class, eventClass = event::class)
        }
    }

    private fun apply(event: TodoListCreated) {
        this.name = event.name
        this.id = TodoListId(event.id)
    }


    companion object {
        fun createNew(id: TodoListId, name: String): TodoList {
            val event = TodoListCreated(id = id.value, name = name)
            return TodoList(emptyList()).apply {
                applyNewChange(event)
            }
        }
    }
}

val Any?.exhaustive get() = Unit
