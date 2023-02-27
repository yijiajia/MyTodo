package com.example.mytodo.logic.domain

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.TaskState
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.utils.FlagHelper
import com.example.mytodo.ui.task.TasksViewModel
import com.google.android.material.textview.MaterialTextView

class TaskItem(private val nameText: MaterialTextView?,
               private val checkTaskBtn : ImageButton,
               private val setTaskStartBtn: ImageButton,
               val task: Task) {

    var nameTextEdit: EditText? = null

    
    fun initItem() {
        initItemUI()
//        Log.d(Constants.TASK_PAGE_TAG, "页面数据初始化成功;task=$task")
    }

    fun initClickListener(viewModel: TasksViewModel) {
        checkTaskBtn.setOnClickListener {
            val upState = if (task.state == TaskState.DONE) {
//                taskClickListener.onTaskDoingClick(task)
                TaskState.DOING
            } else  {
//                animationView.playAnimation()
                Log.d(Constants.TASK_PAGE_TAG,"播放动画")
//                taskClickListener.onTaskDoneClick(task)
                TaskState.DONE
            }
            task.state = upState
            viewModel.updateTask(task)
            initItemUI()
            Log.d(Constants.TASK_PAGE_TAG,"update task state id= ${task.id} state for $upState")
        }
        setTaskStartBtn.setOnClickListener {
            val isStart = !FlagHelper.containsFlag(task.flag, Task.IS_START)
            if (isStart) {
                task.flag = FlagHelper.addFlag(task.flag, Task.IS_START)
            }else {
                task.flag = FlagHelper.removeFlag(task.flag,Task.IS_START)
            }
            viewModel.setStart(task.id, task.flag)
            initItemUI()
            Log.d(Constants.TASK_PAGE_TAG,"update task start id= ${task.id} isStart for $isStart")
        }
    }

    fun initItemUI() {
        val taskName = task.name
        if (nameText?.visibility == View.VISIBLE) {
            nameText.text = taskName
        } else {
            nameTextEdit?.setText(taskName)
        }
        /**
         * checkTaskBtn
         */
        var resId = R.drawable.ic_select
        if (task.state == TaskState.DONE) {
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (nameText?.visibility == View.VISIBLE) {
                nameText.text = spannableString
            } else {
                nameTextEdit?.setText(spannableString) /** 划线的效果 **/
            }
            resId = R.drawable.ic_select_check
        }
        checkTaskBtn.setImageResource(resId)

        var startResId = R.drawable.ic_shoucang
        if (FlagHelper.containsFlag(task.flag, Task.IS_START)) {
           startResId = R.drawable.ic_shoucang_check
        }
        setTaskStartBtn.setImageResource(startResId)
    }


}