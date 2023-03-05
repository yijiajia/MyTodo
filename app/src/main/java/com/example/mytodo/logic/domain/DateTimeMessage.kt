package com.example.mytodo.logic.domain

import java.time.LocalDate
import java.time.LocalTime

class DateTimeMessage {

    var localDate: LocalDate? = null

    var localTime: LocalTime? = null

    constructor(localTime: LocalTime) {
        this.localTime = localTime
    }

    constructor(localDate: LocalDate) {
        this.localDate = localDate
    }
}