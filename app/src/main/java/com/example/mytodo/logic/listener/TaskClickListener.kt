package com.example.mytodo.logic.listener

import com.example.mytodo.logic.domain.entity.Task
import com.google.android.material.card.MaterialCardView


interface TaskClickListener {

    fun onTaskClick(task: Task, card: MaterialCardView)

    fun onTaskDoneClick(task: Task)

    fun onTaskDoingClick(task: Task)

    fun onTaskDelClick(task: Task) {

    }
}