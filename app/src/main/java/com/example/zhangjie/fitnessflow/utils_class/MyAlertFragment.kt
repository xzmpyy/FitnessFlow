package com.example.zhangjie.fitnessflow.utils_class

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.example.zhangjie.fitnessflow.R

class MyAlertFragment (private val childView: View): DialogFragment(){

    private var parentLayout: LinearLayout? = null
    private var confirmButton:Button?=null
    private var confirmButtonClickListener:ConfirmButtonClickListener?=null

    //设置Fragment宽高
    override fun onStart(){
        super.onStart()
        val dialogWindow=dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        dialogWindow.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        //dialog的位置
        dialogWindow.setGravity(Gravity.CENTER)
    }

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        //设置动画
        dialog.window!!.setWindowAnimations(R.style.dialog_pop)
        return inflater.inflate(R.layout.fragment_my_dialog,container,true)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        parentLayout = view.findViewById(R.id.parent_layout)
        parentLayout!!.addView(childView)
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            this.dismiss()
        }
        confirmButton = view.findViewById(R.id.confirm_button)
        confirmButton!!.setOnClickListener {
            if (confirmButtonClickListener!=null){
                confirmButtonClickListener!!.onAlertConfirmButtonClick()
            }
            this.dismiss()
        }
    }

   interface ConfirmButtonClickListener{
       fun onAlertConfirmButtonClick()
   }

   fun setConfirmButtonClickListener(confirmButtonClickListener: ConfirmButtonClickListener){
       this.confirmButtonClickListener = confirmButtonClickListener
   }
}