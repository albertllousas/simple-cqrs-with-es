package com.alo.cqrs.todolist.projection.todolistdetail

import com.alo.cqrs.todolist.projection.QueryHandler
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import java.util.UUID

fun Route.todoListDetails(query: QueryHandler<UUID, TodoListDetailDto?>) =
    route("/todo-lists/{id}/details") {
        get {
            call.parameters["id"]
                .let(UUID::fromString)
                .let { query(it) }
                ?.let { call.respond(OK, it) }
                ?: call.respond(NotFound)
        }
    }
