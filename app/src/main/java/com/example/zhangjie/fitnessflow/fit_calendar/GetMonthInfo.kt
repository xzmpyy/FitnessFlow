package com.example.zhangjie.fitnessflow.fit_calendar

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object GetMonthInfo {
    @SuppressLint("SimpleDateFormat")
    private val sdf= SimpleDateFormat("yyyy-MM-dd")
    private val date = Date()
    //获取当月有多少天
    fun getDaysByYearAndMonth(year:Int, month:Int):Int{
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)
        cal.set(Calendar.DATE, 1)
        cal.roll(Calendar.DATE, -1)
        return cal.get(Calendar.DATE)
    }
    //获取当月1号是周几,周日是1
    fun getFirstDayWeek(year:Int, month:Int):Int{
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)
        cal.set(Calendar.DATE, 1)
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    fun getTodayString():String{
        return sdf.format(date).toString()
    }

    //0是小，1相等，2是大
    fun compareYearAndMonth(dateNow:String,dateTarget:String):Int{
        val nowList = dateNow.split("-".toRegex())
        val targetList = dateTarget.split("-".toRegex())
        return if (nowList[0].toInt()<targetList[0].toInt()){
            0
        }else if (nowList[0].toInt()>targetList[0].toInt()){
            2
        }else if (nowList[0].toInt()==targetList[0].toInt() && nowList[1].toInt()<targetList[1].toInt()){
            0
        }else if (nowList[0].toInt()==targetList[0].toInt() && nowList[1].toInt()>targetList[1].toInt()){
            2
        }else{
            1
        }
    }

    fun getYearAndMonthString(year:Int, month: Int):String{
        var dateString = "$year-"
        dateString += if (month < 10){
            "0$month"
        }else{
            "$month"
        }
        return dateString
    }

}