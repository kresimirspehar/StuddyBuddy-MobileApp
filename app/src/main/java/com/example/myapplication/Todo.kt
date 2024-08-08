package com.example.myapplication

import java.time.Instant
import java.util.Date

data class Todo(
    var id: Int,
    var title: String,
    var createdAt: Date
)

fun getFakeTodo() : List<Todo> {
    return listOf<Todo>(
        Todo(1, "First todo", Date.from(Instant.now())),
        Todo(2, "Second todo", Date.from(Instant.now())),
        Todo(3, "this is my third todo", Date.from(Instant.now())),
        Todo(4, "useee it in uiiii", Date.from(Instant.now()))
    )
}
