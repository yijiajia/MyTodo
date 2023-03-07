package com.example.mytodo.logic.work

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.R
import com.example.mytodo.logic.dao.AppDatabase
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.receiver.RemindAlarmReceiver
import com.example.mytodo.logic.repository.Repository
import com.example.mytodo.logic.toByteArray
import com.example.mytodo.logic.toObject
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class RemindWorker(context: Context, params: WorkerParameters) : Worker(context, params)  {

    companion object {
        val Tag = "RemindWorker"
    }

    private lateinit var alarmManager: AlarmManager

    @RequiresApi(Build.VERSION_CODES.S)
    override fun doWork(): Result {
        /*
        val taskDao = AppDatabase.getDatabase(MyToDoApplication.context).getTaskDao()
        val beginTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
        val endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
        val taskList = taskDao.searchTasksByRemindToday(beginTime, endTime)
        */
        val taskByte = inputData.getByteArray(Constants.TASK_BYTE)
        val task = taskByte?.toObject() as Task
        val projectName = inputData.getString(Constants.PROJECT_NAME)
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.d(Tag,"需要提醒的任务为：task=$task, projectName=$projectName")
        if (LocalDateTime.now().isBefore(task.remindTime)) { // 未执行，发起广播
            alarmTask(task, projectName)
        }

        return Result.success()
    }

    private fun alarmTask(task: Task, projectName: String?) {
        val bundle = Bundle()
        bundle.putByteArray(Constants.TASK, task.toByteArray())
        bundle.putString(Constants.PROJECT_NAME, projectName)
        val intent = Intent(applicationContext, RemindAlarmReceiver::class.java).apply {
            putExtras(bundle)
        }
        val oldAlarmId = Repository.getInteger4Broad(task.id.toString())   // 找到旧的请求id，如果有值的话说明需要重设，取消旧闹钟
        var pi : PendingIntent
        if (oldAlarmId != 0 && LocalDateTime.now().isAfter(task.remindTime)) {
            // 取消闹钟，重设
            pi = PendingIntent.getBroadcast(applicationContext, oldAlarmId, intent, 0)
            alarmManager.cancel(pi)
        }
        var alarmId = Repository.getInteger4Alarm(Constants.ALARM_ID, 0)
        pi = PendingIntent.getBroadcast(applicationContext, alarmId, intent, 0)
        val triggerAtMillis = task.remindTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis , pi)

        Repository.setInteger4Broad(task.id.toString(), alarmId)
        Repository.setInteger4Alarm(Constants.ALARM_ID, ++alarmId)
        Log.d(Tag,
            "闹钟设置成功;taskName=${task.name};remindTime=${task.remindTime};;now=${System.currentTimeMillis()}"
        )
    }

    fun toMillis(alarmTime: LocalDateTime): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, alarmTime.year)
        calendar.set(Calendar.MONTH, alarmTime.monthValue - 1)
        calendar.set(Calendar.DAY_OF_MONTH, alarmTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, alarmTime.hour)
        calendar.set(Calendar.MINUTE, alarmTime.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
