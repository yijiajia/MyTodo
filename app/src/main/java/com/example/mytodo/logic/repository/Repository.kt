package com.example.mytodo.logic.repository

import androidx.lifecycle.liveData
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.logic.Cmd
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.dao.SearchArg
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

    fun deleteProject(project: Project) = projectDao.deleteProject(project)

    fun delProjectById(id : Long) {
        projectDao.delProjectById(id)
        tasksDao.deleteTaskByProjectId(id)
    }

    fun updateProjectNum(moveNum: Int,id: Long) = projectDao.updateProjectNum(moveNum, id)

    fun searchTasks(searchArg: SearchArg) = fire(Dispatchers.IO) {
        var tasksList: List<Task> = when(searchArg.cmd) {
            Cmd.SEARCH_ALL -> tasksDao.searchAllTasks()
            Cmd.SEARCH_BY_PID ->  tasksDao.searchTasksByProjectId(searchArg.value as Long)
            Cmd.SEARCH_IMPORTANT -> searchImportantTasks()
            Cmd.SEARCH_ONE_DAY -> tasksDao.searchTasksByFlag(Task.IN_ONE_DAY)
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

    fun updateTaskState(id: Long,state: Int) = fire(Dispatchers.IO) {
        Result.success(tasksDao.updateTaskState(id, state))
    }

    fun updateTaskFlag(id: Long, flag: Int) = fire(Dispatchers.IO) {
        Result.success(tasksDao.updateFlag(id, flag))
    }



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