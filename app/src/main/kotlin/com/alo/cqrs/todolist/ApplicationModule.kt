package com.alo.cqrs.todolist

import com.alo.cqrs.todolist.application.AddTaskCommandHandler
import com.alo.cqrs.todolist.application.CompleteTaskCommandHandler
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
import com.alo.cqrs.todolist.projection.todolistdetail.TaskAddedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TaskCompletedEventHandler
import com.alo.cqrs.todolist.projection.todolistdetail.TodoListCompletedEventHandler
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
    val (eventStore, commandBus) = wireCommandSide()
    val queryHandlers = wireReadSide(eventStore)

    install(DefaultHeaders)
    install(CallLogging) {
        level = INFO
    }
    install(ContentNegotiation) { jackson {} }
    install(Routing) {
        todoLists(commandBus)
        todoListDetails(queryHandlers.getTodoListDetailsQuery)
    }
}

private fun wireReadSide(eventStore: InMemoryEventStore) : ReadSideQueryHandlers{
    val fakeDataStore = FakeProjectionsDataStore()
    val getTodoListDetailsQuery: QueryHandler<UUID, TodoListDetailDto?> = GetTodoListDetailsQuery(fakeDataStore)
    val todoListCreatedEventHandler = TodoListCreatedEventHandler(fakeDataStore)
    val taskAddedEventHandler = TaskAddedEventHandler(fakeDataStore)
    val taskCompletedEventHandler = TaskCompletedEventHandler(fakeDataStore)
    val todoListCompletedEventHandler = TodoListCompletedEventHandler(fakeDataStore)
    val eventConsumer = EventConsumer(
        todoListCreatedEventHandler, taskAddedEventHandler, taskCompletedEventHandler, todoListCompletedEventHandler
    )
    eventStore.subscribe(Subscription(eventConsumer::receive))
    return ReadSideQueryHandlers(getTodoListDetailsQuery)
}

private fun wireCommandSide(): Pair<InMemoryEventStore, SimpleCommandBus> {
    val eventStore = InMemoryEventStore()
    val todoListRepository: Repository<TodoList, TodoListId> = TodoListInMemoryEventSourcedRepository(eventStore)
    val createTodoListCommandHandler = CreateTodoListCommandHandler(todoListRepository)
    val addTaskCommandHandler = AddTaskCommandHandler(todoListRepository)
    val completeTaskCommandHandler = CompleteTaskCommandHandler(todoListRepository)
    val commandBus = SimpleCommandBus()
        .register(createTodoListCommandHandler)
        .register(addTaskCommandHandler)
        .register(completeTaskCommandHandler)
    return Pair(eventStore, commandBus)
}

data class ReadSideQueryHandlers(val getTodoListDetailsQuery: QueryHandler<UUID, TodoListDetailDto?>)
