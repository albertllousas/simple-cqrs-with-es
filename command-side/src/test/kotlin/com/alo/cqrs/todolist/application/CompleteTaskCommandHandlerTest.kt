package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.Status
import com.alo.cqrs.todolist.domain.model.todolist.Task
import com.alo.cqrs.todolist.domain.model.todolist.TaskAdded
import com.alo.cqrs.todolist.domain.model.todolist.TaskCompleted
import com.alo.cqrs.todolist.domain.model.todolist.TaskId
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCompleted
import com.alo.cqrs.todolist.domain.model.todolist.TodoListCreated
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import com.alo.cqrs.todolist.fixtures.buildTodoList
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID

class CompleteTaskCommandHandlerTest {

    private val repository = mockk<Repository<TodoList, TodoListId>>(relaxed = true)

    private val commandHandler = CompleteTaskCommandHandler(repository)

    @Test
    fun `should handle the completion of a task`() {
        val command = Command.CompleteTask(aggregateId = UUID.randomUUID(), taskId = UUID.randomUUID())
        val todoList = buildTodoList(
            id = TodoListId(command.aggregateId),
            tasks = listOf(Task(TaskId(command.taskId), "my task", Status.TODO))
        )
        every { repository.get(TodoListId(command.aggregateId)) } returns todoList

        commandHandler.handle(command)

        val expected = buildTodoList(
            id = todoList.id,
            name = todoList.name,
            version = todoList.version,
            status = Status.DONE,
            tasks = listOf(Task(TaskId(command.taskId), "my task", Status.DONE)),
            uncommittedChanges = listOf(
                TaskCompleted(command.aggregateId, command.taskId),
                TodoListCompleted(command.aggregateId)
            )
        )
        verify { repository.save(expected) }
    }

}
