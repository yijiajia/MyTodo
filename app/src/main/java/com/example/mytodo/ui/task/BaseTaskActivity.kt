package com.example.mytodo.ui.task

import androidx.appcompat.app.AppCompatActivity
import com.example.mytodo.logic.domain.entity.Task

open class BaseTaskActivity: AppCompatActivity() {

    open fun onSearchResultListener(taskList: List<Task>) {}
}