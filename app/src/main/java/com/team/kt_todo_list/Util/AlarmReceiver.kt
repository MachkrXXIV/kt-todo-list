package com.team.kt_todo_list.Util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.team.kt_todo_list.MainActivity
import com.team.kt_todo_list.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val id = intent.getIntExtra(context.getString(R.string.EXTRA_ID),0)
        Log.d("AlarmReceiver", id.toString())
        val clickIntent:Intent = Intent(context, MainActivity::class.java)
        clickIntent.putExtra(context.getString(R.string.EXTRA_ID),id)
        NotificationUtil().createClickableNotification(context,"Title","$id fired",clickIntent,id)
    }
}