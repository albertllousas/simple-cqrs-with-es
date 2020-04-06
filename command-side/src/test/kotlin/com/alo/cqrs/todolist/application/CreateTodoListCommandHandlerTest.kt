package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.fixtures.buildTodoList
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateTodoListCommandHandlerTest {

    private val repository = mockk<Repository<TodoList, TodoListId>>(relaxed = true)

    private val createTodoListCommandHandler = CreateTodoListCommandHandler(repository)

    @Test
    fun `should handle the creation of a new todo-list`() {
        val command = Command.CreateTodoList(id= UUID.randomUUID(), name = "my todo list")

        createTodoListCommandHandler.handle(command)

        val expected = buildTodoList(
            TodoListId(command.id), command.name, listOf(TodoListCreated(command.id, command.name))
        )
        verify { repository.save(expected) }
    }

}
