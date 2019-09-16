package com.example.zhangjie.fitnessflow.utils_class

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.zhangjie.fitnessflow.R

class ProcessDialogFragment(private val processString:String): DialogFragment(){


    //设置Fragment宽高
    override fun onStart() {
        super.onStart()
        val dialogWindow = dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        //屏幕宽度,0撑满屏幕，1按内容宽度
        dialogWindow.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //dialog的位置
        dialogWindow.setGravity(Gravity.CENTER)
    }

    //视图加载
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //设置动画
        dialog.window!!.setWindowAnimations(R.style.dialog_pop)
        return inflater.inflate(R.layout.process_view, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.process_string).text = processString
    }


}