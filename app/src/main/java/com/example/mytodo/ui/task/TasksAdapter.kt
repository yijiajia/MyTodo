package com.example.mytodo.ui.task

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.mytodo.R
import com.example.mytodo.logic.listener.TaskClickListener
import com.example.mytodo.logic.domain.TaskItem
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class TasksAdapter(val viewModel: TasksViewModel)
    : ListAdapter<Task, TasksAdapter.ViewHolder>(DIFF_CALLBACK) {

    lateinit var taskClickListener: TaskClickListener

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val checkTaskBtn : ImageButton = view.findViewById(R.id.check_task_button)
        val nameText : MaterialTextView = view.findViewById(R.id.task_name_text_view)
        val setTaskStartBtn : ImageButton = view.findViewById(R.id.set_task_important)
        val cardView : MaterialCardView = view.findViewById(R.id.task_card)
        val animationView: LottieAnimationView = view.findViewById(R.id.suc_animation)

        fun bind(task: Task) {
            val taskItem = TaskItem(nameText, checkTaskBtn, setTaskStartBtn, task)
            taskItem.initItem()
            taskItem.initClickListener(viewModel)

            /*val curStartState = FlagHelper.containsFlag(task.flag, Task.IS_START)
            holder.checkTaskBtn.setOnClickListener {
                val upState = if (task.state == TaskState.DONE) {
                    taskClickListener.onTaskDoingClick(task)
                    TaskState.DOING
                } else  {
                    holder.animationView.playAnimation()
                    Log.d(Constants.TASK_PAGE_TAG,"播放动画")
                    taskClickListener.onTaskDoneClick(task)
                    TaskState.DONE
                }
                val toastName = if (upState == TaskState.DOING) "正在做" else  "做完"
                viewModel.updateState(task.id, upState)
                Log.d(Constants.TASK_PAGE_TAG,"update task state id= ${task.id} state for $upState；toastName=$toastName")
            }

            holder.setTaskStartBtn.setOnClickListener {
                val isStart = !curStartState
                viewModel.setStart(task.id, task.flag ,isStart)
                holder.setTaskStartBtn.setImageResource(R.drawable.ic_shoucang_check)
                Log.d(Constants.TASK_PAGE_TAG,"update task start id= ${task.id} isStart for $isStart")
            }*/
            Log.d(Constants.TASK_PAGE_TAG,"adapter holder bind view")
            cardView.setOnClickListener {
                taskClickListener.onTaskClick(task, cardView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(parent.context,"进入任务详情",Toast.LENGTH_SHORT).show()
        }
        viewHolder.animationView.setAnimation("98350-fireworks.json")
//        viewHolder.animationView.repeatCount = 1
        viewHolder.animationView.speed = 1.5f
     /*   viewHolder.animationView.addLottieOnCompositionLoadedListener { composition ->
            Log.d(Constants.TASK_PAGE_TAG,"load animation is suc")
        }*/
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
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