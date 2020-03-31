package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.AggregateRoot
import com.alo.cqrs.todolist.domain.model.DomainEvent

class TodoList(
    history: List<TodoListEvent>,
    override val changes: List<TodoListEvent>
) : AggregateRoot<TodoListEvent>(history) {
    override fun applyChange(event: DomainEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
