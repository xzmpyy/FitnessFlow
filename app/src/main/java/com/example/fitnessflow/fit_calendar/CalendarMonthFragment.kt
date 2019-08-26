package com.example.fitnessflow.fit_calendar

import android.os.Bundle
import android.util.AttributeSet
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.fitnessflow.R
import kotlin.collections.ArrayList

class CalendarMonthFragment: Fragment(),CalendarMonthView.ExpansionAndContractionLimitedChangedListener{

    private var monthViewGroup:RelativeLayout? = null
    private var monthView:CalendarMonthView? = null
    private var year:Int? = null
    private var month:Int? = null
    private var selectMode = 0
    private var columnInit:Int? = null
    private var defaultSelectedStateList: ArrayList<String>? = null
    private var daysCount:Int? = null
    private var attributes:AttributeSet? = null
    private var itemClickListener: CalendarMonthView.ItemClickListener? = null
    private var expansionAndContractionLimitedChangedListener:ExpansionAndContractionLimitedChangedListener? = null

    fun getLuLuMonthFragment(year:Int,month:Int
                             ,selectMode:Int, selectedStateList:ArrayList<String>):CalendarMonthFragment{
        val luLuMonthFragment = CalendarMonthFragment()
        luLuMonthFragment.year = year
        luLuMonthFragment.month = month
        luLuMonthFragment.selectMode = selectMode
        luLuMonthFragment.defaultSelectedStateList = selectedStateList
        luLuMonthFragment.columnInit = GetMonthInfo.getFirstDayWeek(year, month) -1
        luLuMonthFragment.daysCount = GetMonthInfo.getDaysByYearAndMonth(year, month)
        return luLuMonthFragment
    }

    fun updateData(year:Int,month:Int,selectedStateList:ArrayList<String>){
        this.year = year
        this.month = month
        this.defaultSelectedStateList = selectedStateList
        this.columnInit = GetMonthInfo.getFirstDayWeek(year, month) -1
        daysCount = GetMonthInfo.getDaysByYearAndMonth(year, month)
    }


    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?{
        return inflater.inflate(R.layout.calendar_month_fragment,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState:Bundle?){
        //获取AttributeSet
        val parser = resources.getXml(R.xml.month)
        attributes = Xml.asAttributeSet(parser)
        monthViewGroup = view.findViewById(R.id.month_view_group)
        monthView = CalendarMonthView(view.context, attributes!!,year!!,month!!,selectMode,defaultSelectedStateList!!,
            columnInit!!,daysCount!!)
        if (itemClickListener!=null){
            monthView!!.setItemClickListener(itemClickListener!!)
        }
        //monthView!!.setExpansionAndContractionLimitedChangedListener(this)
        monthViewGroup!!.addView(monthView)
    }

    fun updateMonthView(){
        monthViewGroup!!.removeAllViews()
        monthView = CalendarMonthView(view!!.context, attributes!!,year!!,month!!,selectMode,defaultSelectedStateList!!,
            columnInit!!,daysCount!!)
        monthView!!.setExpansionAndContractionLimitedChangedListener(this)
        monthViewGroup!!.addView(monthView)
    }


    fun setItemClickListener(itemClickListener: CalendarMonthView.ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    fun getScrollerLimited():FloatArray{
        return monthView!!.getTheExpansionAndContractionLimited()
    }

    interface ExpansionAndContractionLimitedChangedListener{
        fun onExpansionAndContractionLimitedChanged(monthView:CalendarMonthView)
    }

    fun setExpansionAndContractionLimitedChangedListener(expansionAndContractionLimitedChangedListener:ExpansionAndContractionLimitedChangedListener){
        this.expansionAndContractionLimitedChangedListener = expansionAndContractionLimitedChangedListener
        monthView!!.setExpansionAndContractionLimitedChangedListener(this)
    }

    override fun onExpansionAndContractionLimitedChanged(monthView: CalendarMonthView) {
        if (this.expansionAndContractionLimitedChangedListener != null){
            this.expansionAndContractionLimitedChangedListener!!.onExpansionAndContractionLimitedChanged(monthView)
        }
    }


}