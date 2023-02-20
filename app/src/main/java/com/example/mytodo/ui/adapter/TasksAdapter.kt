package com.example.mytodo.ui.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.logic.domain.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.domain.TaskState
import com.example.mytodo.logic.showToast
import com.example.mytodo.ui.viewModel.TasksViewModel
import com.google.android.material.textview.MaterialTextView

class TasksAdapter(val viewModel: TasksViewModel)
    : ListAdapter<Task,TasksAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val checkTaskBtn : ImageButton = view.findViewById(R.id.check_task_button)
        val nameText : MaterialTextView = view.findViewById(R.id.task_name_text_view)
        val setTaskStartBtn : ImageButton = view.findViewById(R.id.set_task_important)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(parent.context,"进入任务详情",Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        if (task.state == TaskState.DOING) {
            holder.nameText.text = task.name
            holder.checkTaskBtn.setImageResource(R.drawable.ic_select)
        }else {
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.nameText.text = spannableString  /** 划线的效果 **/
            holder.checkTaskBtn.setImageResource(R.drawable.ic_select_check)
        }
        if(task.isStart) {
            holder.setTaskStartBtn.setImageResource(R.drawable.ic_shoucang_check)
        }

        holder.checkTaskBtn.setOnClickListener {
            val state = if (task.state == TaskState.DONE) TaskState.DOING else  TaskState.DONE
            val toastName = if (task.state == TaskState.DONE) "正在做" else  "做完"
            viewModel.updateState(task.id, state)
            Log.d(Constants.TASK_PAGE_TAG,"update task state id= ${task.id} state for $state")
            toastName.showToast()
        }

        holder.setTaskStartBtn.setOnClickListener {
            val isStart = !task.isStart
            viewModel.setStart(task.id, isStart)
            holder.setTaskStartBtn.setImageResource(R.drawable.ic_shoucang_check)
            Log.d(Constants.TASK_PAGE_TAG,"update task start id= ${task.id} isStart for $isStart")
        }
    }


    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Task>() {

            override fun areItemsTheSame(oldTask: Task, newTask: Task) =
                oldTask.id == newTask.id

            override fun areContentsTheSame(oldTask: Task, newTask: Task) =
                oldTask == newTask
        }
    }
}