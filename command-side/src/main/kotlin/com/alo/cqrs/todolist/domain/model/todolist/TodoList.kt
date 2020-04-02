package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.AggregateId
import com.alo.cqrs.todolist.domain.model.AggregateRoot
import com.alo.cqrs.todolist.domain.model.AggregateRootFactory
import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.UnsupportedEventException
import java.util.UUID

data class TodoListId(override val value: UUID) : AggregateId()

data class TodoList private constructor(
    override val id: TodoListId,
    val name: String,
    override val uncommittedChanges: List<DomainEvent>
) : AggregateRoot() {

    companion object Factory: AggregateRootFactory<TodoList>() {

        fun create(id: TodoListId, name: String): TodoList =
            TodoList(id = id, name = name, uncommittedChanges = listOf(TodoListCreated(id.value, name)))

        fun restoreState(id: TodoListId, name: String, uncommittedChanges: List<DomainEvent>): TodoList =
            TodoList(id = id, name = name, uncommittedChanges = uncommittedChanges)

        override fun applyChange(event: DomainEvent, currentState: TodoList?) : TodoList =
             if (event is TodoListEvent)
                when (event) {
                    is TodoListCreated -> create(TodoListId(event.id), event.name)
                }
             else throw UnsupportedEventException(aggregateClass = TodoList::class, eventClass = event::class)
    }
}
