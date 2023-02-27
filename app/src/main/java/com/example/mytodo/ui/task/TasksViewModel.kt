package com.example.mytodo.ui.task

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.mytodo.logic.repository.Repository
import com.example.mytodo.logic.dao.SearchArg
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.utils.FlagHelper

class TasksViewModel : ViewModel() {

    // 查询列表
    private val searchLiveData = MutableLiveData<SearchArg>()
    fun searchTasks(searchArg: SearchArg) {
        searchLiveData.value = searchArg
    }
    val tasksList = ArrayList<Task>()
    val tasksLiveData = Transformations.switchMap(searchLiveData) { query ->
        Log.d(Constants.TASK_PAGE_TAG,"switchMap query=${query.cmd}")
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
    fun updateTask(task: Task) {
        updateLiveData.value = task
    }

    val updateTaskLiveDate = Transformations.switchMap(updateLiveData) { task ->
        Repository.updateTask(task)
    }

    // 设为重要的
    private val setStartLiveData = MutableLiveData<Task>()
    fun setStart(id: Long, flag: Int) {
        setStartLiveData.value = Task().apply {
            this.flag = flag
            this.id = id
        }
    }
    val startTaskLiveData = Transformations.switchMap(setStartLiveData) { task ->
        Repository.updateTaskFlag(task.id,task.flag)
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

    // 设为我的一天
    private val setOneDayLiveData = MutableLiveData<Task>()
    fun setToOneDay(id: Long, flag: Int, inOneDay : Boolean) {
        setOneDayLiveData.value = Task().apply {
            if (inOneDay) {
                this.flag = FlagHelper.addFlag(flag, Task.IN_ONE_DAY)
            }else {
                this.flag = FlagHelper.removeFlag(flag, Task.IN_ONE_DAY)
            }
            this.id = id
        }
    }
    val oneDayLiveData = Transformations.switchMap(setOneDayLiveData) { task ->
        Repository.updateTaskFlag(task.id,task.flag)
    }

}