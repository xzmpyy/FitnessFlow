package com.example.fitnessflow.library.library_child_fragments

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class LinearLayoutManagerForItemSwipe (context: Context):LinearLayoutManager(context){

    private var canScrollVerticallyFlag = true

    override fun canScrollVertically(): Boolean {
        return canScrollVerticallyFlag
    }

    fun setCanScrollVerticallyFlag(flag:Boolean){
        canScrollVerticallyFlag = flag
    }

}