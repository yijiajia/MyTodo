package com.example.mytodo.logic

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 显示软键盘
 * postDelayed：避免界面还没绘制完毕就请求焦点导致不弹出键盘
 */
fun View.showSoftInput(flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    postDelayed({
        requestFocus()
        val inManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inManager.showSoftInput(this, flags)
    },100)
}

/**
 * 隐藏软键盘
 */
fun View.hideSoftInputFromWindow(flags: Int = 0) {
    val inManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inManager.hideSoftInputFromWindow(this.windowToken, flags)
}