package com.alo.cqrs.todolist.domain.model

import java.util.UUID
import kotlin.reflect.KClass

class UnsupportedEventException(
    aggregateClass: KClass<out AggregateRoot>,
    eventClass: KClass<out DomainEvent>
) : Exception("Unsupported event '${eventClass.simpleName}' for aggregate root '${aggregateClass.simpleName}' ")

class AggregateNotFoundException(aggregateClass: KClass<out AggregateRoot>, id: UUID) : Exception(
    "Aggregate root '${aggregateClass.simpleName}' with id '$id' not found"
)

