package com.example.zhangjie.fitnessflow.data_class

data class Action(val actionType:Int,val actionID:Int, var actionName:String,var IsHadWeightUnits:Int
,var addTimes:Int, var unit:String,var initWeight:Float, var initNum:Int,var weightOfIncreaseProgressively:Float,
                  var numOfIncreaseProgressively:Int,var isShow:Int)

data class Template(var templateName:String, var actionNum:Int, var muscleGroupInclude:ArrayList<String>,val templateID:Int)

data class ActionDetailInTemplate(val actionID:Int,val actionType:Int,val actionName:String,val isHadWeightUnits:Int,val unit:String,
                                  var weight:Float,var num:Int,var templateOrder:Int,val ID:Int)

data class ActionDetailInPlan(val actionID:Int,val actionType:Int,val actionName:String,val isHadWeightUnits:Int,val unit:String,
                                  var weight:Float,var num:Int,var done:Int,var planOrder:Int,val ID:Int)

object TodayPlanNum{
    private var flag = false

    fun setFlag(flag:Boolean){
        this.flag = flag
    }

    fun checkFlag():Boolean{
        return flag
    }

}

object FoldState{
    private var foldFlag = true

    fun setFlag(flag:Boolean){
        foldFlag = flag
    }

    fun checkFlag():Boolean{
        return foldFlag
    }

}

object IsScreenRestart{
    private var restartFlag = false

    fun setFlag(flag:Boolean){
        restartFlag = flag
    }

    fun checkFlag():Boolean{
        return restartFlag
    }
}

object SDKVersion{
    private var version = 0

    fun setVersion(version:Int){
        this.version = version
    }

    fun getVersion():Int{
        return this.version
    }

}