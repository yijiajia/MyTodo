package com.example.mytodo.logic.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Task
import com.example.mytodo.logic.equalsUntilMinute
import com.example.mytodo.logic.repository.Repository
import com.example.mytodo.logic.toObject
import com.example.mytodo.ui.task.EditTaskActivity

class RemindAlarmReceiver: BroadcastReceiver() {


    private val channelId = "remind"
    private val channelName = "任务提醒"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RemindAlarmReceiver", "请求收到了.")
        val taskByteArray = intent.getByteArrayExtra(Constants.TASK)
        val task = taskByteArray?.toObject() as Task
        val projectName = intent.getStringExtra(Constants.PROJECT_NAME)
        Log.d("RemindAlarmReceiver","接收到任务 task=$task,projectName=$projectName")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 创建渠道
        // Android8.0 以上才有下面的API
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
//        if (LocalDateTime.now().equalsUntilMinute(task.remindTime)) {
            /* *
              * 构建消息实体：{标题，任务id，提醒时间}
              */
            val intent = Intent(context, EditTaskActivity::class.java).apply {
                putExtra(Constants.TASK, task)
                putExtra(Constants.PROJECT_NAME, projectName)
                setPackage("com.example.mytodo")
            }
            val alarmId = Repository.getAndSet4Alarm(Constants.ALARM_ID, 0)
            val pi = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(context, channelId)   // 必须传入已经创建好的渠道ID
                .setContentTitle("提醒")
                .setContentText(task.name)
                .setSmallIcon(R.drawable.todo)
                .setColor(Color.BLUE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pi)       // 设置内容点击的Intent
                .setAutoCancel(true)        // 点击后自动关闭
                .build()

            manager.notify(1, notification)
            Log.d("RemindAlarmReceiver", "通知发送成功；task=$task")
        /*}else {
            Log.d("RemindAlarmReceiver","任务的提醒时间不符合：task=$task")
        }*/
    }
}