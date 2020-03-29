package com.alo.cqrs.todolist.domain.ports.inbound

interface CommandHandler<T: Command> {
    fun handle(command: T)
}
