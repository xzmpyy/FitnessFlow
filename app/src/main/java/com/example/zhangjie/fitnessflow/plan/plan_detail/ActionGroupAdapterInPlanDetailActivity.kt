package com.example.zhangjie.fitnessflow.plan.plan_detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.library.LibraryUpdateClass
import com.example.zhangjie.fitnessflow.utils_class.MyAlertFragment
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class ActionGroupAdapterInPlanDetailActivity (private val dateInfo:String, private val planDetailMap:MutableMap<Action,ArrayList<ActionDetailInPlan>>,
                                              private val actionIDList:ArrayList<Int>, private val context: AppCompatActivity
): RecyclerView.Adapter<ActionGroupAdapterInPlanDetailActivity.RvHolder>(), MyAlertFragment.ConfirmButtonClickListener,
    ActionDetailAdapterInPlanDetailActivity.LastItemDeleteListener{

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
        val actionDetailRv = view.findViewById<RecyclerView>(R.id.action_detail_rv)!!
        val detailAddButton = view.findViewById<ImageButton>(R.id.detail_add_button)!!
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
        p0.actionGroupName.text = getKeyInPlanDetailMap(actionIDList[p1])!!.actionName
        //用Tag记录ID
        p0.actionGroupName.tag = getKeyInPlanDetailMap(actionIDList[p1])!!.actionID
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
        //每个动作的细节RecyclerView设置
        val layoutManager = LinearLayoutManager(context)
        val actionForChild = getKeyInPlanDetailMap(actionIDList[p1])!!
        val adapterForChild = ActionDetailAdapterInPlanDetailActivity(actionForChild,planDetailMap[actionForChild]!!,dateInfo,context)
        adapterForChild.setLastItemDeleteListener(this)
        p0.actionDetailRv.layoutManager = layoutManager
        p0.actionDetailRv.adapter = adapterForChild
        p0.detailAddButton.setOnClickListener {
            adapterForChild.addActionDetail()
        }
    }


    private fun getKeyInPlanDetailMap(id:Int):Action?{
        for (action in planDetailMap.keys){
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
                val insertSql = "Insert Into PlanDetailTable (ActionID,ActionType,ActionName," +
                        "IsHadWeightUnits,Unit,Weight,Num,Done,PlanOrder,Date) Values(${action.actionID},${action.actionType}," +
                        "\"${action.actionName}\",${action.IsHadWeightUnits},\"${action.unit}\",${action.initWeight},${action.initNum},0,$position,\"$dateInfo\")"
                actionAddTimesTool.execSQL(insertSql)
                val idCheckCursor = actionAddTimesTool.rawQuery("select last_insert_rowid() from PlanDetailTable",null)
                idCheckCursor.moveToNext()
                val lastId = idCheckCursor.getString(0).toInt()
                planDetailMap[action] = arrayListOf(ActionDetailInPlan(action.actionID,action.actionType,action.actionName,
                    action.IsHadWeightUnits,action.unit,
                    action.initWeight,action.initNum,0,position,lastId))
                idCheckCursor.close()
            }else{
                val insertSql = "Insert Into PlanDetailTable (ActionID,ActionType,ActionName," +
                        "IsHadWeightUnits,Unit,Weight,Num,Done,PlanOrder,Date) Values(${action.actionID},${action.actionType}," +
                        "\"${action.actionName}\",${action.IsHadWeightUnits},\"${action.unit}\",${action.initWeight},${action.initNum},0," +
                        "${planDetailMap[getKeyInPlanDetailMap(actionIDList[position-1])]!![0].planOrder + 1},\"$dateInfo\")"
                actionAddTimesTool.execSQL(insertSql)
                val idCheckCursor = actionAddTimesTool.rawQuery("select last_insert_rowid() from PlanDetailTable",null)
                idCheckCursor.moveToNext()
                val lastId = idCheckCursor.getString(0).toInt()
                planDetailMap[action] = arrayListOf(ActionDetailInPlan(action.actionID,action.actionType,action.actionName,
                    action.IsHadWeightUnits,action.unit,
                    action.initWeight,action.initNum,0,
                    (planDetailMap[getKeyInPlanDetailMap(actionIDList[position-1])]!![0].planOrder + 1),lastId))
                idCheckCursor.close()
                //修改上一个底边距
                parentLayoutMarginSet(lastViewHolder!!,0)
            }
            notifyItemInserted(position)
            notifyItemRangeChanged(position,actionIDList.size-position)
            //更新添加次数
            val actionUpdateSql = "Update ActionTable Set AddTimes=AddTimes+1 Where ActionID=${action.actionID}"
            LibraryUpdateClass.putData(action.actionType,action.actionID)
            actionAddTimesTool.execSQL(actionUpdateSql)
            GetMonthInfo.setDefaultSelectedListChangedFlag(true)
            actionAddTimesTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Add In Plan Failed(In ActionGroupAdapterInPlanDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.add_failed)).showToast()
        }finally{
            actionAddTimesTool.endTransaction()
            actionAddTimesTool.close()
            actionAddTimesDatabase.close()
        }
    }

    private fun delAction(toBeDeleteID:Int){
        //数据库删除
        val actionDeleteInDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionDeleteTool=actionDeleteInDatabase.writableDatabase
        actionDeleteTool.beginTransaction()
        try{
            val delSql = "Delete From PlanDetailTable Where ActionID=$toBeDeleteID And Date=\"$dateInfo\" "
            actionDeleteTool.execSQL(delSql)
            //更新添加次数
            val actionUpdateSql = "Update ActionTable Set AddTimes=AddTimes-1 Where ActionID=$toBeDeleteID"
            LibraryUpdateClass.putData(getKeyInPlanDetailMap(toBeDeleteID)!!.actionType,toBeDeleteID)
            actionDeleteTool.execSQL(actionUpdateSql)
            planDetailMap.remove(getKeyInPlanDetailMap(toBeDeleteID))
            val position = actionIDList.indexOf(toBeDeleteID)
            actionIDList.removeAt(position)
            notifyItemRemoved(position)
            if (position == actionIDList.size){
                notifyItemRangeChanged(position-1,actionIDList.size+1-position)
            }else{
                notifyItemRangeChanged(position,actionIDList.size-position)
            }
            if (actionIDList.size == 0){
                GetMonthInfo.setDefaultSelectedListChangedFlag(true)
                if (dateInfo == GetMonthInfo.getTodayString()){
                    val recordCursor = actionDeleteTool.rawQuery("Select * From PlanRecord Where Date=?",
                        arrayOf(dateInfo))
                    if (recordCursor.count>0){
                        val recordDeleteSql = "Delete From PlanRecord Where PlanRecordID=${recordCursor.getString(2)}"
                        actionDeleteTool.execSQL(recordDeleteSql)
                    }
                    recordCursor.close()
                }
            }
            actionDeleteTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Delete In Plan Failed(In ActionGroupAdapterInPlanDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.del_failed)).showToast()
        }finally{
            actionDeleteTool.endTransaction()
            actionDeleteTool.close()
            actionDeleteInDatabase.close()
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
    }


    override fun onLastItemDelete(actionID: Int) {
        delAction(actionID)
    }



}