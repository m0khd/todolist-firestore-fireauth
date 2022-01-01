package com.todo.todolistwithfragmentandrecyclerview.db

import androidx.room.Entity
import androidx.room.PrimaryKey


data class TodoModel(
    var id:String,
    var title:String,
    var date:String,
    var isChecked: Boolean = false
) {
}