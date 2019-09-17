package com.example.zhangjie.fitnessflow.library

object LibraryUpdateClass {

    //键为动作类型，值为动作ID
    private val toBeUpdateMap = mutableMapOf<Int,ArrayList<Int>>()

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

}