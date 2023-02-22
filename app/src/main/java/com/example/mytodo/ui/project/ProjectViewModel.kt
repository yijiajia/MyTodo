package com.example.mytodo.ui.project

import android.util.Log
import androidx.lifecycle.*
import com.example.mytodo.R
import com.example.mytodo.logic.Repository
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.ProjectSign
import com.example.mytodo.logic.domain.constants.ProjectSignValue
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.mapper.ProjectVo
import kotlinx.coroutines.*

class ProjectViewModel : ViewModel() {

    // 默认的Project
    var defaultProjectList = mutableListOf(
        ProjectVo(ProjectSign.ONE_DAY, 1, R.drawable.time),
        ProjectVo(ProjectSign.START, 0, R.drawable.zhongyao),
        ProjectVo(ProjectSign.PLAN, 0, R.drawable.plan),
        ProjectVo(ProjectSign.ASSIGNED, 0, R.drawable.tome)
    )
    val searchLiveData = MutableLiveData<ProjectSignValue>()
    fun searchDefaultProjectNum() {
        val projectSignValue = ProjectSignValue()
        CoroutineScope(Dispatchers.IO).launch {
            val importantTasks  = Repository.searchImportantTasks()
            val startNum = importantTasks.size
            Log.d(Constants.MAIN_PAGE_TAG,"searchDefaultProjectNum,startNum=$startNum")
            projectSignValue.startNum = startNum
            searchLiveData.postValue(projectSignValue)
        }
    }

    // 查询
    var projectList = ArrayList<Project>()
    fun searchProjectList()  {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(Constants.MAIN_PAGE_TAG,"searchProjectList invoke")
            val projectList = ArrayList<Project>()
            projectList.add(Project(ProjectSign.INTRO.signName,0, ProjectSign.INTRO.imageId))
            projectList.add(Project(ProjectSign.ZAHUO.signName,0, ProjectSign.ZAHUO.imageId))
            projectList.addAll(Repository.getProjectList())
            projectLiveData.postValue(projectList)
        }
    }
    var projectLiveData = MutableLiveData<List<Project>>()

    // 插入
    private val insertLiveData = MutableLiveData<Project>()
    fun insert(project: Project) {
        insertLiveData.value = project
    }
    val insertProjectLiveData = Transformations.switchMap(insertLiveData) {
        Repository.saveProject(it)
    }

    // 不好用感觉
    fun saveProject(project: Project) = liveData(Dispatchers.IO) {
        emit(Repository.saveProject(project))
    }

    val delLiveData = MutableLiveData<Long>()
    fun delProjectById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(Constants.MAIN_PAGE_TAG,"ProjectViewModel deleteProjecrId")
            Repository.delProjectById(id)
            delLiveData.postValue(id)
        }
    }

}