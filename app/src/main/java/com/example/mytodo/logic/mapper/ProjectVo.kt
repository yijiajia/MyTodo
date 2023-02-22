package com.example.mytodo.logic.mapper

import com.example.mytodo.logic.domain.constants.ProjectSign

data class ProjectVo(
    var sign: ProjectSign,
    var num : Int,
    var imageId : Int,
)