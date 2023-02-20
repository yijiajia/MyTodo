package com.example.mytodo.logic

enum class Cmd {
    /**
     * 根据project_id查询
     */
    SEARCH_BY_PID,

    /**
     * 查询所有
     */
    SEARCH_ALL,

    /**
     * 查询重要的
     */
    SEARCH_IMPORTANT,

    /**
     * 查询计划内的
     */
    SEARCH_PLAN,


    /**
     * 兼容报错的
     */
    NULL

}