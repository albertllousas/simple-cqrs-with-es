package com.alo.cqrs.todolist.domain.ports.inbound

import com.alo.cqrs.todolist.domain.model.Commands

interface CommandHandler<T: Commands> {
    fun handle(command: T)
}
