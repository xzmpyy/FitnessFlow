package com.example.zhangjie.fitnessflow.utils_class

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate
import com.example.zhangjie.fitnessflow.library.ActionGroupAdapterInTemplateDetailActivity
import java.util.*
import kotlin.collections.ArrayList

class MyItemTouchHelperCallback(private val templateDetailMap:MutableMap<Action,ArrayList<ActionDetailInTemplate>>?,
                                private val planDetailMap:MutableMap<Action,ArrayList<ActionDetailInPlan>>?,
                                private val actionIDList:ArrayList<Int>,
                                private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>):
    ItemTouchHelper.Callback(){

    private var lastViewHolder:RecyclerView.ViewHolder?=null

    //返回int表示是否监听该方向
    override fun getMovementFlags(p0:RecyclerView,p1:RecyclerView.ViewHolder):Int{
        val dragFlags=ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        //不检测横向滑动
        val swipeFlags=0
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun onMove(p0:RecyclerView,p1:RecyclerView.ViewHolder,p2:RecyclerView.ViewHolder):Boolean{
        if (p2.adapterPosition == actionIDList.size -1){
            lastViewHolder = p2
        }
        if (p1.adapterPosition < p2.adapterPosition) {
            for (i in p1.adapterPosition until p2.adapterPosition) {
                if (templateDetailMap!=null){
                    for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[i])]!!){
                        actionDetail.templateOrder = i+1
                    }
                    for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[i+1])]!!){
                        actionDetail.templateOrder = i
                    }
                }
                if (planDetailMap!=null){
                    for (actionDetail in planDetailMap[getKeyInTemplateDetailMap(actionIDList[i])]!!){
                        actionDetail.planOrder = i+1
                    }
                    for (actionDetail in planDetailMap[getKeyInTemplateDetailMap(actionIDList[i+1])]!!){
                        actionDetail.planOrder = i
                    }
                }
                Collections.swap(actionIDList, i, i + 1)
                //通知adapter刷新
                adapter.notifyItemMoved(i,i + 1)
            }
        } else {
            for (i in p1.adapterPosition downTo (p2.adapterPosition+1)) {
                if (templateDetailMap!=null){
                    for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[i-1])]!!){
                        actionDetail.templateOrder = i
                    }
                    for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[i])]!!){
                        actionDetail.templateOrder = i-1
                    }
                }
                if (planDetailMap!=null){
                    for (actionDetail in planDetailMap[getKeyInTemplateDetailMap(actionIDList[i-1])]!!){
                        actionDetail.planOrder = i
                    }
                    for (actionDetail in planDetailMap[getKeyInTemplateDetailMap(actionIDList[i])]!!){
                        actionDetail.planOrder = i-1
                    }
                }
                Collections.swap(actionIDList, i, i - 1)
                adapter.notifyItemMoved(i,i - 1)
            }
        }
        if (p2.adapterPosition == actionIDList.size -1){
            lastViewHolder = p2
        }
        return false
    }

    override fun onSwiped(p0:RecyclerView.ViewHolder,p1:Int){
        //用于实现滑动删除
    }

    //若不允许长按拖拽，则返回false
    override fun isLongPressDragEnabled():Boolean{
        return false
    }
    //不检测横向滑动
    override fun isItemViewSwipeEnabled():Boolean{
        return false
    }

    //被拖拽的组件，透明度改变
    override fun onSelectedChanged(viewHolder:RecyclerView.ViewHolder?,actionState:Int){
        super.onSelectedChanged(viewHolder,actionState)
        if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
            (viewHolder!! as ActionGroupAdapterInTemplateDetailActivity.RvHolder).parentLayout.alpha=0.6f
        }
    }

    //结束拖拽后，还原透明度
    override fun clearView(recyclerView:RecyclerView,viewHolder:RecyclerView.ViewHolder){
        super.clearView(recyclerView,viewHolder)
        (viewHolder as ActionGroupAdapterInTemplateDetailActivity.RvHolder).parentLayout.alpha=1f
        if (viewHolder.adapterPosition == actionIDList.size -1){
            (adapter as ActionGroupAdapterInTemplateDetailActivity).parentLayoutMarginSet(viewHolder, 1)
            if (lastViewHolder!=null){
                adapter.parentLayoutMarginSet(lastViewHolder as ActionGroupAdapterInTemplateDetailActivity.RvHolder, 0)
            }
        }else{
            (adapter as ActionGroupAdapterInTemplateDetailActivity).parentLayoutMarginSet(viewHolder, 0)
            if (lastViewHolder!=null){
                adapter.parentLayoutMarginSet(lastViewHolder as ActionGroupAdapterInTemplateDetailActivity.RvHolder, 1)
            }
        }
        lastViewHolder = null
    }

    private fun getKeyInTemplateDetailMap(id:Int):Action?{
        if (templateDetailMap!=null){
            for (action in templateDetailMap.keys){
                if (action.actionID == id){
                    return action
                }
            }
        }
        if (planDetailMap!=null){
            for (action in planDetailMap.keys){
                if (action.actionID == id){
                    return action
                }
            }
        }

        return null
    }

}
