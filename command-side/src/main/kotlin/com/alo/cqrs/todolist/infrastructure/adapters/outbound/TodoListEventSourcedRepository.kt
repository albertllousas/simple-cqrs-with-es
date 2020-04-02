package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.infrastructure.cqrs.store.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.store.SerializedEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class TodoListInMemoryEventSourcedRepository(
    private val eventStore: InMemoryEventStore
) : Repository<TodoList, TodoListId> {

    private val objectMapper = jacksonObjectMapper()

    override fun save(aggregate: TodoList) {
        eventStore.store(
            aggregateId = aggregate.id.value,
            events = aggregate.uncommittedChanges.map {
                SerializedEvent(payload = objectMapper.writeValueAsString(it), clazz = it::class.java)
            }
        )
    }

    override fun get(id: TodoListId): TodoList? =
        eventStore.load(id.value)
            .let { it.map {
                event -> objectMapper.readValue(event.payload, event.clazz) as DomainEvent
            } }
            .let { TodoList.Factory.recreate(it) }

}
