package com.alo.cqrs.todolist.infrastructure.cqrs.store

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap



class InMemoryEventStore {

    private val storage:Map<UUID, List<SerializedEvent>> = ConcurrentHashMap()

    fun load(aggregateId: UUID): List<SerializedEvent> {
        TODO()
    }

    fun store(aggregateId: UUID, events: List<SerializedEvent>) =
        storage.getOrDefault(aggregateId, emptyList())

}
// https://github.com/andreschaffer/event-sourcing-cqrs-examples/blob/master/src/main/java/bankservice/port/outgoing/adapter/eventstore/InMemoryEventStore.java
data class SerializedEvent(val payload: String, val clazz: Class<*>)
