package com.example.zhangjie.fitnessflow.data_class

import java.io.Serializable

data class Action(val actionType:Int,val actionID:Int, var actionName:String,var IsHadWeightUnits:Int
,var addTimes:Int, var unit:String,var initWeight:Float, var initNum:Int,var weightOfIncreaseProgressively:Float,
                  var numOfIncreaseProgressively:Int,var isShow:Int)

data class Template(var templateName:String, var actionNum:Int, var muscleGroupInclude:ArrayList<String>,val templateID:Int) :Serializable