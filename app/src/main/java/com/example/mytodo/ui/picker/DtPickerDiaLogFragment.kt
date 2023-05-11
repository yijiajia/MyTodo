package com.example.mytodo.ui.picker


import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.R
import com.example.mytodo.logic.listener.DateTimeClickListener
import com.example.mytodo.logic.domain.DateTimeMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DtPickerDiaLogFragment(private val dateTimeClick: DateTimeClickListener) : DialogFragment() {

    private var chooseDate: LocalDate? = null
    private var chooseTime: LocalTime? = null
    private var chooseDateTime : LocalDateTime? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("DtPickerDiaLogFragment","onCreateView")
        val curView = inflater.inflate(R.layout.dialog_datetime_picker, null)

        val navHostFragment : FragmentContainerView = curView.findViewById(R.id.fragment_container_view)
        val switchCalendar : Button = curView.findViewById(R.id.switchCalendar)
        val switchTime : Button = curView.findViewById(R.id.switchTime)
        val cancelDialog : TextView = curView.findViewById(R.id.cancelDialog)
        val saveDateTime : TextView = curView.findViewById(R.id.saveDateTime)

        switchCalendar.setOnClickListener {
            switchCalendar.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.light_blue))
            switchTime.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.gray))
            navHostFragment.findNavController().navigate(R.id.switchCalendar)
        }

        switchTime.setOnClickListener {
            switchCalendar.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.gray))
            switchTime.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.light_blue))
            navHostFragment.findNavController().navigate(R.id.switchTime)
        }

        cancelDialog.setOnClickListener {
            dialog?.dismiss()
        }

        saveDateTime.setOnClickListener {
            chooseDateTime = if (chooseDate == null && chooseTime == null) {
                LocalDateTime.now()
            }else if (chooseDate == null) {
                LocalDateTime.of(LocalDate.now(), chooseTime)
            }else if (chooseTime == null) {
                LocalDateTime.of(chooseDate, LocalTime.now())
            } else {
                LocalDateTime.of(chooseDate, chooseTime)
            }
            Log.d("","选中的时间为：$chooseDateTime")
            dateTimeClick.onSaveDateTimeClick(chooseDateTime!!)
            dialog?.dismiss()
        }

        // 注册
        EventBus.getDefault().register(this)
        return curView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initWindow()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)  // 注销
        super.onDestroy()
    }

    fun initWindow() {
        val window = dialog?.window
        window?.attributes?.width = 800 // 单位px
        window?.attributes?.height = 1450 // 单位px
        window?.attributes?.gravity = Gravity.CENTER    // 居中
    }

    fun getChooseTime() = chooseDateTime


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveDateTime(dateTimeMessage: DateTimeMessage) {
        if (dateTimeMessage.localDate != null) {
            chooseDate = dateTimeMessage.localDate!!
        }
        if (dateTimeMessage.localTime != null) {
            chooseTime = dateTimeMessage.localTime!!
        }
        Log.d("","接收到event消息，chooseDate=$chooseDate,chooseTime=$chooseTime")
    }

}