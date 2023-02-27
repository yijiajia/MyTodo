package com.example.mytodo.ui.task

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.TaskState
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.showToast
import com.example.mytodo.logic.toStringDesc
import com.example.mytodo.logic.utils.FlagHelper
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class EditTaskActivity : AppCompatActivity() {

    private val taskViewModel by lazy {  ViewModelProvider(TasksMainActivity.viewModelOwner!!).get(TasksViewModel::class.java) }
    private lateinit var task : Task
    private lateinit var projectName : String

    private lateinit var addToOneDayIcon : ImageView
    private lateinit var addToOneDayHint : TextView

    private var curFlagState : Boolean = false

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

        initObserve()
    }

    override fun onDestroy() {
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


    private fun init() {
        val projectNameTxt : TextView = findViewById(R.id.edit_task_projectName)
        val nameText : MaterialTextView = findViewById(R.id.task_name_text_view)
        val checkTaskBtn : ImageButton = findViewById(R.id.check_task_button)
        val setTaskStartBtn : ImageButton = findViewById(R.id.set_task_important)
        val createTimeTxt : TextView = findViewById(R.id.edit_task_createTime)
        addToOneDayIcon = findViewById(R.id.addToOneDayIcon)
        addToOneDayHint = findViewById(R.id.addToOneDayHint)

        projectNameTxt.text = projectName
        createTimeTxt.text = "创建于" + task.createTime.toStringDesc()
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
        if(FlagHelper.containsFlag(task.flag, Task.IS_START)) {
            setTaskStartBtn.setImageResource(R.drawable.ic_shoucang_check)
        }

        curFlagState = FlagHelper.containsFlag(task.flag, Task.IN_ONE_DAY)
        setOneDayStyle(curFlagState)
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
                             taskViewModel.delTask(task)
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

        val addToOneDayCard : MaterialCardView = findViewById(R.id.addToOneDayCard)
        addToOneDayCard.setOnClickListener {
            curFlagState = !curFlagState
            // 更新数据
            Log.d(Constants.TASK_PAGE_TAG,"准备更新flag - hh，id=${task.id}, curFlagState=$curFlagState")
            taskViewModel.setToOneDay(task.id, task.flag, curFlagState)
            setOneDayStyle(curFlagState)
        }
    }

    private fun initObserve() {
        taskViewModel.oneDayLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
                Log.d(Constants.TASK_PAGE_TAG,"set in oneDay is ok")
            }
        }
    }

    private fun setOneDayStyle(inOneDay: Boolean) {
        runOnUiThread {
            if (inOneDay) {
                addToOneDayIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue))
                addToOneDayHint.text = "已添加到“我的一天”"
                addToOneDayHint.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue)))
            }else {
                addToOneDayIcon.imageTintList = null
                addToOneDayHint.text = "添加到“我的一天”"
                addToOneDayHint.setHintTextColor(null)
            }
        }
    }
}