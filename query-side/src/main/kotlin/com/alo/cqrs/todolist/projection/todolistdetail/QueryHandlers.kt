package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.QueryHandler
import java.util.UUID

class GetTodoListDetailsQuery(
    private val datastore: FakeProjectionsDataStore
) : QueryHandler<UUID, TodoListDetailDto?> {

    override operator fun invoke(id: UUID) : TodoListDetailDto ? = datastore.get(id)

}
