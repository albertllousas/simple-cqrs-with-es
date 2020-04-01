package com.alo.cqrs.todolist.domain.ports.inbound

import com.alo.cqrs.todolist.domain.model.Command

interface CommandHandler<T: Command> {
    fun handle(command: T)
}
