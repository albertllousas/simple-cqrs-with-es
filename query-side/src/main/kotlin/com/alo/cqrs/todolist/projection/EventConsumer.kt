package com.alo.cqrs.todolist.projection

import com.alo.cqrs.todolist.projection.todolistdetail.TaskAddedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TaskCompletedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCompletedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCreatedEventHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.reflect.KClass

typealias EventType = String

typealias EventPayload = String

class EventConsumer(
    private val todoListCreatedEventHandler: TodoListCreatedEventHandler,
    private val taskAddedEventHandler: TaskAddedEventHandler,
    private val taskCompletedEventHandler: TaskCompletedEventHandler,
    private val todoListCompletedEventHandler: TodoListCompletedEventHandler
) {
    private val objectMapper = jacksonObjectMapper()

    fun receive(type: EventType, payload: EventPayload) {
        when (type) {
            TodoListCreated::class.simpleName!! ->
                todoListCreatedEventHandler.handle(objectMapper.readValue(payload))
            TaskAdded::class.simpleName!! ->
                taskAddedEventHandler.handle(objectMapper.readValue(payload))
            TaskCompleted::class.simpleName!! ->
                taskCompletedEventHandler.handle(objectMapper.readValue(payload))
            TodoListCompleted::class.simpleName!! ->
                todoListCompletedEventHandler.handle(objectMapper.readValue(payload))
            else -> throw UnparseableEventException(
                type, listOf(TodoListCreated::class, TaskAdded::class, TaskCompleted::class)
            )
        }
    }
}

class UnparseableEventException(
    type: EventType,
    allowedTypes: List<KClass<out Event>>
) : Exception(
    "Impossible to parse event type '$type', only types '${allowedTypes.map { it.simpleName }}' are allowed."
)


