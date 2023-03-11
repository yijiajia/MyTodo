package com.example.mytodo.ui.task

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.R
import com.example.mytodo.logic.Cmd
import com.example.mytodo.logic.listener.TaskClickListener
import com.example.mytodo.logic.dao.SearchArg
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.ProjectSign
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.showToast
import com.example.mytodo.ui.BaseFragment
import com.example.mytodo.ui.search.SearchMainActivity
import com.google.android.material.card.MaterialCardView

class TasksFragment: BaseFragment() {

    override fun getResourceId() = R.layout.fragment_task_list

    lateinit var taskViewModel : TasksViewModel
    private var projectId = 0L
    private var projectName = ""
    private var projectSign : ProjectSign? = null

    private lateinit var adapter: TasksAdapter
    private lateinit var taskRecyclerView: RecyclerView

    private var previousList : List<Task>? = null
    private lateinit var baseActivity: BaseTaskActivity

    // 搜索参数
    var searchName = ""
    var isSearchPage = false

    override fun initView(rootView: View) {
        baseActivity = if (activity is TasksMainActivity) {
            activity as TasksMainActivity
        }else {
            isSearchPage = true
            activity as SearchMainActivity
        }
        taskViewModel = ViewModelProvider(baseActivity)[TasksViewModel::class.java]

        projectId = baseActivity.intent.getLongExtra(Constants.PROJECT_ID, 0L)
        projectName = baseActivity.intent.getStringExtra(Constants.PROJECT_NAME).toString()
        val serializable = baseActivity.intent.getSerializableExtra(Constants.PROJECT_SIGN)
        if (serializable != null) {
            projectSign = serializable as ProjectSign
        }

        Log.d(Constants.TASK_PAGE_TAG, "projectId = $projectId, projectName= $projectName")
        refreshList("onCreate")

        adapter = TasksAdapter(taskViewModel)
        taskRecyclerView = rootView.findViewById(R.id.task_recycle_view)
        taskRecyclerView.layoutManager = LinearLayoutManager(baseActivity)
        taskRecyclerView.adapter = adapter

        // 下拉刷新
        val swipeRefreshTask: SwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_task)
        swipeRefreshTask.setOnRefreshListener {
            refreshList("refresh")
            swipeRefreshTask.isRefreshing = false   // 取消刷新状态
        }
    }

    override fun initEvent(rootView: View) {
        initClickListener()

        initObserve()
    }

    private fun initClickListener() {
        adapter.taskClickListener = object : TaskClickListener {
            override fun onTaskClick(task: Task, card: MaterialCardView) {
                Log.d(Constants.MAIN_PAGE_TAG, "click item card; id=${task.id}")
                val intent = Intent(MyToDoApplication.context, EditTaskActivity::class.java).apply {
                    putExtra(Constants.TASK,task)
                    putExtra(Constants.PROJECT_NAME,projectName)
                }
                startActivity(intent)
            }

            override fun onTaskDoneClick(task: Task) {
                Log.d(Constants.MAIN_PAGE_TAG, "done item; id=${task.id}")
            }

            override fun onTaskDoingClick(task: Task) {
                Log.d(Constants.MAIN_PAGE_TAG, "doing item; id=${task.id}")
            }
        }
    }


    private fun initObserve() {

        taskViewModel.tasksLiveData.observe(this) { result ->
            val taskList = result.getOrNull()
            if (taskList != null) {
                Log.d(Constants.TASK_PAGE_TAG, "任务刷新成功，任务列表为：$taskList")
                taskRecyclerView.visibility = View.VISIBLE
                var changePosition: Int? = null
                previousList?.let {
                    if (it.size == taskList.size) {
                        for (i in taskList.indices) {
                            if (taskList[i] != it[i]) {
                                changePosition = i
                                break
                            }
                        }
                    }
                    changePosition?.let {
                        adapter.notifyItemChanged(changePosition!!)
                    }
                }
                previousList = taskList
                adapter.submitList(taskList)
                baseActivity.onSearchResultListener(taskList)
            } else {
                "未能查询到任务".showToast()
                result.exceptionOrNull()?.printStackTrace()
            }
        }


        taskViewModel.insertTaskLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
                refreshList("insert observe")   // 刷新界面
            } else {
                result.exceptionOrNull()?.printStackTrace()
                "添加异常".showToast()
            }
        }

        taskViewModel.updateTaskLiveDate.observe(this) { result ->
            if (result.getOrNull() != null) {
                refreshList("update observe")   // 刷新界面
            } else {
                "更新异常".showToast()
            }
        }
        taskViewModel.startTaskLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
//                refreshList("set start observe")   // 刷新界面
            } else {
                "更新异常".showToast()
            }
        }

        taskViewModel.delTaskLiveData.observe(this) { result ->
            "删除完成".showToast()
            if (result.getOrNull() != null) {
                refreshList("del observe")   // 刷新界面
            } else {
                "更新异常".showToast()
            }
        }
    }



    /**
     * TODO
     * 入门、杂货、计划内、已分配等模块还没有相关数据
     */
    fun refreshList(log: String) {
        Log.d(Constants.TASK_PAGE_TAG, "source:$log")
        val searchArg = SearchArg(Cmd.SEARCH_BY_PID)
        searchArg.value = projectId
        if (projectId == 0L) {
            when (projectSign) {
                ProjectSign.SEARCH -> {
                    searchArg.cmd = Cmd.SEARCH_LIKE_NAME
                    searchArg.value = searchName
                }
                ProjectSign.ONE_DAY -> {
                    searchArg.cmd = Cmd.SEARCH_ONE_DAY
                }
                ProjectSign.START -> {
                    // 查询start任务
                    searchArg.cmd = Cmd.SEARCH_IMPORTANT
                }
                ProjectSign.PLAN -> {}
                ProjectSign.ASSIGNED -> {}
                ProjectSign.INTRO -> {}
                ProjectSign.ZAHUO -> {}
                ProjectSign.TASKS -> {
                    // 查询所有任务
                    searchArg.cmd = Cmd.SEARCH_ALL
                    Log.d(Constants.TASK_PAGE_TAG, "search all tasks")
                }
                else -> throw IllegalArgumentException("ProjectSign not match,projectSign=$projectSign")
            }
        }
        taskViewModel.searchTasks(searchArg)
    }

}