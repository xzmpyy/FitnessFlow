package com.example.zhangjie.fitnessflow.utils_class

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate
import java.util.*
import kotlin.collections.ArrayList

class MyItemTouchHelperCallback(private val templateDetailMap:MutableMap<Action,ArrayList<ActionDetailInTemplate>>?,
                                private val actionIDList:ArrayList<Int>,
                                private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>):
    ItemTouchHelper.Callback(){
    //返回int表示是否监听该方向
    override fun getMovementFlags(p0:RecyclerView,p1:RecyclerView.ViewHolder):Int{
        val dragFlags=ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        //不检测横向滑动
        val swipeFlags=0
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun onMove(p0:RecyclerView,p1:RecyclerView.ViewHolder,p2:RecyclerView.ViewHolder):Boolean{
        //重新排序
        if (templateDetailMap!=null){
            for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[p1.adapterPosition])]!!){
                actionDetail.templateOrder = p2.adapterPosition
            }
            for (actionDetail in templateDetailMap[getKeyInTemplateDetailMap(actionIDList[p2.adapterPosition])]!!){
                actionDetail.templateOrder = p1.adapterPosition
            }
        }
        //交换两个数据列表中两个数据的位置
        Collections.swap(actionIDList,p1.adapterPosition,p2.adapterPosition)
        //通知adapter刷新
        adapter.notifyItemMoved(p1.adapterPosition,p2.adapterPosition)
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
            viewHolder!!.itemView.alpha=0.6f
        }
    }

    //结束拖拽后，还原透明度
    override fun clearView(recyclerView:RecyclerView,viewHolder:RecyclerView.ViewHolder){
        super.clearView(recyclerView,viewHolder)
        viewHolder.itemView.alpha=1f
    }

    private fun getKeyInTemplateDetailMap(id:Int):Action?{
        for (action in templateDetailMap!!.keys){
            if (action.actionID == id){
                return action
            }
        }
        return null
    }

}
