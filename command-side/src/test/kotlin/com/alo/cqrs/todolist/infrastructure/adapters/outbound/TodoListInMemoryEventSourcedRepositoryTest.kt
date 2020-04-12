package com.alo.cqrs.todolist.infrastructure.adapters.outbound

import com.alo.cqrs.todolist.domain.model.todolist.Status.*
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.fixtures.buildTodoList
import com.alo.cqrs.todolist.infrastructure.cqrs.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.Event
import com.alo.cqrs.todolist.infrastructure.cqrs.EventStream
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID


class TodoListInMemoryEventSourcedRepositoryTest {

    private val mapper = jacksonObjectMapper()

    private val eventStore = mockk<InMemoryEventStore>(relaxed = true)

    private val repository = TodoListInMemoryEventSourcedRepository(eventStore)

    @Test
    fun `should get a todo list`() {
        val uuid = UUID.randomUUID()
        val domainEvent = TodoListCreated(uuid, "My todo list")
        val event = Event(
            payload = mapper.writeValueAsString(domainEvent),
            type = domainEvent::class.simpleName!!
        )
        every { eventStore.read(uuid.toString()) } returns EventStream(listOf(event), 1)

        val todoList = repository.get(TodoListId(uuid))

        assertThat(todoList).isEqualTo(
            buildTodoList(TodoListId(uuid), domainEvent.name, 1, TODO, emptyList(), emptyList())
        )
    }

    @Test
    fun `should not get a todo list when there are no previous events`() {
        val aggregateId = UUID.randomUUID()
        every { eventStore.read(aggregateId.toString()) } returns EventStream(emptyList(), 0)

        assertThat(repository.get(TodoListId(aggregateId))).isNull()
    }

    @Test
    fun `should save a todo list`() {
        val todoListCreated = TodoListCreated(UUID.randomUUID(), "My todo list")
        val todoList = buildTodoList(
            id = TodoListId(todoListCreated.id),
            uncommittedChanges = listOf(todoListCreated))

        repository.save(todoList)

        val event = Event(
            mapper.writeValueAsString(todoListCreated),
            "TodoListCreated"
        )
        verify { eventStore.write(todoList.id.value.toString(), listOf(event), todoList.version) }
    }
}
