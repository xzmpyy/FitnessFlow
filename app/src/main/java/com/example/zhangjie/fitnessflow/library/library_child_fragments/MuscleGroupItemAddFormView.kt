package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.zhangjie.fitnessflow.R

class MuscleGroupItemAddFormView (context: Context,set:AttributeSet):LinearLayout(context,set){

    constructor(context: Context,set:AttributeSet,actionType:Int):this(context,set){
        this.actionType = actionType
        println("cc")
    }

    private var actionType = 1
    private var formView: View? = null
    private val muscleGroupNameList = resources.getStringArray(R.array.muscle_group)

    init {
        println("ii")
        formView = LayoutInflater.from(context).inflate(R.layout.action_addition_form, this,false)
        this.addView(formView)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        formView!!.findViewById<TextView>(R.id.muscle_group).text = muscleGroupNameList[actionType]
        super.onWindowFocusChanged(hasWindowFocus)
    }

}