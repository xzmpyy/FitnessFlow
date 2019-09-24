package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import com.example.zhangjie.fitnessflow.library.LibraryUpdateClass

object ActionOrderInPlanDetailCorrect {

    fun correct(context: Context, targetDaysList:ArrayList<String>):Boolean{
        var correctFlag = true
        val correctDatabase=MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val correctDataBaseTool=correctDatabase.writableDatabase
        val actionAddTimesToBeUpdateMap = mutableMapOf<String,Int>()
        correctDataBaseTool.beginTransaction()
        try{
            for (day in targetDaysList){
                val actionIDList = arrayListOf<String>()
                val actionOrderList = arrayListOf<String>()
                val cursor=correctDataBaseTool.rawQuery("Select ActionID,PlanOrder,ActionType From PlanDetailTable where Date=? Order By ID",arrayOf(day))
                while(cursor.moveToNext()){
                    if (!actionIDList.contains(cursor.getString(0))){
                        actionIDList.add(cursor.getString(0))
                        if (cursor.getString(1).toInt() == 0){
                            if (actionAddTimesToBeUpdateMap.keys.contains(cursor.getString(0))){
                                actionAddTimesToBeUpdateMap[cursor.getString(0)] = actionAddTimesToBeUpdateMap[cursor.getString(0)]!!+1
                            }else{
                                actionAddTimesToBeUpdateMap[cursor.getString(0)] = 1
                                LibraryUpdateClass.putData(cursor.getString(2).toInt(), cursor.getString(0).toInt())
                            }
                            if (actionOrderList.size == 0){
                                actionOrderList.add("1")
                            }else{
                                actionOrderList.add((actionOrderList[actionOrderList.size-1].toInt() + 1).toString())
                            }
                        }else{
                            actionOrderList.add(cursor.getString(1))
                        }
                    }
                }
                cursor.close()
                //更新顺序
                for (i in 0 until actionIDList.size){
                    val orderCorrectSql = "Update PlanDetailTable Set PlanOrder=${actionOrderList[i]} Where Date=\"$day\" And ActionID=${actionIDList[i]}"
                    correctDataBaseTool.execSQL(orderCorrectSql)
                }
            }
            //新添动作，添加次数加一
            for (actionID in actionAddTimesToBeUpdateMap.keys){
                val addTimesUpdate = "Update ActionTable Set AddTimes=AddTimes+${actionAddTimesToBeUpdateMap[actionID]} Where ActionID=$actionID"
                correctDataBaseTool.execSQL(addTimesUpdate)
            }
            if (!correctFlag){
                correctFlag = true
            }
            correctDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Order Correct Failed(In ActionOrderInPlanDetailCorrect):$e")
            if (correctFlag){
                correctFlag = false
            }
        }finally{
            correctDataBaseTool.endTransaction()
            correctDataBaseTool.close()
            correctDatabase.close()
        }
        return correctFlag
    }

}