package com.example.fitnessflow.fit_calendar

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.fitnessflow.R
import java.util.*
import kotlin.collections.ArrayList

class FitCalendarView (context: Context?, attrs: AttributeSet?):
    LinearLayout(context,attrs),CalendarMonthFragment.ExpansionAndContractionLimitedChangedListener,CalendarMonthFragment.ItemClickListener{

    private var selectMode:Int? = null

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
    //生成默认选中列表项的接口实例
    private var defaultSelectedListGenerator:DefaultSelectedListGenerator? = null
    private var itemClickListener:ItemClickListener? = null
    //Handler实例
    private val myHandler= MyHandler(this)
    //初始化月视图的Margin标识
    private var isMonthViewInit = true
    private var viewLayoutParams: LayoutParams? = null
    private var viewPagerLayoutParams:FrameLayout.LayoutParams? = null
    //月视图伸缩变化相关
    private var maxHeightChange:Float? = null
    private var maxMarginChange:Float? = null
    private var fitCalendarInitHeight:Float? = null
    private var monthViewInitMargin:Int? = null
    private var minMargin:Int? = null
    private var currentItemChanged = false
    //0高度变化阶段,1margin变化阶段
    private var expansionAndContractionState = 0
    private var yearAndMonthChangedListener:YearAndMonthChangedListener? = null
    //监听伸缩动画的接口
    private var scaleAnimationListener:ScaleAnimationListener? = null

    init {
        //获取attrs中的值，如果不为空，则进行组件的修改
        val attrsList=context!!.obtainStyledAttributes(attrs, R.styleable.FitCalendarView)
        selectMode = attrsList.getInt(R.styleable.FitCalendarView_selectMode,0)
        attrsList.recycle()
        monthFragmentInit()
        initView(context)
        this.addView(fitCalendarView)
    }

    private fun initView(context: Context?){
        fitCalendarView= LayoutInflater.from(context).inflate(R.layout.calendar_view,this,false)!!
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
        weekTitle = CalendarWeekTitleView(context,attributesForWeekTitle)
        group.addView(weekTitle)
        //月视图栏
        viewPager = fitCalendarView!!.findViewById<CalendarViewPager>(R.id.month_view_pager)
        viewPager!!.offscreenPageLimit = 3
        fragmentLeft = CalendarMonthFragment().getLuLuMonthFragment(leftYear!!,leftMonth!!,selectMode!!,leftDefaultSelectedList!!)
        fragmentMiddleLeft = CalendarMonthFragment().getLuLuMonthFragment(middleLeftYear!!,middleLeftMonth!!,selectMode!!,middleLeftDefaultSelectedList!!)
        fragmentMiddleRight = CalendarMonthFragment().getLuLuMonthFragment(middleRightYear!!,middleRightMonth!!,selectMode!!,middleRightDefaultSelectedList!!)
        fragmentRight = CalendarMonthFragment().getLuLuMonthFragment(rightYear!!,rightMonth!!,selectMode!!,rightDefaultSelectedList!!)
        monthFragmentList = listOf(fragmentLeft as Fragment,fragmentMiddleLeft as Fragment,fragmentMiddleRight as Fragment,fragmentRight as Fragment)
        val thisActivity = context as FragmentActivity
        val myAdapter = CalendarViewPagerAdapter(thisActivity.supportFragmentManager, monthFragmentList!!)
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
                            currentItemChanged = true
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
                            currentItemChanged = true
                        }
                        1->{
                            currentItemChanged = true
                        }
                        2->{
                            currentItemChanged = true
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
                        if (yearAndMonthChangedListener != null){
                            yearAndMonthChangedListener!!.onYearAndMonthChangedListener(leftYear!!,leftMonth!!)
                        }
                    }
                    1->{
                        dateInfoView!!.setMothText(middleLeftMonth!!)
                        dateInfoView!!.setYearText(middleLeftYear!!)
                        dateInfoView!!.invalidate()
                        if (yearAndMonthChangedListener != null){
                            yearAndMonthChangedListener!!.onYearAndMonthChangedListener(middleLeftYear!!,middleLeftMonth!!)
                        }
                    }
                    2->{
                        dateInfoView!!.setMothText(middleRightMonth!!)
                        dateInfoView!!.setYearText(middleRightYear!!)
                        dateInfoView!!.invalidate()
                        if (yearAndMonthChangedListener != null){
                            yearAndMonthChangedListener!!.onYearAndMonthChangedListener(middleRightYear!!,middleRightMonth!!)
                        }
                    }
                    3->{
                        dateInfoView!!.setMothText(rightMonth!!)
                        dateInfoView!!.setYearText(rightYear!!)
                        dateInfoView!!.invalidate()
                        if (yearAndMonthChangedListener != null){
                            yearAndMonthChangedListener!!.onYearAndMonthChangedListener(rightYear!!,rightMonth!!)
                        }
                    }
                }
            }

        })
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
        if (defaultSelectedListGenerator!=null){
            return defaultSelectedListGenerator!!.setDefaultSelectedList(year,month)
        }
        return arrayListOf()
    }
    //自定义默认选中项的接口
    interface DefaultSelectedListGenerator{
        fun setDefaultSelectedList(year:Int,month:Int):ArrayList<String>
    }
    fun setDefaultSelectedListGenerator(defaultSelectedListGenerator:DefaultSelectedListGenerator){
        this.defaultSelectedListGenerator = defaultSelectedListGenerator
        middleLeftDefaultSelectedList = getDefaultSelectedList(middleLeftYear!!,middleLeftMonth!!)
        fragmentMiddleLeft!!.updateData(middleLeftYear!!,middleLeftMonth!!,middleLeftDefaultSelectedList!!)
        leftDefaultSelectedList = getDefaultSelectedList(leftYear!!,leftMonth!!)
        fragmentLeft!!.updateData(leftYear!!,leftMonth!!,leftDefaultSelectedList!!)
        middleRightDefaultSelectedList = getDefaultSelectedList(middleRightYear!!,middleRightMonth!!)
        fragmentMiddleRight!!.updateData(middleRightYear!!,middleRightMonth!!,middleRightDefaultSelectedList!!)
        rightDefaultSelectedList = getDefaultSelectedList(rightYear!!,rightMonth!!)
        fragmentRight!!.updateData(rightYear!!,rightMonth!!,rightDefaultSelectedList!!)
    }

    interface ItemClickListener{
        fun onItemClickListener(date:String)
    }

    fun setItemClickListener(itemClickListener:ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun onItemClickListener(date: String) {
        if (this.itemClickListener != null){
            this.itemClickListener!!.onItemClickListener(date)
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

    //从0跳2时
    private fun getNextMonth(fragmentPosition: Int){
        when (fragmentPosition){
            0 ->{
                if (leftMonth!=10 && leftMonth!=11){
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SelectedItemClass.clear()
    }


    companion object {
        //自定义的Handler类
        class MyHandler(private val fitCalendarView: FitCalendarView) : Handler(){
            override fun handleMessage(msg: Message?){
                //如果该消息是本程序发送的
                if (msg!!.what == 0){
                    fitCalendarView.fragmentMiddleLeft!!.updateMonthView()
                    fitCalendarView.fragmentRight!!.updateMonthView()
                    fitCalendarView.fragmentLeft!!.updateMonthView()
                }
                if (msg.what == 3){
                    fitCalendarView.fragmentMiddleRight!!.updateMonthView()
                    fitCalendarView.fragmentRight!!.updateMonthView()
                    fitCalendarView.fragmentLeft!!.updateMonthView()
                }
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = (widthSize/ 7)*8
        if (layoutParams!!.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
            fitCalendarInitHeight = height.toFloat()
        }
    }


    //首次绘制时，由于FrameLayout的layout_height是wrap_content，因此需要设置margin才能出现置底效果
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (isMonthViewInit){
            viewPagerLayoutParams = FrameLayout.LayoutParams(viewPager!!.layoutParams)
            val marginTop = this.width/7*2
            monthViewInitMargin = marginTop
            viewPagerLayoutParams!!.topMargin = marginTop
            viewPager!!.layoutParams = viewPagerLayoutParams
            isMonthViewInit = false
            fragmentMiddleLeft!!.setExpansionAndContractionLimitedChangedListener(this)
            fragmentMiddleRight!!.setExpansionAndContractionLimitedChangedListener(this)
            fragmentMiddleLeft!!.setItemClickListener(this)
            fragmentMiddleRight!!.setItemClickListener(this)
        }
        super.onWindowFocusChanged(hasWindowFocus)
    }

    fun scrollerListener(distance:Float) {
        //伸缩限制设置
        getScrollerLimited()
        //根据滑动距离改变height和margin
        viewLayoutParams!!.height = this.height + distance.toInt()
        if (expansionAndContractionState == 0) {
            if (viewLayoutParams!!.height.toFloat() < fitCalendarInitHeight!! && viewLayoutParams!!.height.toFloat() >maxHeightChange!!) {
                this.layoutParams = viewLayoutParams
            } else if (viewLayoutParams!!.height.toFloat() <= maxHeightChange!!) {
                viewLayoutParams!!.height = maxHeightChange!!.toInt()
                this.layoutParams = viewLayoutParams
                expansionAndContractionState = 1
            } else if (viewLayoutParams!!.height.toFloat() >= fitCalendarInitHeight!! && this.measuredHeight != fitCalendarInitHeight!!.toInt()) {
                viewLayoutParams!!.height = fitCalendarInitHeight!!.toInt()
                this.layoutParams = viewLayoutParams
                viewPager!!.setCanScrollHorizontally(true)
                if (scaleAnimationListener!=null){
                    scaleAnimationListener!!.duringScaleAnimation(1)
                }
            }
        } else if (expansionAndContractionState == 1) {
            val targetMargin = viewPagerLayoutParams!!.topMargin + distance.toInt()
            if (targetMargin< monthViewInitMargin!! && targetMargin>minMargin!!){
                this.layoutParams = viewLayoutParams
                viewPagerLayoutParams!!.topMargin = targetMargin
                viewPager!!.layoutParams = viewPagerLayoutParams
            }else if (targetMargin<=minMargin!! && viewPagerLayoutParams!!.topMargin != minMargin!!){
                viewLayoutParams!!.height = maxMarginChange!!.toInt()
                this.layoutParams = viewLayoutParams
                viewPagerLayoutParams!!.topMargin = minMargin!!
                viewPager!!.layoutParams = viewPagerLayoutParams
                viewPager!!.setCanScrollHorizontally(false)
                if (scaleAnimationListener!=null){
                    scaleAnimationListener!!.duringScaleAnimation(0)
                }
            }else if (viewLayoutParams!!.height.toFloat() >= maxHeightChange!! && viewPagerLayoutParams!!.topMargin != monthViewInitMargin!!){
                viewLayoutParams!!.height = maxHeightChange!!.toInt()
                this.layoutParams = viewLayoutParams
                viewPagerLayoutParams!!.topMargin = monthViewInitMargin!!
                viewPager!!.layoutParams = viewPagerLayoutParams
                expansionAndContractionState = 0
            }else if (monthViewInitMargin!! == minMargin!!){
                expansionAndContractionState = 0
                if (scaleAnimationListener!=null){
                    scaleAnimationListener!!.duringScaleAnimation(0)
                }
            }
        }
    }

    override fun onExpansionAndContractionLimitedChanged(monthView: CalendarMonthView) {
        val changeLimited = monthView.getTheExpansionAndContractionLimited()
        maxHeightChange = fitCalendarInitHeight!! - changeLimited[0]
        maxMarginChange = fitCalendarInitHeight!! - changeLimited[0] - changeLimited[1]
        minMargin = (monthViewInitMargin!! - changeLimited[1]).toInt()
        if (currentItemChanged){
            when(viewPager!!.currentItem){
                1->{
                    Thread{myHandler.sendEmptyMessage(3)}.start()
                    currentItemChanged = false
                }
                2->{
                    Thread{myHandler.sendEmptyMessage(0)}.start()
                    currentItemChanged =false
                }
            }
        }
    }

    fun startResetAnimation(){
        //自动展开
        if (this.height >= this.width/7*5 && this.height<fitCalendarInitHeight!!.toInt()){
            if (scaleAnimationListener!=null){
                scaleAnimationListener!!.duringScaleAnimation(1)
            }
            val animationHeight = ValueAnimator.ofInt(this.height,fitCalendarInitHeight!!.toInt())
            animationHeight.addUpdateListener {
                val height = it.animatedValue
                viewLayoutParams!!.height = height as Int
                this.layoutParams = viewLayoutParams
            }
            animationHeight.duration = 300
            animationHeight.start()
            val animationMargin = ValueAnimator.ofInt(viewPagerLayoutParams!!.topMargin,monthViewInitMargin!!.toInt())
            animationMargin.addUpdateListener {
                val margin = it.animatedValue
                viewPagerLayoutParams!!.topMargin = margin as Int
                viewPager!!.layoutParams = viewPagerLayoutParams!!
            }
            animationMargin.duration = 300
            animationMargin.start()
            viewPager!!.setCanScrollHorizontally(true)
            expansionAndContractionState = 0
        }
        //自动收缩
        else if (this.height < this.width/7*5 && this.height > maxMarginChange!!){
            if (scaleAnimationListener!=null){
                scaleAnimationListener!!.duringScaleAnimation(0)
            }
            val animationHeight = ValueAnimator.ofInt(this.height,maxMarginChange!!.toInt())
            animationHeight.addUpdateListener {
                val height = it.animatedValue
                viewLayoutParams!!.height = height as Int
                this.layoutParams = viewLayoutParams
            }
            val animationMargin = ValueAnimator.ofInt(viewPagerLayoutParams!!.topMargin,minMargin!!)
            animationMargin.addUpdateListener {
                val margin = it.animatedValue
                viewPagerLayoutParams!!.topMargin = margin as Int
                viewPager!!.layoutParams = viewPagerLayoutParams!!
            }
            animationHeight.duration = 300
            animationMargin.duration = 300
            animationMargin.start()
            animationHeight.start()
            viewPager!!.setCanScrollHorizontally(false)
            expansionAndContractionState = 1
        }
    }

    //监听年月变化
    interface YearAndMonthChangedListener{

        fun onYearAndMonthChangedListener(year: Int,month: Int)

    }

    fun setYearAndMonthChangedListener(yearAndMonthChangedListener:YearAndMonthChangedListener){
        this.yearAndMonthChangedListener = yearAndMonthChangedListener
    }

    fun reStart(){
        when(viewPager!!.currentItem){
            1->{
                Thread{myHandler.sendEmptyMessage(3)}.start()
            }
            2->{
                Thread{myHandler.sendEmptyMessage(0)}.start()
            }
        }
        currentItemChanged = true
    }

    //设置height和margin变化限制
    private fun getScrollerLimited(){
        //第一次初始化
        if (viewLayoutParams == null) {
            viewLayoutParams = this.layoutParams as LayoutParams
            viewLayoutParams!!.height = this.measuredHeight
        }
        if (maxHeightChange == null) {
            when (viewPager!!.currentItem) {
                1 -> {
                    val changeLimited = fragmentMiddleLeft!!.getScrollerLimited()
                    maxHeightChange = fitCalendarInitHeight!! - changeLimited[0]
                    maxMarginChange = fitCalendarInitHeight!! - changeLimited[0] - changeLimited[1]
                    minMargin = (monthViewInitMargin!! - changeLimited[1]).toInt()
                }
                2 -> {
                    val changeLimited = fragmentMiddleRight!!.getScrollerLimited()
                    maxHeightChange = fitCalendarInitHeight!! - changeLimited[0]
                    maxMarginChange = fitCalendarInitHeight!! - changeLimited[0] - changeLimited[1]
                    minMargin = (monthViewInitMargin!! - changeLimited[1]).toInt()
                }
            }
            currentItemChanged = false
        }
        //页面变化时
        if (currentItemChanged) {
            when (viewPager!!.currentItem) {
                1 -> {
                    val changeLimited = fragmentMiddleLeft!!.getScrollerLimited()
                    maxHeightChange = fitCalendarInitHeight!! - changeLimited[0]
                    maxMarginChange = fitCalendarInitHeight!! - changeLimited[0] - changeLimited[1]
                    minMargin = (monthViewInitMargin!! - changeLimited[1]).toInt()
                }
                2 -> {
                    val changeLimited = fragmentMiddleRight!!.getScrollerLimited()
                    maxHeightChange = fitCalendarInitHeight!! - changeLimited[0]
                    maxMarginChange = fitCalendarInitHeight!! - changeLimited[0] - changeLimited[1]
                    minMargin = (monthViewInitMargin!! - changeLimited[1]).toInt()
                }
            }
            currentItemChanged = false
        }
    }

    fun scaleAnimation(){
        if (this.height == fitCalendarInitHeight!!.toInt() || this.height == maxMarginChange!!.toInt()){
            getScrollerLimited()
            when(expansionAndContractionState){
                //展开状态下
                0->{
                    if (scaleAnimationListener!=null){
                        scaleAnimationListener!!.duringScaleAnimation(expansionAndContractionState)
                    }
                    val animationHeight = ValueAnimator.ofInt(fitCalendarInitHeight!!.toInt(),maxMarginChange!!.toInt())
                    animationHeight.addUpdateListener {
                        val height = it.animatedValue
                        viewLayoutParams!!.height = height as Int
                        this.layoutParams = viewLayoutParams
                    }
                    animationHeight.duration = 300
                    animationHeight.start()
                    val animationMargin = ValueAnimator.ofInt(monthViewInitMargin!!.toInt(),minMargin!!)
                    animationMargin.addUpdateListener {
                        val margin = it.animatedValue
                        viewPagerLayoutParams!!.topMargin = margin as Int
                        viewPager!!.layoutParams = viewPagerLayoutParams!!
                    }
                    animationMargin.duration = 300
                    animationMargin.start()
                    viewPager!!.setCanScrollHorizontally(false)
                    expansionAndContractionState = 1
                }
                //收起状态下
                1->{
                    if (scaleAnimationListener!=null){
                        scaleAnimationListener!!.duringScaleAnimation(expansionAndContractionState)
                    }
                    val animationHeight = ValueAnimator.ofInt(this.height,fitCalendarInitHeight!!.toInt())
                    animationHeight.addUpdateListener {
                        val height = it.animatedValue
                        viewLayoutParams!!.height = height as Int
                        this.layoutParams = viewLayoutParams
                    }
                    val animationMargin = ValueAnimator.ofInt(viewPagerLayoutParams!!.topMargin,monthViewInitMargin!!)
                    animationMargin.addUpdateListener {
                        val margin = it.animatedValue
                        viewPagerLayoutParams!!.topMargin = margin as Int
                        viewPager!!.layoutParams = viewPagerLayoutParams!!
                    }
                    animationHeight.duration = 300
                    animationMargin.duration = 300
                    animationMargin.start()
                    animationHeight.start()
                    viewPager!!.setCanScrollHorizontally(true)
                    expansionAndContractionState = 0
                }
            }
        }
    }

    interface ScaleAnimationListener{
        fun duringScaleAnimation(expansionAndContractionState:Int)
    }

    fun setScaleAnimationListener(scaleAnimationListener:ScaleAnimationListener){
        this.scaleAnimationListener = scaleAnimationListener
    }

}