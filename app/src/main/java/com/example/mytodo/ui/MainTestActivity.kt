package com.example.mytodo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mytodo.R

class MainTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)
        /*val diaLog = DtPickerDiaLogFragment()

        val openDateTimePicker : Button = findViewById(R.id.openDateTimePicker)
        openDateTimePicker.setOnClickListener {
            diaLog.showNow(supportFragmentManager,"datePickerDialog")
        }*/

       /* val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment

        val switchCalendar : Button = findViewById(R.id.switchCalendar)
        val switchTime : Button = findViewById(R.id.switchTime)

        switchCalendar.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.switchCalendar)
        }

        switchTime.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.switchTime)
        }
*/
    }
}