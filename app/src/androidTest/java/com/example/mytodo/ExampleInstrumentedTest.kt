package com.example.mytodo

import android.util.Log
import android.widget.Button
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.toSH
import com.example.mytodo.logic.toStringDesc
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.LocalDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mytodo", appContext.packageName)
    }

    /**
     * 更新project数据的方法，在Activity中点击按钮，利用协程更新
     */
    fun updateProjectData() {
        CoroutineScope(Dispatchers.IO).launch {
            val projectDao = AppDatabase.getDatabase(MyToDoApplication.context).getProjectDao()
            val taskDao = AppDatabase.getDatabase(MyToDoApplication.context).getTaskDao()
            val job = async {
                val list = projectDao.getAllList()
                list.forEach {
                    val projectId = it.id
                    val taskList = taskDao.searchTasksByProjectId(projectId)
                    Log.d("", "查询到列表, 项目id=${it.id}，数目为：${taskList.size}")
                    projectDao.updateProjectNum(taskList.size, projectId)
                }
            }
            job.await()
            val projectList = projectDao.getAllList()
            Log.d("", "更新了数目；当前数目为：size=${projectList[0]?.num}")
        }
    }

    @Test
    fun timeTest() {
        runBlocking {
            Log.d("test",LocalDateTime.now().toStringDesc())
            Log.d("test",LocalDateTime.now().toSH().toString())
        }
    }

    @Test
    fun ArrayTest() {
        runBlocking {
            val ans = Array(5) {
                it * it
            }
            println(ans)
        }
    }
}