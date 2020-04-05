package com.alo.cqrs.todolist.infrastructure.adapters.inbound.rest

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.infrastructure.cqrs.CommandBus
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Route.totoLists")

fun Route.todoLists(commandBus: CommandBus) = route("/todo-lists") {
    post {
        val request = call.receive<CreateTodoListHttpRequest>()
        commandBus.safeDispatch(Command.CreateTodoList(name = request.name))
        call.respond(HttpStatusCode.Accepted)
    }
}

data class CreateTodoListHttpRequest(val name: String)

private fun <T : Command> CommandBus.safeDispatch(command: T) {
    GlobalScope.launch(exceptionHandler) {
        this@safeDispatch.dispatch(command)
    }
}

private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    logger.error("Caught $exception")
}
