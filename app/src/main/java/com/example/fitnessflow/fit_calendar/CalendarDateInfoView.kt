package com.example.fitnessflow.fit_calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.fitnessflow.R


class CalendarDateInfoView (context: Context, set:AttributeSet):View(context,set){

    constructor(context: Context,set:AttributeSet,year: Int, month:Int):this(context, set){
        this.year = year
        this.month = month
    }

    private val paint= Paint()
    //不同画笔的颜色
    private var naturalTextColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private var backGroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var year:Int?=null
    private var month:Int?=null
    private var monthTextCenterY:Float? = null
    private var yearTextCenterY:Float? = null
    private var monthTextCenterX:Float? = null
    private var yearTextCenterX:Float? = null
    private val monthAbbreviation = resources.getStringArray(R.array.month)
    private var strokeWidth = 4f

    init{
        //去锯齿
        paint.isAntiAlias=true
        //填充风格
        paint.style=Paint.Style.FILL_AND_STROKE
        //设置文字基点为中心点
        paint.textAlign=Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.textSize = (this.width/21).toFloat()
        //画笔颜色
        paint.color = naturalTextColor
        //线宽
        paint.strokeWidth=strokeWidth
        canvas!!.drawColor(backGroundColor)
        monthTextCenterY = this.width.toFloat()/14f + (this.width/42).toFloat()
        yearTextCenterY = monthTextCenterY
        monthTextCenterX = this.width.toFloat()/4*1.5f
        yearTextCenterX = this.width.toFloat()/4*2.5f
        canvas.drawText(monthAbbreviation[month!!-1],monthTextCenterX!!,monthTextCenterY!!,paint)
        canvas.drawText(year.toString(), yearTextCenterX!!,yearTextCenterY!!,paint)

    }

    //设置高度自适应
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = widthSize/7
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }

    }

    fun setMothText(month:Int){
        this.month = month
    }

    fun setYearText(year:Int){
        this.year = year
    }

}