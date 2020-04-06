package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.TodoListCreated

class TodoListCreatedEventHandler(
    private val datastore: FakeProjectionsDataStore
) {
    fun handle(event: TodoListCreated) {
        datastore.save(
            TodoListDetailDto(id = event.id, name = event.name, status = Status.TODO, tasks = emptyList()
            )
        )
    }
}
