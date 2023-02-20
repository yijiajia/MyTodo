package com.example.mytodo.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mytodo.R
import com.example.mytodo.logic.Cmd
import com.example.mytodo.logic.dao.SearchArg
import com.example.mytodo.logic.domain.Constants
import com.example.mytodo.logic.domain.ProjectSign
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.domain.TaskState
import com.example.mytodo.logic.showToast
import com.example.mytodo.ui.adapter.TasksAdapter
import com.example.mytodo.ui.viewModel.TasksViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksMainActivity : AppCompatActivity() {

    private val taskViewModel by lazy { ViewModelProvider(this).get(TasksViewModel::class.java) }
    private var projectId = 0L
    private var projectName = ""
    private var projectSign : ProjectSign? = null


    private lateinit var editTaskName : EditText
    private lateinit var addTasksFab : FloatingActionButton
    private lateinit var addTaskLayout : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_main)
        val taskRecyclerView: RecyclerView = findViewById(R.id.task_recycle_view)
        addTasksFab = findViewById(R.id.add_task_fab)
        val toolbar: Toolbar = findViewById(R.id.tasks_tool_bar)
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.tasks_collapsingToolbar)
        editTaskName = findViewById(R.id.edit_task_name)
        addTaskLayout = findViewById(R.id.addTaskLayout)
        val swipeRefreshTask: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_task)
        val addTaskBtn: ImageButton = findViewById(R.id.add_task_btn)

        projectId = intent.getLongExtra(Constants.PROJECT_ID, 0L)
        projectName = intent.getStringExtra(Constants.PROJECT_NAME).toString()
        val serializable = intent.getSerializableExtra(Constants.PROJECT_SIGN)
        if (serializable != null) {
            projectSign = serializable as ProjectSign
        }

        Log.d(Constants.TASK_PAGE_TAG, "projectId = $projectId, projectName= $projectName")
        refreshList()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 显示home图标
        collapsingToolbar.title = projectName

        val adapter = TasksAdapter(taskViewModel)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter

        taskViewModel.tasksLiveData.observe(this) { result ->
            val taskList = result.getOrNull()
            if (taskList != null) {
                Log.d(Constants.TASK_PAGE_TAG, "任务刷新成功，任务列表为：$taskList")
                taskRecyclerView.visibility = View.VISIBLE
                adapter.submitList(taskList)
            } else {
                "未能查询到任务".showToast()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

        addTasksFab.setOnClickListener {
            addTaskLayout.visibility = View.VISIBLE
            addTasksFab.visibility = View.GONE
            editTaskName.requestFocus()
            val inManager =
                editTaskName.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inManager.showSoftInput(editTaskName, InputMethodManager.SHOW_IMPLICIT)
        }

        addTaskBtn.setOnClickListener {
            insertTask()
        }

        editTaskName.setOnEditorActionListener { v, actionId, event ->
            Log.d(Constants.TASK_PAGE_TAG,"响应了回车事件，actionId=$actionId")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                insertTask()
            }
            true
        }

        taskViewModel.insertTaskLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
                refreshList()   // 刷新界面
            } else {
                "添加异常".showToast()
            }
        }

        taskViewModel.updateTaskLiveDate.observe(this) { result ->
            if (result.getOrNull() != null) {
                refreshList()   // 刷新界面
            } else {
                "更新异常".showToast()
            }
        }

        taskViewModel.startTaskLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
                refreshList()   // 刷新界面
            } else {
                "更新异常".showToast()
            }
        }

        // 下拉刷新
        swipeRefreshTask.setOnRefreshListener {
            refreshList()
            swipeRefreshTask.isRefreshing = false   // 取消刷新状态
        }

    }

    private fun insertTask() {
        if (editTaskName.text.toString().isNotEmpty()) {
            val newTask = Task(
                editTaskName.text.toString(),
                TaskState.DOING,
                projectId
            )
            editTaskName.text.clear()
            taskViewModel.insert(newTask)

            val inManager =
                editTaskName.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inManager.hideSoftInputFromWindow(editTaskName.windowToken, 0)

            addTaskLayout.visibility = View.GONE
            addTasksFab.visibility = View.VISIBLE
        } else {
            "输入框为空".showToast()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_tool_bar,menu)
        return true
    }


    private fun refreshList() {
        val searchArg = SearchArg(Cmd.SEARCH_BY_PID)
        searchArg.value = projectId
        if (projectId == 0L) {
            when (projectSign) {
                ProjectSign.ONE_DAY -> {}
                ProjectSign.START -> {
                    // 查询start任务
                    searchArg.cmd = Cmd.SEARCH_IMPORTANT
                }
                ProjectSign.PLAN -> {}
                ProjectSign.ASSIGNED -> {}
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