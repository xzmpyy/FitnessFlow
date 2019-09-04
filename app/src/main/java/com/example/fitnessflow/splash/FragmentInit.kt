package com.example.fitnessflow.splash

import androidx.fragment.app.Fragment
import com.example.fitnessflow.library.LibraryFragment
import com.example.fitnessflow.library.library_child_fragments.TemplateFragment
import com.example.fitnessflow.mine.MineFragment
import com.example.fitnessflow.plan.PlanFragment
import com.example.fitnessflow.today.TodayFragment

object FragmentInit {

    private var indexFragmentInViewPagerList:List<Fragment>? = null
    private var muscleGroupFragmentsList:List<Fragment>? = null

    fun init(){
        muscleGroupFragmentsList = listOf(TemplateFragment() as Fragment)
        indexFragmentInViewPagerList = listOf(TodayFragment() as Fragment,
            PlanFragment() as Fragment,LibraryFragment() as Fragment,MineFragment() as Fragment)
    }

    fun getIndexFragmentInViewPagerList():List<Fragment>{
        return indexFragmentInViewPagerList!!
    }

    fun getMuscleGroupFragmentsList():List<Fragment>{
        return muscleGroupFragmentsList!!
    }

}