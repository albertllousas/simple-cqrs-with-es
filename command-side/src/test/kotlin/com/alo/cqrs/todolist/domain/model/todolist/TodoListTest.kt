package com.alo.cqrs.todolist.domain.model.todolist

import com.alo.cqrs.todolist.domain.model.DomainEvent
import com.alo.cqrs.todolist.fixtures.buildAnemicTodoListForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class TodoListTest {
    @Test
    fun `should create a todo list from scratch`() {
        val todoListId = TodoListId(UUID.randomUUID())
        val name = "my todo list"

        val todoList = TodoList.createNew(id = todoListId, name = name)

        val expected = buildAnemicTodoListForTest(todoListId, name, listOf(TodoListCreated(todoListId.value, name)))
        assertThat(todoList).isEqualToComparingFieldByField(expected)
    }
}
