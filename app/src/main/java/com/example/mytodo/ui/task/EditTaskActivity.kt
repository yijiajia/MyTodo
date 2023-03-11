package com.example.mytodo.ui.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.mytodo.R
import com.example.mytodo.logic.*
import com.example.mytodo.logic.domain.PageSource
import com.example.mytodo.logic.domain.TaskItem
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.listener.DateTimeClickListener
import com.example.mytodo.logic.utils.ColorUtils
import com.example.mytodo.logic.utils.FlagHelper
import com.example.mytodo.logic.work.RemindWorker
import com.example.mytodo.ui.picker.DtPickerDiaLogFragment
import com.example.mytodo.ui.project.ProjectViewModel
import com.example.mytodo.ui.search.SearchMainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class EditTaskActivity : AppCompatActivity() {

//    private val taskViewModel by lazy { ViewModelProvider(TasksMainActivity.viewModelOwner!!)[TasksViewModel::class.java] }
    private lateinit var taskViewModel : TasksViewModel
    private lateinit var context: Context

    private lateinit var task : Task
    private lateinit var originTask : Task
    private lateinit var projectName : String

    private lateinit var projectNameTxt : TextView

    private lateinit var addToOneDayIcon : ImageView
    private lateinit var addToOneDayHint : TextView

    private lateinit var remindTaskIcon : ImageView
    private lateinit var remindTaskHint : TextView
    private lateinit var remindTaskLayout : LinearLayout
    private lateinit var remindTime : TextView
    private lateinit var remindDate : TextView

    private lateinit var addEndTimeIcon : ImageView
    private lateinit var addEndTimeHint : TextView


    private lateinit var nameTextEdit : EditText
    private lateinit var descTextEdit : EditText

    private lateinit var dtPicDialog : DtPickerDiaLogFragment

    private var curFlagState : Boolean = false
    private var hasEndTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        val toolbar : Toolbar = findViewById(R.id.edit_task_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        context = this

        taskViewModel = if (TasksMainActivity.viewModelOwner != null) {
            ViewModelProvider(TasksMainActivity.viewModelOwner!!)[TasksViewModel::class.java]
        }else if (SearchMainActivity.viewModelOwner != null){
            ViewModelProvider(SearchMainActivity.viewModelOwner!!)[TasksViewModel::class.java]
        }else {
            Log.d("","自定义生成viewModel")
            ViewModelProvider(this)[TasksViewModel::class.java]
        }

        task = intent.getSerializableExtra(Constants.TASK) as Task
        originTask = task.copy()
        projectName = intent.getStringExtra(Constants.PROJECT_NAME).toString()
        if (projectName.isEmpty() or ("null" == projectName)) {
            val projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
            projectViewModel.getProjectTitleById(task.projectId).observe(this) {
                projectName = it
                projectNameTxt.text = projectName
            }
        }

        init()

        initClickListener()

        initObserve()
    }

    override fun onStop() {
        super.onStop()
        task.name = nameTextEdit.text.toString()
        task.description = descTextEdit.text.toString()
        if (originTask != task) {
            taskViewModel.updateTask(task)
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


    private fun init() {
        projectNameTxt = findViewById(R.id.edit_task_projectName)
        val nameText : MaterialTextView = findViewById(R.id.task_name_text_view)
        nameTextEdit = findViewById(R.id.task_name_text_view_edit)
        descTextEdit = findViewById(R.id.edit_task_desc)
        val checkTaskBtn : ImageButton = findViewById(R.id.check_task_button)
        val setTaskStartBtn : ImageButton = findViewById(R.id.set_task_important)
        val createTimeTxt : TextView = findViewById(R.id.edit_task_createTime)
        addToOneDayIcon = findViewById(R.id.addToOneDayIcon)
        addToOneDayHint = findViewById(R.id.addToOneDayHint)

        remindTaskIcon = findViewById(R.id.remindTaskIcon)
        remindTaskHint = findViewById(R.id.remindTaskHint)
        remindTaskLayout = findViewById(R.id.remindTaskLayout)
        remindTime = findViewById(R.id.remindTime)
        remindDate = findViewById(R.id.remindDate)

        addEndTimeIcon = findViewById(R.id.addEndTimeIcon)
        addEndTimeHint = findViewById(R.id.addEndTimeHint)

        nameText.visibility = View.GONE
        nameTextEdit.visibility = View.VISIBLE

        val taskItem = TaskItem(nameText, checkTaskBtn, setTaskStartBtn, task)
        taskItem.nameTextEdit = nameTextEdit
        taskItem.initItem()
        taskItem.initClickListener(taskViewModel)

        projectNameTxt.text = projectName
        createTimeTxt.text = "创建于" + task.createTime.toStringDesc()
        descTextEdit.setText(task.description)

        curFlagState = FlagHelper.containsFlag(task.flag, Task.IN_ONE_DAY)
        setOneDayStyle(curFlagState)


        hasEndTime = !task.endTime?.isEqual(LocalDate.of(1970,1,1))!!
        Log.d(Constants.DEBUG_TAG,"task=$task")
        setRemindTimeStyle(!task.remindTime?.isEmptyTime()!!)
        setEndTimeStyle(hasEndTime)

        dtPicDialog = DtPickerDiaLogFragment(object : DateTimeClickListener {
            override fun onSaveDateTimeClick(time: LocalDateTime) {
                task.remindTime = time
                // 更新UI
               setRemindTimeStyle(true)
                val data = Data.Builder()
                    .putString(Constants.PROJECT_NAME, projectName)
                    .putByteArray(Constants.TASK_BYTE, task.toByteArray())
                    .build()
                // 设置任务，提交提醒闹钟
                val remindRequest = OneTimeWorkRequest.Builder(RemindWorker::class.java)
                    .setInputData(data)
                    .addTag(RemindWorker.Tag).build()
                WorkManager.getInstance(context)
                    .enqueue(remindRequest)
            }
        })
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

        val builder = AlertDialog.Builder(this)
        val diaLogView = LayoutInflater.from(this).inflate(R.layout.dialog_remind_menu, null)
        val remindDiaLog = builder.setView(diaLogView).create()
        val diaLogWin = remindDiaLog.window
        diaLogWin?.attributes?.width = 700
        diaLogWin?.attributes?.width = 1000
        val selectDateTime : TextView = diaLogView.findViewById(R.id.selectDateTime)
        selectDateTime.setOnClickListener {
            remindDiaLog.dismiss()
            dtPicDialog.showNow(supportFragmentManager, "DtPickerDiaLogFragment")
        }

        val remindTaskCard : MaterialCardView = findViewById(R.id.remindTaskCard)
        remindTaskCard.setOnClickListener {
            remindDiaLog.show()
        }


        val addEndTimeCard : MaterialCardView = findViewById(R.id.addEndTimeCard)
        addEndTimeCard.setOnClickListener {
            val ca = Calendar.getInstance()
            var mYear = ca[Calendar.YEAR]
            var mMonth = ca[Calendar.MONTH]
            var mDay = ca[Calendar.DAY_OF_MONTH]
            if (hasEndTime) {
                mYear = task.endTime?.year!!
                mMonth = task.endTime?.month?.value!! - 1
                mDay = task.endTime?.dayOfMonth!!
            }
            val dialog = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    val mDate = "${year}/${month + 1}/${dayOfMonth}"
                    task.endTime = LocalDate.of(year, month + 1, dayOfMonth)
                    setEndTimeStyle(true)
                    // 将选择的日期赋值给TextView
                    Log.d("", "mDate=$mDate")
                },
                mYear, mMonth, mDay
            )
            dialog.show()
        }

    }

    private fun initObserve() {
        taskViewModel.oneDayLiveData.observe(this) { result ->
            if (result.getOrNull() != null) {
                Log.d(Constants.TASK_PAGE_TAG,"set in oneDay is ok")
            }
        }

        taskViewModel.delTaskLiveData.observe(this) {
            Log.d("","监听到删除了")
        }
    }

    /**
     * 设置我的一天样式
     */
    private fun setOneDayStyle(inOneDay: Boolean) {
        runOnUiThread {
            if (inOneDay) {
                addToOneDayIcon.imageTintList = ColorUtils.setColorHint(R.color.light_blue)
                addToOneDayHint.text = "已添加到“我的一天”"
                addToOneDayHint.setHintTextColor(ColorUtils.setColorHint(R.color.light_blue))
            }else {
                addToOneDayIcon.imageTintList = null
                addToOneDayHint.text = "添加到“我的一天”"
                addToOneDayHint.setHintTextColor(null)
            }
        }
    }

    /**
     * 设置提醒我的样式
     */
    @SuppressLint("SetTextI18n")
    private fun setRemindTimeStyle(hasRemind: Boolean) {
        runOnUiThread {
            if (hasRemind) {
                remindTaskLayout.visibility = View.VISIBLE
                remindTaskHint.visibility = View.GONE
                remindTime.text = "在 ${task.remindTime?.toLocalTimeName()} 时提醒我"
                remindDate.text = task.remindTime?.toStringDesc()
                // 如果时间没过才显示颜色
                if (LocalDateTime.now().isBefore(task.remindTime)) {
                    remindTaskIcon.imageTintList = ColorUtils.setColorHint(R.color.light_blue)
                    remindTaskHint.setHintTextColor(ColorUtils.setColorHint(R.color.light_blue))
                    remindTime.setTextColor(ColorUtils.setColorHint(R.color.light_blue))
                }
            }else {
                remindTaskLayout.visibility = View.GONE
                remindTaskHint.visibility = View.VISIBLE
                remindTaskIcon.imageTintList = null
                remindTaskHint.text = "提醒我"
                remindTaskHint.setHintTextColor(null)
//                remindTime.setTextColor(null)
            }
        }
    }

    /**
     * 设置截止日期的样式
     */
    private fun setEndTimeStyle(hasEndTime: Boolean) {
        if (hasEndTime) {
            addEndTimeIcon.imageTintList = ColorUtils.setColorHint(R.color.light_blue)
            addEndTimeHint.setHintTextColor(ColorUtils.setColorHint(R.color.light_blue))
            addEndTimeHint.text = task.endTime?.toStringDesc()
        }
    }



}