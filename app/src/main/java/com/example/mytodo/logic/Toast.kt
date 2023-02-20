package com.example.mytodo.logic

import android.content.Context
import android.widget.Toast
import com.example.mytodo.MyToDoApplication
import java.time.Duration

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(MyToDoApplication.context, this, duration).show()
}