package com.example.fitnessflow.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessflow.R
import com.example.fitnessflow.navigation_bar.NavigationBarView
import com.example.fitnessflow.plan.PlanFragment

class IndexActivity : AppCompatActivity(),NavigationBarView.OperationButtonClickListener,NavigationBarView.NavigatorClickListener{

    private var indexViewPager: ViewPagerScrollerFalse? = null
    private var indexFragmentInViewPagerList = FragmentInit.getIndexFragmentInViewPagerList()
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null
    private var navigatorBar:NavigationBarView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        indexViewPager = findViewById(R.id.index_view_pager)
        navigatorBar = findViewById(R.id.navigator)
        indexViewPager!!.offscreenPageLimit =3
        navigatorBar!!.setOperationButtonClickListener(this)
        navigatorBar!!.setNavigatorClickListener(this)
        indexViewPagerAdapter = IndexViewPagerAdapter(supportFragmentManager, indexFragmentInViewPagerList)
        indexViewPager!!.adapter = indexViewPagerAdapter
        indexViewPager!!.currentItem = 1
    }

    //OperatorButton点击事件
    override fun onOperationButtonClick(position: Int) {

    }

    override fun onNavigatorClick(position: Int) {
        indexViewPager!!.setCurrentItem(position,false)
    }

    //息屏后点亮状态恢复
    override fun onRestart() {
        (indexFragmentInViewPagerList[1] as PlanFragment).fitCalendarReStart()
        super.onRestart()
    }


}