package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.zhangjie.fitnessflow.R

class AdapterForViewPagerInAdditionForm :PagerAdapter(){

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val viewInViewPager: View?
        when(position){
            0->{
                viewInViewPager = LayoutInflater.from(container.context).inflate(R.layout.action_add_in_form_without_unit, container, false)
                container.addView(viewInViewPager)
            }
            else->{
                viewInViewPager = LayoutInflater.from(container.context).inflate(R.layout.action_add_in_form_with_unit, container, false)
                container.addView(viewInViewPager)
            }
        }
        return viewInViewPager!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 2
    }

}