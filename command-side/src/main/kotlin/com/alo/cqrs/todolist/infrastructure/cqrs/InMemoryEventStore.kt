package com.alo.cqrs.todolist.infrastructure.cqrs

import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryEventStore {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val storage: ConcurrentHashMap<UUID, List<EventEntry>> = ConcurrentHashMap()

    private var subscribers = mutableListOf<Subscription>()

    fun read(aggregateId: UUID): ReadResponse =
        storage.getOrDefault(aggregateId, emptyList())
            .let { eventEntries ->
                ReadResponse(
                    events = eventEntries.map { it.event },
                    currentVersion = if (eventEntries.isNotEmpty()) eventEntries.last().version else 0
                )
            }

    fun write(aggregateId: UUID, events: List<Event>, expectedVersion: Long) {
        val entries = events.mapIndexed { index, event -> EventEntry(event, expectedVersion.plus(index.inc())) }
        storage.merge(aggregateId, entries) { oldEntries, newEntries ->
            if (oldEntries.last().version != expectedVersion)
                throw OptimisticLockingException(aggregateId, oldEntries.last().version, expectedVersion)
            else oldEntries + newEntries
        }
            .also { events.forEach { event -> logger.info("Event '$event' stored.") } }
            .also { events.forEach(::publish) }

    }

    private fun publish(event: Event) {
        subscribers.forEach { it.trigger(event.type, event.payload) }
    }

    fun subscribe(subscription: Subscription) = subscribers.add(subscription)

}

data class Event(
    val payload: String,
    val type: String
)

private data class EventEntry(
    val event: Event,
    val version: Long
)

data class ReadResponse(
    val events: List<Event>,
    val currentVersion: Long
)

data class Subscription(val trigger: (String, String) -> Unit)

class OptimisticLockingException(aggregateId: UUID, currentVersion: Long, expectedVersion: Long) : Exception(
    "Current version '$expectedVersion' does not match stored version '$currentVersion' for aggregateId '$aggregateId'"
)

