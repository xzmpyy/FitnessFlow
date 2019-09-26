package com.example.zhangjie.fitnessflow.plan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
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
    private val textColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private var textSize = 0f
    private val strokeWidth = 1f
    private var eachBarWidth = 0f
    private var maxBarHeight = 0f
    private val paddingInView = context.resources.getDimension(R.dimen.viewPaddingHorizontal)
    private var maxNum = 0

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = strokeWidth
        paint.color = redColor
        //设置文字基点为中心点
        paint.textAlign=Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in 0 until detailList!!.size) {
            //绘制红矩阵
            val barTopPosition = if (detailList!![i].num <= 0) {
                maxBarHeight
            } else {
                (1f - (detailList!![i].num.toFloat() / maxNum.toFloat())) * maxBarHeight
            }
            val barLeftPosition = (i + 1) * paddingInView + (i) * eachBarWidth
            canvas!!.drawRect(
                barLeftPosition,
                barTopPosition,
                barLeftPosition + eachBarWidth,
                maxBarHeight,
                paint
            )
            //绘制绿矩阵
            if (detailList!![i].done != 0) {
                paint.color = greenColor
                val greenBarTopPosition =
                    (1f - (detailList!![i].done.toFloat() / maxNum.toFloat())) * maxBarHeight
                canvas.drawRect(
                    barLeftPosition,
                    greenBarTopPosition,
                    barLeftPosition + eachBarWidth,
                    maxBarHeight,
                    paint
                )
                paint.color = redColor
            }
            if (detailList!![i].isHadWeightUnits == 1) {
                paint.color = textColor
                val textString = detailList!![i].weight.toString()
                paint.textSize = textSize/textString.length.toFloat()
                canvas.drawText(textString,barLeftPosition+eachBarWidth/2,(maxBarHeight-barTopPosition)/2f+barTopPosition,paint)
                paint.color = redColor
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = widthSize/5
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
        eachBarWidth = if (detailList!!.size > 10){
            (widthSize.toFloat()-paddingInView*2)/(detailList!!.size)-2*strokeWidth
        }else{
            (widthSize.toFloat()-paddingInView*2)/10-2*strokeWidth
        }
        textSize = eachBarWidth
        maxBarHeight = height.toFloat()
        getMaxNum()
    }

    private fun getMaxNum(){
        for (detail in detailList!!){
            if (detail.num > maxNum){
                maxNum = detail.num
            }
        }
    }

}