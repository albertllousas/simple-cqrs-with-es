package com.alo.cqrs.todolist.infrastructure.cqrs.bus

import com.alo.cqrs.todolist.domain.model.Command
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import kotlin.reflect.KClass

class SimpleCommandBus : CommandBus {

    private val commandHandlers = mutableMapOf<KClass<out Command>, CommandHandler<out Command>>()

    inline fun <reified T : Command> register(commandHandler: CommandHandler<T>): SimpleCommandBus {
        `access$commandHandlers`[T::class] = commandHandler
        return this
    }


    override suspend fun <T : Command> dispatch(command: T) {
        (commandHandlers.getOrElse(
            key = command::class, defaultValue = { throw CommandHandlerNotRegisteredException(command::class) }
        ) as CommandHandler<T>).handle(command)
    }

    @PublishedApi
    internal val `access$commandHandlers`: MutableMap<KClass<out Command>, CommandHandler<out Command>>
        get() = commandHandlers
}

class CommandHandlerNotRegisteredException(clazz: KClass<out Command>) : Exception(
    "No CommandHandler registered to handle ${clazz.simpleName}"
)
