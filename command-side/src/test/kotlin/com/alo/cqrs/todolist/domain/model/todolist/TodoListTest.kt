package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.todolist.Status.*
import com.alo.cqrs.todolist.fixtures.buildTodoList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class TodoListTest {

    @Nested
    @DisplayName("Tests for TodoList factories")
    inner class TodoListFactoryTest {

        @Test
        fun `should create a todo list from scratch`() {
            val todoListId = TodoListId(UUID.randomUUID())
            val name = "my todo list"

            val todoList = TodoList.Factory.create(id = todoListId, name = name)

            val expected = buildTodoList(
                id = todoListId,
                name = name,
                version = 1,
                uncommittedChanges = listOf(TodoListCreated(todoListId.value, name))
            )
            assertThat(todoList).isEqualTo(expected)
        }

        @Test
        fun `should recreate a todo list from history`() {
            val todoListId = TodoListId(UUID.randomUUID())
            val todoListName = "my todo list"
            val taskId = TaskId(UUID.randomUUID())
            val taskName = "my todo list"
            val history = listOf(
                TodoListCreated(todoListId.value, todoListName),
                TaskAdded(todoListId.value, taskId.value, taskName),
                TaskCompleted(todoListId.value, taskId.value),
                TodoListCompleted(todoListId.value)
            )

            val todoList = TodoList.Factory.recreate(history, 10)

            assertThat(todoList).isEqualTo(
                buildTodoList(
                    id = todoListId,
                    name = todoListName,
                    version = 10,
                    status = DONE,
                    tasks = listOf(Task(taskId, taskName, DONE)),
                    uncommittedChanges = emptyList()
                )
            )
        }
    }

    @Nested
    @DisplayName("Tests for adding tasks to a todo-list")
    inner class AddTasksTest {

        @Test
        fun `should add a task to an existent todo-list`() {
            val todoList = buildTodoList(tasks = emptyList())
            val task = Task(TaskId(UUID.randomUUID()), "task name", TODO)

            val result = todoList.addTask(task)

            assertThat(result).isEqualTo(
                buildTodoList(
                    id = todoList.id,
                    name = todoList.name,
                    version = todoList.version,
                    tasks = listOf(task),
                    uncommittedChanges = todoList.uncommittedChanges + listOf(TaskAdded(todoList.id.value, task.id.value, task.name))
                )
            )
        }
    }

    @Nested
    @DisplayName("Tests for completing a task")
    inner class CompleteTaskTest {

        @Test
        fun `should complete a task`() {
            val completedTask = Task(TaskId(UUID.randomUUID()), "task name", TODO)
            val uncompletedTask = Task(TaskId(UUID.randomUUID()), "another", TODO)
            val anotherUncompletedTask = Task(TaskId(UUID.randomUUID()), "more", TODO)
            val todoList = buildTodoList(tasks = listOf(completedTask, uncompletedTask, anotherUncompletedTask))

            val result = todoList.completeTask(uncompletedTask.id)

            val newEvent = TaskCompleted(todoList.id.value, uncompletedTask.id.value)
            assertThat(result).isEqualTo(
                buildTodoList(
                    id = todoList.id,
                    name = todoList.name,
                    version = todoList.version,
                    status = TODO,
                    tasks = listOf(completedTask, anotherUncompletedTask, uncompletedTask.copy(status = DONE)),
                    uncommittedChanges = todoList.uncommittedChanges + listOf(newEvent)
                )
            )
        }

        @Test
        fun `should do nothing trying to complete a non existent task`() {
            val todoList = buildTodoList()

            val result = todoList.completeTask(TaskId(UUID.randomUUID()))

            assertThat(result).isEqualTo(todoList)
        }

        @Test
        fun `should do nothing trying to complete an already completed task`() {
            val completedTask = Task(TaskId(UUID.randomUUID()), "task name", DONE)
            val todoList = buildTodoList(tasks = listOf(completedTask))

            val result = todoList.completeTask(TaskId(UUID.randomUUID()))

            assertThat(result).isEqualTo(todoList)
        }

        @Test
        fun `should complete a todoList when we complete the last non complete task`() {
            val completedTask = Task(TaskId(UUID.randomUUID()), "task name", DONE)
            val uncompletedTask = Task(TaskId(UUID.randomUUID()), "task name", TODO)
            val todoList = buildTodoList(tasks = listOf(completedTask, uncompletedTask))

            val result = todoList.completeTask(uncompletedTask.id)

            val taskCompleted = TaskCompleted(todoList.id.value, uncompletedTask.id.value)
            val todoListCompleted = TodoListCompleted(todoList.id.value)
            assertThat(result).isEqualTo(
                buildTodoList(
                    id = todoList.id,
                    name = todoList.name,
                    version = todoList.version,
                    status = DONE,
                    tasks = listOf(completedTask, uncompletedTask.copy(status = DONE)),
                    uncommittedChanges = todoList.uncommittedChanges + listOf(taskCompleted, todoListCompleted)
                )
            )
        }
    }

}
