package com.example.zhangjie.fitnessflow.today

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan

class AdapterForActionDetailInTodayFragment (private val action:Action,
                                             private val detailList:ArrayList<ActionDetailInPlan>,
                                             private val context: Context
): RecyclerView.Adapter<AdapterForActionDetailInTodayFragment.RvHolder>(){

    private var maxNum = 0
    private val barViewList = arrayListOf<BarViewInTodayFragment>()
    private val foldViewList = arrayListOf<LinearLayout>()
    private val slash = context.resources.getString(R.string.slash)
    private val allDoneGreen = ContextCompat.getDrawable(context,R.drawable.all_done_green)!!
    private val allDoneGray = ContextCompat.getDrawable(context,R.drawable.all_done_gray)!!
    //默认零未选中
    private var allDoneButtonFlag = 0

    init {
        getMaxNum()
    }

    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        val parentLayout = view.findViewById<LinearLayout>(R.id.parent_layout)!!
        val toBeFold = view.findViewById<LinearLayout>(R.id.fold)!!
        val weightText = view.findViewById<TextView>(R.id.weight)!!
        val executionText = view.findViewById<TextView>(R.id.num)!!
        val allDoneButton = view.findViewById<ImageButton>(R.id.all_done)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int): RvHolder {
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(
            LayoutInflater.from(context).inflate(
                R.layout.action_detail_in_today_fragment_recyclerview,
                p0,
                false
            )
        )
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return detailList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        if (action.IsHadWeightUnits == 1){
            p0.weightText.text = "${detailList[p1].weight}${action.unit}"
            p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
        }else{
            p0.weightText.text = "${detailList[p1].done}${action.unit}"
            p0.executionText.text = "${detailList[p1].num}${action.unit}"
        }
        val parser = context.resources.getXml(R.xml.bar_view_in_today_fragment)
        val attributesForDateInfo = Xml.asAttributeSet(parser)
        val barView = BarViewInTodayFragment(context,attributesForDateInfo,detailList[p1],maxNum)
        p0.parentLayout.addView(barView)
        barViewList.add(barView)
        foldViewList.add(p0.toBeFold)
        p0.allDoneButton.setOnClickListener {
            if (allDoneButtonFlag == 0){
                barViewList[p1].allDoneButtonClick()
                p0.allDoneButton.setImageDrawable(allDoneGreen)
                if (action.IsHadWeightUnits == 1){
                    detailList[p1].done = detailList[p1].num
                    p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
                }else{
                    detailList[p1].done = detailList[p1].num
                    p0.weightText.text = "${detailList[p1].done}${action.unit}"
                }
                allDoneButtonFlag = 1
            }else{
                p0.allDoneButton.setImageDrawable(allDoneGray)
                allDoneButtonFlag = 0
            }
        }
    }

    override fun getItemViewType(position:Int):Int{
        return position
    }


    fun fold(){
        val foldAnimator = ValueAnimator.ofInt(barViewList[0].getBarHeight(),0)
        foldAnimator.addUpdateListener {
            val marginValue = it.animatedValue as Int
            for (foldView in foldViewList){
                val layoutParams = FrameLayout.LayoutParams(foldView.layoutParams)
                layoutParams.topMargin = marginValue
                foldView.layoutParams = layoutParams
            }
        }
        foldAnimator.duration = 200
        foldAnimator.start()
    }

    fun unfold(){
        val unfoldAnimator = ValueAnimator.ofInt(0,barViewList[0].getBarHeight())
        unfoldAnimator.addUpdateListener {
            val marginValue = it.animatedValue as Int
            for (foldView in foldViewList){
                val layoutParams = FrameLayout.LayoutParams(foldView.layoutParams)
                layoutParams.topMargin = marginValue
                foldView.layoutParams = layoutParams
            }
        }
        unfoldAnimator.duration = 200
        unfoldAnimator.start()
    }

    private fun getMaxNum(){
        for (detail in detailList){
            if (detail.num > maxNum){
                maxNum = detail.num
            }
        }
    }

}