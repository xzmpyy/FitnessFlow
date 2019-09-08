package com.example.zhangjie.fitnessflow.splash

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class IndexViewPagerAdapter (fm: FragmentManager, private val fragmentsList:List<Fragment>): FragmentPagerAdapter(fm){
    //Fragment个数
    override fun getCount():Int{
        return fragmentsList.size
    }
    //当前位置的Fragment
    override fun getItem(p0:Int):Fragment{
        return fragmentsList[p0]
    }

    //不销毁Item
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}