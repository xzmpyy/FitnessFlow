package com.example.fitnessflow.fit_calendar


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


class CalendarViewPager(context: Context, attributeSet: AttributeSet): ViewPager(context, attributeSet) {


    private var xPosition:Float? = null
    private var horizontalScrollFlag = true

    //事件拦截，当为首位两页时，直接拦截，为中间页时，判断点击还是滑动
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //println("Click first")
        val position = this.currentItem
        if (position == 0 || position == 3){
            return true
        }else{
            val res = super.onInterceptTouchEvent(ev)
            xPosition = if (ev!!.action == MotionEvent.ACTION_DOWN){
                ev.x
            }else{
                if (abs(ev.x - xPosition!!) > 5){
                    return true
                }else{
                    ev.x
                }
            }
            return res
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return false
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return horizontalScrollFlag
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = (widthSize/ 7)*6
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
    }

    fun setCanScrollHorizontally(horizontalScrollFlag:Boolean){
        this.horizontalScrollFlag = horizontalScrollFlag
    }

}