package com.alo.cqrs.todolist.domain.model

import java.util.UUID

abstract class AggregateId {
    abstract val value: UUID
}

abstract class AggregateRoot() {
    abstract val id: AggregateId
    abstract val version: Long
    abstract val uncommittedChanges: List<DomainEvent>
}
