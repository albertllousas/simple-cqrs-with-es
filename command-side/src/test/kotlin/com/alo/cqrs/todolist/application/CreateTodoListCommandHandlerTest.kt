package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.fixtures.buildTodoList
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateTodoListCommandHandlerTest {

    private val repository = mockk<Repository<TodoList, TodoListId>>(relaxed = true)

    private val commandHandler = CreateTodoListCommandHandler(repository)

    @Test
    fun `should handle the creation of a new todo-list`() {
        val command = Command.CreateTodoList(aggregateId = UUID.randomUUID(), name = "my todo list")

        commandHandler.handle(command)

        val expected = buildTodoList(
            TodoListId(command.aggregateId),
            command.name,
            emptyList(),
            listOf(TodoListCreated(command.aggregateId, command.name)
            )
        )
        verify { repository.save(expected) }
    }

}
