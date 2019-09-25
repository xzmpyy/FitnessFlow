package com.example.zhangjie.fitnessflow.plan

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan

class AdapterInPlanFragment(private val actionList:ArrayList<Action>,
                            private val actionDetailMap:MutableMap<Action, ArrayList<ActionDetailInPlan>>,
                            private val context: Context):
    RecyclerView.Adapter<AdapterInPlanFragment.RvHolder>(){

    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val actionName = view.findViewById<TextView>(R.id.action_name)!!
        val actionDetailInfo = view.findViewById<TextView>(R.id.detail_info)!!
        val dataSpace = view.findViewById<LinearLayout>(R.id.data_show)!!
        val parentLayout = view.findViewById<LinearLayout>(R.id.item_parent_layout)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.every_day_plan_item,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        if (p1 == actionList.size - 1){
            val layoutParams = LinearLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
        }
        //向viewHolder中的View控件赋值需显示的内容
        p0.actionName.text=actionList[p1].actionName
        //信息栏
        if (actionList[p1].IsHadWeightUnits == 1){
            val weightBoundary = maxAndMinWeight(actionDetailMap[actionList[p1]]!!)
            p0.actionDetailInfo.text = context.resources.getString(R.string.weight_boundary) + "${weightBoundary.first}-${weightBoundary.second} ${actionList[p1].unit}"
        }else{
            val targetString = getTargetString(actionDetailMap[actionList[p1]]!!)
            p0.actionDetailInfo.text = context.resources.getString(R.string.target_string) + "$targetString ${actionList[p1].unit}"
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    private fun maxAndMinWeight(detailList:ArrayList<ActionDetailInPlan>):Pair<Float,Float>{
        var minWeight = 0f
        var maxWeight = 0f
        for (detail in detailList){
            if (detailList.indexOf(detail) == 0){
                minWeight = detail.weight
                maxWeight = detail.weight
            }else{
                if (detail.weight < minWeight){
                    minWeight = detail.weight
                }
                if (detail.weight>maxWeight){
                    maxWeight = detail.weight
                }
            }
        }
        return Pair(minWeight,maxWeight)
    }

    private fun getTargetString(detailList:ArrayList<ActionDetailInPlan>):String{
        return if (detailList.size == 1){
            detailList[0].num.toString()
        }else{
            val targetString = StringBuffer()
            for (detail in detailList){
                if (detailList.indexOf(detail) == detailList.size-1){
                    targetString.append(detail.num.toString())
                }else{
                    targetString.append("${detail.num}-")
                }
            }
            targetString.toString()
        }
    }

}
