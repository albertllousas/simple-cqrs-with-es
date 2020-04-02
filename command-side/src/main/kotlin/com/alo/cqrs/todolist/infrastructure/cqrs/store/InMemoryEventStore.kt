package com.alo.cqrs.todolist.infrastructure.cqrs.store

import java.util.UUID

class InMemoryEventStore {
    fun load(aggregateId: UUID): List<SerializedEvent> {
        TODO()
    }

    fun store(aggregateId: UUID, events: List<SerializedEvent>) {
        TODO()
    }

}
// https://github.com/andreschaffer/event-sourcing-cqrs-examples/blob/master/src/main/java/bankservice/port/outgoing/adapter/eventstore/InMemoryEventStore.java
data class SerializedEvent(val payload: String, val clazz: Class<*>)
