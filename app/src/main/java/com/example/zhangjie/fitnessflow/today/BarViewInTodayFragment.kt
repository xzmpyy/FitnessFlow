package com.example.zhangjie.fitnessflow.today

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan

class BarViewInTodayFragment (context: Context, set: AttributeSet): View(context, set){

    constructor(context: Context, set: AttributeSet,actionDetail:ActionDetailInPlan,maxNum:Int):this(context,set){
        this.actionDetail = actionDetail
        this.maxNum = maxNum
    }

    private var actionDetail:ActionDetailInPlan? = null
    private var maxNum:Int? = null
    private val paint = Paint()
    private val redColor = ContextCompat.getColor(context, R.color.primaryRed)
    private val greenColor = ContextCompat.getColor(context, R.color.primaryGreen)
    private val strokeWidth = 1f
    private val paddingInView = context.resources.getDimension(R.dimen.viewPaddingHorizontal)
    private var barHeight:Int? = null

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = strokeWidth
        paint.color = redColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawRect(paddingInView,0f,(this.width-2*paddingInView)*(actionDetail!!.num.toFloat()/maxNum!!.toFloat())+paddingInView,barHeight!!.toFloat(),paint)
        paint.color = greenColor
        canvas.drawRect(paddingInView,0f,(this.width-2*paddingInView)*(actionDetail!!.done.toFloat()/maxNum!!.toFloat())+paddingInView,barHeight!!.toFloat(),paint)
        paint.color = redColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        barHeight = widthSize/10
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, barHeight!!)
        }
    }

    fun getBarHeight():Int{
        return this.height + paddingInView.toInt()
    }

    fun allDoneButtonClick(){
        val initDone = actionDetail!!.done
        val doneNumAnimation = ValueAnimator.ofInt(initDone,actionDetail!!.num)
        doneNumAnimation.addUpdateListener {
            val value = it.animatedValue as Int
            actionDetail!!.done = value
            invalidate()
        }
        doneNumAnimation.duration = 200
        doneNumAnimation.start()
    }

    fun allDoneButtonClickForClear(){
        val initDone = actionDetail!!.done
        val doneNumAnimation = ValueAnimator.ofInt(initDone,0)
        doneNumAnimation.addUpdateListener {
            val value = it.animatedValue as Int
            actionDetail!!.done = value
            invalidate()
        }
        doneNumAnimation.duration = 200
        doneNumAnimation.start()
    }

    fun doneNumChanged(done:Int){
        val initDone = actionDetail!!.done
        val doneNumAnimation = ValueAnimator.ofInt(initDone,done)
        doneNumAnimation.addUpdateListener {
            val value = it.animatedValue as Int
            actionDetail!!.done = value
            invalidate()
        }
        doneNumAnimation.duration = 100
        doneNumAnimation.start()
    }

}