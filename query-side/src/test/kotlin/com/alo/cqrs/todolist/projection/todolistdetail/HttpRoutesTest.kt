package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.QueryHandler
import com.alo.cqrs.todolist.projection.todolistdetail.Status.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class HttpRoutesTest {

    private val objectMapper = jacksonObjectMapper()

    private val query = mockk<QueryHandler<UUID, TodoListDetailDto?>>()

    @Test
    fun `should get a todo-list details`(): Unit = withTestApp {
        val id = UUID.randomUUID()
        val todoListDetailDto = TodoListDetailDto(id, "my todo list", TODO, emptyList())
        every { query(id) } returns todoListDetailDto

        val call = handleRequest(HttpMethod.Get, "/todo-lists/$id/details") {
            addHeader("Content-Type", "application/json")
        }

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.OK)
        assertThat(objectMapper.readValue<TodoListDetailDto>(call.response.byteContent!!)).isEqualTo(todoListDetailDto)
    }

    @Test
    fun `should not found a non existent todo-list details`(): Unit = withTestApp {
        val id = UUID.randomUUID()
        every { query(id) } returns null

        val call = handleRequest(HttpMethod.Get, "/todo-lists/$id/details") {
            addHeader("Content-Type", "application/json")
        }

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.NotFound)
    }

    private fun withTestApp(callback: TestApplicationEngine.() -> Unit): Unit {
        withTestApplication({
            install(ContentNegotiation) { jackson {} }
            install(Routing) {
                todoListDetails(query)
            }
        }, callback)
    }

}
