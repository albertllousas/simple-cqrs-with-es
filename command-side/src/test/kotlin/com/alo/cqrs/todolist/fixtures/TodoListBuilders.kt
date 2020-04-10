package com.alo.cqrs.todolist.fixtures

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.domain.model.todolist.Status
import com.alo.cqrs.todolist.domain.model.todolist.Task
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.github.javafaker.Faker
import java.util.UUID

private val faker = Faker()

fun buildTodoList(
    id: TodoListId = TodoListId(UUID.randomUUID()),
    name: String = faker.funnyName().name(),
    status: Status = Status.TODO,
    tasks: List<Task> = emptyList(),
    uncommittedChanges: List<DomainEvent> = emptyList()
) = TodoList.Factory.restoreState(id, name, status, tasks, uncommittedChanges)
