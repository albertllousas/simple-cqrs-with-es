package com.alo.cqrs.todolist.infrastructure.service.bus

import com.alo.cqrs.todolist.domain.model.Commands
import com.alo.cqrs.todolist.domain.ports.inbound.CommandHandler
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SimpleCommandBusTest {

    @Test
    fun `should dispatch a command to the specific handler`() = runBlocking {
        val commandHandler = mockk<CommandHandler<Commands.CreateTodoList>>()
        val commandBus: CommandBus = SimpleCommandBus().register(commandHandler)
        val command = Commands.CreateTodoList("my list")
        every { commandHandler.handle(command) } just Runs

        commandBus.dispatch(command)

        verify { commandHandler.handle(command) }
    }

    @Test
    fun `should fail dispatching a command when there is no handler registered`() {
        assertThatThrownBy {
            runBlocking {
                SimpleCommandBus().dispatch(Commands.CreateTodoList(name = "my todo list"))
            }
        }.isInstanceOf(CommandHandlerNotRegisteredException::class.java)
            .hasMessageContaining("No CommandHandler registered to handle CreateTodoList")
    }
}
