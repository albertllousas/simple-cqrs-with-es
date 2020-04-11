package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.EventHandler
import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.TaskAdded
import com.alo.cqrs.todolist.projection.TaskCompleted
import com.alo.cqrs.todolist.projection.TodoListCompleted
import com.alo.cqrs.todolist.projection.TodoListCreated
import com.alo.cqrs.todolist.projection.todolistdetail.Status.*
import java.util.UUID

class TodoListCreatedEventHandler(
    private val datastore: FakeProjectionsDataStore
) : EventHandler<TodoListCreated> {
    override fun handle(event: TodoListCreated) {
        datastore.save(
            TodoListDetailDto(id = event.id, name = event.name, status = TODO, tasks = emptyList()
            )
        )
    }
}

class TaskAddedEventHandler(
    private val datastore: FakeProjectionsDataStore
) : EventHandler<TaskAdded> {
    override fun handle(event: TaskAdded) {
        datastore.get(event.aggregateId)!!
            .let { it.copy(tasks = it.tasks + listOf(TaskDto(event.id, event.name, TODO))) }
            .also { datastore.save(it) }
    }
}

class TaskCompletedEventHandler(
    private val datastore: FakeProjectionsDataStore
) : EventHandler<TaskCompleted> {
    override fun handle(event: TaskCompleted) {
        datastore.get(event.aggregateId)!!
            .let { it.copy(tasks = markTaskAsDone(event.id, it.tasks)) }
            .also { datastore.save(it) }
    }

    private fun markTaskAsDone(taskId: UUID, tasks: List<TaskDto>): List<TaskDto> =
        tasks.find { task -> task.id == taskId && task.status == TODO }
            ?.let { uncompletedTask -> uncompletedTask.copy(status = DONE) }
            ?.let { completedTask -> tasks.filter { it.id != taskId } + listOf(completedTask) }
            ?: tasks
}

class TodoListCompletedEventHandler(
    private val datastore: FakeProjectionsDataStore
) : EventHandler<TodoListCompleted> {
    override fun handle(event: TodoListCompleted) {
        datastore.get(event.id)!!
            .let { it.copy(status = DONE) }
            .also { datastore.save(it) }
    }
}
