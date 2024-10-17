package com.team.kt_todo_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.team.kt_todo_list.Model.Task
import com.team.kt_todo_list.Model.TaskRepository
import kotlinx.coroutines.launch

class TodoListViewModel(private val repository: TaskRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTasks: LiveData<Map<Int, Task>> = repository.allTasks.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}

class TodoListViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}