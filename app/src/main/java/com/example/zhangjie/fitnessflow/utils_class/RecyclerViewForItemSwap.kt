package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.library.ActionGroupAdapterInTemplateDetailActivity

class RecyclerViewForItemSwap (context: Context, set: AttributeSet): RecyclerView(context,set),
    ActionGroupAdapterInTemplateDetailActivity.OnStartDragListener{

    private var helper:ItemTouchHelper?=null

    fun setHelper(helper: ItemTouchHelper){
        this.helper=helper
    }

    override fun onStartDrag(viewHolder:ActionGroupAdapterInTemplateDetailActivity.RvHolder){
        helper!!.startDrag(viewHolder)
    }


}
