package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.model.Command.CreateTodoList
import com.alo.cqrs.todolist.domain.model.todolist.Task
import com.alo.cqrs.todolist.domain.model.todolist.TaskId
import com.alo.cqrs.todolist.domain.model.todolist.TodoList
import com.alo.cqrs.todolist.domain.model.todolist.TodoListId
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import com.alo.cqrs.todolist.domain.ports.outbound.Repository
import java.util.UUID

//class AddTaskCommandHandler(
//    private val repository: Repository<TodoList, TodoListId>,
//    private val generateId: () -> UUID = { UUID.randomUUID() }
//) : CommandHandler<Command.AddTask> {
//
//    override fun handle(command: Command.AddTask) =
//        repository.get(TodoListId(command.aggregateId))
//            .let { it.addTask(Task(TaskId(generateId()), command.name)) }
//            .let(repository::save)
//
//}
