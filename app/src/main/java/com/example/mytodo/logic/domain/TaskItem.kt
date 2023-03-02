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

    var curTaskName : String? = null
    
    fun initItem() {
        flushItemUI()
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
            flushItemUI()
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
            updateStartUI()
            Log.d(Constants.TASK_PAGE_TAG,"update task start id= ${task.id} isStart for $isStart")
        }
    }


    fun flushItemUI() {
        updateNameUI()
        updateStartUI()
    }

    fun updateNameUI() {
        /**
         * 从task中获取到名字 或者从输入框获取到名字
         */
        if (curTaskName == null) {
            curTaskName = task.name
        }else {
            curTaskName = if (nameText?.visibility == View.VISIBLE) {
                nameText.text.toString()
            } else {
                nameTextEdit?.text.toString()
            }
        }

        /**
         * checkTaskBtn
         */
        var resId = R.drawable.ic_select
        if (task.state == TaskState.DONE) {
            val spannableString = SpannableString(curTaskName)
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
        }else {
            if (nameText?.visibility == View.VISIBLE) {
                nameText.text = curTaskName
            } else {
                nameTextEdit?.setText(curTaskName)
            }
        }
        checkTaskBtn.setImageResource(resId)
    }

    fun updateStartUI() {
        var startResId = R.drawable.ic_shoucang
        if (FlagHelper.containsFlag(task.flag, Task.IS_START)) {
            startResId = R.drawable.ic_shoucang_check
        }
        setTaskStartBtn.setImageResource(startResId)
    }

}