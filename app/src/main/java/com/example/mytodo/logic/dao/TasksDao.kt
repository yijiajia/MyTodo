package com.example.mytodo.logic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mytodo.logic.domain.entity.Task

@Dao
interface TasksDao {

    @Insert
    fun insertTask(task: Task) : Long

    @Query("select * from task where projectId = :projectId")
    fun searchTasksByProjectId(projectId : Long) : List<Task>

    @Query("select * from task")
    fun searchAllTasks() : List<Task>

    @Query("select * from task where isStart = 1 and state = 0")
    fun searchImportantTasks() : List<Task>

    @Query("delete from task where id = :id")
    fun deleteTaskById(id : Long)

    @Query("delete from task where projectId = :projectId")
    fun deleteTaskByProjectId(projectId : Long)

    @Query("update task set state = :state where id = :id")
    fun updateTaskState(id: Long, state : Int)

    @Query("update task set isStart = :isStart where id = :id")
    fun updateTaskStart(id: Long, isStart: Boolean)


}