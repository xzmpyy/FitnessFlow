package com.example.fitnessflow.utils_class

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.example.fitnessflow.R

//参数是弹窗位置，Gravity.bottom等及子视图
class MyDialogFragment (private val gravity: Int,private val childView:View):DialogFragment(){

    private var parentLayout:LinearLayout? = null

    //设置Fragment宽高
    override fun onStart(){
        super.onStart()
        //屏幕宽度
        val windowWidth=ScreenInfoClass.getScreenWidthDP(this.context!!)
        val dialogWindow=dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        dialogWindow.setLayout(windowWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
        //dialog的位置
        dialogWindow.setGravity(gravity)
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
    }

}