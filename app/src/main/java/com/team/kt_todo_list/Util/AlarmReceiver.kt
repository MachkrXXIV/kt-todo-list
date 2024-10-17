package com.team.kt_todo_list.Util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.team.kt_todo_list.MainActivity
import com.team.kt_todo_list.R

class AlarmReceiver : BroadcastReceiver() {
    private val LOG_TAG = "AlarmReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val taskId = intent.getIntExtra(context.getString(R.string.EXTRA_ID), -1)
        val taskTitle = intent.getStringExtra(context.getString(R.string.EXTRA_TITLE))
        Log.d(LOG_TAG, "Alarm fired for task: $taskId with title: $taskTitle")
        val clickIntent = Intent(context, MainActivity::class.java)
        clickIntent.putExtra(context.getString(R.string.EXTRA_ID), taskId)
        clickIntent.putExtra(context.getString(R.string.EXTRA_TITLE), taskTitle)
        NotificationUtil().createClickableNotification(
            context,
            "$taskTitle is due",
            "Don't forget to complete this task!",
            clickIntent,
            taskId
        )
    }
}