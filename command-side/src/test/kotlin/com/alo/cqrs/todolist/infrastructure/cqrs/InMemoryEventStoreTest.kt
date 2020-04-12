package com.alo.cqrs.todolist.infrastructure.cqrs

import com.alo.cqrs.todolist.domain.model.AggregateNotFoundException
import com.alo.cqrs.todolist.domain.model.todolist.TaskAdded
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class InMemoryEventStoreIntegrationTest {

    private val objectMapper = jacksonObjectMapper()

    private val eventStore = InMemoryEventStore()

    @Test
    fun `should store and load serialized events for an aggregate`() {
        val aggregateId = UUID.randomUUID()
        val todoListCreated = TodoListCreated(aggregateId, "My todo list")
        val taskAdded = TaskAdded(aggregateId, UUID.randomUUID(), "My task")
        val first = Event(
            payload = objectMapper.writeValueAsString(todoListCreated),
            type = todoListCreated::class.simpleName!!
        )
        val second = Event(
            payload = objectMapper.writeValueAsString(taskAdded),
            type = taskAdded::class.simpleName!!
        )

        eventStore.write(aggregateId, listOf(first, second), 0)
        val readResponse = eventStore.read(aggregateId)

        assertThat(readResponse).isEqualTo(ReadResponse(events = listOf(first, second), currentVersion = 2))
    }

    @Test
    fun `should publish events to the subscribers`() {
        val aggregateId = UUID.randomUUID()
        val domainEvent = TodoListCreated(aggregateId, "My todo list")
        val event = Event(
            payload = objectMapper.writeValueAsString(domainEvent),
            type = domainEvent::class.java.simpleName!!
        )
        val callback = mockk<(String, String) -> Unit>(relaxed = true)

        eventStore.subscribe(Subscription(callback)).also { eventStore.write(aggregateId, listOf(event), 1) }

        verify { callback.invoke(event.type, event.payload) }
    }

    @Test
    fun `should fail with optimistic locking trying to store an outdated version`() {
        val aggregateId = UUID.randomUUID()
        val todoListCreated = TodoListCreated(aggregateId, "My todo list")
        val taskAdded = TaskAdded(aggregateId, UUID.randomUUID(), "My task")
        val first = Event(
            payload = objectMapper.writeValueAsString(todoListCreated),
            type = todoListCreated::class.simpleName!!
        )
        val second = Event(
            payload = objectMapper.writeValueAsString(taskAdded),
            type = taskAdded::class.simpleName!!
        )
        eventStore.write(aggregateId, listOf(first), 0)

        Assertions.assertThatThrownBy { eventStore.write(aggregateId, listOf(second), 0) }
            .isInstanceOf(OptimisticLockingException::class.java)
            .hasMessageContaining(
                "Current version '0' does not match stored version '1' for aggregateId '$aggregateId'"
            )


    }
}
