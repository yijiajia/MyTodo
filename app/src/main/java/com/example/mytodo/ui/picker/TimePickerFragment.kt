package com.example.mytodo.ui.picker

import android.view.View
import android.widget.TimePicker
import com.example.mytodo.R
import com.example.mytodo.logic.domain.DateTimeMessage
import com.example.mytodo.ui.BaseFragment
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.LocalTime

class TimePickerFragment : BaseFragment() {
    override fun getResourceId() = R.layout.fragment_timepicker

    private lateinit var tp : TimePicker
    private lateinit var localTime: LocalTime

    override fun initView(rootView: View) {
        tp = rootView.findViewById(R.id.timePicker)
    }

    override fun initEvent(rootView: View) {
        tp.setOnTimeChangedListener { view, hourOfDay, minute ->
            localTime = LocalTime.of(hourOfDay,minute)
            EventBus.getDefault().post(DateTimeMessage(localTime))
        }
    }
}