package com.example.mytodo.logic.convert

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeConverter {

    @TypeConverter
    fun dateTimeToLong(time : LocalDateTime?): Long{
        if (time == null) {
            return 0
        }
        return time.toEpochSecond(ZoneOffset.ofHours(8))
    }


    @TypeConverter
    fun longToDateTime(time: Long) =  LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8))


    @TypeConverter
    fun localDateToLong(time : LocalDate?) : Long {
        if (time == null) {
            return 0
        }
        return time.toEpochDay()
    }


    @TypeConverter
    fun longToLocalDate(time: Long) =  LocalDate.ofEpochDay(time)
}