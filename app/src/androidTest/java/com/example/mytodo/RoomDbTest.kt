package com.example.mytodo

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.dao.TasksDao
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.repository.Repository
import com.example.mytodo.logic.toSH
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class RoomDbTest {

    lateinit var taskDao: TasksDao
    lateinit var db : AppDatabase

    @Before
    fun createDB() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabase.getDatabase(appContext)
        taskDao = db.getTaskDao()
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

}