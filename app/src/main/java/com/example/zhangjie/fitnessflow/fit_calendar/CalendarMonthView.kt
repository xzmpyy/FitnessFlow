package com.example.zhangjie.fitnessflow.fit_calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import java.util.*
import kotlin.collections.ArrayList


class CalendarMonthView (context: Context, set: AttributeSet): View(context, set){

    constructor(context: Context, set: AttributeSet, year:Int, month:Int
                , selectMode:Int, defaultSelectedStateList: ArrayList<String>,
                columnInit:Int, daysCount:Int):this(context,set){
        this.year = year
        this.month = month
        this.selectMode = selectMode
        this.daysCount = daysCount
        //设置起始绘制的列
        this.columnInit = columnInit
        this.column = columnInit
        this.defaultSelectedStateList = defaultSelectedStateList
    }

    private val paint = Paint()
    //每个区域所占宽高
    private var eachItemWidth: Int? = null
    private var textSize: Float? = null
    private var viewPadding: Float? = null
    private var halfWidth: Float? = null
    //不同画笔的颜色
    private var naturalTextColor = ContextCompat.getColor(context, R.color.primaryTextColor)
    private var selectedTextColor  = ContextCompat.getColor(context, R.color.primaryButtonBackground)
    private var defaultSelectedColor = ContextCompat.getColor(context, R.color.primaryButtonBackground)
    private var todayTextColor = ContextCompat.getColor(context, R.color.primaryRed)
    private var backGroundColor  = ContextCompat.getColor(context, R.color.colorPrimary)
    private var shadowColor = ContextCompat.getColor(context, R.color.primaryShadowColor)
    //双缓冲
    private var textBitmap: Bitmap? = null
    private val bitmapCanvas = Canvas()
    //格子行列
    private var row = 0
    private var column = 0
    private var columnInit = 0
    //日期
    private var year:Int? = null
    private var month:Int? = null
    private var daysCount = 0
    //选择模式0为单选1位多选
    private var selectMode = 0
    private var moveStartX:Float? = null
    private var moveStartY:Float? = null
    private var defaultSelectedStateList :ArrayList<String>? = null
    private val myPaintMode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
    private var strokeWidth = 4f
    private var yearAndMonthString:String? = null
    private var itemClickListener:ItemClickListener? = null
    private var expansionAndContractionLimitedChangedListener:ExpansionAndContractionLimitedChangedListener? = null

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        //设置文字基点为中心点
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.column = columnInit
        row = 0
        if (eachItemWidth == null) {
            eachItemWidth = this.width / 7
            textSize = (eachItemWidth!! / 3).toFloat()
            viewPadding = (this.width - eachItemWidth!! * 7).toFloat() / 2f
            halfWidth = (eachItemWidth!!.toFloat())/2f
        }
        canvas!!.drawColor(backGroundColor)
        drawDayText(canvas)
        if (!textBitmap!!.isRecycled){
            textBitmap!!.recycle()
        }
    }


    private fun drawDayText(canvas: Canvas) {
        paint.textSize = textSize!!
        //线宽
        paint.strokeWidth = strokeWidth
        //画笔颜色
        paint.color = naturalTextColor
        textBitmap = Bitmap.createBitmap(eachItemWidth!!*7,eachItemWidth!!*6,Bitmap.Config.ARGB_8888)
        bitmapCanvas.setBitmap(textBitmap)
        for (dayText in 1 .. daysCount){
            textBitmap!!.setHasAlpha(true)
            val position = getTextCenterPosition()
            if (yearAndMonthString == null){
                yearAndMonthString = GetMonthInfo.getYearAndMonthString(year!!,month!!)
            }
            val dateString = if(dayText<10){
                "$yearAndMonthString-0$dayText"
            }else{
                "$yearAndMonthString-$dayText"
            }
            paint.xfermode = myPaintMode
            //今日
            if (dateString == GetMonthInfo.getTodayString()){
                paint.color = todayTextColor
                bitmapCanvas.drawText(dayText.toString(),position[0],position[1]+textSize!!/2,paint)
                paint.color = naturalTextColor
                //单选模式且无选中项时，默认选中今日
                if (SelectedItemClass.getListCount() == 0 && selectMode==0){
                    SelectedItemClass.addItem(dateString)
                 }
            }else{
                bitmapCanvas.drawText(dayText.toString(),position[0],position[1]+textSize!!/2,paint)
            }
            //选中
            if (SelectedItemClass.checkItem(dateString)){
                if (expansionAndContractionLimitedChangedListener!=null){
                    expansionAndContractionLimitedChangedListener!!.onExpansionAndContractionLimitedChanged(this)
                }
                paint.color = selectedTextColor
                bitmapCanvas.drawRoundRect(position[0]-textSize!!,position[1]+textSize!!-strokeWidth*3,position[0]+textSize!!,position[1]+textSize!!+strokeWidth*5,10f,10f,paint)
                paint.color = shadowColor
                bitmapCanvas.drawRoundRect(position[0]-textSize!!+strokeWidth*3,position[1]+textSize!!,position[0]+textSize!!+strokeWidth*3,position[1]+textSize!!+strokeWidth*8,10f,10f,paint)
            }
            //默认选中
            if (defaultSelectedStateList!!.contains(dateString)){
                paint.color = defaultSelectedColor
                bitmapCanvas.drawCircle(position[0],position[1]+textSize!!,strokeWidth*3,paint)
                paint.color = shadowColor
                bitmapCanvas.drawCircle(position[0],position[1]+textSize!! + strokeWidth*3,strokeWidth*3,paint)
            }
            paint.color = naturalTextColor
            paint.xfermode = null
            column += 1
            if (column == 7){
                column =0
                row += 1
            }
        }
        canvas.drawBitmap(textBitmap!!,0f+viewPadding!!,0f,paint)
    }

    //获取文字的准心
    private fun getTextCenterPosition(): FloatArray {
        val x = viewPadding!! + (column * eachItemWidth!!).toFloat() + halfWidth!!
        val y = (row * eachItemWidth!!).toFloat() + halfWidth!!
        return floatArrayOf(x, y)
    }

    //设置高度自适应
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val height = (widthSize/ 7)*6
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN){
            moveStartX = event.x
            moveStartY = event.y
        }
        if (event.action == MotionEvent.ACTION_UP) {
            if (kotlin.math.abs(event.x - moveStartX!!) < 5 && kotlin.math.abs(event.y - moveStartY!!) < 5) {
                val clickWhich = numOfClick(moveStartX!!,moveStartY!!) + 1
                var dateClick = year.toString() + "-"
                dateClick += if (month!!<10){
                    "0$month"
                }else{
                    month.toString()
                }
                if (clickWhich in 1..daysCount){
                    dateClick += if(clickWhich<10){
                        "-0$clickWhich"
                    }else{
                        "-$clickWhich"
                    }
                    if (selectMode == 0){
                        if (!SelectedItemClass.checkItem(dateClick)){
                            SelectedItemClass.clear()
                            SelectedItemClass.addItem(dateClick)
                            this.invalidate()
                        }
                    }else{
                        when (GetMonthInfo.compareDate(dateClick,GetMonthInfo.getTodayString())){
                            0->{
                                MyToast(context,context.resources.getString(R.string.multiple_select_toast)).showToast()
                            }
                            else->{
                                if (SelectedItemClass.checkItem(dateClick)){
                                    SelectedItemClass.removeItem(dateClick)
                                }
                                else{
                                    SelectedItemClass.addItem(dateClick)
                                }
                                this.invalidate()
                            }
                        }
                    }
                    if (itemClickListener!=null){
                        itemClickListener!!.onItemClickListener(dateClick)
                    }
                    moveStartX = null
                    moveStartY = null
                    return false
                }
            }
        }
        return true
    }


    //判断点击位置属于第几个单元格
    private fun numOfClick(x:Float, y: Float):Int{
        return ((x - viewPadding!!).toInt()/eachItemWidth!! + y.toInt()/eachItemWidth!!*7) - columnInit
    }


    //点击事件监听
    interface ItemClickListener{
        fun onItemClickListener(date:String)
    }

    fun setItemClickListener(itemClickListener:ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    //日历主类根据点击刷新伸缩尺寸限制
    interface ExpansionAndContractionLimitedChangedListener{
        fun onExpansionAndContractionLimitedChanged(monthView:CalendarMonthView)
    }

    fun setExpansionAndContractionLimitedChangedListener(expansionAndContractionLimitedChangedListener:ExpansionAndContractionLimitedChangedListener){
        this.expansionAndContractionLimitedChangedListener = expansionAndContractionLimitedChangedListener
    }

    //可收起的height和margin限制
    fun getTheExpansionAndContractionLimited():FloatArray{
        val selectedDate = SelectedItemClass.getSelectedList()[0]
        when (GetMonthInfo.compareYearAndMonth(yearAndMonthString!!,selectedDate.substring(0,7))){
            //本月不是选中项当月，且比选中项所在月小时，默认收缩至最后一行
            0->{
                val rowPosition = if ((daysCount + columnInit)%7 == 0){
                    (daysCount + columnInit)/7
                }else{
                    (daysCount + columnInit)/7 + 1
                }
                return floatArrayOf((6-rowPosition)*eachItemWidth!!.toFloat(), (rowPosition -1)*eachItemWidth!!.toFloat())
            }
            //当月，返回选中项所在行
            1->{
                val dateSelected = selectedDate.substring(8).toInt()
                val rowPosition = if ((dateSelected + columnInit)%7 == 0){
                    (dateSelected + columnInit)/7
                }else{
                    (dateSelected + columnInit)/7 + 1
                }
                return floatArrayOf((6-rowPosition)*eachItemWidth!!.toFloat(), (rowPosition -1)*eachItemWidth!!.toFloat())
            }
            //本月不是选中项当月，且比选中项所在月大时，默认收缩至第一行
            2->{
                return floatArrayOf(eachItemWidth!!.toFloat()*5 ,0f)
            }
        }

        return floatArrayOf(eachItemWidth!!.toFloat()*5 ,0f)
    }

    fun setDefaultSelectedList(defaultSelectedList:ArrayList<String>){
        this.defaultSelectedStateList = defaultSelectedList
        invalidate()
    }

}