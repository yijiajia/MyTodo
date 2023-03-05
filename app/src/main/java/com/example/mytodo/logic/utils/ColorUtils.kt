package com.example.mytodo.logic.utils

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.example.mytodo.MyToDoApplication
import com.example.mytodo.R

object ColorUtils {

    fun setColorHint(resId: Int, context: Context = MyToDoApplication.context) = ColorStateList.valueOf(ContextCompat.getColor(context, resId))

}