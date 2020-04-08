package com.alo.cqrs.todolist.projection

import com.alo.cqrs.todolist.projection.todolistdetail.TaskAddedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCreatedEventHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class EventConsumerTest {

    private val objectMapper = jacksonObjectMapper()

    private val todoListCreatedEventHandler = mockk<TodoListCreatedEventHandler>(relaxed = true)

    private val taskAddedEventHandler = mockk<TaskAddedEventHandler>(relaxed = true)

    private val eventConsumer = EventConsumer(
        todoListCreatedEventHandler = todoListCreatedEventHandler,
        taskAddedEventHandler = taskAddedEventHandler
    )

    @Test
    fun `should receive and dispatch 'TodoListCreated' event to the handler`() {
        val event = TodoListCreated(UUID.randomUUID(), "my todo list")

        eventConsumer.receive("TodoListCreated", objectMapper.writeValueAsString(event))

        verify { todoListCreatedEventHandler.handle(event) }
    }

    @Test
    fun `should receive and dispatch 'TaskAdded' event to the handler`() {
        val event = TaskAdded(UUID.randomUUID(), UUID.randomUUID(), "my task")

        eventConsumer.receive("TaskAdded", objectMapper.writeValueAsString(event))

        verify { taskAddedEventHandler.handle(event) }
    }

    @Test
    fun `should fail when event received is not recognized and can not be parsed`() {
        assertThatThrownBy {
            eventConsumer.receive("NonExistentEvent", "payload")
        }.isInstanceOf(UnparseableEventException::class.java)
            .hasMessageContaining(
                "Impossible to parse event type 'NonExistentEvent', only types '[TodoListCreated]' are accepted."
            )
    }
}
