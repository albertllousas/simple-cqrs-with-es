package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.AggregateNotFoundException
import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.todolist.TaskId
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import com.alo.cqrs.todolist.domain.ports.outbound.Repository

class CompleteTaskCommandHandler(
    private val repository: Repository<TodoList, TodoListId>
) : CommandHandler<Command.CompleteTask> {

    override fun handle(command: Command.CompleteTask): Unit {
        val todoList: TodoList = repository.get(TodoListId(command.aggregateId))
            ?: throw AggregateNotFoundException(TodoList::class, command.aggregateId)
        return todoList
            .let { it.completeTask(TaskId(command.taskId)) }
            .let(repository::save)
    }
}
