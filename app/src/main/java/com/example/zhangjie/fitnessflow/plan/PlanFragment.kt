package com.example.zhangjie.fitnessflow.plan

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.fit_calendar.FitCalendarView
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.library.library_child_fragments.LinearLayoutManagerForItemSwipe
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import java.lang.Exception

class PlanFragment : Fragment(),FitCalendarView.YearAndMonthChangedListener,
    FitCalendarView.DefaultSelectedListGenerator,FitCalendarView.ItemClickListener,
FitCalendarView.ScaleAnimationListener{

    //测试数据
    private val actionList = arrayListOf<Action>()
    private val actionDetailMap = mutableMapOf<Action, ArrayList<ActionDetailInPlan>>()
    private val actionIdList = arrayListOf<Int>()
    //RecyclerView相关
    private var planRecyclerView:RecyclerView? = null
    private var layoutManager:LinearLayoutManagerForItemSwipe?=null
    private var adapter:AdapterInPlanFragment? = null
    private var canRecyclerViewScroll = false
    //日历相关
    private var fitCalendar:FitCalendarView? = null
    private var initRecyclerViewPosition = 0f
    private var recyclerViewMovedDistance = 0f
    private var scaleAnimationButton:ImageButton? = null
    private var initScaleAnimationButtonX:Float? = null
    private var yearAndMonthNow = GetMonthInfo.getTodayString().substring(0,7)
    private var yearAndMonthNowFlag = true
    //0日历展开，1日历收起
    private var expansionAndContractionState = 0
    private var jumTodayButton:ImageButton? = null

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_plan,container,false)
    }

    //视图初始化
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view:View, savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        fitCalendar = view.findViewById(R.id.calendar_view_in_plan)
        //日历回调接口的设置
        fitCalendar!!.setYearAndMonthChangedListener(this)
        fitCalendar!!.setDefaultSelectedListGenerator(this)
        fitCalendar!!.setItemClickListener(this)
        fitCalendar!!.setScaleAnimationListener(this)
        scaleAnimationButton = view.findViewById(R.id.expand_contract_button)
        jumTodayButton = view.findViewById(R.id.jump_today_button)
        scaleAnimationButton!!.setOnClickListener {
            fitCalendar!!.scaleAnimation()
        }
        jumTodayButton!!.setOnClickListener {
            when (expansionAndContractionState){
                0->{
                    fitCalendar!!.jumpToToday()
                }
                1->{
                    fitCalendar!!.scaleAnimation()
                    expansionAndContractionState = 0
                    fitCalendar!!.jumpToToday()
                }
            }
        }
        planRecyclerView = view.findViewById(R.id.rv_in_plan)
        layoutManager = LinearLayoutManagerForItemSwipe(view.context)
        layoutManager!!.setCanScrollVerticallyFlag(canRecyclerViewScroll)
        planRecyclerView!!.layoutManager = layoutManager
        //初始化第一天数据
        dayDataInit(GetMonthInfo.getTodayString(),view.context)
        planRecyclerView!!.setOnTouchListener { _, event ->
            try{
                when(event!!.action){
                    MotionEvent.ACTION_DOWN ->{
                        println("D")
                        //要使用rawY，否则会抖动
                        initRecyclerViewPosition = event.rawY
                    }
                    MotionEvent.ACTION_MOVE->{
                        println("M")
                        recyclerViewMovedDistance = event.rawY - initRecyclerViewPosition
                        initRecyclerViewPosition = event.rawY
                        if (recyclerViewMovedDistance <-2 || recyclerViewMovedDistance>2 ){
                            if (actionList.size!=0 && layoutManager!!.findFirstCompletelyVisibleItemPosition() == 0){
                                fitCalendar!!.scrollerListener(recyclerViewMovedDistance)
                            }
                            if (actionList.size==0){
                                fitCalendar!!.scrollerListener(recyclerViewMovedDistance)
                            }
                        }
                    }
                    MotionEvent.ACTION_UP->{
                        fitCalendar!!.startResetAnimation()
                        initRecyclerViewPosition = 0f
                    }
                }
            }catch (exception:Exception){
                println(exception)
                fitCalendar!!.startResetAnimation()
                initRecyclerViewPosition = 0f
            }
            false
        }
    }


    override fun onYearAndMonthChangedListener(year: Int, month: Int) {
        if (initScaleAnimationButtonX == null){
            initScaleAnimationButtonX = scaleAnimationButton!!.x
        }
        val currentYearAndMonthString = GetMonthInfo.getYearAndMonthString(year,month)
        if (!yearAndMonthNowFlag && currentYearAndMonthString==yearAndMonthNow){
            val jumpTodayButtonPositionAnimation = ValueAnimator.ofFloat(initScaleAnimationButtonX!!+150f, initScaleAnimationButtonX!!)
            jumpTodayButtonPositionAnimation.addUpdateListener {
                val distance = it.animatedValue as Float
                jumTodayButton!!.x =distance
            }
            jumpTodayButtonPositionAnimation.duration = 300
            jumpTodayButtonPositionAnimation.start()
            val scaleButtonPositionAnimation = ValueAnimator.ofFloat(initScaleAnimationButtonX!!-150f, initScaleAnimationButtonX!!)
            scaleButtonPositionAnimation.addUpdateListener {
                val distance = it.animatedValue as Float
                scaleAnimationButton!!.x = distance
            }
            scaleButtonPositionAnimation.duration = 300
            scaleButtonPositionAnimation.start()
            jumTodayButton!!.isClickable = false
            yearAndMonthNowFlag = true
        }else if (yearAndMonthNowFlag && currentYearAndMonthString !=yearAndMonthNow){
            val jumpTodayButtonPositionAnimation = ValueAnimator.ofFloat(initScaleAnimationButtonX!!, initScaleAnimationButtonX!!+150f)
            jumpTodayButtonPositionAnimation.addUpdateListener {
                val distance = it.animatedValue as Float
                jumTodayButton!!.x =distance
            }
            jumpTodayButtonPositionAnimation.duration = 300
            jumpTodayButtonPositionAnimation.start()
            val scaleButtonPositionAnimation = ValueAnimator.ofFloat(initScaleAnimationButtonX!!, initScaleAnimationButtonX!!-150f)
            scaleButtonPositionAnimation.addUpdateListener {
                val distance = it.animatedValue as Float
                scaleAnimationButton!!.x =distance
            }
            scaleButtonPositionAnimation.duration = 300
            scaleButtonPositionAnimation.start()
            jumTodayButton!!.isClickable = true
            yearAndMonthNowFlag = false
        }
    }

    fun fitCalendarReStart(){
        fitCalendar!!.reStart()
    }

    //默认选中项
    override fun setDefaultSelectedList(year: Int, month: Int): ArrayList<String> {
        val defaultSelectedList = arrayListOf<String>()
        val defaultSelectedDatabase= MyDataBaseTool(context!!,"FitnessFlowDB",null,1)
        val defaultSelectedDataBaseTool=defaultSelectedDatabase.writableDatabase
        defaultSelectedDataBaseTool.beginTransaction()
        try{
            val dateLikeString = GetMonthInfo.getYearAndMonthString(year,month)
            val cursor=defaultSelectedDataBaseTool.rawQuery("Select Date From PlanDetailTable where Date like '$dateLikeString-%' Group By Date",
                null)
            while(cursor.moveToNext()){
                defaultSelectedList.add(cursor.getString(0))
            }
            cursor.close()
            defaultSelectedDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("DefaultSelectedList Init Failed(In PlanFragment):$e")
        }finally{
            defaultSelectedDataBaseTool.endTransaction()
            defaultSelectedDataBaseTool.close()
            defaultSelectedDatabase.close()
        }
        return defaultSelectedList
    }

    //日期点击事件监听
    override fun onItemClickListener(date: String) {
        dayDataInit(date,view!!.context)
    }

    override fun duringScaleAnimation(expansionAndContractionState: Int) {
        when (expansionAndContractionState){
            0->{
                val animationRotation = ValueAnimator.ofFloat(scaleAnimationButton!!.rotation,360f)
                animationRotation.addUpdateListener {
                    val rotation = it.animatedValue as Float
                    scaleAnimationButton!!.rotation = rotation
                }
                animationRotation.duration = 300
                animationRotation.start()
                if (!canRecyclerViewScroll){
                    canRecyclerViewScroll = true
                    layoutManager!!.setCanScrollVerticallyFlag(canRecyclerViewScroll)
                }
                this.expansionAndContractionState = 1
            }
            1->{
                val animationRotation = ValueAnimator.ofFloat(scaleAnimationButton!!.rotation,180f)
                animationRotation.addUpdateListener {
                    val rotation = it.animatedValue as Float
                    scaleAnimationButton!!.rotation = rotation
                }
                animationRotation.duration = 300
                animationRotation.start()
                if (canRecyclerViewScroll){
                    canRecyclerViewScroll = false
                    layoutManager!!.setCanScrollVerticallyFlag(canRecyclerViewScroll)
                }
                planRecyclerView!!.scrollToPosition(0)
                this.expansionAndContractionState = 0
            }
        }
    }

    //当默认标记日期有变化时，更新视图
    override fun onResume() {
        updateDefaultSelectedList()
        super.onResume()
    }

    fun updateDefaultSelectedList(){
        if (GetMonthInfo.getDefaultSelectedListChangedFlag()){
            fitCalendar!!.updateDefaultSelectedList()
        }
    }

    private fun getKeyInPlanDetailMap(id:Int):Action?{
        for (action in actionDetailMap.keys){
            if (action.actionID == id){
                return action
            }
        }
        return null
    }

    private fun dayDataInit(date:String,context: Context){
        actionIdList.clear()
        actionList.clear()
        actionDetailMap.clear()
        val planCheckDatabase=MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val planCheckTool=planCheckDatabase.writableDatabase
        planCheckTool.beginTransaction()
        try{
            val planDetailCursor=planCheckTool.rawQuery("Select * From PlanDetailTable where Date=\"$date\" Order By PlanOrder",null)
            while(planDetailCursor.moveToNext()){
                val actionDetailInPlan = ActionDetailInPlan(planDetailCursor.getString(0).toInt(),
                    planDetailCursor.getString(1).toInt(),planDetailCursor.getString(2),
                    planDetailCursor.getString(3).toInt(),planDetailCursor.getString(4),
                    planDetailCursor.getString(5).toFloat(),planDetailCursor.getString(6).toInt(),
                    planDetailCursor.getString(7).toInt(),planDetailCursor.getString(8).toInt(),
                    planDetailCursor.getString(10).toInt())
                if (actionIdList.contains(planDetailCursor.getString(0).toInt())){
                    actionDetailMap[getKeyInPlanDetailMap(actionDetailInPlan.actionID)]!!.add(actionDetailInPlan)
                }else{
                    actionIdList.add(planDetailCursor.getString(0).toInt())
                    //生成动作类，插入新键值
                    val actionSelectCursor=planCheckTool.rawQuery("Select * From ActionTable where ActionID=?",arrayOf(planDetailCursor.getString(0)))
                    while(actionSelectCursor.moveToNext()){
                        val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                            actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                            actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                            actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                        actionList.add(action)
                        actionDetailMap[action] = arrayListOf(actionDetailInPlan)
                        break
                    }
                    actionSelectCursor.close()
                }
            }
            planDetailCursor.close()
            planCheckTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("$date Data Check Failed(In PlanFragment):$e")
            MyToast(context,context.resources.getString(R.string.loading_failed)).showToast()
        }finally{
            planCheckTool.endTransaction()
            planCheckTool.close()
            planCheckDatabase.close()
        }
        adapter = AdapterInPlanFragment(actionList, actionDetailMap,context)
        planRecyclerView!!.adapter = adapter
    }

}