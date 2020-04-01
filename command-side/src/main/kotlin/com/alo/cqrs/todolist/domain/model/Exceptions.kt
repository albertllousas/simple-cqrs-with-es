package com.alo.cqrs.todolist.domain.model

import kotlin.reflect.KClass

class UnsupportedEventException(aggregateClass: KClass<out AggregateRoot>, eventClass: KClass<out DomainEvent>)
    : Exception("Unsupported event '${eventClass.simpleName}' for aggregate root '${aggregateClass.simpleName}' ")
