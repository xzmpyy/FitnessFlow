package com.example.zhangjie.fitnessflow.utils_class

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.library.library_child_fragments.MuscleGroupItemAddFormView

//参数是弹窗类型，弹窗位置，Gravity.bottom等、宽度wrap_content还是撑满屏幕及子视图
class MyDialogFragment (private val dialogType:Int,private val gravity: Int,private val widthType:Int,
                        private val childView:View):DialogFragment(),MuscleGroupItemAddFormView.DialogDismissListener{

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
                dialogWindow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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
        //0只有确认按钮，1动作编辑，2名称编辑，3日历选择
        parentLayout = view.findViewById(R.id.parent_layout)
        parentLayout!!.addView(childView)
        when(dialogType){
            0->{
                view.findViewById<Button>(R.id.cancel_button).visibility = LinearLayout.GONE
                view.findViewById<Button>(R.id.confirm_button).setOnClickListener {
                    this.dismiss()
                }
            }
            1->{
                (childView as MuscleGroupItemAddFormView).setDialogDismissListener(this)
                //取消按钮
                view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                    this.dismiss()
                }
                //确认按钮
                view.findViewById<Button>(R.id.confirm_button).setOnClickListener {
                    this.view!!.focusable = View.FOCUSABLE
                    this.view!!.isFocusableInTouchMode = true
                    this.view!!.requestFocus()
                    //收起键盘
                    val imm: InputMethodManager = this.context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(this.view!!.windowToken, 0)
                    if (confirmButtonClickListener!=null){
                        confirmButtonClickListener!!.onConfirmButtonClick()
                    }
                }
                this.view!!.setOnTouchListener { v, _ ->
                    this.view!!.focusable = View.FOCUSABLE
                    this.view!!.isFocusableInTouchMode = true
                    this.view!!.requestFocus()
                    //收起键盘
                    val imm: InputMethodManager = v!!.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    false
                }
            }
            2->{
                //取消按钮
                view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                    this.dismiss()
                }
                //确认按钮
                view.findViewById<Button>(R.id.confirm_button).setOnClickListener {
                    this.view!!.focusable = View.FOCUSABLE
                    this.view!!.isFocusableInTouchMode = true
                    this.view!!.requestFocus()
                    //收起键盘
                    val imm: InputMethodManager = this.context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(this.view!!.windowToken, 0)
                    if (confirmButtonClickListener!=null){
                        confirmButtonClickListener!!.onConfirmButtonClick()
                    }
                }
            }
        }
    }

    interface ConfirmButtonClickListener{
        fun onConfirmButtonClick()
    }

    fun setConfirmButtonClickListener(confirmButtonClickListener:ConfirmButtonClickListener){
        this.confirmButtonClickListener =confirmButtonClickListener
    }

    override fun dialogDismiss() {
        this.dismiss()
    }


}