package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.TodoListCreated
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID


class EventHandlersTest {

    private val fakeDataStore = mockk<FakeProjectionsDataStore>(relaxed = true)

    @Nested
    @DisplayName("Tests for 'TodoListCreated' event handler")
    inner class TodoListCreatedHandlerTest {

        private val eventHandler = TodoListCreatedEventHandler(fakeDataStore)

        @Test
        fun `should create and save a todo list details`() {
            val event = TodoListCreated(UUID.randomUUID(), "to do list")

            eventHandler.handle(event)

            verify { fakeDataStore.save(TodoListDetailDto(event.id, event.name, Status.TODO, emptyList())) }
        }

    }
}
