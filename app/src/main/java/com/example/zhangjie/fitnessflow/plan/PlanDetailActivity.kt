package com.example.zhangjie.fitnessflow.plan

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.fit_calendar.SelectedItemClass
import com.example.zhangjie.fitnessflow.plan.plan_detail.ActionGroupAdapterInPlanDetailActivity
import com.example.zhangjie.fitnessflow.utils_class.*
import com.example.zhangjie.fitnessflow.utils_class.action_pick.ActionPickDialog

class PlanDetailActivity : AppCompatActivity() ,
    ActionPickDialog.AddButtonClickListener,CalendarDialog.DateSelectedListener{

    private var dateInfo:String?=null
    private var backButton: Button? = null
    private var saveButton:Button? = null
    private var dateInFoTextView: TextView? = null
    private var processDialogFragment: ProcessDialogFragment? = null
    //RecyclerView相关
    private var actionGroupRv: RecyclerViewForItemSwap? = null
    private val actionGroupLayoutManager = LinearLayoutManager(this)
    private var actionGroupRvAdapter:ActionGroupAdapterInPlanDetailActivity? = null
    private var myTouchHelperCallback: MyItemTouchHelperCallback? = null
    //编辑相关
    private var saveFlag = true
    private var detailSaveFlag = true
    private var addButton: ImageButton? = null
    private val planDetailMap = mutableMapOf<Action,ArrayList<ActionDetailInPlan>>()
    private val actionIDListInPlanDetail = arrayListOf<Int>()
    private var sendButton:ImageButton? = null
    private var singlePickDate:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_detail)
        dateInfo = intent!!.extras!!.getString("Date")
        //相关视图
        backButton = this.findViewById(R.id.back)
        saveButton = this.findViewById(R.id.save)
        dateInFoTextView = this.findViewById(R.id.date_info)
        sendButton = this.findViewById(R.id.day_send)
        processDialogFragment = ProcessDialogFragment(this.resources.getString(R.string.save_process))
        backButton!!.setOnClickListener {
            finish()
        }
        saveButton!!.setOnClickListener {
            processDialogFragment!!.show(supportFragmentManager, null)
            val dataSaving = DataSaving()
            dataSaving.execute()
        }
        dateInFoTextView!!.text = dateInfo
        addButton = findViewById(R.id.add_button)
        addButton!!.setOnClickListener {
            val actionPickDialog = ActionPickDialog(actionIDListInPlanDetail,this)
            actionPickDialog.setAddButtonClickListener(this)
            actionPickDialog.show(supportFragmentManager,null)
        }
        sendButton!!.setOnClickListener {
            singlePickDate = SelectedItemClass.getSelectedList()[0]
            SelectedItemClass.clear()
            val calendarDialog = CalendarDialog()
            calendarDialog.setDateSelectedListener(this)
            calendarDialog.show(this.supportFragmentManager,null)
        }
        //RecyclerView
        recyclerViewDataInit()
        actionGroupRv = this.findViewById(R.id.action_group_rv)
        actionGroupRv!!.layoutManager = actionGroupLayoutManager
        actionGroupRvAdapter = ActionGroupAdapterInPlanDetailActivity(dateInfo!!,planDetailMap,actionIDListInPlanDetail,this)
        actionGroupRv!!.adapter = actionGroupRvAdapter
        //RecyclerView中的移动换位
        myTouchHelperCallback = MyItemTouchHelperCallback(null,planDetailMap,actionIDListInPlanDetail,actionGroupRvAdapter!!)
        val helper = ItemTouchHelper(myTouchHelperCallback!!)
        helper.attachToRecyclerView(actionGroupRv)
        actionGroupRv!!.setHelper(helper)
        actionGroupRvAdapter!!.setDragListener(actionGroupRv!!)
    }


    private fun planDetailUpdate(){
        val planDetailUpdateDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val planDetailUpdateDataBaseTool=planDetailUpdateDatabase.writableDatabase
        planDetailUpdateDataBaseTool.beginTransaction()
        try{
            for (key in planDetailMap.keys){
                for (detail in planDetailMap[key]!!){
                    val updateSql = "Update PlanDetailTable Set ActionID=${detail.actionID},ActionType=${detail.actionType}," +
                            "ActionName=\"${detail.actionName}\",IsHadWeightUnits=${detail.isHadWeightUnits}," +
                            "Unit=\"${detail.unit}\",Weight=${detail.weight},Num=${detail.num}," +
                            "Done=${detail.done},PlanOrder=${detail.planOrder} WHERE ID=${detail.ID}"
                    planDetailUpdateDataBaseTool.execSQL(updateSql)
                }
            }
            if (!detailSaveFlag){
                detailSaveFlag = true
            }
            planDetailUpdateDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Plan Detail Update Failed(In PlanDetailActivity):$e")
            if (detailSaveFlag){
                detailSaveFlag = false
            }
        }finally{
            planDetailUpdateDataBaseTool.endTransaction()
            planDetailUpdateDataBaseTool.close()
            planDetailUpdateDatabase.close()
        }
    }


    @SuppressLint("StaticFieldLeak")
    inner class DataSaving: AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            planDetailUpdate()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (saveFlag && detailSaveFlag){
                processDialogFragment!!.dismiss()
                finish()
            }else{
                MyToast(this@PlanDetailActivity,resources.getString(R.string.save_failed)).showToast()
                processDialogFragment!!.dismiss()
            }
        }

    }

    //初始化RecyclerView的数据
    private fun recyclerViewDataInit(){
        val recyclerViewDataInitDatabase=MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val recyclerViewDataInitTool=recyclerViewDataInitDatabase.writableDatabase
        recyclerViewDataInitTool.beginTransaction()
        try{
            val planDetailCursor=recyclerViewDataInitTool.rawQuery("Select * From PlanDetailTable where Date=? Order By PlanOrder",arrayOf(dateInfo))
            while(planDetailCursor.moveToNext()){
                val actionDetailInPlan = ActionDetailInPlan(planDetailCursor.getString(0).toInt(),
                    planDetailCursor.getString(1).toInt(),planDetailCursor.getString(2),
                    planDetailCursor.getString(3).toInt(),planDetailCursor.getString(4),
                    planDetailCursor.getString(5).toFloat(),planDetailCursor.getString(6).toInt(),
                    planDetailCursor.getString(7).toInt(),planDetailCursor.getString(8).toInt(),planDetailCursor.getString(10).toInt())
                if (actionIDListInPlanDetail.contains(planDetailCursor.getString(0).toInt())){
                    planDetailMap[getKeyInPlanDetailMap(actionDetailInPlan.actionID)]!!.add(actionDetailInPlan)
                }else{
                    actionIDListInPlanDetail.add(planDetailCursor.getString(0).toInt())
                    //生成动作类，插入新键值
                    val actionSelectCursor=recyclerViewDataInitTool.rawQuery("Select * From ActionTable where ActionID=?",arrayOf(planDetailCursor.getString(0)))
                    while(actionSelectCursor.moveToNext()){
                        val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                            actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                            actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                            actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                        planDetailMap[action] = arrayListOf(actionDetailInPlan)
                        break
                    }
                    actionSelectCursor.close()
                }
            }
            planDetailCursor.close()
            recyclerViewDataInitTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("RecyclerView Init Failed(In TemplateDetailActivity):$e")
            MyToast(this,this.resources.getString(R.string.loading_failed)).showToast()
        }finally{
            recyclerViewDataInitTool.endTransaction()
            recyclerViewDataInitTool.close()
            recyclerViewDataInitDatabase.close()
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

    //实现动作添加的监听接口
    override fun onAddButtonClick(action: Action) {
        actionGroupRvAdapter!!.addAction(action, actionIDListInPlanDetail.size)
        actionGroupRv!!.scrollToPosition(actionIDListInPlanDetail.size-1)
    }

    override fun onDateCancelButtonClick() {
        SelectedItemClass.clear()
        SelectedItemClass.addItem(singlePickDate!!)
    }

    override fun onDateConfirmButtonClick() {
        SelectedItemClass.clear()
        SelectedItemClass.addItem(singlePickDate!!)
    }

}
