package com.example.fitnessflow.plan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessflow.R
import com.example.fitnessflow.fit_calendar.FitCalendarView

class PlanFragment : Fragment(),FitCalendarView.YearAndMonthChangedListener{

    //测试数据
    private val testDataList = arrayListOf<String>()
    //RecyclerView相关
    private var planRecyclerView:RecyclerView? = null
    private var layoutManager = LinearLayoutManager(this.activity)
    private var adapter:AdapterInPlanFragment? = null
    //日历相关
    private var fitCalendar:FitCalendarView? = null
    private var initRecyclerViewPosition = 0f
    private var recyclerViewMovedDistance = 0f
    private var yearAndMonthChangedListener:YearAndMonthChangedListener? = null
    private var setYearAndMonthChangedListenerFlag = false

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_plan,container,false)
    }

    //视图初始化
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view:View, savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        fitCalendar = view.findViewById(R.id.calendar_view_in_plan)
        println("111")
        view.findViewById<ImageButton>(R.id.expand_contract_button).setOnClickListener {
            val intent = Intent(view.context, PlanDetailActivity::class.java)
            startActivity(intent)
        }
        fitCalendar!!.setParentFragment(this)
        for (i in 1..30){
            testDataList.add(i.toString())
        }
        planRecyclerView = view.findViewById(R.id.rv_in_plan)
        planRecyclerView!!.layoutManager = layoutManager
        adapter = AdapterInPlanFragment(testDataList, view.context)
        planRecyclerView!!.adapter = adapter
        planRecyclerView!!.setOnTouchListener { _, event ->
            when(event!!.action){
                MotionEvent.ACTION_DOWN ->{
                    //要使用rawY，否则会抖动
                    initRecyclerViewPosition = event.rawY
                }
                MotionEvent.ACTION_MOVE->{
                    recyclerViewMovedDistance = event.rawY - initRecyclerViewPosition
                    initRecyclerViewPosition = event.rawY
                    if (recyclerViewMovedDistance <=0 || (recyclerViewMovedDistance>0 && layoutManager.findFirstVisibleItemPosition() == 0)){
                        fitCalendar!!.scrollerListener(recyclerViewMovedDistance)
                    }

                }
                MotionEvent.ACTION_UP->{
                    fitCalendar!!.startResetAnimation()
                }
            }
            false
        }
    }

    fun calendarJumpToday(){
        fitCalendar!!.jumpToToday()
    }


    fun resetRecyclerView(){
        planRecyclerView!!.scrollToPosition(0)
    }

    interface YearAndMonthChangedListener{
        fun onYearAndMonthChangedListener(year: Int,month: Int)
    }

    fun setYearAndMonthChangedListener(yearAndMonthChangedListener:YearAndMonthChangedListener){
        this.yearAndMonthChangedListener = yearAndMonthChangedListener
        println("222")
        fitCalendar!!.setYearAndMonthChangedListener(this)
    }



    override fun onYearAndMonthChangedListener(year: Int, month: Int) {
        if (this.yearAndMonthChangedListener!=null){
            this.yearAndMonthChangedListener!!.onYearAndMonthChangedListener(year,month)
        }
    }

}