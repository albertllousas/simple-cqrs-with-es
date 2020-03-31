package com.alo.cqrs.todolist.infrastructure.service.bus

import com.alo.cqrs.todolist.domain.model.Commands

interface CommandBus {
    suspend fun <T: Commands> dispatch(command: T)
}
