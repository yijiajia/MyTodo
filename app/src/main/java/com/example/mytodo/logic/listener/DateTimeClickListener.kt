package com.example.mytodo.logic.listener

import java.time.LocalDateTime

interface DateTimeClickListener {
    fun onSaveDateTimeClick(time: LocalDateTime)
}