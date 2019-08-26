package com.example.fitnessflow.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.fitnessflow.R
import com.example.fitnessflow.plan.PlanFragment

class IndexActivity : AppCompatActivity() {

    private var indexViewPager: ViewPager? = null
    private var indexFragmentInViewPagerList:List<Fragment>? = null
    private var planFragment:PlanFragment? = null
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        indexViewPager = findViewById(R.id.index_view_pager)
        indexViewPager!!.offscreenPageLimit =3
        planFragment = PlanFragment()
        indexFragmentInViewPagerList = listOf(planFragment!! as Fragment)
        indexViewPagerAdapter = IndexViewPagerAdapter(supportFragmentManager, indexFragmentInViewPagerList!!)
        indexViewPager!!.adapter = indexViewPagerAdapter
        indexViewPager!!.currentItem = 0

    }


}