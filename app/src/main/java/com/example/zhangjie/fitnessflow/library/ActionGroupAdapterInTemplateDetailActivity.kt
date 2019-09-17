package com.example.zhangjie.fitnessflow.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate
import com.example.zhangjie.fitnessflow.utils_class.MyAlertFragment
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class ActionGroupAdapterInTemplateDetailActivity (private val templateID:Int,private val templateDetailMap:MutableMap<Action,ArrayList<ActionDetailInTemplate>>,
                                                  private val actionIDList:ArrayList<Int>,private val context: AppCompatActivity
): RecyclerView.Adapter<ActionGroupAdapterInTemplateDetailActivity.RvHolder>(),MyAlertFragment.ConfirmButtonClickListener{

    private var currentItemPosition = 0
    private var dragListener:OnStartDragListener?=null

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val actionGroupName = view.findViewById<TextView>(R.id.action_name)!!
        val deleteButton = view.findViewById<ImageButton>(R.id.action_delete_button)!!
        val actionGroupMoveButton = view.findViewById<ImageButton>(R.id.action_group_move_button)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.action_group_in_template_detail,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionIDList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        //向viewHolder中的View控件赋值需显示的内容
        p0.actionGroupName.text = getKeyInTemplateDetailMap(actionIDList[p1])!!.actionName
        p0.deleteButton.setOnClickListener {
            currentItemPosition = p1
            val alertView = View.inflate(it.context,R.layout.alert_text_view, null)
            alertView.findViewById<TextView>(R.id.alert_text).text = it.context.resources.getString(R.string.confirm_to_delete)
            val alertFragment = MyAlertFragment(alertView)
            alertFragment.setConfirmButtonClickListener(this)
            alertFragment.show(context.supportFragmentManager, null)
        }
        p0.actionGroupMoveButton.setOnTouchListener{_,event->
            if(event.action== MotionEvent.ACTION_DOWN){
                dragListener!!.onStartDrag(p0)
            }
            false
        }

    }


    private fun getKeyInTemplateDetailMap(id:Int):Action?{
        for (action in templateDetailMap.keys){
            if (action.actionID == id){
                return action
            }
        }
        return null
    }


    fun addAction(action:Action,position:Int){
        actionIDList.add(action.actionID)
        if (position == 0){
            templateDetailMap[action] = arrayListOf(ActionDetailInTemplate(action.actionID,action.actionType,action.actionName,
                action.IsHadWeightUnits,action.unit,
                action.initWeight,action.initNum,position))
            notifyItemInserted(position)
            notifyItemRangeChanged(position,actionIDList.size-position)
        }else{
            templateDetailMap[action] = arrayListOf(ActionDetailInTemplate(action.actionID,action.actionType,action.actionName,
                action.IsHadWeightUnits,action.unit,
                action.initWeight,action.initNum,
                templateDetailMap[getKeyInTemplateDetailMap(actionIDList[position-1])]!![0].templateOrder + 1))
            notifyItemInserted(position)
            notifyItemRangeChanged(position-1,actionIDList.size-position+1)
        }
    }

    private fun delAction(position:Int){
        //数据库删除
        val actionDeleteInTemplateDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionDeleteInTemplateTool=actionDeleteInTemplateDatabase.writableDatabase
        actionDeleteInTemplateTool.beginTransaction()
        try{
            val delSql = "Delete From TemplateDetailTable Where ActionID=${actionIDList[position]} And TemplateID=$templateID"
            actionDeleteInTemplateTool.execSQL(delSql)
            templateDetailMap.remove(getKeyInTemplateDetailMap(actionIDList[position]))
            actionIDList.removeAt(position)
            notifyItemRemoved(position)
            if (position != actionIDList.size){
                notifyItemRangeChanged(position,actionIDList.size-position)
            }else{
                notifyItemRangeChanged(position-1,actionIDList.size-position+1)
            }
            actionDeleteInTemplateTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Delete In Template Failed(In ActionGroupAdapterInTemplateDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.del_failed))
        }finally{
            actionDeleteInTemplateTool.endTransaction()
            actionDeleteInTemplateTool.close()
            actionDeleteInTemplateDatabase.close()
        }
    }

    override fun onAlertConfirmButtonClick() {
        delAction(currentItemPosition)
    }

    //定义一个接口，在item拖拽时回调
    interface OnStartDragListener{
        fun onStartDrag(viewHolder:RvHolder)
    }

    fun setDragListener(dragListener:OnStartDragListener){
        this.dragListener=dragListener
    }

}
