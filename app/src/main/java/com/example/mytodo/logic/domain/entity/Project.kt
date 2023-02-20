package com.example.mytodo.logic.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(var title : String, var num : Int, var imageId : Int) {

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}