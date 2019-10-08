package com.example.zhangjie.fitnessflow.today

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
    private val selectNumBackground = ContextCompat.getDrawable(context,R.drawable.round_rect_background)!!
    private val unSelectNumBackground = ContextCompat.getDrawable(context,R.drawable.round_rect_background_gray)!!
    private val numPickButtonMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private val allNumButtonList = arrayListOf<ArrayList<Button>>()
    private val buttonWidth = context.resources.getDimension(R.dimen.minButtonWidth).toInt()
    //默认零未选中
    private var allDoneButtonFlagList = arrayListOf<Int>()

    init {
        getMaxNum()
    }

    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        val parentLayout = view.findViewById<LinearLayout>(R.id.parent_layout)!!
        val toBeFold = view.findViewById<LinearLayout>(R.id.fold)!!
        val weightText = view.findViewById<TextView>(R.id.weight)!!
        val executionText = view.findViewById<TextView>(R.id.num)!!
        val allDoneButton = view.findViewById<ImageButton>(R.id.all_done)!!
        val numSelectLayout = view.findViewById<LinearLayout>(R.id.select)!!
        val horBar = view.findViewById<HorizontalScrollView>(R.id.hor)!!
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
        p0.toBeFold.visibility = LinearLayout.GONE
        if (detailList[p1].done == detailList[p1].num){
            p0.allDoneButton.setImageDrawable(allDoneGreen)
            allDoneButtonFlagList.add(1)
        }else{
            allDoneButtonFlagList.add(0)
        }
        val numButtonList = arrayListOf<Button>()
        if (detailList[p1].num<=25){
            //数值选择器
            for (pickNum in detailList[p1].num downTo 0){
                val numPickButton = View.inflate(context,R.layout.num_pick_button_in_today_fragment,null) as Button
                numPickButton.text = pickNum.toString()
                p0.numSelectLayout.addView(numPickButton)
                p0.numSelectLayout.tag = detailList[p1].done
                numButtonList.add(numPickButton)
                numPickButton.layoutParams.width = buttonWidth
                if (pickNum!=0){
                    val numLayoutParams = LinearLayout.LayoutParams(numPickButton.layoutParams)
                    numLayoutParams.marginEnd = numPickButtonMargin
                    numPickButton.layoutParams = numLayoutParams
                }
                if (pickNum == p0.numSelectLayout.tag){
                    numPickButton.background = selectNumBackground
                }
                numPickButton.setOnClickListener {
                    if (p0.numSelectLayout.tag!=pickNum){
                        allNumButtonList[p1][detailList[p1].num - p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                        numPickButton.background = selectNumBackground
                        p0.numSelectLayout.tag = pickNum
                        barViewList[p1].doneNumChanged(pickNum)
                        if (action.IsHadWeightUnits == 1){
                            detailList[p1].done = pickNum
                            p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
                        }else{
                            detailList[p1].done = pickNum
                            p0.weightText.text = "${detailList[p1].done}${action.unit}"
                        }
                        if (pickNum == detailList[p1].num){
                            barViewList[p1].allDoneButtonClick()
                            p0.allDoneButton.setImageDrawable(allDoneGreen)
                            allDoneButtonFlagList[p1] = 1
                        }
                        if (pickNum == 0){
                            barViewList[p1].allDoneButtonClickForClear()
                            p0.allDoneButton.setImageDrawable(allDoneGray)
                            allDoneButtonFlagList[p1] = 0
                        }
                        if (pickNum>0 && pickNum<detailList[p1].num && allDoneButtonFlagList[p1] == 1){
                            p0.allDoneButton.setImageDrawable(allDoneGray)
                            allDoneButtonFlagList[p1] = 0
                        }
                    }
                }
            }
            allNumButtonList.add(numButtonList)
        }else{
            for (pickNum in 10 downTo 0){
                val numPickButton = View.inflate(context,R.layout.num_pick_button_in_today_fragment,null) as Button
                if (pickNum == 0){
                    numPickButton.text = "$pickNum%"
                }else{
                    numPickButton.text = "${pickNum}0%"
                }
                p0.numSelectLayout.addView(numPickButton)
                p0.numSelectLayout.tag =  ((detailList[p1].done.toFloat()/detailList[p1].num.toFloat())*10f).toInt()*10
                numButtonList.add(numPickButton)
                numPickButton.layoutParams.width = buttonWidth
                if (pickNum!=0){
                    val numLayoutParams = LinearLayout.LayoutParams(numPickButton.layoutParams)
                    numLayoutParams.marginEnd = numPickButtonMargin
                    numPickButton.layoutParams = numLayoutParams
                }
                if (pickNum == p0.numSelectLayout.tag){
                    numPickButton.background = selectNumBackground
                }
                numPickButton.setOnClickListener {
                    if (p0.numSelectLayout.tag!=pickNum){
                        allNumButtonList[p1][10 - p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                        numPickButton.background = selectNumBackground
                        p0.numSelectLayout.tag = pickNum
                        barViewList[p1].doneNumChanged((pickNum.toFloat()/10f*detailList[p1].num.toFloat()).toInt())
                        if (action.IsHadWeightUnits == 1){
                            detailList[p1].done = (pickNum.toFloat()/10f*detailList[p1].num.toFloat()).toInt()
                            p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
                        }else{
                            detailList[p1].done = (pickNum.toFloat()/10f*detailList[p1].num.toFloat()).toInt()
                            p0.weightText.text = "${detailList[p1].done}${action.unit}"
                        }
                        if (pickNum==10){
                            barViewList[p1].allDoneButtonClick()
                            p0.allDoneButton.setImageDrawable(allDoneGreen)
                            allDoneButtonFlagList[p1] = 1
                        }
                        if (pickNum == 0){
                            barViewList[p1].allDoneButtonClickForClear()
                            p0.allDoneButton.setImageDrawable(allDoneGray)
                            allDoneButtonFlagList[p1] = 0
                        }
                        if (pickNum in 1..9 && allDoneButtonFlagList[p1] == 1){
                            p0.allDoneButton.setImageDrawable(allDoneGray)
                            allDoneButtonFlagList[p1] = 0
                        }
                    }
                }
            }
            allNumButtonList.add(numButtonList)
        }
        p0.allDoneButton.setOnClickListener {
            if (allDoneButtonFlagList[p1] == 0){
                barViewList[p1].allDoneButtonClick()
                p0.allDoneButton.setImageDrawable(allDoneGreen)
                if (action.IsHadWeightUnits == 1){
                    detailList[p1].done = detailList[p1].num
                    p0.executionText.text = "${detailList[p1].done}$slash${detailList[p1].num}"
                }else{
                    detailList[p1].done = detailList[p1].num
                    p0.weightText.text = "${detailList[p1].done}${action.unit}"
                }
                if (detailList[p1].num<=25){
                    allNumButtonList[p1][detailList[p1].num - p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                    p0.numSelectLayout.tag = detailList[p1].num
                }else{
                    allNumButtonList[p1][10- p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                    p0.numSelectLayout.tag = 10
                }
                allNumButtonList[p1][0].background = selectNumBackground
                p0.horBar.smoothScrollTo(0,allNumButtonList[p1][0].pivotY.toInt())
                allDoneButtonFlagList[p1] = 1
            }else{
                barViewList[p1].allDoneButtonClickForClear()
                p0.allDoneButton.setImageDrawable(allDoneGray)
                if (action.IsHadWeightUnits == 1){
                    detailList[p1].done = 0
                    p0.executionText.text = "0$slash${detailList[p1].num}"
                }else{
                    detailList[p1].done = 0
                    p0.weightText.text = "0${action.unit}"
                }
                if (detailList[p1].num<=25){
                    allNumButtonList[p1][detailList[p1].num - p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                }else{
                    allNumButtonList[p1][10- p0.numSelectLayout.tag!!.toString().toInt()].background = unSelectNumBackground
                }
                allNumButtonList[p1][allNumButtonList[p1].size-1].background = selectNumBackground
                p0.horBar.smoothScrollTo(allNumButtonList[p1][0].scrollX,allNumButtonList[p1][0].scrollY)
                p0.numSelectLayout.tag = 0
                allDoneButtonFlagList[p1] = 0
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
        for (foldView in foldViewList){
            foldView.visibility = LinearLayout.GONE
        }
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
        for (foldView in foldViewList){
            foldView.visibility = LinearLayout.VISIBLE
        }
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