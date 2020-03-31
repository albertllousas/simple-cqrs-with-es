package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.model.Commands.CreateTodoList
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler

class CreateTodoListCommandHandler: CommandHandler<CreateTodoList> {
    override fun handle(command: CreateTodoList) {
        TODO("not implemented")
    }
}
