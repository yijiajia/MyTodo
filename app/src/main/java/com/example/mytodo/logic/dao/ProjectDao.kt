package com.example.mytodo.logic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mytodo.logic.domain.entity.Project

@Dao
interface ProjectDao {

    @Insert
    fun insertProject(project: Project) : Long

    @Query("select * from project")
    fun getAllList() : List<Project>

    @Delete
    fun deleteProject(project: Project)

    @Query("update project set num = num + :moveNum where id = :id" )
    fun updateProjectNum(moveNum : Int,id : Long)

    @Query("delete from project where id = :id")
    fun delProjectById(id : Long)

}