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

    private val generateId = mockk<() -> UUID>()

    private val createTodoListCommandHandler = CreateTodoListCommandHandler(
        repository = repository,
        generateId = generateId
    )

    @Test
    fun `should handle the creation of a new todo-list`() {
        val command = Command.CreateTodoList(name = "my todo list")
        val uuid = UUID.randomUUID()
        every { generateId.invoke() } returns uuid

        createTodoListCommandHandler.handle(command)

        val expected = buildTodoList(
            TodoListId(uuid), command.name, listOf(TodoListCreated(uuid, command.name))
        )
        verify { repository.save(expected) }
    }

}
