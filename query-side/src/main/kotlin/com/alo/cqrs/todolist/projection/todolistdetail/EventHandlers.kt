package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeDataStore
import com.alo.cqrs.todolist.projection.TodoListCreated

class TodoListCreatedEventHandler(
    private val datastore: FakeDataStore
) {
    fun handle(event: TodoListCreated) {
        datastore.save(
            TodoListDetailDto(
                id = event.id,
                name = event.name,
                status = Status.TODO,
                tasks = emptyList()
            )
        )
    }
}
