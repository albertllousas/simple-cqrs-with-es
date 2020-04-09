package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.TaskAdded
import com.alo.cqrs.todolist.projection.TaskCompleted
import com.alo.cqrs.todolist.projection.TodoListCreated
import com.alo.cqrs.todolist.projection.todolistdetail.Status.*
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.UUID.*
import kotlin.TODO


class EventHandlersTest {

    @Nested
    @DisplayName("Tests for updating todolistdetails projection when 'TodoListCreated' comes")
    inner class TodoListCreatedHandlerTest {

        private val dataStore = FakeProjectionsDataStore()

        private val eventHandler = TodoListCreatedEventHandler(dataStore)

        @Test
        fun `should create and save a todo list details view`() {
            val event = TodoListCreated(randomUUID(), "to do list")

            eventHandler.handle(event)

            assertThat(dataStore.get(event.id))
                .isEqualTo(TodoListDetailDto(event.id, event.name, TODO, emptyList()))
        }

    }

    @Nested
    @DisplayName("Tests for updating todolistdetails projection when 'TaskAdded' comes")
    inner class TaskAddedHandlerTest {

        private val existentDetails = TodoListDetailDto(randomUUID(), "my todo list", TODO, emptyList())

        private val dataStore = FakeProjectionsDataStore()
            .also { it.save(existentDetails) }

        private val eventHandler = TaskAddedEventHandler(dataStore)

        @Test
        fun `should add a task into a todo list details view`() {
            val event = TaskAdded(existentDetails.id, randomUUID(), "my task name")

            eventHandler.handle(event)

            assertThat(dataStore.get(event.aggregateId))
                .isEqualTo(existentDetails.copy(tasks = listOf(TaskDto(event.id, "my task name", TODO))))
        }

    }

    @Nested
    @DisplayName("Tests for updating todolistdetails projection when 'TaskCompleted' comes")
    inner class TaskCompletedHandlerTest {

        private val existentTaskId = randomUUID()

        private val existentDetails = TodoListDetailDto(
            id = randomUUID(),
            name = "my todo list",
            status = TODO,
            tasks = listOf(TaskDto(existentTaskId, "my task name", TODO))
        )

        private val dataStore = FakeProjectionsDataStore()
            .also { it.save(existentDetails) }

        private val eventHandler = TaskCompletedEventHandler(dataStore)

        @Test
        fun `should update a task to 'done' from a todolist view`() {
            val event = TaskCompleted(existentDetails.id, existentTaskId)

            eventHandler.handle(event)

            assertThat(dataStore.get(event.aggregateId))
                .isEqualTo(existentDetails.copy(tasks = listOf(TaskDto(event.id, "my task name", DONE))))
        }

    }

}
