package com.team.kt_todo_list.TaskActivity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.team.kt_todo_list.Model.Task
import com.team.kt_todo_list.R
import com.team.kt_todo_list.TasksApplication
import com.team.kt_todo_list.Util.AlarmReceiver
import com.team.kt_todo_list.Util.NotificationUtil
import java.util.Date

class TaskActivity : AppCompatActivity() {
    private val LOG_TAG = "TaskActivity"
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var checkbox: CheckBox
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var task: Task
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as TasksApplication).repository)
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationUtil().createNotificationChannel(this)
                scheduleNotification(task)
            } else {
                Toast.makeText(this,
                    "Unable to schedule notification",
                    Toast.LENGTH_SHORT)
                    .show()
            }
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
        Log.d(LOG_TAG, "Received intent id: $id and isChecked: ${intent.getBooleanExtra("EXTRA_IS_CHECKED", false)}")
        checkbox.isChecked = intent.getBooleanExtra("EXTRA_IS_CHECKED", false)

        if (id == -1) {
            task = Task(null, "", "", false, Date())
        } else {
            taskViewModel.start(id)
            taskViewModel.task.observe(this) {
                if (it != null) {
                    task = it
                    etTitle.setText(it.title)
                    etDescription.setText(it.description)
                    checkbox.isChecked = it.isCompleted
                    datePicker.updateDate(it.dueDate.year, it.dueDate.month, it.dueDate.day)
                    timePicker.hour = it.dueDate.hours
                    timePicker.minute = it.dueDate.minutes
                }
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
                val dueDate = Date(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth,
                    timePicker.hour,
                    timePicker.minute
                )
                if (taskViewModel.task.value?.id == null) {
                    taskViewModel.insert(Task(null, title, description, isCompleted, dueDate))
                } else {
                    taskViewModel.task.value?.let { it1 ->
                        Log.d(LOG_TAG, "Updating task $it1")
                        taskViewModel.update(
                            Task(
                                it1.id,
                                title,
                                description,
                                isCompleted,
                                dueDate
                            )
                        )

                    }
                }
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)
            }
            //End the activity
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I just added a new task!")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            if (checkNotificationPrivilege()) {
                Log.d(LOG_TAG, "Checking Notis privileges")
                scheduleNotification(task)
            }
            finish()
        }

//        val deleteBtn
    }

    private fun checkNotificationPrivilege(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,

            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationUtil().createNotificationChannel(this)
            return true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return false
            }
            return true
        }
    }

    private fun scheduleNotification(task: Task) {
        Log.d(LOG_TAG, "Scheduling notification for task: $task")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_TASK_ID", task.id)
            putExtra("EXTRA_TASK_TITLE", task.title)
        }
        val pendingAlarmIntent = PendingIntent.getBroadcast(this.applicationContext, task.id!!, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, task.dueDate.time, 1000 * 5, pendingAlarmIntent)
    }
}