package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command.CreateTodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import java.util.UUID

class CreateTodoListCommandHandler(
    private val repository: Repository<TodoList, TodoListId>
) : CommandHandler<CreateTodoList> {

    override fun handle(command: CreateTodoList) =
        TodoList.Factory.create(id = TodoListId(command.aggregateId), name = command.name)
            .let(repository::save)

}
