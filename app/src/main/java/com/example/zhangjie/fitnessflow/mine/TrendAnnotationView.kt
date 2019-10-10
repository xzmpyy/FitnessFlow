package com.example.zhangjie.fitnessflow.mine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R

class TrendAnnotationView (context: Context, set: AttributeSet): View(context, set) {

    private val paint = Paint()
    private val redColor = ContextCompat.getColor(context, R.color.primaryRed)
    private val greenColor = ContextCompat.getColor(context, R.color.primaryGreen)
    private val purpleColor = ContextCompat.getColor(context, R.color.primaryButtonBackground)
    private val textColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private val strokeWidth = 12f
    private val lineWidth = context.resources.getDimension(R.dimen.iconSize)
    private val padding = context.resources.getDimension(R.dimen.viewPaddingHorizontal)*4
    private val weight = context.resources.getString(R.string.weight)
    private val fat = context.resources.getString(R.string.body_fat)
    private val bmi = context.resources.getString(R.string.bmi)
    private val textSize = context.resources.getDimension(R.dimen.smallTextSize)

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = strokeWidth
        paint.color = redColor
        paint.textSize = textSize
        //设置文字基点为中心点
        paint.textAlign=Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //体重
        canvas!!.drawLine(0f,lineWidth/2,lineWidth,lineWidth/2,paint)
        paint.color = textColor
        paint.strokeWidth = 2f
        canvas.drawText(weight,lineWidth+padding+textSize/2,lineWidth/2+textSize/2,paint)
        //体脂
        paint.color = greenColor
        paint.strokeWidth = strokeWidth
        canvas.drawLine(lineWidth+padding*2+textSize,lineWidth/2,lineWidth*2+padding*2+textSize,lineWidth/2,paint)
        paint.color = textColor
        paint.strokeWidth = 2f
        canvas.drawText(fat,lineWidth*2+padding*2+textSize+padding+textSize/2,lineWidth/2+textSize/2,paint)
        //BMI
        paint.color = purpleColor
        paint.strokeWidth = strokeWidth
        canvas.drawLine(lineWidth*2+padding*2+textSize*2+padding*2,lineWidth/2,lineWidth*3+padding*2+textSize*2+padding*2,lineWidth/2,paint)
        paint.color = textColor
        paint.strokeWidth = 2f
        canvas.drawText(bmi,lineWidth*3+padding*2+textSize*2+padding*3+textSize/2,lineWidth/2+textSize/2,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val height = context!!.resources.getDimension(R.dimen.iconSize).toInt()
        setMeasuredDimension(height*10, height)
    }

}