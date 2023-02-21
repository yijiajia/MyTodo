package com.example.mytodo.logic.domain.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity
data class Task(
    var name : String,
    var description : String?,
    var state: Int,
    var projectId : Long,
    var isStart : Boolean,
//    var createTime : LocalDateTime?
) : Serializable {
    @Ignore
    constructor() : this("", "", 0, 0, false)
    @Ignore
    constructor(  name : String,
                  state: Int,
                  projectId : Long) : this(name, "", state, projectId, false)


    @PrimaryKey(autoGenerate = true)
    var id : Long = 0


    /**
     * Flag 常量
     */
    companion object {
        const val IS_START = 1
        const val IS_ONE_DAY = 2


    }
}