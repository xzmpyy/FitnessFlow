package com.example.zhangjie.fitnessflow.utils_class

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Xml
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.fit_calendar.*
import java.util.*

class CalendarDialog : DialogFragment(){

    private var dateSelectedListener:DateSelectedListener?=null
    private var parentLayout:LinearLayout? = null
    //三个部位
    private var fitCalendarView:View? = null
    private var dateInfoView:CalendarDateInfoView? = null
    private var weekTitle:CalendarWeekTitleView? = null
    //当前日期
    private val yearNow = Calendar.getInstance().get(Calendar.YEAR)
    private val monthNow = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val dateNowString = GetMonthInfo.getYearAndMonthString(yearNow,monthNow)
    //月视图
    private var viewPager:CalendarViewPager? = null
    private var leftYear:Int? = null
    private var leftMonth:Int? = null
    private var leftDefaultSelectedList:ArrayList<String>? = null
    private var middleLeftYear:Int? = null
    private var middleLeftMonth:Int? = null
    private var middleLeftDefaultSelectedList:ArrayList<String>? = null
    private var middleRightYear:Int? = null
    private var middleRightMonth:Int? = null
    private var middleRightDefaultSelectedList:ArrayList<String>? = null
    private var rightYear:Int? = null
    private var rightMonth:Int? = null
    private var rightDefaultSelectedList:ArrayList<String>? = null
    private var fragmentLeft :CalendarMonthFragment? = null
    private var fragmentMiddleLeft:CalendarMonthFragment? = null
    private var fragmentMiddleRight:CalendarMonthFragment? = null
    private var fragmentRight :CalendarMonthFragment? = null
    private var monthFragmentList:List<Fragment>? = null
    //Handler实例
    private var myHandler=MyHandler(this)
    private var selectMode = 1

