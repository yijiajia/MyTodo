package com.example.mytodo.logic.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.R
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InitWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("","初始化数据任务 init")
        InitData().invoke()
        return Result.success()
    }

    inner class InitData {
        operator fun invoke() {
            Log.d("","初始化数据任务 invoke")
            val projectList = mutableListOf(
                Project("MySQL",0, R.drawable.task)
            )

            CoroutineScope(Dispatchers.IO).launch {
                val projectDao = AppDatabase.getDatabase(MyToDoApplication.context).getProjectDao()
                val taskDao = AppDatabase.getDatabase(MyToDoApplication.context).getTaskDao()
                val job = async {
                    val curProjectList = projectDao.getAllList()
                    val curTaskList = taskDao.searchAllTasks()
                    val pidList = ArrayList<Long>()
                    if (curProjectList.isEmpty()) {
                        projectList.forEach { project ->
                            val result = projectDao.insertProject(project)
                            Log.d("","项目插入 $result")
                            pidList.add(result)
                        }
                    }
                    if (curTaskList.isEmpty()) {
                        if (pidList.isNotEmpty()) {
                            pidList.forEach{ pid ->
                                // 插入任务
                                val taskList = mutableListOf(
                                    Task("索引的结构是什么",0,pid),
                                    Task("InnoDB和MyISAM的区别是什么",0,pid),
                                    Task("InnoDB引擎中索引的实现原理",0,pid),
                                    Task("谈谈MySQL的事务隔离级别",0,pid),
                                    Task("redo log 和 binlog的区别是什么",0,pid),
                                    Task("主从同步",0,pid),
                                    Task("事务的原理",0,pid),
                                    Task("MVCC的原理",0,pid),
                                    Task("索引失效的场景",0,pid),
                                    Task("慢日志的查询方式",0,pid),
                                )
                                taskList.forEach { task ->
                                    val taskId = taskDao.insertTask(task)
                                    projectDao.updateProjectNum(1, pid)
                                }
                            }
                            Log.d("","init task list is suc;pidList=$pidList")
                        }
                    }
                }
                job.await()
                Log.d("","初始化DB数据结束")
            }
        }
    }
}