package com.example.zhangjie.fitnessflow.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate

class ActionDetailAdapterInTemplateDetailActivity (private val action:Action,
                                                   private val actionDetailList:ArrayList<ActionDetailInTemplate>,
                                                   private val context:Context):
    RecyclerView.Adapter<ActionDetailAdapterInTemplateDetailActivity.RvHolder>(){

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件

    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //根据有无重量单位返回不同布局
        return if (actionDetailList[p1].isHadWeightUnits == 0){
            RvHolder(LayoutInflater.from(context).inflate(R.layout.action_detail_view_without_weight,p0,false))
        }else{
            RvHolder(LayoutInflater.from(context).inflate(R.layout.action_detail_view,p0,false))
        }
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionDetailList.size
    }

    override fun onBindViewHolder(p0:RvHolder,p1:Int){
        //向viewHolder中的View控件赋值需显示的内容

    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }


}
