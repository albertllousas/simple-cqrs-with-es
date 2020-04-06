package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class QueryHandlersTest {

    private val fakeDataStore = mockk<FakeProjectionsDataStore>(relaxed = true)

    @Nested
    @DisplayName("Tests for 'TodoListCreated' event handler")
    inner class GetTodoListDetailsQueryTest {

        private val query = GetTodoListDetailsQuery(fakeDataStore)

        @Test
        fun `should get an existent todo list details`() {
            val id = UUID.randomUUID()
            val dto = TodoListDetailDto(id, "my todo list", Status.TODO, emptyList())
            every { fakeDataStore.get(id) } returns dto

            val result = query(id)

            assertThat(result).isEqualTo(dto)
        }

        @Test
        fun `should not return an non existent todo list details`() {
            every { fakeDataStore.get(any()) } returns null

            val result = query(UUID.randomUUID())

            assertThat(result).isNull()
        }

    }

}
