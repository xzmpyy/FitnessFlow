package com.example.fitnessflow.library.library_child_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessflow.R

class TemplateFragment : Fragment(){

    private var templateRv:RecyclerView? = null
    private val layoutManager = LinearLayoutManager(this.activity)
    private var adapter:TemplateFragmentAdapter? = null
    //测试数据
    private val testDataList = arrayListOf<String>()

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_template,container,false)
    }

    //视图初始化
    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        templateRv = view.findViewById(R.id.template_rv)
        for (i in 1..30){
            testDataList.add(i.toString())
        }
        templateRv!!.layoutManager = layoutManager
        adapter = TemplateFragmentAdapter(testDataList, view.context)
        templateRv!!.adapter = adapter
    }

}