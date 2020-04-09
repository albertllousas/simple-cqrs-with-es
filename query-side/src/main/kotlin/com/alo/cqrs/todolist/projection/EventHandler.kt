package com.alo.cqrs.todolist.projection

interface EventHandler<E: Event> {
    fun handle(event: E): Unit
}
