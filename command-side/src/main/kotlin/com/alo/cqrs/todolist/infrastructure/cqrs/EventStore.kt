package com.alo.cqrs.todolist.infrastructure.cqrs

interface EventStore {
    fun read(streamId: String): EventStream
    fun write(streamId: String, events: List<Event>, expectedVersion: Long)
}

data class Event(val payload: String, val type: String)

data class EventStream(val events: List<Event>, val currentVersion: Long)
