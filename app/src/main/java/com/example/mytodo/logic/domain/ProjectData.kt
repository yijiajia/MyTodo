package com.example.mytodo.logic.domain

import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task

data class ProjectData(val project: Project, val tasks: List<Task>)