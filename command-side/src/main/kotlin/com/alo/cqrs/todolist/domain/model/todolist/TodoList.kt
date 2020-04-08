package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.AggregateId
import com.alo.cqrs.todolist.domain.model.AggregateRoot
import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.UnsupportedEventException
import java.util.UUID

data class TodoListId(override val value: UUID) : AggregateId()

data class TodoList private constructor(
    override val id: TodoListId,
    val name: String,
    val tasks: List<Task>,
    override val uncommittedChanges: List<DomainEvent>
) : AggregateRoot() {

    fun addTask(task: Task): TodoList {
        val event = TaskAdded(id.value, task.id.value, task.name)
        return apply(event)
    }

    private fun apply(event: TaskAdded): TodoList =
        this.copy(
            tasks = this.tasks + listOf(Task(TaskId(event.id), event.name)),
            uncommittedChanges = this.uncommittedChanges + listOf(event)
        )

    companion object Factory {

        fun create(id: TodoListId, name: String): TodoList =
            TodoList(id, name, tasks = emptyList(), uncommittedChanges = listOf(TodoListCreated(id.value, name)))

        fun restoreState(id: TodoListId, name: String, tasks: List<Task>, uncommittedChanges: List<DomainEvent>): TodoList =
            TodoList(id, name, tasks, uncommittedChanges)

        fun recreate(history: List<DomainEvent>):TodoList {
            val creationEvent = history.first() as TodoListCreated
            val initial = create(TodoListId(creationEvent.id), creationEvent.name)
            return history.foldRight(initial, this::apply).copy(uncommittedChanges = emptyList())
        }

        private fun apply(event: DomainEvent, currentState: TodoList): TodoList =
            if (event is TodoListEvent)
                when (event) {
                    is TodoListCreated -> currentState
                    is TaskAdded -> currentState.apply(event)
                }
            else throw UnsupportedEventException(aggregateClass = TodoList::class, eventClass = event::class)

    }
}
