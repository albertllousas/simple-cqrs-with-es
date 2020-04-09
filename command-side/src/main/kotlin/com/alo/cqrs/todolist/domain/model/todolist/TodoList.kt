package com.alo.cqrs.todolist.domain.model.todolist

import arrow.core.extensions.list.foldable.foldLeft
import com.alo.cqrs.todolist.domain.model.AggregateId
import com.alo.cqrs.todolist.domain.model.AggregateRoot
import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.UnsupportedEventException
import com.alo.cqrs.todolist.domain.model.todolist.Status.DONE
import com.alo.cqrs.todolist.domain.model.todolist.Status.TODO
import java.util.UUID

data class TodoListId(override val value: UUID) : AggregateId()

data class TodoList private constructor(
    override val id: TodoListId,
    val name: String,
    val tasks: List<Task>,
    override val uncommittedChanges: List<DomainEvent>
) : AggregateRoot() {

    fun addTask(task: Task): TodoList =
        if (task.isCompleted()) this
        else apply(TaskAdded(id.value, task.id.value, task.name))

    fun completeTask(taskId: TaskId): TodoList = apply(TaskCompleted(this.id.value, taskId.value))

    private fun apply(event: TaskAdded): TodoList =
        this.copy(
            tasks = this.tasks + listOf(Task(id = TaskId(event.id), name = event.name, status = TODO)),
            uncommittedChanges = this.uncommittedChanges + listOf(event)
        )

    private fun apply(event: TaskCompleted): TodoList =
        this.tasks.find { task -> task.id.value == event.id && !task.isCompleted() }
            ?.let { uncompletedTask -> uncompletedTask.copy(status = DONE) }
            ?.let { completedTask -> this.tasks.filter { it.id.value != event.id } + listOf(completedTask) }
            ?.let { updatedTasks ->
                this.copy(tasks = updatedTasks, uncommittedChanges = this.uncommittedChanges + listOf(event))
            }
            ?: this

    companion object Factory {

        fun create(
            id: TodoListId,
            name: String
        ): TodoList =
            TodoList(id, name, tasks = emptyList(), uncommittedChanges = listOf(TodoListCreated(id.value, name)))

        fun restoreState(
            id: TodoListId,
            name: String,
            tasks: List<Task>,
            uncommittedChanges: List<DomainEvent>
        ): TodoList =
            TodoList(id, name, tasks, uncommittedChanges)

        fun recreate(history: List<DomainEvent>): TodoList {
            val creationEvent = history.first() as TodoListCreated
            val initial = create(TodoListId(creationEvent.id), creationEvent.name)
            return history.foldLeft(initial, this::apply).copy(uncommittedChanges = emptyList())
        }

        private fun apply(
            currentState: TodoList,
            event: DomainEvent
        ): TodoList =
            if (event is TodoListEvent)
                when (event) {
                    is TodoListCreated -> currentState
                    is TaskAdded -> currentState.apply(event)
                    is TaskCompleted -> currentState.apply(event)
                }
            else throw UnsupportedEventException(aggregateClass = TodoList::class, eventClass = event::class)

    }
}
