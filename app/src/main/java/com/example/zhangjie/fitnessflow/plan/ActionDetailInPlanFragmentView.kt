package com.example.zhangjie.fitnessflow.plan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan

class ActionDetailInPlanFragmentView (context: Context, set: AttributeSet): View(context, set){

    constructor(context: Context, set: AttributeSet,action:Action, detailList:ArrayList<ActionDetailInPlan>):this(context,set){
        this.action = action
        this.detailList = detailList
    }

    private var action:Action? = null
    private var detailList:ArrayList<ActionDetailInPlan>? = null
    private val paint = Paint()
    private val redColor = ContextCompat.getColor(context, R.color.primaryRed)
    private val greenColor = ContextCompat.getColor(context, R.color.primaryGreen)
    private val strokeWidth = 4f
    private var eachBarWidth = 0f
    private var maxBarHeight = 20f

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

}