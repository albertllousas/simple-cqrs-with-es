package com.alo.cqrs.todolist.domain.model

import kotlin.reflect.KClass

class UnsupportedEventException(
    aggregateClass: KClass<out AggregateRoot>,
    eventClass: KClass<out DomainEvent>
) : Exception("Unsupported event '${eventClass.simpleName}' for aggregate root '${aggregateClass.simpleName}' ")

class DeserializationEventException(
    eventClass: KClass<out DomainEvent>,
    typeToParse: String
) : Exception(
    "Impossible to deserialize event type '$typeToParse' to any of '${eventClass.sealedSubclasses}' "
)
