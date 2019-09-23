package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.library.ActionGroupAdapterInTemplateDetailActivity
import com.example.zhangjie.fitnessflow.plan.plan_detail.ActionGroupAdapterInPlanDetailActivity

class RecyclerViewForItemSwap (context: Context, set: AttributeSet): RecyclerView(context,set),
    ActionGroupAdapterInTemplateDetailActivity.OnStartDragListener,ActionGroupAdapterInPlanDetailActivity.OnStartDragListener{

    private var helper:ItemTouchHelper?=null

    fun setHelper(helper: ItemTouchHelper){
        this.helper=helper
    }

    override fun onStartDrag(viewHolder:ActionGroupAdapterInTemplateDetailActivity.RvHolder){
        helper!!.startDrag(viewHolder)
    }

    override fun onStartDrag(viewHolder: ActionGroupAdapterInPlanDetailActivity.RvHolder) {
        helper!!.startDrag(viewHolder)
    }

}
