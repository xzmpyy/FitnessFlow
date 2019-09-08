package com.example.zhangjie.fitnessflow.fit_calendar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R

class CalendarWeekTitleView(context: Context, set:AttributeSet):View(context, set){


    private val paint= Paint()
    //每个区域所占宽高
    private var eachItemWidth:Int? = null
    private var eachItemHeight:Int? = null
    private var textSize:Float? = null
    private var viewPadding:Float? = null
    private var halfWidth:Float? = null
    //不同画笔的颜色
    private var naturalTextColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private var weekendTextColor= ContextCompat.getColor(context, R.color.primaryGreen)
    private var backGroundColor= ContextCompat.getColor(context, R.color.colorPrimary)
    //双缓冲
    private var textBitmap: Bitmap?=null
    private val bitmapCanvas=Canvas()
    //格子行列
    private var row = 0
    private var column = 0
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
        //线宽
        paint.strokeWidth=strokeWidth
        if (eachItemWidth == null){
            eachItemWidth = this.width/7
            eachItemHeight = eachItemWidth
            textSize = (eachItemWidth!!/3).toFloat()
            viewPadding = (this.width - eachItemWidth!!*7).toFloat()/2f
            halfWidth = eachItemWidth!!.toFloat()/2f
            textBitmap = null
        }
        canvas!!.drawColor(backGroundColor)
        //画笔颜色
        paint.color = weekendTextColor
        drawWeekTitle(canvas)
        column = 0
        row = 0
    }


    private fun drawWeekTitle(canvas: Canvas){
        paint.textSize = textSize!!
        val weekStringList=resources.getStringArray(R.array.week)
        for (week in weekStringList){
            if (weekStringList.indexOf(week) == 1){
                paint.color = naturalTextColor
            }
            if (weekStringList.indexOf(week) == 6){
                paint.color = weekendTextColor
            }
            textBitmap=Bitmap.createBitmap(eachItemWidth!!,eachItemHeight!!,Bitmap.Config.ARGB_8888)
            bitmapCanvas.setBitmap(textBitmap)
            textBitmap!!.setHasAlpha(true)
            val position = getBitmapCenterPosition()
            bitmapCanvas.drawText(week,halfWidth!!,halfWidth!!+textSize!!/2,paint)
            canvas.drawBitmap(textBitmap!!,position[0],position[1],paint)
            column += 1
        }
        row += 1
    }

    //获取文字的准心
    private fun getBitmapCenterPosition():FloatArray{
        val x = viewPadding!! + (column*eachItemWidth!!).toFloat()
        val y = (row*eachItemWidth!!).toFloat()
        return floatArrayOf(x,y)
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


}