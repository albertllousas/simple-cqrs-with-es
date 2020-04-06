package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeDataStore
import com.alo.cqrs.todolist.projection.QueryHandler
import java.util.UUID

class GetTodoListDetailsQuery(
    private val datastore: FakeDataStore
) : QueryHandler<UUID, TodoListDetailDto?> {

    override operator fun invoke(id: UUID) : TodoListDetailDto ? = datastore.get(id)

}
