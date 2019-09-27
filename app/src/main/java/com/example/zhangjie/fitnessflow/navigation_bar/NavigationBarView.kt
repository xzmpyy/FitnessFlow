package com.example.zhangjie.fitnessflow.navigation_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.TodayPlanNum
import com.example.zhangjie.fitnessflow.utils_class.OverturnAnimation
import com.example.zhangjie.fitnessflow.utils_class.ScreenInfoClass

class NavigationBarView (context:Context,set: AttributeSet):LinearLayout(context,set),
    OverturnAnimation.InterpolatedTimeListener{

    private var navigationView: View? = null
    private var navigatorList:ArrayList<TextView>? = null
    private var todayButton:TextView? = null
    private var planButton:TextView? = null
    private var libraryButton:TextView? = null
    private var mineButton:TextView? = null
    private var operationButton:ImageButton? = null
    private var selectedPosition = 1
    private val operationDrawableListId = listOf(R.drawable.play_icon,R.drawable.edit_icon,R.drawable.add_icon,R.drawable.chart_icon,R.drawable.pause_icon)
    private var refreshTextFlag = 0
    private val anim:OverturnAnimation=OverturnAnimation(400)
    private val operationButtonCenterX = (ScreenInfoClass.getScreenWidthDP(context)/10).toFloat()
    private val operationButtonCenterY = (ScreenInfoClass.sp2px(20,context)+ ScreenInfoClass.dp2px(20,context)).toFloat()/2f
    private var navigatorClickListener:NavigatorClickListener? = null
    //今日页默认状态为0，点击后为4
    private var operationButtonInTodayPageClickFlag = 0
    private var operationButtonClickListener:OperationButtonClickListener? = null

    init {
        navigationView = LayoutInflater.from(context).inflate(R.layout.navigation_button_view, this, true)
        todayButton = navigationView!!.findViewById(R.id.today)
        planButton = navigationView!!.findViewById(R.id.plan)
        libraryButton = navigationView!!.findViewById(R.id.library)
        mineButton = navigationView!!.findViewById(R.id.mine)
        operationButton = navigationView!!.findViewById(R.id.operation)
        navigatorList = arrayListOf(todayButton!!,planButton!!,libraryButton!!,mineButton!!)
        for (navigator in navigatorList!!){
            navigator.setOnClickListener {
                if (selectedPosition != navigatorList!!.indexOf(navigator)){
                    navigatorList!![selectedPosition].setTextColor(ContextCompat.getColor(context, R.color.unSelectedTextColor))
                    navigatorList!![navigatorList!!.indexOf(navigator)].setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
                    selectedPosition = navigatorList!!.indexOf(navigator)
                    operationButton!!.startAnimation(anim)
                    if (navigatorClickListener != null){
                        navigatorClickListener!!.onNavigatorClick(selectedPosition)
                    }
                }
            }
        }
        operationButton!!.setOnClickListener {
            if (selectedPosition == 0){
                when(operationButtonInTodayPageClickFlag){
                    0->{
                        if (operationButtonClickListener != null){
                            operationButtonClickListener!!.onOperationButtonClick(operationButtonInTodayPageClickFlag)
                        }
                        if (TodayPlanNum.checkFlag()){
                            operationButtonInTodayPageClickFlag = 4
                            operationButton!!.startAnimation(anim)
                        }
                    }
                    4->{
                        if (operationButtonClickListener != null){
                            operationButtonClickListener!!.onOperationButtonClick(operationButtonInTodayPageClickFlag)
                        }
                        operationButtonInTodayPageClickFlag = 0
                        operationButton!!.startAnimation(anim)
                    }
                }
            }
            else{
                if (operationButtonClickListener != null){
                    operationButtonClickListener!!.onOperationButtonClick(selectedPosition)
                }
            }
        }
        anim.baseSet(context.resources.displayMetrics.density,operationButtonCenterX,operationButtonCenterY)
        anim.setInterpolatedTimeListener(this)
    }

    private fun setOperationButtonBackground(){
        if (selectedPosition == 0){
            when (operationButtonInTodayPageClickFlag){
                0 ->{
                    operationButton!!.setImageDrawable(ContextCompat.getDrawable(context,operationDrawableListId[selectedPosition]))
                }
                4->{
                    operationButton!!.setImageDrawable(ContextCompat.getDrawable(context,operationDrawableListId[4]))
                }
            }
        }
        else{
            operationButton!!.setImageDrawable(ContextCompat.getDrawable(context,operationDrawableListId[selectedPosition]))
        }
    }

    override fun doInHalfRotateTime(time:Float){
        if(time<0.5f && refreshTextFlag==1){
            refreshTextFlag=0
        }
        if(time>0.5f&&refreshTextFlag==0){
            setOperationButtonBackground()
            refreshTextFlag=1
        }
    }

    interface NavigatorClickListener{
        fun onNavigatorClick(position:Int)
    }

    fun setNavigatorClickListener(navigatorClickListener:NavigatorClickListener){
        this.navigatorClickListener = navigatorClickListener
    }


    fun resetOperationButtonInTodayPageClickFlag(){
        if (operationButtonInTodayPageClickFlag == 4 && !TodayPlanNum.checkFlag()){
            operationButtonInTodayPageClickFlag = 4
            operationButton!!.startAnimation(anim)
        }
    }

    interface OperationButtonClickListener{
        //type值从0到3位4个页面，为4时代表点击的是pause
        fun onOperationButtonClick(position:Int)
    }

    fun setOperationButtonClickListener(operationButtonClickListener:OperationButtonClickListener){
        this.operationButtonClickListener = operationButtonClickListener
    }

}