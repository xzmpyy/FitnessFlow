package com.example.zhangjie.fitnessflow.splash

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.utils_class.ScreenInfoClass

class TransitionBackgroundView (context: Context, set:AttributeSet): View(context,set){

    private val marginBottom = resources.getDimension(R.dimen.viewMargin)
    private val paint = Paint()
    private val myPaintMode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    private val colorRed = ContextCompat.getColor(context, R.color.primaryRed)
    private val shadowColor = ContextCompat.getColor(context, R.color.primaryShadowColor)
    private val processBarHeight = ScreenInfoClass.sp2dp(resources.getDimension(R.dimen.biggestTextSize).toInt(),context).toFloat()
    private var upperRectWidth:Float? = null
    private var lowerRectWidth:Float? = null
    private var upperProcessBarWidth:Float? = null
    private var lowerProcessBarWidth:Float? = null
    private var upperStartY:Float? = null
    private var lowerStartY:Float? = null
    //双缓冲
    private var bitmap: Bitmap? = null
    private val bitmapCanvas = Canvas()

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = shadowColor
        paint.xfermode = myPaintMode
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (upperRectWidth == null){
            upperRectWidth = this.width.toFloat()
            lowerRectWidth = this.width.toFloat()/4*3
            upperProcessBarWidth = upperRectWidth!!/4*3
            lowerProcessBarWidth = lowerRectWidth!!/4*3
            upperStartY = marginBottom
            lowerStartY = marginBottom*2 + processBarHeight
            bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888)
            bitmapCanvas.setBitmap(bitmap)
            //绘制阴影
            paint.color = shadowColor
            bitmapCanvas.drawRect(0f,upperStartY!!,upperRectWidth!!,processBarHeight + upperStartY!!,paint)
            bitmapCanvas.drawRect(0f,lowerStartY!!,lowerRectWidth!!,processBarHeight + lowerStartY!!,paint)
            //绘制红矩形，右边下边各留出一像素作为阴影
            paint.color = colorRed
            bitmapCanvas.drawRect(0f,upperStartY!!,upperRectWidth!!-4f,processBarHeight + upperStartY!!-4f,paint)
            bitmapCanvas.drawRect(0f,lowerStartY!!,lowerRectWidth!!-4f,processBarHeight + lowerStartY!!-4f,paint)
        }
        canvas!!.drawBitmap(bitmap!!,0f,0f,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = (processBarHeight+marginBottom)*2
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height.toInt())
        }
    }


}