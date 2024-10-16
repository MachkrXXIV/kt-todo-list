package com.team.kt_todo_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team.kt_todo_list.Model.Task

class TaskListAdapter(
    val onItemClicked: (id: Int) -> Unit
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            current.id?.let { it1 -> onItemClicked(it1) }
        }
        holder.bind(current)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val LOG_TAG = TaskViewHolder::class.java.simpleName
        private val taskItemView: LinearLayout = itemView.findViewById(R.id.task_item)
        private val taskCheckBox: CheckBox = itemView.findViewById(R.id.task_item_checkbox)
        private val taskTextView: TextView = itemView.findViewById(R.id.task_item_textview)

        fun bind(task: Task?) {
            if (task != null) {
                taskCheckBox.isChecked = task.isCompleted
                taskTextView.text = task.title
            }
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title
        }
    }
}
