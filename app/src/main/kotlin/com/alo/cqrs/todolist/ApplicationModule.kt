package com.bank.transfers.infrastructure.config

import com.alo.cqrs.todolist.application.CreateTodoListCommandHandler
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.infrastructure.adapters.inbound.rest.todoLists
import com.alo.cqrs.todolist.infrastructure.adapters.outbound.TodoListInMemoryEventSourcedRepository
import com.alo.cqrs.todolist.infrastructure.cqrs.InMemoryEventStore
import com.alo.cqrs.todolist.infrastructure.cqrs.SimpleCommandBus
import com.alo.cqrs.todolist.infrastructure.cqrs.Subscription
import com.alo.cqrs.todolist.projection.EventConsumer
import com.alo.cqrs.todolist.projection.FakeProjectionsDataStore
import com.alo.cqrs.todolist.projection.QueryHandler
import com.alo.cqrs.todolist.projection.todolistdetail.GetTodoListDetailsQuery
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCreatedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListDetailDto
import com.alo.cqrs.todolist.projection.todolistdetail.todoListDetails
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import org.slf4j.event.Level.INFO
import java.util.UUID

fun Application.module() {
    //command-side wiring
    val eventStore = InMemoryEventStore()
    val todoListRepository: Repository<TodoList, TodoListId> = TodoListInMemoryEventSourcedRepository(eventStore)
    val createTodoListCommandHandler = CreateTodoListCommandHandler(todoListRepository)
    val commandBus = SimpleCommandBus().register(createTodoListCommandHandler)
    //read-side wiring
    val fakeDataStore = FakeProjectionsDataStore()
    val getTodoListDetailsQuery: QueryHandler<UUID, TodoListDetailDto?> = GetTodoListDetailsQuery(fakeDataStore)
    val todoListCreatedEventHandler = TodoListCreatedEventHandler(fakeDataStore)
    val eventConsumer = EventConsumer(todoListCreatedEventHandler)
    eventStore.subscribe(Subscription(eventConsumer::receive))

    install(DefaultHeaders)
    install(CallLogging) {
        level = INFO
    }
    install(ContentNegotiation) { jackson {} }
    install(Routing) {
        // command routes
        todoLists(commandBus)
        // read routes
        todoListDetails(getTodoListDetailsQuery)
    }
}
