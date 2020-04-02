package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository

class TodoListInMemoryEventSourcedRepository : Repository<TodoList, TodoListId> {
    override fun save(aggregate: TodoList) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(id: TodoListId): TodoList {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
