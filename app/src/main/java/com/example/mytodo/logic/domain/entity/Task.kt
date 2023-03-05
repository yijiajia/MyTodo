package com.example.mytodo.logic.domain.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.mytodo.logic.toSH
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
data class Task(
    var name : String,
    var description : String?,
    var state: Int,
    var projectId : Long,
    var flag : Int,
    var createTime : LocalDateTime,
    var remindTime: LocalDateTime?,
    var endTime: LocalDate?,
    var isRepeat: Boolean
) : Serializable {
    @Ignore
    constructor()
            : this("", "", 0, 0, 0, LocalDateTime.now().toSH(), null, null, false)
    @Ignore
    constructor(  name : String,
                  state: Int,
                  projectId : Long)
            : this(name, "", state, projectId, 0, LocalDateTime.now().toSH(), null, null, false)


    @PrimaryKey(autoGenerate = true)
    var id : Long = 0

    override fun hashCode(): Int {
        return Objects.hash(id,name)
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            !is Task -> false
            else -> this === other
                    || (id == other.id
                        && name == other.name
                        && flag == other.flag
                        &&  state == other.state
                        && description == other.description
                        && remindTime == other.remindTime
                        && endTime == other.endTime
                        && isRepeat == other.isRepeat)
        }
    }


    /**
     * Flag 常量
     */
    companion object {
        /** 设为重要的 **/
        const val IS_START = 1
        /** 设为我的一天 **/
        const val IN_ONE_DAY = 2
    }
}