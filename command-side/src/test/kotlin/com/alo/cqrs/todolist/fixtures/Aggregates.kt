package com.alo.cqrs.todolist.fixtures

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.github.javafaker.Faker
import java.util.UUID

private val faker = Faker()

data class AnemicTodoList(val id: TodoListId, val name: String, val uncommittedChanges: List<DomainEvent>)

fun buildAnemicTodoListForTest(
    id: TodoListId = TodoListId(UUID.randomUUID()),
    name: String = faker.funnyName().name(),
    history: List<DomainEvent> = listOf(TodoListCreated(id.value, name))
) = AnemicTodoList(id, name, history)
