package com.alo.cqrs.todolist.projection

interface QueryHandler<A,B> {
    operator fun invoke(input: A): B
}
