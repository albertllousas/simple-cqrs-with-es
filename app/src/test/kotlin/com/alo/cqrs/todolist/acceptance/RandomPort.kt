package com.alo.cqrs.todolist.acceptance

import java.net.ServerSocket

object RandomPort {
    fun get() = ServerSocket(0).use { it.localPort }

}
