package com.example.zhangjie.fitnessflow.splash

import androidx.fragment.app.Fragment
import com.example.zhangjie.fitnessflow.library.LibraryFragment
import com.example.zhangjie.fitnessflow.library.library_child_fragments.MuscleGroupFragment
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateFragment
import com.example.zhangjie.fitnessflow.mine.MineFragment
import com.example.zhangjie.fitnessflow.plan.PlanFragment
import com.example.zhangjie.fitnessflow.today.TodayFragment

object FragmentInit {

    private var indexFragmentInViewPagerList:List<Fragment>? = null
    private var muscleGroupFragmentsList:List<Fragment>? = null

    fun init(){
        muscleGroupFragmentsList = listOf(TemplateFragment() as Fragment, MuscleGroupFragment.getMuscleGroupFragmentWithType(1) as Fragment,
            MuscleGroupFragment.getMuscleGroupFragmentWithType(2) as Fragment,MuscleGroupFragment.getMuscleGroupFragmentWithType(3) as Fragment,
            MuscleGroupFragment.getMuscleGroupFragmentWithType(4) as Fragment,MuscleGroupFragment.getMuscleGroupFragmentWithType(5) as Fragment,
            MuscleGroupFragment.getMuscleGroupFragmentWithType(6) as Fragment,MuscleGroupFragment.getMuscleGroupFragmentWithType(7) as Fragment,
            MuscleGroupFragment.getMuscleGroupFragmentWithType(8) as Fragment)
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