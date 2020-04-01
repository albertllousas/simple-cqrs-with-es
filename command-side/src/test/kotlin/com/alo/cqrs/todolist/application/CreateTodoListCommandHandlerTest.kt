package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateTodoListCommandHandlerTest {

    private val repository = mockk<Repository<TodoList, TodoListId>>()

    private val generateId = mockk<() -> UUID>()

    private val createTodoListCommandHandler = CreateTodoListCommandHandler(
        repository = repository,
        generateId = generateId
    )

//    @Test
//    fun `should handle the creation of a new todo-list`() {
//        val command = Command.CreateTodoList(name = "my todo list")
//        val uuid = UUID.randomUUID()
//        every { generateId.invoke() } returns uuid
//
//        createTodoListCommandHandler.handle(command)
//
//        verify {  }
//    }

}
