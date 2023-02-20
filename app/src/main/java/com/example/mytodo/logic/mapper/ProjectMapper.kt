package com.example.mytodo.logic.mapper

import com.example.mytodo.logic.domain.entity.Project

object ProjectMapper {

    fun ProjectVo?.toEntity() : Project? = this?.let {
        Project(
            title = it.sign.name,
            num = it.num,
            imageId = it.imageId
        )
    }

  /*  fun Project?.toVo() : ProjectVo? = this?.let {
        ProjectVo(

        )
    }*/

}