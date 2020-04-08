package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.fixtures.buildTodoList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
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
                id= todoListId,
                name = name,
                uncommittedChanges = listOf(TodoListCreated(todoListId.value, name))
            )
            assertThat(todoList).isEqualTo(expected)
        }

        @Test
        fun `should recreate a todo list from history`() {
            val todoListId = TodoListId(UUID.randomUUID())
            val name = "my todo list"
            val pastEvent = TodoListCreated(todoListId.value, name)

            val todoList = TodoList.Factory.recreate(listOf(pastEvent))

            val expected = buildTodoList(todoListId, name)
            assertThat(todoList).isEqualTo(expected)
        }
    }

    @Nested
    @DisplayName("Tests for adding tasks to a todo-list")
    inner class AddTasksTest {

        @Test
        fun `should add a task to an existent todo-list`() {
            val todoList = buildTodoList(tasks = emptyList())
            val task = Task(TaskId(UUID.randomUUID()), "task name")

            val result = todoList.addTask(task)

            assertThat(result).isEqualTo(
                buildTodoList(
                    id = todoList.id,
                    name = todoList.name,
                    tasks = listOf(task),
                    uncommittedChanges = todoList.uncommittedChanges + listOf(TaskAdded(todoList.id.value, task.id.value, task.name))
                )
            )
        }
    }

}
