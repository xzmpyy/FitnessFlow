package com.example.fitnessflow.splash

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.fitnessflow.R
import com.example.fitnessflow.utils_class.ScreenInfoClass

class TransitionView (context: Context, set: AttributeSet): View(context,set){

    private val marginBottom = resources.getDimension(R.dimen.viewMargin)
    private val paint = Paint()
    private val myPaintMode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    private val colorGreen = ContextCompat.getColor(context, R.color.primaryGreen)
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
        paint.color = colorGreen
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
            bitmap = Bitmap.createBitmap(this.width,this.height, Bitmap.Config.ARGB_8888)
            bitmapCanvas.setBitmap(bitmap)
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