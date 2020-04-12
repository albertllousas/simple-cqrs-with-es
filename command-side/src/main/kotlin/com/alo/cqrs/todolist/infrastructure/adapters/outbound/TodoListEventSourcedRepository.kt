package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.infrastructure.cqrs.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.Event
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KClass

class TodoListInMemoryEventSourcedRepository(
    private val eventStore: InMemoryEventStore
) : Repository<TodoList, TodoListId> {

    private val objectMapper = jacksonObjectMapper()

    override fun save(aggregate: TodoList) {
        eventStore.write(
            aggregateId = aggregate.id.value,
            events = aggregate.uncommittedChanges.mapIndexed { index, domainEvent ->
                Event(
                    payload = objectMapper.writeValueAsString(domainEvent),
                    type = domainEvent::class.simpleName!!
                )
            },
            expectedVersion = aggregate.version
        )
    }

    override fun get(id: TodoListId): TodoList? {
        val response = eventStore.read(id.value)
        if (response.events.isEmpty()) return null
        return response.events
            .let { it.map { event -> objectMapper.readValue(event.payload, resolveClass(event.type)) as TodoListEvent } }
            .let { TodoList.Factory.recreate(it, response.currentVersion) }
    }

    private fun resolveClass(type: String): Class<out TodoListEvent> =
        TodoListEvent::class.sealedSubclasses.find { clazz -> clazz.simpleName!! == type }?.java
            ?: throw DeserializationEventException(TodoListEvent::class, type)

}

class DeserializationEventException(eventClass: KClass<out DomainEvent>, typeToParse: String) : Exception(
    "Impossible to deserialize event type '$typeToParse' to any of '${eventClass.sealedSubclasses}' "
)
