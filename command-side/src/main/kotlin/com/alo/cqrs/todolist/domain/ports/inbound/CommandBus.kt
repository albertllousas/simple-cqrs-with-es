package com.alo.cqrs.todolist.domain.ports.inbound

import kotlin.reflect.KClass

interface CommandBus {
    suspend fun <T: Command> dispatch(command: T)
}
