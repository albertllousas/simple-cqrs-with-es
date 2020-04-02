package com.alo.cqrs.todolist.fixtures

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.github.javafaker.Faker
import java.util.UUID

private val faker = Faker()

fun buildTodoList(
    id: TodoListId = TodoListId(UUID.randomUUID()),
    name: String = faker.funnyName().name(),
    history: List<DomainEvent> = listOf(TodoListCreated(id.value, name))
) = TodoList.Factory.restoreState(id, name, history)
