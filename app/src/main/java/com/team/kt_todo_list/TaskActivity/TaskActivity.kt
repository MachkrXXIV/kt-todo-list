package com.team.kt_todo_list.TaskActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.team.kt_todo_list.Model.Task
import com.team.kt_todo_list.R
import com.team.kt_todo_list.TasksApplication
import java.util.Date

class TaskActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var checkbox: CheckBox
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var task: Task
    val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as TasksApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etTitle = findViewById(R.id.task_title)
        etDescription = findViewById(R.id.task_description)
        checkbox = findViewById(R.id.task_completion)
        datePicker = findViewById(R.id.task_due_date)
        timePicker = findViewById(R.id.task_due_time)

        val id = intent.getIntExtra("EXTRA_ID", -1)
        if (id == -1) {
            task = Task(null, "", "", false, Date())
        } else {
            taskViewModel.start(id)
            taskViewModel.task.observe(this) {
                task = it
                etTitle.setText(it.title)
                etDescription.setText(it.description)
                datePicker.updateDate(it.dueDate.year, it.dueDate.month, it.dueDate.day)
                timePicker.hour = it.dueDate.hours
                timePicker.minute = it.dueDate.minutes
            }
        }

        //Get reference to the button
        val saveBtn = findViewById<Button>(R.id.save_btn)
        //Set the click listener functionality
        //If text is empty, return with nothing
        saveBtn.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(etTitle.text) || TextUtils.isEmpty(etDescription.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                //If text isn't empty, determine whether to update
                //or insert
                val title = etTitle.text.toString()
                val description = etDescription.text.toString()
                val isCompleted = checkbox.isChecked
                val dueDate = Date(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour, timePicker.minute)
                if(taskViewModel.task.value?.id == null){
                    taskViewModel.insert(Task(null,title, description, isCompleted, dueDate))
                }else{
                    taskViewModel.task.value?.let { it1 -> taskViewModel.update(it1) }
                }
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)
            }
            //End the activity
            finish()
        }

//        val deleteBtn
    }
}