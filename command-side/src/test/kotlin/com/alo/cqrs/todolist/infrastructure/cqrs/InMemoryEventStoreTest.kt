package com.alo.cqrs.todolist.infrastructure.cqrs

import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class InMemoryEventStoreIntegrationTest {

    private val objectMapper = jacksonObjectMapper()

    private val eventStore = InMemoryEventStore()

    @Test
    fun `should store and load serialized event for an aggregate`() {
        val aggregateId = UUID.randomUUID()
        val domainEvent = TodoListCreated(aggregateId, "My todo list")
        val event = SerializedEvent(
            payload = objectMapper.writeValueAsString(domainEvent),
            type = domainEvent::class.simpleName!!
        )

        eventStore.write(aggregateId, listOf(event))
        val loadResult = eventStore.read(aggregateId)

        assertThat(loadResult).isEqualTo(listOf(event))
    }

    @Test
    fun `should publish events to the subscribers`() {
        val aggregateId = UUID.randomUUID()
        val domainEvent = TodoListCreated(aggregateId, "My todo list")
        val event = SerializedEvent(
            payload = objectMapper.writeValueAsString(domainEvent),
            type = domainEvent::class.java.simpleName!!
        )
        val callback = mockk<(String, String)->Unit>(relaxed = true)

        eventStore.subscribe(Subscription(callback)).also { eventStore.write(aggregateId, listOf(event)) }

        verify { callback.invoke(event.type, event.payload) }
    }
}
