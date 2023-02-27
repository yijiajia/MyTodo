package com.example.mytodo.logic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytodo.logic.domain.entity.Task

@Dao
interface TasksDao {

    @Insert
    fun insertTask(task: Task) : Long

    @Query("select * from task where projectId = :projectId")
    fun searchTasksByProjectId(projectId : Long) : List<Task>

    @Query("select * from task")
    fun searchAllTasks() : List<Task>

    @Query("select * from task where flag & :flag = :flag and state = 0")
    fun searchTasksByFlag(flag: Int) : List<Task>

    @Query("delete from task where id = :id")
    fun deleteTaskById(id : Long)

    @Query("delete from task where projectId = :projectId")
    fun deleteTaskByProjectId(projectId : Long)

    @Query("update task set state = :state where id = :id")
    fun updateTaskState(id: Long, state : Int)

    @Query("update task set flag = :flag where id = :id")
    fun updateFlag(id: Long, flag: Int)

    @Update
    fun updateTask(task: Task)

    @Query("select count(id) from task where flag&:flag = :flag and state = 0")
    fun searchCountByFlag(flag: Int) : Int
}