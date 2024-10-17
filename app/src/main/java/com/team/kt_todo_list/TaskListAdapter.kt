package com.team.kt_todo_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team.kt_todo_list.Model.Task
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2

class TaskListAdapter(
    val onItemClicked: KFunction2<Int?, Boolean, Unit>,
    val onItemDeleted: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {
    private val LOG_TAG = TaskListAdapter::class.java.simpleName
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent, onItemDeleted)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        Log.d(LOG_TAG, "Binding item $current")
        holder.itemView.setOnClickListener {
            current?.let { it1 -> onItemClicked(it1.id, it1.isCompleted) }
        }
        holder.bind(current)
    }

    class TaskViewHolder(itemView: View, val onItemDeleted: (Task) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val LOG_TAG = TaskViewHolder::class.java.simpleName
        private val taskItemView: LinearLayout = itemView.findViewById(R.id.task_item)
        private val taskCheckBox: CheckBox = itemView.findViewById(R.id.task_item_checkbox)
        private val taskTextView: TextView = itemView.findViewById(R.id.task_item_textview)
        private val taskDeleteBtn: ImageButton = itemView.findViewById(R.id.task_item_delete)

        fun bind(task: Task?) {
            if (task != null) {
                Log.d(LOG_TAG, "Binding task in List Adapter: $task")
                taskCheckBox.isChecked = task.isCompleted
                taskTextView.text = task.title
                taskDeleteBtn.setOnClickListener {
                    onItemDeleted(task)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onItemDeleted: (Task) -> Unit): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view, onItemDeleted)
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
