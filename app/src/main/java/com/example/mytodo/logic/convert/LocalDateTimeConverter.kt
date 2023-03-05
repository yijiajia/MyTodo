package com.example.mytodo.logic.convert

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeConverter {

    @TypeConverter
    fun dateTimeToLong(time : LocalDateTime) = time.toEpochSecond(ZoneOffset.ofHours(8))


    @TypeConverter
    fun longToDateTime(time: Long) =  LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8))


    @TypeConverter
    fun localDateToLong(time : LocalDate) = time.toEpochDay()


    @TypeConverter
    fun longToLocalDate(time: Long) =  LocalDate.ofEpochDay(time)
}