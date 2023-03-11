package com.example.mytodo.logic.domain

import java.io.Serializable

/**
 * 页面来源
 */
enum class PageSource: Serializable {
    TASKS_MAIN, /** 任务主页 **/
    SEARCH /** 查询页 **/
}