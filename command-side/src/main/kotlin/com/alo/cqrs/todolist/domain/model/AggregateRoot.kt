package com.alo.cqrs.todolist.domain.model

import java.util.UUID

abstract class AggregateId {
    abstract val value: UUID
}

abstract class AggregateRoot(
    history: List<out DomainEvent>
) {

    abstract val id: AggregateId

    private val uncommittedChanges = mutableListOf<DomainEvent>() // this make it immutable?

    val uncommittedEvents: List<DomainEvent>
        get() = uncommittedChanges

    init {
        history.forEach { event -> applyChange(event) }
    }

    protected fun applyNewChange(event: DomainEvent)  = applyChange(event).also { uncommittedChanges.add(event) }

    protected abstract fun applyChange(event: DomainEvent)
}
