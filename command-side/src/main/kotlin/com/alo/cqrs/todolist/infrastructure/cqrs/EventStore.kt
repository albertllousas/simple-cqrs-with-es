package com.alo.cqrs.todolist.infrastructure.cqrs

interface EventStore {
    fun read(streamId: String): EventStream
    @Throws(OptimisticLockingException::class)
    fun write(streamId: String, events: List<Event>, expectedVersion: Long)
    fun subscribe(subscription: Subscription)
}

data class Subscription(val callback: (eventType: String, eventPayload: String) -> Unit)

data class Event(val payload: String, val type: String)

data class EventStream(val events: List<Event>, val currentVersion: Long)

class OptimisticLockingException(streamId: String, currentVersion: Long, expectedVersion: Long) : Exception(
    "Current version '$expectedVersion' does not match stored version '$currentVersion' for stream '$streamId'"
)
