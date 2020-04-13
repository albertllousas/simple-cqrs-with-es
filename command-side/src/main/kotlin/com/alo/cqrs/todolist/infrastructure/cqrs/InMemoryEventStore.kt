package com.alo.cqrs.todolist.infrastructure.cqrs

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class InMemoryEventStore : EventStore {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val storage: ConcurrentHashMap<String, List<StreamEntry>> = ConcurrentHashMap()

    private var subscribers = mutableListOf<Subscription>()

    override fun read(streamId: String): EventStream =
        storage.getOrDefault(streamId, emptyList())
            .let { eventEntries ->
                EventStream(
                    events = eventEntries.map { it.event },
                    currentVersion = if (eventEntries.isNotEmpty()) eventEntries.last().version else 0
                )
            }

    override fun write(streamId: String, events: List<Event>, expectedVersion: Long) {
        val entries = events.mapIndexed { index, event -> StreamEntry(event, expectedVersion.plus(index.inc())) }
        storage.merge(streamId, entries) { oldEntries, newEntries ->
            if (oldEntries.last().version != expectedVersion)
                throw OptimisticLockingException(streamId, oldEntries.last().version, expectedVersion)
            else oldEntries + newEntries
        }
            .also { events.forEach { event -> logger.info("Event '$event' stored.") } }
            .also { events.forEach(::publish) }

    }

    private fun publish(event: Event) {
        subscribers.forEach { it.callback(event.type, event.payload) }
    }

    override fun subscribe(subscription: Subscription) {
        subscribers.add(subscription)
    }

}

private data class StreamEntry(val event: Event, val version: Long)
