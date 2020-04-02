package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.fixtures.buildTodoList
import com.alo.cqrs.todolist.infrastructure.cqrs.store.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.store.SerializedEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID


class TodoListInMemoryEventSourcedRepositoryTest {

    private val objectMapper = jacksonObjectMapper()

    private val eventStore = mockk<InMemoryEventStore>(relaxed = true)

    private val repository = TodoListInMemoryEventSourcedRepository(eventStore)

    @Test
    fun `should get all events of a todo list`() {
        val uuid = UUID.randomUUID()
        val domainEvent = TodoListCreated(uuid, "My todo list")
        val event = SerializedEvent(
            payload = objectMapper.writeValueAsString(domainEvent),
            clazz = domainEvent::class.java
        )
        every { eventStore.load(uuid) } returns listOf(event)

        val todoList = repository.get(TodoListId(uuid))

        assertThat(todoList).isEqualTo(
            TodoList.Factory.restoreState(TodoListId(uuid), domainEvent.name, listOf(domainEvent))
        )
    }

    @Test
    fun `should save a todo list`() {
        val uuid = UUID.randomUUID()
        val domainEvent = TodoListCreated(uuid, "My todo list")
        val todoList = buildTodoList(id = TodoListId(uuid), uncommittedChanges = listOf(domainEvent))

        repository.save(todoList)

        val event = SerializedEvent(objectMapper.writeValueAsString(domainEvent), TodoListCreated::class.java)
        verify { eventStore.store(uuid, listOf(event)) }
    }
}
