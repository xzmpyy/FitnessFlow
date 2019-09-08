package com.example.zhangjie.fitnessflow.fit_calendar

import android.content.Context
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

class ViewPagerScroll(context: Context?) : Scroller(context) {
    private var scrollDuration = 2000

    fun setScrollDuration(duration: Int){
        this.scrollDuration = duration
    }

    //用自定义时间取代默认滑动时间
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, this.scrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, this.scrollDuration)
    }

    //通过反射重设ViewPager的滑动速度
    fun initViewPagerScrollDuration(v: ViewPager){
        try {
            val vScroll = ViewPager::class.java.getDeclaredField("mScroller")
            vScroll.isAccessible = true
            vScroll.set(v, this)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}