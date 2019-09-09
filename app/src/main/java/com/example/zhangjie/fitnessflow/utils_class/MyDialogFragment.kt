package com.example.zhangjie.fitnessflow.utils_class

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.example.zhangjie.fitnessflow.R

//参数是弹窗位置，Gravity.bottom等、宽度wrap_content还是撑满屏幕及子视图
class MyDialogFragment (private val gravity: Int,private val widthType:Int,private val childView:View):DialogFragment(){

    private var parentLayout:LinearLayout? = null
    private var confirmButtonClickListener:ConfirmButtonClickListener?=null

    //设置Fragment宽高
    override fun onStart(){
        super.onStart()
        val dialogWindow=dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        //屏幕宽度,0撑满屏幕，1按内容宽度
        when(widthType){
            0->{
                val windowWidth=ScreenInfoClass.getScreenWidthDP(this.context!!)
                dialogWindow.setLayout(windowWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                //dialog的位置
                dialogWindow.setGravity(gravity)
            }
            1->{
                dialogWindow.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                //dialog的位置
                dialogWindow.setGravity(gravity)
            }
        }
    }

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        //设置动画
        dialog.window!!.setWindowAnimations(R.style.dialog_pop)
        return inflater.inflate(R.layout.fragment_my_dialog,container,true)
    }

    //视图初始化
    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        parentLayout = view.findViewById(R.id.parent_layout)
        parentLayout!!.addView(childView)
        //取消按钮
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            this.dismiss()
        }
        //去人按钮
        view.findViewById<Button>(R.id.confirm_button).setOnClickListener {
            if (confirmButtonClickListener!=null){
                confirmButtonClickListener!!.onConfirmButtonClick()
            }
        }
    }

    interface ConfirmButtonClickListener{
        fun onConfirmButtonClick()
    }

    fun setConfirmButtonClickListener(confirmButtonClickListener:ConfirmButtonClickListener){
        this.confirmButtonClickListener =confirmButtonClickListener
    }

}