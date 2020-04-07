package com.alo.cqrs.todolist.domain.model

import java.util.UUID

abstract class AggregateId {
    abstract val value: UUID
}

abstract class AggregateRoot() {
    abstract val id: AggregateId
    abstract val uncommittedChanges: List<DomainEvent>
}

abstract class AggregateRootFactory<AR : AggregateRoot> {
    abstract fun recreate(history: List<DomainEvent>): AR

    protected fun <DE : DomainEvent, CE : DomainEvent> recreate(
        history: List<DE>,
        createAggregate: (CE) -> AR,
        applyChange: (DE, AR) -> AR,
        clearUncommittedChanges: (AR) -> AR
    ): AR {
        if (history.isEmpty()) throw Exception("Change this by custom exception")
        val createEvent = history.first() as CE
        val aggregate = createAggregate(createEvent)
        return applyRecursive(history.tail(), aggregate, applyChange).let(clearUncommittedChanges)
    }

    private tailrec fun <DE : DomainEvent, AR : AggregateRoot> applyRecursive(
        stream: List<DE>,
        acc: AR,
        applyChange: (DE, AR) -> AR
    ): AR =
        if (stream.isEmpty()) acc
        else applyRecursive(stream.tail(), applyChange(stream.first(), acc), applyChange)

}
