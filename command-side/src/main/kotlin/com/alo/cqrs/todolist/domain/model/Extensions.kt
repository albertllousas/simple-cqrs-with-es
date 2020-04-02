package com.alo.cqrs.todolist.domain.model

fun <T> List<T>.tail() = drop(1)
