package com.foodapp.app.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foodapp.app.utils.Common

abstract class BaseFragmnet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(setView(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Init(view)
    }

    protected abstract fun setView(): Int
    protected abstract fun Init(view: View)

    open fun openActivity(destinationClass: Class<*>?) {
        startActivity(Intent(activity, destinationClass))
    }
    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!, false)
    }
}



