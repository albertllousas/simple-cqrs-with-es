package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.TaskAdded
import com.alo.cqrs.todolist.projection.TodoListCreated
import com.alo.cqrs.todolist.projection.todolistdetail.Status.*

class TodoListCreatedEventHandler(
    private val datastore: FakeProjectionsDataStore
) {
    fun handle(event: TodoListCreated) {
        datastore.save(
            TodoListDetailDto(id = event.id, name = event.name, status = TODO, tasks = emptyList()
            )
        )
    }
}

class TaskAddedEventHandler(
    private val datastore: FakeProjectionsDataStore
) {
    fun handle(event: TaskAdded) {
        datastore.get(event.aggregateId)!!
            .let { it.copy(tasks = it.tasks + listOf(TaskDto(event.id, event.name, TODO))) }
            .also { datastore.save(it) }
    }
}
