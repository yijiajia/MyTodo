package com.example.mytodo.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.mytodo.MyToDoApplication

/**
 * SharedPreferences 存储app的状态
 */
object SharedPreDao {

    private const val ALARM_TABLE = "alarm"
    private const val BROADCAST_TABLE = "broadCastRequest"
    private const val STATUS_TABLE = "appStatus"

    fun getInteger4Alarm(key: String, defaultValue: Int): Int{
        return sharedPreferences(ALARM_TABLE).getInt(key,defaultValue)
    }

    fun setInteger4Alarm(key: String, value: Int) {
        sharedPreferences(ALARM_TABLE).edit {
            putInt(key, value)
        }
    }

    fun saveRemindWorkInit() {
        sharedPreferences(STATUS_TABLE).edit {
            putBoolean("remindWork", true)
        }
    }

    fun setInteger4Broad(key: String, value: Int) {
        sharedPreferences(BROADCAST_TABLE).edit {
            putInt(key, value)
        }
    }

    fun getInteger4Broad(key: String) = sharedPreferences(BROADCAST_TABLE).getInt(key,0)

    fun isInitRemindWork() = sharedPreferences(STATUS_TABLE).getBoolean("remindWork",false)


    private fun sharedPreferences(tableName: String) = MyToDoApplication.context
        .getSharedPreferences(tableName, Context.MODE_PRIVATE)

}