package com.example.mytodo.logic.convert

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeConverter {

    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateTimeToLong(time : LocalDateTime) = time.toEpochSecond(ZoneOffset.ofHours(8));


    @TypeConverter
    @RequiresApi(Build.VERSION_CODES.O)
    fun longToDateTime(time: Long) =  LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
}