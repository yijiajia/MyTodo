package com.example.mytodo.ui.picker

import android.view.View
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController
import com.example.mytodo.R
import com.example.mytodo.logic.domain.DateTimeMessage
import com.example.mytodo.ui.BaseFragment
import org.greenrobot.eventbus.EventBus
import java.time.LocalDate

class DatePickerFragment : BaseFragment() {

    private lateinit var dp : DatePicker
    lateinit var localDate : LocalDate

    override fun getResourceId() = R.layout.fragment_datepicker

    override fun initView(rootView: View) {
        dp = rootView.findViewById(R.id.datePicker)
    }

    override fun initEvent(rootView: View) {
        /**
         * The month that was set (0-11) for compatibility with java.util.Calendar.
         */
       dp.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
           localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
           EventBus.getDefault().post(DateTimeMessage(localDate))
           findNavController().navigate(R.id.switchTime)
       }
    }


}