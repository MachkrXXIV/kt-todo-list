package com.team.kt_todo_list.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @MapInfo(keyColumn = "id")
    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAlphabetizedTasks(): Flow<Map<Int,Task>>

    @Update
    suspend fun update(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("DELETE FROM task_table WHERE id = :id")
    suspend fun delete(id: Int)
}