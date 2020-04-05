package com.alo.cqrs.todolist.projection

import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCreatedEventHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

typealias EventType = String

typealias EventPayload = String

class EventConsumer(
    private val todoListCreatedEventHandler: TodoListCreatedEventHandler
) {
    private val objectMapper = jacksonObjectMapper()

    fun receive(type: EventType, payload: EventPayload) {
        when (type) {
            TodoListCreated::class.simpleName!! ->
                todoListCreatedEventHandler.handle(objectMapper.readValue(payload))
            else -> throw UnparseableEventException(type)
        }
    }
}

class UnparseableEventException(
    type: EventType
) : Exception(
    "Impossible to parse event type '$type', only types '${Event::class.sealedSubclasses.map { it.simpleName }}' are accepted."
)


