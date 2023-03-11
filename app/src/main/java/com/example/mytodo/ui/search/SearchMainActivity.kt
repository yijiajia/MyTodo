package com.example.mytodo.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelStoreOwner
import com.example.mytodo.R
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.showSoftInput
import com.example.mytodo.ui.task.BaseTaskActivity
import com.example.mytodo.ui.task.TasksFragment

class SearchMainActivity : BaseTaskActivity() {
    companion object {
        const val Tag = "SearchMainActivity"
        var viewModelOwner : ViewModelStoreOwner? = null
    }

    private var hideDoneTask = false

    // UI
    private lateinit var emptyListLayout : LinearLayout
    private lateinit var tasksFragmentLayout : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_main)
        viewModelOwner = this

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        emptyListLayout = findViewById(R.id.empty_list)
        tasksFragmentLayout = findViewById(R.id.tasksFragment)

        val tasksFragment = supportFragmentManager.findFragmentById(R.id.tasksFragment) as TasksFragment


        val searchContent: EditText = findViewById(R.id.searchContent)
        searchContent.showSoftInput()
        searchContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                Log.d(Tag, "改变后：s=${s.toString()}")
                // 查询接口
                tasksFragment.searchName = s.toString()
                // 刷新界面
                tasksFragment.refreshList("search")
            }
        })

    }

    override fun onDestroy() {
        viewModelOwner = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.hideDoneTask -> {
                hideDoneTask = !hideDoneTask
                item.title = if (hideDoneTask) "显示已完成项目" else {
                    "隐藏已完成项目"
                }

                // TODO 过滤结果
                if (hideDoneTask) {

                }
            }
        }
        return true
    }

    override fun onSearchResultListener(taskList: List<Task>) {
        if (taskList.isNotEmpty()) {
            emptyListLayout.visibility = View.GONE
            tasksFragmentLayout.visibility = View.VISIBLE
        }else {
            emptyListLayout.visibility = View.VISIBLE
            tasksFragmentLayout.visibility = View.GONE
        }
    }

}