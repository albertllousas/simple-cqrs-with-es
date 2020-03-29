package com.alo.cqrs.todolist.application

import com.alo.cqrs.todolist.domain.ports.inbound.Command
import com.alo.cqrs.todolist.domain.ports.inbound.CommandBus
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import java.lang.reflect.ParameterizedType

class SimpleCommandBus(commandHandlers: List<CommandHandler<Command>>): CommandBus {

    private val register: Map<Class<out Any>, CommandHandler<Command>>

    init {
        register = commandHandlers.map {
            val clazz = (it.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            clazz to it
        }.toMap()
    }

    override suspend fun <T : Command> dispatch(command: T) {
        ((register[command::class.java]) as CommandHandler<T>).handle(command)
    }
}
