package com.example.todolist.room

import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    suspend fun addNote(note: Data)

    @Update
    suspend fun upadateNote(note: Data)

    @Delete
    suspend fun deleteNote(note: Data)

    @Query("SELECT * FROM data")
    suspend fun getNotes(): List<Data>

    @Query("SELECT * FROM data WHERE id_note=:id")
    suspend fun getNote(id: Int): List<Data>
}