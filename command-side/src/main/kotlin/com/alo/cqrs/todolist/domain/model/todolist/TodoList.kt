package com.alo.cqrs.todolist.domain.model.todolist

import arrow.core.extensions.list.foldable.exists
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
    override val version: Long,
    val name: String,
    val status: Status,
    val tasks: List<Task>,
    override val uncommittedChanges: List<DomainEvent>
) : AggregateRoot() {

    private val myself = this

    fun addTask(task: Task): TodoList =
        if (task.isCompleted()) myself
        else apply(TaskAdded(id.value, task.id.value, task.name))

    fun completeTask(taskId: TaskId): TodoList =
        TaskCompleted(this.id.value, taskId.value)
            .let { apply(it) }
            .let { newTodoList ->
                if(myself.allTasksAreCompleted()) newTodoList
                else newTodoList.tryToComplete()
            }

    private fun tryToComplete() =
        if (this.allTasksAreCompleted()) apply(TodoListCompleted(id.value))
        else myself


    private fun allTasksAreCompleted(): Boolean = !this.tasks.exists { task -> !task.isCompleted() }

    private fun apply(event: TodoListCompleted): TodoList =
        this.copy(
            status = DONE,
            uncommittedChanges = this.uncommittedChanges + listOf(event)
        )

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
            ?: myself

    companion object Factory {

        fun create(id: TodoListId, name: String): TodoList =
            TodoList(
                id = id,
                name = name,
                tasks = emptyList(),
                status = TODO,
                version = 1,
                uncommittedChanges = listOf(TodoListCreated(id.value, name))
            )

        fun restoreState(
            id: TodoListId,
            name: String,
            version: Long,
            status: Status,
            tasks: List<Task>,
            uncommittedChanges: List<DomainEvent>
        ): TodoList =
            TodoList(id, version, name, status, tasks, uncommittedChanges)

        fun recreate(history: List<DomainEvent>, version: Long): TodoList {
            val creationEvent = history.first() as TodoListCreated
            val initial = create(TodoListId(creationEvent.id), creationEvent.name)

            return history.foldLeft(initial, this::apply)
                .copy(uncommittedChanges = emptyList())
                .copy(version = version)
        }

        private fun apply(currentState: TodoList, event: DomainEvent): TodoList =
            if (event is TodoListEvent)
                when (event) {
                    is TodoListCreated -> currentState
                    is TaskAdded -> currentState.apply(event)
                    is TaskCompleted -> currentState.apply(event)
                    is TodoListCompleted -> currentState.apply(event)
                }
            else throw UnsupportedEventException(aggregateClass = TodoList::class, eventClass = event::class)
    }
}
