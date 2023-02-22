package com.example.mytodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.ui.project.CustomProjectAdapter
import com.example.mytodo.ui.project.DefaultProjectAdapter
import com.example.mytodo.logic.domain.constants.ProjectSign
import com.example.mytodo.logic.mapper.ProjectVo
import com.example.mytodo.logic.showToast
import com.example.mytodo.ui.project.AddProjectDiaLogFragment
import com.example.mytodo.ui.project.ProjectViewModel
import com.google.android.material.bottomappbar.BottomAppBar

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(ProjectViewModel::class.java) }
    private lateinit var adapter : CustomProjectAdapter
    private lateinit var defaultAdapter : DefaultProjectAdapter

    private var mPosition = -1

    companion object {
        const val DEFAULT_NUM = 0 // 默认的任务数
    }

    private var defaultList = mutableListOf(
        ProjectVo(ProjectSign.ONE_DAY, 1, R.drawable.time),
        ProjectVo(ProjectSign.START, 0, R.drawable.zhongyao),
        ProjectVo(ProjectSign.PLAN, 0, R.drawable.plan),
        ProjectVo(ProjectSign.ASSIGNED, 0, R.drawable.tome)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(Constants.MAIN_PAGE_TAG,"onCreate ")
        val defaultProjectRecyclerView: RecyclerView = findViewById(R.id.default_project)
        val customProjectRecyclerView: RecyclerView = findViewById(R.id.custom_project)
        val searchTasks: ImageView = findViewById(R.id.search)
        val bottomBar : BottomAppBar = findViewById(R.id.bottom_app_bar)

        defaultAdapter = DefaultProjectAdapter(this, viewModel.defaultProjectList)
        defaultProjectRecyclerView.layoutManager = LinearLayoutManager(this)
        defaultProjectRecyclerView.adapter = defaultAdapter

        adapter = CustomProjectAdapter(this, viewModel.projectList,viewModel)
        customProjectRecyclerView.layoutManager = LinearLayoutManager(this)
        customProjectRecyclerView.adapter = adapter

        searchTasks.setOnClickListener {
            Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show()
        }
        bottomBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.addProject -> {
                    Log.d(Constants.MAIN_PAGE_TAG,"准备添加项目")
                    val dialogFragment = AddProjectDiaLogFragment(viewModel)
                    dialogFragment.show(supportFragmentManager,"addProjectFragment")
                    true
                }else -> false
            }
        }
        refreshObserve()

        saveLiveDataObserve()

        delLiveDataObserve()

    }

    override fun onStart() {
        super.onStart()
        Log.d(Constants.MAIN_PAGE_TAG,"onStart ")
    }

    override fun onResume() {
        super.onResume()
        viewModel.searchDefaultProjectNum()
        viewModel.searchProjectList()
        Log.d(Constants.MAIN_PAGE_TAG,"onResume ")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_task -> {
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun refreshObserve() {

        // 刷新自定义创建的Project 以及 默认Project中的所有任务
        viewModel.projectLiveData.observe(this) { result ->
            var projectList = result
            if (projectList != null) {
                viewModel.projectList.clear()
                projectList = projectList
                Log.d(Constants.MAIN_PAGE_TAG,"project has changed,size=${projectList.size}, new projectList=$projectList")
                viewModel.projectList.addAll(projectList)
                adapter.notifyDataSetChanged()

                var taskSize = 0
                projectList.forEach{
                    taskSize +=it.num
                }
                taskSize -= DEFAULT_NUM
                val tasksProject = viewModel.defaultProjectList.find { projectVo -> ProjectSign.TASKS == projectVo.sign }
                if (tasksProject != null) {
                    if(tasksProject.num != taskSize) {
                        tasksProject.num = taskSize
                    }
                }else {
                    viewModel.defaultProjectList.add( ProjectVo(ProjectSign.TASKS, taskSize, ProjectSign.TASKS.imageId))
                }
                defaultAdapter.notifyDataSetChanged()
            } else {
                "查询为空".showToast()
            }
        }

        // 刷新默认Project中的任务数
        viewModel.searchLiveData.observe(this) { result ->
            Log.d(Constants.MAIN_PAGE_TAG,"default project num has update : result=$result")
            if (result != null) {
                val project = viewModel.defaultProjectList.find { projectVo -> ProjectSign.START == projectVo.sign  }
                project?.num = result.startNum
                defaultAdapter.notifyDataSetChanged()
            }
        }

    }

    /**
     * 新增操作检测
     */
    private fun saveLiveDataObserve() {
        viewModel.insertProjectLiveData.observe(this) { result ->
            Log.d(Constants.MAIN_PAGE_TAG,"insert project suc，ready to flush page")
            viewModel.searchProjectList()   // 更新
        }
    }

    /**
     * 删除操作监测
     */
    private fun delLiveDataObserve() {
        viewModel.delLiveData.observe(this) { id ->
            Log.d(Constants.MAIN_PAGE_TAG,"del project $id is suc, flush page")
            viewModel.searchProjectList()   // 更新
        }
    }

}