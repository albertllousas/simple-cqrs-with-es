package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.DeserializationEventException
import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.infrastructure.cqrs.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.SerializedEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class TodoListInMemoryEventSourcedRepository(
    private val eventStore: InMemoryEventStore
) : Repository<TodoList, TodoListId> {

    private val objectMapper = jacksonObjectMapper()

    override fun save(aggregate: TodoList) {
        eventStore.write(
            aggregateId = aggregate.id.value,
            events = aggregate.uncommittedChanges.map {
                SerializedEvent(payload = objectMapper.writeValueAsString(it), type = it::class.simpleName!!)
            }
        )
    }

    override fun get(id: TodoListId): TodoList? =
        eventStore.read(id.value)
            .let { it.map { event -> objectMapper.readValue(event.payload, resolveClass(event.type)) as DomainEvent } }
            .let { TodoList.Factory.recreate(it) }

    private fun resolveClass(type: String): Class<out TodoListEvent> =
        TodoListEvent::class.sealedSubclasses.find { clazz -> clazz.simpleName!! == type }?.java
            ?: throw DeserializationEventException(TodoListEvent::class, type)

}
