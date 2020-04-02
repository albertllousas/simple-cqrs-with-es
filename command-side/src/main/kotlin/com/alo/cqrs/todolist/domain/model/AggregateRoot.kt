package com.alo.cqrs.todolist.domain.model

import java.util.UUID

abstract class AggregateId {
    abstract val value: UUID
}

abstract class AggregateRoot() {
    abstract val id: AggregateId
    abstract val uncommittedChanges : List<DomainEvent>
}

abstract class AggregateRootFactory<AR: AggregateRoot> {

    fun recreate(history: List<DomainEvent>): AR? = apply(history.tail(), null)

    private tailrec fun apply(stream: List<DomainEvent>, acc: AR?): AR? =
        if(stream.isEmpty()) acc
        else apply(stream.tail(), applyChange(stream.first(), acc))

    protected abstract fun applyChange(event: DomainEvent, currentState: AR?) : AR

}
