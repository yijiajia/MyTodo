package com.example.mytodo.logic

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * 输出文案表示
 * 格式：MM月dd日周几
 */
fun LocalDateTime.toStringDesc() : String {
    val formatter = DateTimeFormatter.ofPattern("MM月dd日")
    return format(formatter) + dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA).replace("星期","周")
}

/**
 * 默认now方法的时区要转为上海
 */
fun LocalDateTime.toSH() : LocalDateTime {
    val zone = ZoneId.of("Asia/Shanghai")
    return LocalDateTime.now(zone)
}

/**
 * 判断是否为空，db默认时间为1970年
 */
fun LocalDateTime.isEmptyTime(): Boolean {
    return this == LocalDateTime.ofEpochSecond(0,0, ZoneOffset.ofHours(8))
}

/**
 * 格式：上午/下午 hh:mm
 */
fun LocalDateTime.toLocalTimeName(): String {
    val hour = toLocalTime().hour
    val minute = toLocalTime().minute
    val moment = if (hour < 12) {
        "上午"
    } else if (hour == 12) {
        "中午"
    } else {
        "下午"
    }
    return "$moment$hour:$minute"
}