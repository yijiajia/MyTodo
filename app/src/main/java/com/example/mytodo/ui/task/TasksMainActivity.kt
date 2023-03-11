package com.example.mytodo.ui.task

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
import androidx.lifecycle.ViewModelStoreOwner
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.ProjectSign
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.domain.constants.TaskState
import com.example.mytodo.logic.hideSoftInputFromWindow
import com.example.mytodo.logic.showSoftInput
import com.example.mytodo.logic.showToast
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksMainActivity : BaseTaskActivity() {

    private lateinit var addTasksFab : FloatingActionButton
    private lateinit var addTaskLayout : ConstraintLayout
    private lateinit var addTaskBtn: ImageButton
    private lateinit var editTaskName : EditText

    private var projectId = 0L
    private var projectSign : ProjectSign? = null
    private lateinit var taskViewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_main)
        viewModelOwner = this

        val toolbar: Toolbar = findViewById(R.id.tasks_tool_bar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 显示home图标

        val projectName = intent.getStringExtra(Constants.PROJECT_NAME).toString()
        projectId = intent.getLongExtra(Constants.PROJECT_ID, 0L)
        val serializable = intent.getSerializableExtra(Constants.PROJECT_SIGN)
        if (serializable != null) {
            projectSign = serializable as ProjectSign
        }

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.tasks_collapsingToolbar)
        collapsingToolbar.title = projectName

        val tasksFragment = supportFragmentManager.findFragmentById(R.id.tasksFragment) as TasksFragment
        taskViewModel = tasksFragment.taskViewModel

        addTasksFab =   findViewById(R.id.add_task_fab)
        editTaskName =  findViewById(R.id.edit_task_name)
        addTaskLayout =  findViewById(R.id.addTaskLayout)
        addTaskBtn =  findViewById(R.id.add_task_btn)

        addTasksFab.setOnClickListener {
            addTaskLayout.visibility = View.VISIBLE
            addTasksFab.visibility = View.GONE
            editTaskName.showSoftInput()
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
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(Constants.TASK_PAGE_TAG,"on resume")
        // TODO refresh fragment
    }

    override fun onDestroy() {
        viewModelOwner = null
        super.onDestroy()
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

    private fun insertTask() {
        if (editTaskName.text.toString().isNotEmpty()) {
            val newTask = Task(
                editTaskName.text.toString(),
                TaskState.DOING,
                projectId
            )
            if (projectId == 0L) {
                var flag = 0
                when(projectSign) {
                    ProjectSign.ONE_DAY -> flag = Task.IN_ONE_DAY
                    ProjectSign.START -> flag = Task.IS_START
                    else -> false
                }
                newTask.flag = flag
            }
            editTaskName.text.clear()
            taskViewModel.insert(newTask)

            editTaskName.hideSoftInputFromWindow()

            addTaskLayout.visibility = View.GONE
            addTasksFab.visibility = View.VISIBLE
        } else {
            "输入框为空".showToast()
        }
    }

    companion object {
        var viewModelOwner : ViewModelStoreOwner? = null
    }

}