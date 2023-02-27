package com.example.mytodo.logic.utils

object FlagHelper {

    /**
     * 添加标识
     */
    fun addFlag(flag: Int, newFlag : Int) : Int {
        return flag.or(newFlag)
    }

    /**
     * 移除标识
     */
    fun removeFlag(flag: Int, newFlag: Int) : Int {
        return flag.and(newFlag.inv())
    }

    /**
     * 判断是否包含该标识
     */
    fun containsFlag(flag: Int, checkFlag: Int) : Boolean {
        return flag.and(checkFlag) == checkFlag
    }
}