package com.example.zhangjie.fitnessflow.utils_class

import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo

object GetPlanCountsList {

    private val todayString = GetMonthInfo.getTodayString()
    private val dayList = arrayListOf<String>()

    fun getDayList():List<String>{
        var lastDay = todayString
        for (i in 1..30){
            lastDay = getLastDay(lastDay)
            dayList.add(lastDay)
        }
        return dayList.reversed()
    }

    private fun getLastDay(date:String):String{
        val dateSplitList = date.split("-".toRegex())
        return if (dateSplitList[2].toInt() == 1){
            if (dateSplitList[1].toInt() == 1){
                "${dateSplitList[0].toInt()-1}-12-${GetMonthInfo.getDaysByYearAndMonth(dateSplitList[0].toInt()-1, 12)}"
            }else{
                "${dateSplitList[0].toInt()}-${zeroFill(dateSplitList[1].toInt()-1)}-${GetMonthInfo.getDaysByYearAndMonth(dateSplitList[0].toInt(), dateSplitList[1].toInt()-1)}"
            }
        }else{
            "${dateSplitList[0]}-${dateSplitList[1]}-${zeroFill(dateSplitList[2].toInt()-1)}"
        }
    }

    private fun zeroFill(num:Int):String{
        return if (num < 10){
            "0$num"
        }else{
            num.toString()
        }
    }


}