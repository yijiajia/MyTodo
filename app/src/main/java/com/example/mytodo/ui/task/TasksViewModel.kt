package com.example.mytodo.ui.task

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.mytodo.logic.Repository
import com.example.mytodo.logic.dao.SearchArg
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task

class TasksViewModel : ViewModel() {

    // 查询列表
    private val searchLiveData = MutableLiveData<SearchArg>()
    fun searchTasks(searchArg: SearchArg) {
        searchLiveData.value = searchArg
    }
    val tasksList = ArrayList<Task>()
    val tasksLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchTasks(query)
    }

    // 新增任务
    private val insertLiveData = MutableLiveData<Task>()
    fun insert(task: Task) {
        insertLiveData.value = task
    }
    val insertTaskLiveData = Transformations.switchMap(insertLiveData) {
        Repository.insertTask(it)
    }

    // 更新状态
    private val updateLiveData = MutableLiveData<Task>()
    fun updateState(id: Long,state : Int) {
        updateLiveData.value = Task().apply {
            this.state = state
            this.id = id
        }
    }

    val updateTaskLiveDate = Transformations.switchMap(updateLiveData) { task ->
        Repository.updateTaskState(task.id, task.state)
    }

    // 设为重要的
    private val setStartLiveData = MutableLiveData<Task>()
    fun setStart(id: Long,isStart: Boolean) {
        setStartLiveData.value = Task().apply {
            this.isStart = isStart
            this.id = id
        }
    }
    val startTaskLiveData = Transformations.switchMap(setStartLiveData) { task ->
        Repository.updateTaskStart(task.id,task.isStart)
    }

    // 删除任务
    private val delLiveData = MutableLiveData<Task>()
    fun delTask(task: Task) {
        delLiveData.value = task
    }
    val delTaskLiveData = Transformations.switchMap(delLiveData) {
        Log.d(Constants.TASK_PAGE_TAG,"准备删除数据")
        Repository.deleteTask(it)
    }


}