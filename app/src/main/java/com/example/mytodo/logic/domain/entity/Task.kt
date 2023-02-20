package com.example.mytodo.logic.domain.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Task(
    var name : String,
    var description : String?,
    var state: Int,
    var projectId : Long,
    var isStart : Boolean
) {
    @Ignore
    constructor() : this("", "", 0, 0, false)
    @Ignore
    constructor(  name : String,
                  state: Int,
                  projectId : Long) : this(name, "", state, projectId, false)


    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}