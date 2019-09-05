package com.example.fitnessflow.library.library_child_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessflow.R

class MuscleGroupFragment : Fragment(){

    companion object{
        fun getMuscleGroupFragmentWithType(type:Int):MuscleGroupFragment{
            val fragment = MuscleGroupFragment()
            fragment.muscleGroupType = type
            return fragment
        }
    }

    private var muscleGroupRv: RecyclerView? = null
    private var layoutManager:LinearLayoutManagerForItemSwipe? = null
    private var adapter:MuscleGroupFragmentAdapter? = null
    private var muscleGroupType = 0
    //测试数据
    private val testDataList = arrayListOf<String>()

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_muscle_group,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        muscleGroupRv = view.findViewById(R.id.muscle_group_rv)
        for (i in 1..30){
            testDataList.add(i.toString())
        }
        layoutManager = LinearLayoutManagerForItemSwipe((view.context))
        muscleGroupRv!!.layoutManager = layoutManager
        adapter = MuscleGroupFragmentAdapter(testDataList, layoutManager!!,view.context)
        muscleGroupRv!!.adapter = adapter
    }

}