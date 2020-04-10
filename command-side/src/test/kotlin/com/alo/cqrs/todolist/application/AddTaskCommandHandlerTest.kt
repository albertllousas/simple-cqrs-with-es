package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.AggregateNotFoundException
import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.Status
import com.alo.cqrs.todolist.domain.model.todolist.Task
import com.alo.cqrs.todolist.domain.model.todolist.TaskAdded
import com.alo.cqrs.todolist.domain.model.todolist.TaskId
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.fixtures.buildTodoList
import com.alo.cqrs.todolist.infrastructure.cqrs.CommandHandlerNotRegisteredException
import com.alo.cqrs.todolist.infrastructure.cqrs.SimpleCommandBus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class AddTaskCommandHandlerTest {

    private val repository = mockk<Repository<TodoList, TodoListId>>(relaxed = true)

    private val staticTaskUuuid = UUID.randomUUID()

    private val commandHandler = AddTaskCommandHandler(
        repository = repository,
        generateId = { staticTaskUuuid }
    )

    @Test
    fun `should handle the creation of a new task`() {
        val command = Command.AddTask(aggregateId = UUID.randomUUID(), name = "my task")
        val todoList = buildTodoList(id = TodoListId(command.aggregateId))
        every { repository.get(TodoListId(command.aggregateId)) } returns todoList

        commandHandler.handle(command)

        val expected = buildTodoList(
            todoList.id,
            todoList.name,
            Status.TODO,
            listOf(Task(TaskId(staticTaskUuuid), command.name, Status.TODO)),
            listOf(TaskAdded(command.aggregateId, staticTaskUuuid, command.name)
            )
        )
        verify { repository.save(expected) }
    }

    @Test
    fun `should handling the creation of a new task for a non existent todo list`() {
        val command = Command.AddTask(aggregateId = UUID.randomUUID(), name = "my task")
        every { repository.get(TodoListId(command.aggregateId)) } returns null

        assertThatThrownBy { commandHandler.handle(command) }
            .isInstanceOf(AggregateNotFoundException::class.java)
            .hasMessageContaining(
                "Aggregate root 'TodoList' with id '${command.aggregateId}' not found"
            )
    }
}
