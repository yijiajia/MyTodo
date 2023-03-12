package com.example.mytodo

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.dao.ProjectDao
import com.example.mytodo.logic.dao.TasksDao
import com.example.mytodo.logic.domain.ProjectData
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class RoomDbTest {

    lateinit var taskDao: TasksDao
    lateinit var projectDao: ProjectDao
    lateinit var db : AppDatabase

    @Before
    fun createDB() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabase.getDatabase(appContext)
        taskDao = db.getTaskDao()
        projectDao = db.getProjectDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    fun fixFlagData() {
        runBlocking {
            val tasks = taskDao.searchAllTasks()
            Log.d("","task size=${tasks.size}")

            val targetTasks = tasks.filter {  task ->
                task.flag < 0
            }
            Log.d("","异常的tasks有=${targetTasks.size}个")

            targetTasks.forEach { task ->
                taskDao.updateFlag(task.id,0)
            }

            Log.d("","更新结束")
        }
    }

    @Test
    fun searchCountTest() {
        runBlocking {
            val startCount = taskDao.searchCountByFlag(Task.IS_START)
            val oneDayCount = taskDao.searchCountByFlag(Task.IN_ONE_DAY)
            Log.d("","查询结束；startCount=$startCount, oneDayCount=$oneDayCount")
        }
    }

    @Test
    fun searchTasksByFlag() {
        runBlocking {
            val taskList = taskDao.searchTasksByFlag(Task.IN_ONE_DAY)
            Log.d("","taskList=$taskList")
        }
    }

    @Test
    fun searchTasksByRemind() {
        runBlocking {
            var taskList = taskDao.searchTasksByNeedRemind()
            Log.d("","searchTasksByNeedRemind-taskList=$taskList")
            val beginTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
            val endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
            taskList = taskDao.searchTasksByRemindToday(beginTime, endTime)
            Log.d("","searchTasksByRemindToday-taskList=$taskList")

            val now = LocalDate.now()
            val endTime2: LocalDateTime =
                LocalDateTime.of(now.year, now.month, now.dayOfMonth, 23, 59, 59)
            Log.d("","endTime=$endTime, endTime2=$endTime2 , equals=${endTime.equals(endTime2)}")
        }
    }

    @Test
    fun getProjectNameByJoinProjectId() {
        runBlocking {
            println(projectDao.getProjectTitleById(8))
        }
    }

    @Test
    fun exportOldData() {
        runBlocking {
            val projects= projectDao.getAllList()
            val projectDataList = mutableListOf<ProjectData>()
            projects.forEach {  project ->
                val tasks = taskDao.searchTasksByProjectId(project.id)
                projectDataList.add(ProjectData(project,tasks))
            }
            val path = "E:\\code\\android\\MyToDo\\projectDataList.json"
            PrintWriter(FileWriter(path)).use {
                val gson = Gson()
                it.write(gson.toJson(projectDataList))
            }
        }
    }

}