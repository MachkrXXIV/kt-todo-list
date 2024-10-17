package com.team.kt_todo_list.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean,
    @ColumnInfo(name = "dueDate") val dueDate: LocalDateTime
)