package com.example.zhangjie.fitnessflow.plan

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.fit_calendar.FitCalendarView
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import java.lang.Exception

class PlanFragment : Fragment(),FitCalendarView.YearAndMonthChangedListener,
    FitCalendarView.DefaultSelectedListGenerator,FitCalendarView.ItemClickListener,
FitCalendarView.ScaleAnimationListener{

    //测试数据
    private val testDataList = arrayListOf<String>()
    //RecyclerView相关
    private var planRecyclerView:RecyclerView? = null
    private val layoutManager = LinearLayoutManager(this.activity)
    private var adapter:AdapterInPlanFragment? = null
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
        for (i in 1..30){
            testDataList.add(i.toString())
        }
        planRecyclerView = view.findViewById(R.id.rv_in_plan)
        planRecyclerView!!.layoutManager = layoutManager
        adapter = AdapterInPlanFragment(testDataList, view.context)
        planRecyclerView!!.adapter = adapter
        planRecyclerView!!.setOnTouchListener { _, event ->
            try{
                when(event!!.action){
                    MotionEvent.ACTION_DOWN ->{
                        //要使用rawY，否则会抖动
                        initRecyclerViewPosition = event.rawY
                    }
                    MotionEvent.ACTION_MOVE->{
                        recyclerViewMovedDistance = event.rawY - initRecyclerViewPosition
                        initRecyclerViewPosition = event.rawY
                        if (recyclerViewMovedDistance <-2 || (recyclerViewMovedDistance>2 && layoutManager.findFirstVisibleItemPosition() == 0)){
                            fitCalendar!!.scrollerListener(recyclerViewMovedDistance)
                        }
                    }
                    MotionEvent.ACTION_UP->{
                        fitCalendar!!.startResetAnimation()
                        initRecyclerViewPosition = 0f
                    }
                }
            }catch (exception:Exception){
                println(exception)
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
        val yearAndMonthText = GetMonthInfo.getYearAndMonthString(year,month)
        for (i in 11..15){
            defaultSelectedList.add("$yearAndMonthText-$i")
        }
        return defaultSelectedList
    }

    //日期点击事件监听
    override fun onItemClickListener(date: String) {
        println(date)
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
                this.expansionAndContractionState = 0
            }
        }
    }


}