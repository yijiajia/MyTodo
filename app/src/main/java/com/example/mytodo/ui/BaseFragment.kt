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
        val rootView = inflater.inflate(getResourceId(), container, false)
        initView(rootView)
        initEvent(rootView)
        return rootView
    }

    open fun initView(rootView: View) {}

    open fun initEvent(rootView: View) {}



    abstract fun getResourceId() : Int


}