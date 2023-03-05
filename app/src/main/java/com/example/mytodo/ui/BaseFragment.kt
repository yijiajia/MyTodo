package com.example.mytodo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = LayoutInflater.from(container?.context).inflate(getResourceId(), container, false)
        initView(rootView)
        initEvent(rootView)
        return rootView
    }

    open fun initEvent(rootView: View) {}

    open fun initView(rootView: View) {}


    abstract fun getResourceId() : Int


}