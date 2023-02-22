package com.example.mytodo.ui.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.TaskState
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.showToast
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.textview.MaterialTextView

class EditTaskActivity : AppCompatActivity() {

    private val taskViewModel by lazy { TasksMainActivity.viewModelOwner?.let { ViewModelProvider(it).get(TasksViewModel::class.java) } }
    private lateinit var task : Task
    private lateinit var projectName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        val toolbar : Toolbar = findViewById(R.id.edit_task_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        task = intent.getSerializableExtra(Constants.TASK) as Task
        projectName = intent.getStringExtra(Constants.PROJECT_NAME).toString()

        init()

        initClickListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }


    private fun init() {
        val projectNameTxt : TextView = findViewById(R.id.edit_task_projectName)
        val nameText : MaterialTextView = findViewById(R.id.task_name_text_view)
        val checkTaskBtn : ImageButton = findViewById(R.id.check_task_button)
        val setTaskStartBtn : ImageButton = findViewById(R.id.set_task_important)

        projectNameTxt.text = projectName
        if (task.state == TaskState.DOING) {
            nameText.text = task.name
            checkTaskBtn.setImageResource(R.drawable.ic_select)
        }else {
            val spannableString = SpannableString(task.name)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            nameText.text = spannableString  /** 划线的效果 **/
            checkTaskBtn.setImageResource(R.drawable.ic_select_check)
        }
        if(task.isStart) {
            setTaskStartBtn.setImageResource(R.drawable.ic_shoucang_check)
        }
    }

    private fun initClickListener() {
        val bottomBar : BottomAppBar = findViewById(R.id.edit_task_bottom_app_bar)
        bottomBar.setOnMenuItemClickListener {
             when(it.itemId) {
                 R.id.deleteProject -> {
                     val builder = AlertDialog.Builder(this)
                     builder.setTitle("你确定吗？")
                         .setMessage("将永久删除“${task.name}”")
                         .setPositiveButton("删除") { dialog, _ ->
                             "删除任务222".showToast()
                             taskViewModel?.delTask(task)
                             dialog.dismiss()
                             finish()
                         }
                         .setNegativeButton("取消"){ dialog, _ ->
                             "取消了任务".showToast()
                             dialog.dismiss()
                         }
                     builder.create().show()
                     true
                 }
                 else -> false
             }
        }
    }
}