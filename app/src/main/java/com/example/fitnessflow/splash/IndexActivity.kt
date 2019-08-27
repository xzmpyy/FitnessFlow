package com.example.fitnessflow.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.fitnessflow.R
import com.example.fitnessflow.fit_calendar.GetMonthInfo
import com.example.fitnessflow.navigation_bar.NavigationBarView
import com.example.fitnessflow.plan.PlanFragment

class IndexActivity : AppCompatActivity(),NavigationBarView.OperationButtonClickListener{

    private var indexViewPager: ViewPager? = null
    private var indexFragmentInViewPagerList:List<Fragment>? = null
    private var planFragment:PlanFragment? = null
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null
    private var navigatorBar:NavigationBarView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        indexViewPager = findViewById(R.id.index_view_pager)
        navigatorBar = findViewById(R.id.navigator)
        indexViewPager!!.offscreenPageLimit =3
        planFragment = PlanFragment()
        navigatorBar!!.setOperationButtonClickListener(this)
        indexFragmentInViewPagerList = listOf(planFragment!! as Fragment)
        indexViewPagerAdapter = IndexViewPagerAdapter(supportFragmentManager, indexFragmentInViewPagerList!!)
        indexViewPager!!.adapter = indexViewPagerAdapter
        indexViewPager!!.currentItem = 0

    }

    //OperatorButton点击事件
    override fun onOperationButtonClick(position: Int) {

    }

    //息屏后点亮状态恢复
    override fun onRestart() {
        planFragment!!.fitCalendarReStart()
        super.onRestart()
    }


}