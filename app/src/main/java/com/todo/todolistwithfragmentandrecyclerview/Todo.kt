package com.todo.todolistwithfragmentandrecyclerview

data class Todo(
    val id:Int,
    val title:String,
    val date:String,
    val isChecked: Boolean = false
    )