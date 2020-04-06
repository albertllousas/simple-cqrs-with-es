package com.alo.cqrs.todolist.projection

import com.alo.cqrs.todolist.projection.todolistdetail.TodoListDetailDto
import java.util.UUID

class FakeProjectionsDataStore {

    private val details = mutableMapOf<UUID, TodoListDetailDto>()

    fun save(todoListDetailDto: TodoListDetailDto) {
        details[todoListDetailDto.id] = todoListDetailDto
    }

    fun get(id: UUID): TodoListDetailDto? = details[id]

}
