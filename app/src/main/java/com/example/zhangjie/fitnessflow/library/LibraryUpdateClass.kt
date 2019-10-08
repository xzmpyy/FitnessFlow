package com.example.zhangjie.fitnessflow.library

import com.example.zhangjie.fitnessflow.data_class.Template

object LibraryUpdateClass {

    //键为动作类型，值为动作ID
    private val toBeUpdateMap = mutableMapOf<Int,ArrayList<Int>>()
    private val newTemplateList = arrayListOf<Template>()
    private var todayDataUpdateFlag = false

    fun putData(type:Int, actionID:Int){
        if (toBeUpdateMap.keys.contains(type)){
            if (!toBeUpdateMap[type]!!.contains(actionID)){
                toBeUpdateMap[type]!!.add(actionID)
            }
        }else{
            toBeUpdateMap[type] = arrayListOf(actionID)
        }
    }

    fun getData(type:Int):ArrayList<Int>?{
        return if (toBeUpdateMap.keys.contains(type)){
            toBeUpdateMap[type]!!
        }else{
            null
        }
    }

    fun removeData(type:Int){
        toBeUpdateMap.remove(type)
    }

    fun putNewTemplate(template: Template){
        newTemplateList.add(template)
    }

    fun getTemplateList():ArrayList<Template>{
        return newTemplateList
    }

    fun clearTemplateList(){
        newTemplateList.clear()
    }

    fun setTodayDataUpdateFlag(flag:Boolean){
        todayDataUpdateFlag = flag
    }

    fun checkTodayDataUpdateFlag():Boolean{
        return todayDataUpdateFlag
    }

}