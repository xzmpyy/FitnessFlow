package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class ViewPagerForAdditionForm (context: Context, set: AttributeSet): ViewPager(context,set){

    private var maxHeight:Int? = null

    override fun canScrollHorizontally(direction: Int): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeight == null){
            //获取高度最高的子视图
            val maxHeightView = adapter!!.instantiateItem(this,1) as View
            maxHeightView.measure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            maxHeight = maxHeightView.measuredHeight
            maxHeight = MeasureSpec.makeMeasureSpec(maxHeight!!,
                MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, maxHeight!!)
    }

}