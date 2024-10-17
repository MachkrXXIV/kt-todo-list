package com.team.kt_todo_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.team.kt_todo_list.TaskActivity.TaskActivity

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "MainActivity"

    //ViewModel object to communicate between Activity and repository
    private val todoListViewModel: TodoListViewModel by viewModels {
        TodoListViewModelFactory((application as TasksApplication).repository)
    }

    /**
    Callback function passed through to RecyclerViewItems to launch
    A new activity based on id
    @param id id of the item that is clicked
     */
    fun launchNewTaskActivity(id: Int?, isChecked: Boolean) {
        Log.d(LOG_TAG, "Launching new task activity with id: $id and checkState: $isChecked")
        val secondActivityIntent = Intent(this, TaskActivity::class.java)
        secondActivityIntent.putExtra("EXTRA_ID", id)
        secondActivityIntent.putExtra("EXTRA_IS_CHECKED", isChecked)
        this.startActivity(secondActivityIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get reference to recyclerView object
        val recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        //Create adapter class, passing the launchNewWordActivity callback
        val adapter = TaskListAdapter(this::launchNewTaskActivity)
        //Set the adapter for the recyclerView to the adapter object
        recyclerView.adapter = adapter
        //Set the recyclerview layout to be a linearLayoutManager with activity context
        recyclerView.layoutManager = LinearLayoutManager(this)
        //Start observing the words list (now map), and pass updates through
        //to the adapter
        todoListViewModel.allTasks.observe(this, Observer { tasks ->
            // Update the cached copy of the tasks in the adapter.
            tasks?.let { adapter.submitList(it.values.toList()) }
        })
        //Get reference to floating action button
        val fab = findViewById<FloatingActionButton>(R.id.addTodo)
        //Start the TaskActivity when it is clicked
        fab.setOnClickListener {
            Log.d(LOG_TAG, "Add new task")
            val intent = Intent(this@MainActivity, TaskActivity::class.java)
            startActivity(intent)
        }
    }
}