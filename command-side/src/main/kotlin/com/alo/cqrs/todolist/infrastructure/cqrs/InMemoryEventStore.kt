package com.alo.cqrs.todolist.infrastructure.cqrs

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


class InMemoryEventStore {

    private val storage: ConcurrentHashMap<UUID, List<SerializedEvent>> = ConcurrentHashMap()

    private var subscribers = mutableListOf<Subscription>()

    fun read(aggregateId: UUID): List<SerializedEvent> =
        storage.getOrDefault(aggregateId, emptyList())

    fun write(
        aggregateId: UUID,
        events: List<SerializedEvent>
    ) {
        storage.merge(aggregateId, events, { oldEvents, newEvents ->
            oldEvents + newEvents
//            if (baseVersion !== oldValue[oldValue.size() - 1].getVersion()) {
//                throw OptimisticLockingException("Base version does not match current stored version")
//            }
        }).also { events.forEach(::publish) }

    }

    private fun publish(serializedEvent: SerializedEvent) {
        subscribers.forEach { it.trigger(serializedEvent) }
    }

    fun subscribe(subscription: Subscription) = subscribers.add(subscription)

}

data class SerializedEvent(
    val payload: String,
    val type: String
)

data class Subscription(val trigger: (SerializedEvent) -> Unit)
