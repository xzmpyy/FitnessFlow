package com.example.zhangjie.fitnessflow.plan.plan_detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.today.BarViewInTodayFragment

class PastActionDetailAdapter (private val action: Action,
                               private val detailList:ArrayList<ActionDetailInPlan>,
                               private val context: Context
): RecyclerView.Adapter<PastActionDetailAdapter.RvHolder>() {

    private var maxNum = 0
    private val slash = context.resources.getString(R.string.slash)

    init {
        getMaxNum()
    }

    class RvHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parentLayout = view.findViewById<LinearLayout>(R.id.parent_layout)!!
        val weightText = view.findViewById<TextView>(R.id.weight)!!
        val executionText = view.findViewById<TextView>(R.id.num)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RvHolder {
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(
            LayoutInflater.from(context).inflate(
                R.layout.past_action_detail_second_layer,
                p0,
                false
            )
        )
    }

    //获取Item个数的方法
    override fun getItemCount(): Int {
        //返回列表长度
        return detailList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: RvHolder, p1: Int) {
        if (action.IsHadWeightUnits == 1) {
            p0.weightText.text = "${detailList[p1].weight}${action.unit}"
            p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
        } else {
            p0.weightText.text = "${detailList[p1].done}${action.unit}"
            p0.executionText.text = "${detailList[p1].num}${action.unit}"
        }
        val parser = context.resources.getXml(R.xml.bar_view_in_today_fragment)
        val attributesForDateInfo = Xml.asAttributeSet(parser)
        val barView = BarViewInTodayFragment(context, attributesForDateInfo, detailList[p1], maxNum)
        p0.parentLayout.addView(barView)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun getMaxNum() {
        for (detail in detailList) {
            if (detail.num > maxNum) {
                maxNum = detail.num
            }
        }
    }

}