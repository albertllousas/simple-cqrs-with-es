package com.alo.cqrs.todolist.infrastructure.adapters.inbound.ktor

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.infrastructure.service.bus.CommandBus
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TodoListRoutesTest {

    private val commandBus = mockk<CommandBus>()

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should accept to create a todo-list`(): Unit =
        withTestApp {
            val request = CreateTodoListHttpRequest("my daily tasks")
            coEvery { commandBus.dispatch(Command.CreateTodoList(request.name)) } just Runs

            val call = handleRequest(HttpMethod.Post, "/todo-lists") {
                addHeader("Content-Type", "application/json")
                setBody(objectMapper.writeValueAsString(request))
            }

            Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Accepted)
        }

    @Test
    fun `should accept to create a todo-list when dispatch command fails`(): Unit =
        withTestApp {
            val request = CreateTodoListHttpRequest("my daily tasks")
            coEvery { commandBus.dispatch(Command.CreateTodoList(request.name)) } coAnswers { throw Exception("Boom!") }

            val call = handleRequest(HttpMethod.Post, "/todo-lists") {
                addHeader("Content-Type", "application/json")
                setBody(objectMapper.writeValueAsString(request))
            }

            Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Accepted)
        }

    private fun withTestApp(callback: TestApplicationEngine.() -> Unit): Unit {
        withTestApplication({
            install(ContentNegotiation) { jackson {} }
            install(Routing) {
                totoLists(commandBus)
            }
        }, callback)
    }

}

