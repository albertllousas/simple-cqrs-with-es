package com.alo.cqrs.todolist.infrastructure.service.bus

import com.alo.cqrs.todolist.domain.model.Commands
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import kotlin.reflect.KClass

class SimpleCommandBus : CommandBus {

    private val commandHandlers = mutableMapOf<KClass<out Commands>, CommandHandler<out Commands>>()

    inline fun <reified T : Commands> register(commandHandler: CommandHandler<T>): SimpleCommandBus {
        `access$commandHandlers`[T::class] = commandHandler
        return this
    }


    override suspend fun <T : Commands> dispatch(command: T) {
        (commandHandlers.getOrElse(
            key = command::class, defaultValue = { throw CommandHandlerNotRegisteredException(command::class) }
        ) as CommandHandler<T>).handle(command)
    }

    @PublishedApi
    internal val `access$commandHandlers`: MutableMap<KClass<out Commands>, CommandHandler<out Commands>>
        get() = commandHandlers
}

class CommandHandlerNotRegisteredException(clazz: KClass<out Commands>) : Exception(
    "No CommandHandler registered to handle ${clazz.simpleName}"
)
