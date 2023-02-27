package com.example.mytodo.logic

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * 输出文案表示
 */
fun LocalDateTime.toStringDesc() : String {
    val formatter = DateTimeFormatter.ofPattern("MM月dd日")
    return format(formatter) + dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
}

/**
 * 默认now方法的时区要转为上海
 */
fun LocalDateTime.toSH() : LocalDateTime {
    val zone = ZoneId.of("Asia/Shanghai")
    return LocalDateTime.now(zone)
}