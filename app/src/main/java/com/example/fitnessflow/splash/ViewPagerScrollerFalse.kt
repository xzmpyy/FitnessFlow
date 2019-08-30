package com.example.fitnessflow.splash

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ViewPagerScrollerFalse (context: Context,set: AttributeSet):ViewPager(context,set){

    override fun canScrollHorizontally(direction: Int): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

}