package com.example.fitnessflow.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.fitnessflow.R
import com.example.fitnessflow.fit_calendar.GetMonthInfo
import com.example.fitnessflow.navigation_bar.NavigationBarView
import com.example.fitnessflow.plan.PlanFragment

class IndexActivity : AppCompatActivity(),PlanFragment.YearAndMonthChangedListener,NavigationBarView.OperationButtonClickListener{

    private var indexViewPager: ViewPager? = null
    private var indexFragmentInViewPagerList:List<Fragment>? = null
    private var planFragment:PlanFragment? = null
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null
    private var navigatorBar:NavigationBarView? = null
    private val dateNow = GetMonthInfo.getTodayString()
    private var isDateNowOrNot = true

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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        planFragment!!.setYearAndMonthChangedListener(this)
        super.onWindowFocusChanged(hasFocus)
    }

    //监听PlanFragment中日历的变化
    override fun onYearAndMonthChangedListener(year: Int, month: Int) {
        val dateCurrent = GetMonthInfo.getYearAndMonthString(year,month)
        if (dateNow == dateCurrent && !isDateNowOrNot){
            navigatorBar!!.changeOperatorButtonToJumpTodayOrReset(0)
            isDateNowOrNot = true
        }else if (dateNow != dateCurrent && isDateNowOrNot){
            navigatorBar!!.changeOperatorButtonToJumpTodayOrReset(1)
            isDateNowOrNot = false
        }
    }

    override fun onOperationButtonClick(position: Int) {
        when(position){
            5->{
                planFragment!!.calendarJumpToday()
                isDateNowOrNot = true
            }
        }
    }

}