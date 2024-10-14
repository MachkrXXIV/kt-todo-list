package com.team.kt_todo_list

import android.app.Application
import com.team.kt_todo_list.Model.TaskRepository
import com.team.kt_todo_list.Model.TaskRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TasksApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { TaskRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}