package com.example.zhangjie.fitnessflow.data_class

data class Action(val actionType:Int,val actionID:Int, var actionName:String,var IsHadWeightUnits:Int
,var addTimes:Int, var unit:String,var initWeight:Float, var initNum:Int,var weightOfIncreaseProgressively:Float,
                  var numOfIncreaseProgressively:Int,var isShow:Int)

data class Template(var templateName:String, var actionNum:Int, var muscleGroupInclude:ArrayList<String>,val templateID:Int)

data class ActionDetailInTemplate(val actionID:Int,val actionType:Int,val actionName:String,val isHadWeightUnits:Int,val unit:String,
                                  var weight:Float,var num:Int,var templateOrder:Int,val ID:Int)

data class ActionDetailInPlan(val actionID:Int,val actionType:Int,val actionName:String,val isHadWeightUnits:Int,val unit:String,
                                  var weight:Float,var num:Int,var done:Int,var planOrder:Int,val ID:Int)