package com.example.zhangjie.fitnessflow.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
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

    private var dragListener:OnStartDragListener?=null
    private var toBeDeleteID = 0
    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()
    private val itemBottomMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private var lastViewHolder:RvHolder? = null


    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val actionGroupName = view.findViewById<TextView>(R.id.action_name)!!
        val deleteButton = view.findViewById<ImageButton>(R.id.action_delete_button)!!
        val actionGroupMoveButton = view.findViewById<ImageButton>(R.id.action_group_move_button)!!
        val parentLayout = view.findViewById<LinearLayout>(R.id.parent_layout)!!
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
        if (p1 == actionIDList.size - 1){
            val layoutParams = LinearLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
            lastViewHolder = p0
        }else{
            val layoutParams = LinearLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = itemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
            lastViewHolder = p0
        }
        p0.actionGroupName.text = getKeyInTemplateDetailMap(actionIDList[p1])!!.actionName
        //用Tag记录ID
        p0.actionGroupName.tag = getKeyInTemplateDetailMap(actionIDList[p1])!!.actionID
        p0.deleteButton.setOnClickListener {
            toBeDeleteID = p0.actionGroupName.tag.toString().toInt()
            val alertView = View.inflate(it.context,R.layout.alert_text_view, null)
            alertView.findViewById<TextView>(R.id.alert_text).text = it.context.resources.getString(R.string.confirm_to_delete)
            alertView.findViewById<TextView>(R.id.delete_item).text = p0.actionGroupName.text
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
        //数据库中更新添加次数,插入第一组数据
        val actionAddTimesDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionAddTimesTool=actionAddTimesDatabase.writableDatabase
        actionAddTimesTool.beginTransaction()
        try{
            if (position == 0){
                val insertSql = "Insert Into TemplateDetailTable (ActionID,ActionType,ActionName," +
                        "IsHadWeightUnits,Unit,Weight,Num,TemplateID,TemplateOrder) Values(${action.actionID},${action.actionType}," +
                        "\"${action.actionName}\",${action.IsHadWeightUnits},\"${action.unit}\",${action.initWeight},${action.initNum},$templateID,$position)"
                actionAddTimesTool.execSQL(insertSql)
                val idCheckCursor = actionAddTimesTool.rawQuery("select last_insert_rowid()from TemplateDetailTable",null)
                idCheckCursor.moveToNext()
                val lastId = idCheckCursor.getString(0).toInt()
                templateDetailMap[action] = arrayListOf(ActionDetailInTemplate(action.actionID,action.actionType,action.actionName,
                    action.IsHadWeightUnits,action.unit,
                    action.initWeight,action.initNum,position,lastId))
                idCheckCursor.close()
            }else{
                val insertSql = "Insert Into TemplateDetailTable (ActionID,ActionType,ActionName," +
                        "IsHadWeightUnits,Unit,Weight,Num,TemplateID,TemplateOrder) Values(${action.actionID},${action.actionType}," +
                        "\"${action.actionName}\",${action.IsHadWeightUnits},\"${action.unit}\",${action.initWeight},${action.initNum},$templateID," +
                        "${templateDetailMap[getKeyInTemplateDetailMap(actionIDList[position-1])]!![0].templateOrder + 1})"
                actionAddTimesTool.execSQL(insertSql)
                val idCheckCursor = actionAddTimesTool.rawQuery("select last_insert_rowid()from TemplateDetailTable",null)
                idCheckCursor.moveToNext()
                val lastId = idCheckCursor.getString(0).toInt()
                templateDetailMap[action] = arrayListOf(ActionDetailInTemplate(action.actionID,action.actionType,action.actionName,
                    action.IsHadWeightUnits,action.unit,
                    action.initWeight,action.initNum,
                    (templateDetailMap[getKeyInTemplateDetailMap(actionIDList[position-1])]!![0].templateOrder + 1),lastId))
                idCheckCursor.close()
                //修改上一个底边距
                parentLayoutMarginSet(lastViewHolder!!,0)
            }
            notifyItemInserted(position)
            notifyItemRangeChanged(position,actionIDList.size-position)
            actionAddTimesTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Add In Template Failed(In ActionGroupAdapterInTemplateDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.add_failed)).showToast()
        }finally{
            actionAddTimesTool.endTransaction()
            actionAddTimesTool.close()
            actionAddTimesDatabase.close()
        }
    }

    private fun delAction(toBeDeleteID:Int){
        //数据库删除
        val actionDeleteInTemplateDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionDeleteInTemplateTool=actionDeleteInTemplateDatabase.writableDatabase
        actionDeleteInTemplateTool.beginTransaction()
        try{
            val delSql = "Delete From TemplateDetailTable Where ActionID=$toBeDeleteID And TemplateID=$templateID"
            actionDeleteInTemplateTool.execSQL(delSql)
            templateDetailMap.remove(getKeyInTemplateDetailMap(toBeDeleteID))
            val position = actionIDList.indexOf(toBeDeleteID)
            actionIDList.removeAt(position)
            notifyItemRemoved(position)
            if (position == actionIDList.size){
                notifyItemRangeChanged(position-1,actionIDList.size+1-position)
            }else{
                notifyItemRangeChanged(position,actionIDList.size-position)
            }
            actionDeleteInTemplateTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Delete In Template Failed(In ActionGroupAdapterInTemplateDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.del_failed)).showToast()
        }finally{
            actionDeleteInTemplateTool.endTransaction()
            actionDeleteInTemplateTool.close()
            actionDeleteInTemplateDatabase.close()
        }
    }

    override fun onAlertConfirmButtonClick() {
        delAction(toBeDeleteID)
    }

    //定义一个接口，在item拖拽时回调
    interface OnStartDragListener{
        fun onStartDrag(viewHolder:RvHolder)
    }

    fun setDragListener(dragListener:OnStartDragListener){
        this.dragListener=dragListener
    }

    //修改padding_bottom,0正常，1加长
    fun parentLayoutMarginSet(viewHolder: RvHolder,type:Int){
        when(type){
            0->{
                val layoutParams = LinearLayout.LayoutParams(viewHolder.parentLayout.layoutParams)
                layoutParams.bottomMargin = itemBottomMargin
                viewHolder.parentLayout.layoutParams = layoutParams
            }
            1->{
                val layoutParams = LinearLayout.LayoutParams(viewHolder.parentLayout.layoutParams)
                layoutParams.bottomMargin = lastItemBottomMargin
                viewHolder.parentLayout.layoutParams = layoutParams
                lastViewHolder = viewHolder
            }
        }
        println(actionIDList)
    }

}
