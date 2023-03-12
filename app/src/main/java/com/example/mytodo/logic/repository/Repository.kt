package com.example.mytodo.logic.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.logic.Cmd
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.dao.SearchArg
import com.example.mytodo.logic.dao.SharedPreDao
import com.example.mytodo.logic.domain.constants.ProjectSignValue
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object Repository {

    private val projectDao = db().getProjectDao()
    private val tasksDao = db().getTaskDao()

    fun getProjectList() = projectDao.getAllList()

    fun saveProject(project: Project) = fire(Dispatchers.IO) {
        Result.success(projectDao.insertProject(project))
    }

    fun delProjectById(id : Long) {
        projectDao.delProjectById(id)
        tasksDao.deleteTaskByProjectId(id)
    }

    fun getProjectTitleById(projectId: Long) = projectDao.getProjectTitleById(projectId)

    fun updateProjectNum(moveNum: Int,id: Long) = projectDao.updateProjectNum(moveNum, id)

    fun searchTasks(searchArg: SearchArg) = fire(Dispatchers.IO) {
        var tasksList: List<Task> = when(searchArg.cmd) {
            Cmd.SEARCH_ALL -> tasksDao.searchAllTasks()
            Cmd.SEARCH_BY_PID ->  tasksDao.searchTasksByProjectId(searchArg.value as Long)
            Cmd.SEARCH_IMPORTANT -> searchImportantTasks()
            Cmd.SEARCH_ONE_DAY -> tasksDao.searchTasksByFlag(Task.IN_ONE_DAY)
            Cmd.SEARCH_LIKE_NAME -> {
                val searchValue = searchArg.value as String
                if (searchValue.isEmpty()) {
                    return@fire Result.success(listOf<Task>())
                }
                tasksDao.searchTasksLikeName(searchValue)
            }
            else -> throw IllegalArgumentException("arg is error")
        }
        tasksList = tasksList.sortedBy { task -> task.state }
        Result.success(tasksList)
    }

    fun searchImportantTasks() = tasksDao.searchTasksByFlag(Task.IS_START)

    fun searchTaskCount() : ProjectSignValue {
        val startCount = tasksDao.searchCountByFlag(Task.IS_START)
        val oneDayCount = tasksDao.searchCountByFlag(Task.IN_ONE_DAY)

        return ProjectSignValue().apply {
            startNum = startCount
            oneDayNum = oneDayCount
        }
    }


    fun insertTask(task: Task) = fire(Dispatchers.IO) {
        val taskId = tasksDao.insertTask(task)
        // 更新project中的数目
        val projectId = task.projectId
        updateProjectNum(1, projectId)
        Result.success(taskId)
    }

    fun deleteTask(task: Task) = fire(Dispatchers.IO) {
        val projectId = task.projectId
        updateProjectNum(-1, projectId)
        Result.success(tasksDao.deleteTaskById(task.id))
    }

    fun updateTask(task: Task) = fire(Dispatchers.IO) {
        Result.success(tasksDao.updateTask(task))
    }

    fun updateTaskState(id: Long,state: Int) = fire(Dispatchers.IO) {
        Result.success(tasksDao.updateTaskState(id, state))
    }

    fun updateTaskFlag(id: Long, flag: Int) = fire(Dispatchers.IO) {
        Result.success(tasksDao.updateFlag(id, flag))
    }

    fun searchTasksLikeName(name: String) = fire(Dispatchers.IO) {
        Result.success(tasksDao.searchTasksLikeName(name))
    }


    fun getInteger4Alarm(key: String, defaultValue: Int) = SharedPreDao.getInteger4Alarm(key, defaultValue)
    fun setInteger4Alarm(key: String, value: Int) = SharedPreDao.setInteger4Alarm(key, value)
    fun getAndSet4Alarm(key: String, defaultValue: Int) : Int {
        val id = getInteger4Alarm(key, defaultValue)
        setInteger4Alarm(key, id + 1)
        return id
    }

    fun getInteger4Broad(taskId: String) = SharedPreDao.getInteger4Broad(taskId)
    fun setInteger4Broad(taskId: String, requestCode: Int) = SharedPreDao.setInteger4Broad(taskId,requestCode)


    private fun db() : AppDatabase {
        return AppDatabase.getDatabase(MyToDoApplication.context)
    }

    private fun <T> fire(context: CoroutineContext, block : suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

}