    //设置Fragment宽高
    override fun onStart(){
        super.onStart()
        val dialogWindow=dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        dialogWindow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        //dialog的位置
        dialogWindow.setGravity(Gravity.BOTTOM)
        val viewPagerLayoutParams = FrameLayout.LayoutParams(viewPager!!.layoutParams)
        val marginTop = ScreenInfoClass.getScreenWidthPx(this.context!!)/7*2
        viewPagerLayoutParams.topMargin = marginTop
        viewPager!!.layoutParams = viewPagerLayoutParams
        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
    }

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        //设置动画
        dialog.window!!.setWindowAnimations(R.style.dialog_pop)
        return inflater.inflate(R.layout.calendar_dialog,container,false)
    }

    //视图初始化
    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        //fitCalendar = view.findViewById(R.id.calendar_view_in_plan)
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            this.dismiss()
        }
        view.findViewById<Button>(R.id.confirm_button).setOnClickListener {
            if (dateSelectedListener!=null){
                dateSelectedListener!!.onDateConfirmButtonClick()
            }
            this.dismiss()
        }
        parentLayout = view.findViewById(R.id.parent_layout)
        monthFragmentInit()
        //日历相关
        fitCalendarView= LayoutInflater.from(context).inflate(R.layout.calendar_view,parentLayout,false)!!
        val group = fitCalendarView!!.findViewById<LinearLayout>(R.id.calendar_view_group)
        //年月信息栏
        var parser = resources.getXml(R.xml.date_info)
        val attributesForDateInfo = Xml.asAttributeSet(parser)
        dateInfoView = CalendarDateInfoView(context!!,attributesForDateInfo,yearNow,monthNow)
        group.addView(dateInfoView)
        //星期栏
        //年月信息栏
        parser = resources.getXml(R.xml.week_title)
        val attributesForWeekTitle = Xml.asAttributeSet(parser)
        weekTitle = CalendarWeekTitleView(this.context!!,attributesForWeekTitle)
        group.addView(weekTitle)
        //月视图栏
        viewPager = fitCalendarView!!.findViewById(R.id.month_view_pager)
        viewPager!!.offscreenPageLimit = 3
        fragmentLeft = CalendarMonthFragment().getLuLuMonthFragment(leftYear!!,leftMonth!!,selectMode,leftDefaultSelectedList!!)
        fragmentMiddleLeft = CalendarMonthFragment().getLuLuMonthFragment(middleLeftYear!!,middleLeftMonth!!,selectMode,middleLeftDefaultSelectedList!!)
        fragmentMiddleRight = CalendarMonthFragment().getLuLuMonthFragment(middleRightYear!!,middleRightMonth!!,selectMode,middleRightDefaultSelectedList!!)
        fragmentRight = CalendarMonthFragment().getLuLuMonthFragment(rightYear!!,rightMonth!!,selectMode,rightDefaultSelectedList!!)
        monthFragmentList = listOf(fragmentLeft as Fragment,fragmentMiddleLeft as Fragment,fragmentMiddleRight as Fragment,fragmentRight as Fragment)
        val myAdapter = CalendarViewPagerAdapter(this.childFragmentManager, monthFragmentList!!)
        viewPager!!.adapter = myAdapter
        viewPager!!.currentItem = 1
        //重设ViewPager的滑动速度
        val myScroll = ViewPagerScroll(context)
        myScroll.setScrollDuration(800)
        myScroll.initViewPagerScrollDuration(viewPager!!)
        //ViewPager设置监听
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnAdapterChangeListener,
            ViewPager.OnPageChangeListener{
            override fun onAdapterChanged(p0: ViewPager, p1: PagerAdapter?, p2: PagerAdapter?) {
            }

            //滑动状态监听,切换页面
            override fun onPageScrollStateChanged(p0: Int) {
                //1时表示正在滑动、2时表示滑动完毕、0时什么都没做，滑动时变化顺序为1>2>0
                //在0时设置页面位置互换
                if (p0 == 0){
                    when (viewPager!!.currentItem) {
                        0 -> {
                            updateDateInfo(2,0)
                            fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                            fragmentMiddleRight!!.updateMonthView()
                            viewPager!!.setCurrentItem(2, false)
                            Thread{
                                updateDateInfo(1,0)
                                updateDateInfo(3,0)
                                updateDateInfo(0,0)
                                fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                                fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                                fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                                myHandler.sendEmptyMessage(0)
                            }.start()
                        }
                        3 -> {
                            updateDateInfo(1,1)
                            fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                            fragmentMiddleLeft!!.updateMonthView()
                            viewPager!!.setCurrentItem(1, false)
                            Thread{
                                updateDateInfo(2,1)
                                updateDateInfo(0,1)
                                updateDateInfo(3,1)
                                fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                                fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                                fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                                myHandler.sendEmptyMessage(3)
                            }.start()
                        }
                    }
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            //滑动即将完成时，更新年月信息
            override fun onPageSelected(p0: Int) {
                when (p0){
                    0->{
                        dateInfoView!!.setMothText(leftMonth!!)
                        dateInfoView!!.setYearText(leftYear!!)
                        dateInfoView!!.invalidate()
                    }
                    1->{
                        dateInfoView!!.setMothText(middleLeftMonth!!)
                        dateInfoView!!.setYearText(middleLeftYear!!)
                        dateInfoView!!.invalidate()
                    }
                    2->{
                        dateInfoView!!.setMothText(middleRightMonth!!)
                        dateInfoView!!.setYearText(middleRightYear!!)
                        dateInfoView!!.invalidate()

                    }
                    3->{
                        dateInfoView!!.setMothText(rightMonth!!)
                        dateInfoView!!.setYearText(rightYear!!)
                        dateInfoView!!.invalidate()
                    }
                }
            }

        })
        parentLayout!!.addView(fitCalendarView)
    }


    override fun onDismiss(dialog: DialogInterface?) {
        if (dateSelectedListener!=null){
            dateSelectedListener!!.onDateCancelButtonClick()
        }
        super.onDismiss(dialog)
    }

    interface DateSelectedListener{
        fun onDateConfirmButtonClick()

        fun onDateCancelButtonClick()
    }

    fun setDateSelectedListener(dateSelectedListener:DateSelectedListener){
        this.dateSelectedListener = dateSelectedListener
    }

    private fun monthFragmentInit(){
        //初始化四个Fragment对应的日期
        middleLeftMonth = monthNow
        middleLeftYear = yearNow
        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftMonth!!, middleLeftYear!!)
        when (middleLeftMonth) {
            1 -> {
                leftMonth = 12
                leftYear = middleLeftYear!! -1
                middleRightMonth = middleLeftMonth!! + 1
                middleRightYear = middleLeftYear!!
                rightMonth = middleLeftMonth!! + 2
                rightYear = middleLeftYear!!
            }
            11 -> {
                leftMonth = middleLeftMonth!! - 1
                leftYear = middleLeftYear!!
                middleRightMonth = middleLeftMonth!! + 1
                middleRightYear = middleLeftYear!!
                rightMonth = 1
                rightYear = middleLeftYear!! + 1
            }
            12 -> {
                leftMonth = middleLeftMonth!! - 1
                leftYear = middleLeftYear!!
                middleRightMonth = 1
                middleRightYear = middleLeftYear!! + 1
                rightMonth = 2
                rightYear = middleLeftYear!! + 1
            }
            else -> {
                leftMonth = middleLeftMonth!! - 1
                leftYear = middleLeftYear!!
                middleRightMonth = middleLeftMonth!! + 1
                middleRightYear = middleLeftYear!!
                rightMonth = middleLeftMonth!! + 2
                rightYear = middleLeftYear!!
            }
        }
        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
    }


    //获取默认标记列表
    private fun getDefaultSelectedList(year:Int,month:Int):ArrayList<String>{
        val defaultSelectedList = arrayListOf<String>()
        val yearAndMonthText = GetMonthInfo.getYearAndMonthString(year,month)
        for (i in 11..15){
            defaultSelectedList.add("$yearAndMonthText-$i")
        }
        return defaultSelectedList
    }

    //数据更新
    private fun updateDateInfo(fragmentPosition: Int, lastOrNext:Int){
        when(fragmentPosition){
            0->{
                leftDefaultSelectedList = if (lastOrNext == 0){
                    getLastMonth(0)
                    getDefaultSelectedList(leftYear!!,leftMonth!!)
                }else{
                    getNextMonth(0)
                    getDefaultSelectedList(leftYear!!,leftMonth!!)
                }
            }
            1->{
                middleLeftDefaultSelectedList = if (lastOrNext == 0){
                    getLastMonth(1)
                    getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                }else{
                    getNextMonth(1)
                    getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                }
            }
            2->{
                middleRightDefaultSelectedList = if (lastOrNext == 0){
                    getLastMonth(2)
                    getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                }else{
                    getNextMonth(2)
                    getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                }
            }
            3->{
                rightDefaultSelectedList = if (lastOrNext == 0){
                    getLastMonth(3)
                    getDefaultSelectedList(rightYear!!,rightMonth!!)
                }else{
                    getNextMonth(3)
                    getDefaultSelectedList(rightYear!!,rightMonth!!)
                }
            }
        }
    }

    //从0跳2时
    private fun getLastMonth(fragmentPosition: Int){
        when (fragmentPosition){
            0 ->{
                if (leftMonth!=2 && leftMonth!=1){
                    leftMonth = leftMonth!! -2
                }else if (leftMonth == 2){
                    leftMonth = 12
                    leftYear = leftYear!! - 1
                }else if (leftMonth == 1){
                    leftMonth = 11
                    leftYear = leftYear!! - 1
                }
            }
            1->{
                if (middleLeftMonth!=2 && middleLeftMonth!=1){
                    middleLeftMonth = middleLeftMonth!! -2
                }else if(middleLeftMonth == 2){
                    middleLeftMonth = 12
                    middleLeftYear = middleLeftYear!! - 1
                }else if (middleLeftMonth==1){
                    middleLeftMonth = 11
                    middleLeftYear = middleLeftYear!! - 1
                }
            }
            2->{
                middleRightMonth = leftMonth
                middleRightYear = leftYear
            }
            3->{
                if (rightMonth!=2 && rightMonth!=1){
                    rightMonth = rightMonth!! -2
                }else if (rightMonth == 2){
                    rightMonth = 12
                    rightYear = rightYear!! - 1
                }else if (rightMonth == 1){
                    rightMonth = 11
                    rightYear = rightYear!! - 1
                }
            }
        }
    }

    //从3跳1时
    private fun getNextMonth(fragmentPosition: Int){
        when (fragmentPosition){
            0 ->{
                if (leftMonth!=12 && leftMonth!=11){
                    leftMonth = leftMonth!! +2
                }else if (leftMonth == 11){
                    leftMonth = 1
                    leftYear = leftYear!! + 1
                }else if (leftMonth == 12){
                    leftMonth = 2
                    leftYear = leftYear!! + 1
                }
            }
            1->{
                middleLeftMonth = rightMonth
                middleLeftYear = rightYear
            }
            2->{
                if (middleRightMonth!=11 && middleRightMonth!=12){
                    middleRightMonth = middleRightMonth!! +2
                }else if (middleRightMonth == 11){
                    middleRightMonth = 1
                    middleRightYear = middleRightYear!! + 1
                }else if (middleRightMonth == 12){
                    middleRightMonth = 2
                    middleRightYear = middleRightYear!! + 1
                }
            }
            3->{
                if (rightMonth!=11 && rightMonth!=12){
                    rightMonth = rightMonth!! +2
                }else if (rightMonth == 11){
                    rightMonth = 1
                    rightYear = rightYear!! + 1
                }else if (rightMonth == 12){
                    rightMonth = 2
                    rightYear = rightYear!! + 1
                }
            }
        }
    }

    companion object {
        //自定义的Handler类
        class MyHandler(private val dialog: CalendarDialog) : Handler(){
            override fun handleMessage(msg: Message?){
                //如果该消息是本程序发送的
                if (msg!!.what == 0){
                    dialog.fragmentMiddleLeft!!.updateMonthView()
                    dialog.fragmentRight!!.updateMonthView()
                    dialog.fragmentLeft!!.updateMonthView()
                }
                if (msg.what == 3){
                    dialog.fragmentMiddleRight!!.updateMonthView()
                    dialog.fragmentRight!!.updateMonthView()
                    dialog.fragmentLeft!!.updateMonthView()
                }
            }
        }
    }

    //跳转今日
    fun jumpToToday(){
        when (viewPager!!.currentItem){
            1->{
                val dateString = GetMonthInfo.getYearAndMonthString(middleLeftYear!!,middleLeftMonth!!)
                when(GetMonthInfo.compareYearAndMonth(dateString,dateNowString)){
                    0->{
                        middleRightYear = yearNow
                        middleRightMonth = monthNow
                        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                        fragmentMiddleRight!!.updateMonthView()
                        viewPager!!.currentItem = 2
                        when (monthNow) {
                            2 -> {
                                middleLeftYear = yearNow
                                middleLeftMonth = 1
                                leftYear = yearNow -1
                                leftMonth = 12
                            }
                            1 -> {
                                middleLeftYear = yearNow - 1
                                middleLeftMonth = 12
                                leftYear = yearNow -1
                                leftMonth = 11
                            }
                            else -> {
                                middleLeftYear = yearNow
                                middleLeftMonth = monthNow - 1
                                leftYear = yearNow
                                leftMonth = monthNow - 2
                            }
                        }
                        if (monthNow == 12){
                            rightYear = yearNow + 1
                            rightMonth = 1
                        }else{
                            rightYear = yearNow
                            rightMonth = monthNow + 1
                        }
                        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                        fragmentMiddleLeft!!.updateMonthView()
                        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
                        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                        fragmentLeft!!.updateMonthView()
                        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
                        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                        fragmentRight!!.updateMonthView()
                    }
                    2->{
                        leftYear = yearNow
                        leftMonth = monthNow
                        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
                        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                        fragmentLeft!!.updateMonthView()
                        viewPager!!.currentItem = 0
                        middleLeftYear = yearNow
                        middleLeftMonth = monthNow
                        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                        fragmentMiddleLeft!!.updateMonthView()
                        viewPager!!.setCurrentItem(1,false)
                        if (monthNow == 1){
                            leftYear = yearNow - 1
                            leftMonth = 12
                        }else{
                            leftYear = yearNow
                            leftMonth = monthNow - 1
                        }
                        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
                        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                        fragmentLeft!!.updateMonthView()
                        when (monthNow) {
                            11 -> {
                                middleRightYear = yearNow
                                middleRightMonth = monthNow + 1
                                rightYear = yearNow + 1
                                rightMonth = 1
                            }
                            12 -> {
                                middleRightYear = yearNow + 1
                                middleRightMonth = 1
                                rightYear = yearNow + 1
                                rightMonth = 2
                            }
                            else -> {
                                middleRightYear = yearNow
                                middleRightMonth = monthNow + 1
                                rightYear = yearNow
                                rightMonth = monthNow + 2
                            }
                        }
                        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                        fragmentMiddleRight!!.updateMonthView()
                        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
                        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                        fragmentRight!!.updateMonthView()
                    }
                }

            }
            2->{
                val dateString = GetMonthInfo.getYearAndMonthString(middleRightYear!!,middleRightMonth!!)
                when(GetMonthInfo.compareYearAndMonth(dateString,dateNowString)){
                    0->{
                        rightYear = yearNow
                        rightMonth = monthNow
                        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
                        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                        fragmentRight!!.updateMonthView()
                        viewPager!!.currentItem = 3
                        middleRightYear = yearNow
                        middleRightMonth = monthNow
                        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                        fragmentMiddleRight!!.updateMonthView()
                        viewPager!!.setCurrentItem(2,false)
                        when (monthNow) {
                            2 -> {
                                middleLeftYear = yearNow
                                middleLeftMonth = 1
                                leftYear = yearNow -1
                                leftMonth = 12
                            }
                            1 -> {
                                middleLeftYear = yearNow - 1
                                middleLeftMonth = 12
                                leftYear = yearNow -1
                                leftMonth = 11
                            }
                            else -> {
                                middleLeftYear = yearNow
                                middleLeftMonth = monthNow - 1
                                leftYear = yearNow
                                leftMonth = monthNow - 2
                            }
                        }
                        if (monthNow == 12){
                            rightYear = yearNow + 1
                            rightMonth = 1
                        }else{
                            rightYear = yearNow
                            rightMonth = monthNow + 1
                        }
                        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                        fragmentMiddleLeft!!.updateMonthView()
                        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
                        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                        fragmentLeft!!.updateMonthView()
                        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
                        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                        fragmentRight!!.updateMonthView()
                    }
                    2->{
                        middleLeftYear = yearNow
                        middleLeftMonth = monthNow
                        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
                        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
                        fragmentMiddleLeft!!.updateMonthView()
                        viewPager!!.currentItem = 1
                        if (monthNow == 1){
                            leftYear = yearNow - 1
                            leftMonth = 12
                        }else{
                            leftYear = yearNow
                            leftMonth = monthNow - 1
                        }
                        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
                        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
                        fragmentLeft!!.updateMonthView()
                        when (monthNow) {
                            11 -> {
                                middleRightYear = yearNow
                                middleRightMonth = monthNow + 1
                                rightYear = yearNow + 1
                                rightMonth = 1
                            }
                            12 -> {
                                middleRightYear = yearNow + 1
                                middleRightMonth = 1
                                rightYear = yearNow + 1
                                rightMonth = 2
                            }
                            else -> {
                                middleRightYear = yearNow
                                middleRightMonth = monthNow + 1
                                rightYear = yearNow
                                rightMonth = monthNow + 2
                            }
                        }
                        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
                        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
                        fragmentMiddleRight!!.updateMonthView()
                        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
                        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
                        fragmentRight!!.updateMonthView()
                    }
                }
            }
        }
    }

}