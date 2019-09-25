package com.example.zhangjie.fitnessflow.plan

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewForPlanDataShow(context: Context,set:AttributeSet) :RecyclerView(context,set){

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return true
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return false
    }

}