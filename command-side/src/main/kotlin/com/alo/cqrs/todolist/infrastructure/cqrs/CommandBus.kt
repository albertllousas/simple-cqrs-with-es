package com.alo.cqrs.todolist.infrastructure.cqrs

import com.alo.cqrs.todolist.domain.model.Command

interface CommandBus {
    suspend fun <T: Command> dispatch(command: T)
}
