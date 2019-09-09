package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.zhangjie.fitnessflow.R

class MyToast (private val context:Context, private val msg:String){

    fun showToast(){
        val toast =Toast(context)
        val toastView = View.inflate(context, R.layout.toast_view,null)
        toastView.findViewById<TextView>(R.id.toast_text).text = msg
        toast.view = toastView
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}