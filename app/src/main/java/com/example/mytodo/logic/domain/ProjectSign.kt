package com.example.mytodo.logic.domain

import com.example.mytodo.R
import java.io.Serializable

enum class ProjectSign( val signName: String,val imageId : Int) : Serializable {

    INTRO("入门", R.drawable.rumeng),
    ZAHUO("杂货", R.drawable.zahuo),

    ONE_DAY("我的一天", R.drawable.time),
    START("重要", R.drawable.zhongyao),
    PLAN("计划内", R.drawable.plan),
    ASSIGNED("已分配", R.drawable.tome),
    TASKS("任务",R.drawable.task)

}