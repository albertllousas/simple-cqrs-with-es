package com.alo.cqrs.todolist.domain.ports.inbound

interface CommandBus {
    suspend fun <T: Command> dispatch(command: T)
}
