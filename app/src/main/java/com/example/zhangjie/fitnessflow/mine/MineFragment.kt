package com.example.zhangjie.fitnessflow.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zhangjie.fitnessflow.R

class MineFragment : Fragment(){

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_mine,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text).text = "Mine"
    }

}