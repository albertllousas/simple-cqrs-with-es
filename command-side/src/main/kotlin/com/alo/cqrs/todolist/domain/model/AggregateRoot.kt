package com.alo.cqrs.todolist.domain.model

abstract class AggregateRoot<T : DomainEvent>(history: List<T>) {
    abstract val changes: List<T>

    init {
        history.forEach { event -> applyChange(event)}
    }

    abstract fun applyChange(event: DomainEvent)
}
