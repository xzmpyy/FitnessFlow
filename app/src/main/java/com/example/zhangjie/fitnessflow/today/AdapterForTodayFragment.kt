package com.example.zhangjie.fitnessflow.today

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan

class AdapterForTodayFragment (private val actionList:ArrayList<Action>,
                               private val actionDetailMap:MutableMap<Action, ArrayList<ActionDetailInPlan>>,
                               private val context: Context
): RecyclerView.Adapter<AdapterForTodayFragment.RvHolder>(){

    private val adapterList = arrayListOf<AdapterForActionDetailInTodayFragment>()
    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()

    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        val actionName = view.findViewById<TextView>(R.id.action_name)!!
        val actionDetailRecyclerView = view.findViewById<RecyclerView>(R.id.action_detail)!!
        val parentLayout = view.findViewById<LinearLayout>(R.id.parent_layout)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int): RvHolder {
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(
            LayoutInflater.from(context).inflate(
                R.layout.action_detail_in_today_fragment,
                p0,
                false
            )
        )
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionList.size
    }

    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        p0.actionName.text = actionList[p1].actionName
        val layoutManager = LinearLayoutManager(context)
        val adapter = AdapterForActionDetailInTodayFragment(actionList[p1],actionDetailMap[actionList[p1]]!!,context)
        p0.actionDetailRecyclerView.layoutManager = layoutManager
        p0.actionDetailRecyclerView.adapter = adapter
        adapterList.add(adapter)
        if (p1 == actionList.size - 1){
            val layoutParams = LinearLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
        }
    }

    override fun getItemViewType(position:Int):Int{
        return position
    }

    fun fold(){
        for (adapter in adapterList){
            adapter.fold()
        }
    }

    fun unfold(){
        for (adapter in adapterList){
            adapter.unfold()
        }
    }

}