package com.alo.cqrs.todolist.domain.ports.outbound

import com.alo.cqrs.todolist.domain.model.AggregateId
import com.alo.cqrs.todolist.domain.model.AggregateRoot

interface Repository<AR: AggregateRoot, ID: AggregateId> {
    fun save(aggregate: AR)
    fun get(id: ID): AR?
}
