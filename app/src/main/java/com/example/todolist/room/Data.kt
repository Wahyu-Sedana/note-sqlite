package com.example.todolist.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @PrimaryKey(autoGenerate = true)
    var id_note: Int = 0,
    val title: String,
    val description: String
)
