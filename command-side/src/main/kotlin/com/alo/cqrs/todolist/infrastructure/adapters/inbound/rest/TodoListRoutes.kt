package com.alo.cqrs.todolist.infrastructure.adapters.inbound.rest

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.infrastructure.cqrs.CommandBus
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID

private val logger = LoggerFactory.getLogger("Route.totoLists")

fun Route.todoLists(commandBus: CommandBus) = route("/todo-lists") {
    post {
        call.receive<CreateTodoListHttpRequest>()
            .let { Command.CreateTodoList(aggregateId = it.id, name = it.name) }
            .let(commandBus::safeDispatch)
            .let { call.respond(HttpStatusCode.Accepted) }
    }

    post("/{id}/tasks") {
        Pair(call.parameters["id"], call.receive<AddTaskHttpRequest>())
            .let { (todoListId, payload) -> Command.AddTask(UUID.fromString(todoListId), payload.name) }
            .let(commandBus::safeDispatch)
            .let { call.respond(HttpStatusCode.Accepted) }
    }

    put("/{todoListId}/tasks/{id}/completed") {
        Pair(call.parameters["todoListId"], call.parameters["id"])
            .let { (todoListId, taskId) ->
                Command.CompleteTask(UUID.fromString(todoListId), UUID.fromString(taskId))
            }
            .let(commandBus::safeDispatch)
            .let { call.respond(HttpStatusCode.Accepted) }
    }
}

data class AddTaskHttpRequest(
    val name: String
)


data class CreateTodoListHttpRequest(
    val id: UUID,
    val name: String
)

private fun <T : Command> CommandBus.safeDispatch(command: T) {
    GlobalScope.launch(exceptionHandler) {
        this@safeDispatch.dispatch(command)
    }
}

private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    logger.error("Caught $exception")
}
