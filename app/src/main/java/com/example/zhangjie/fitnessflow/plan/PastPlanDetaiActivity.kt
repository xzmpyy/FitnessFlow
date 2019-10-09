package com.example.zhangjie.fitnessflow.plan

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInPlan
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.fit_calendar.SelectedItemClass
import com.example.zhangjie.fitnessflow.library.LibraryUpdateClass
import com.example.zhangjie.fitnessflow.plan.plan_detail.PastActionGroupAdapter
import com.example.zhangjie.fitnessflow.utils_class.*

class PastPlanDetailActivity : AppCompatActivity() ,
    CalendarDialog.DateSelectedListener,
    MyDialogFragment.ConfirmButtonClickListener{

    private var dateInfo:String?=null
    private var backButton: Button? = null
    private var dateInFoTextView: TextView? = null
    private var processDialogFragment: ProcessDialogFragment? = null
    //编辑相关
    private val actionList = arrayListOf<Action>()
    private val planDetailMap = mutableMapOf<Action,ArrayList<ActionDetailInPlan>>()
    private val actionIDListInPlanDetail = arrayListOf<Int>()
    private var sendButton: ImageButton? = null
    private var singlePickDate:String? = null
    private var templateCopyFlag = true
    private var planOrderCorrectFlag = true
    private val targetDaysList = arrayListOf<String>()
    private var newTemplateButton: ImageButton? = null
    private var newTemplateFlag = true
    private var formView: View? = null
    private var formDialog: MyDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_plan_detail)
        dateInfo = intent!!.extras!!.getString("Date")
        //相关视图
        backButton = this.findViewById(R.id.back)
        dateInFoTextView = this.findViewById(R.id.date_info)
        sendButton = this.findViewById(R.id.day_send)
        processDialogFragment = ProcessDialogFragment(this.resources.getString(R.string.save_process))
        backButton!!.setOnClickListener {
            finish()
        }
        dateInFoTextView!!.text = dateInfo
        sendButton!!.setOnClickListener {
            singlePickDate = SelectedItemClass.getSelectedList()[0]
            SelectedItemClass.clear()
            val calendarDialog = CalendarDialog()
            calendarDialog.setDateSelectedListener(this)
            calendarDialog.show(this.supportFragmentManager,null)
        }
        newTemplateButton = this.findViewById(R.id.new_template)
        newTemplateButton!!.setOnClickListener {
            formView = View.inflate(this, R.layout.template_create_dialog,null)
            formDialog = MyDialogFragment(2, Gravity.CENTER,1,formView!!)
            formDialog!!.setConfirmButtonClickListener(this)
            formDialog!!.show(supportFragmentManager,null)

        }
        //RecyclerView
        recyclerViewDataInit()
        val pastDetailRecyclerView = this.findViewById<RecyclerView>(R.id.action_detail)
        pastDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        pastDetailRecyclerView.adapter = PastActionGroupAdapter(actionList,planDetailMap,this)
    }

    //初始化RecyclerView的数据
    private fun recyclerViewDataInit(){
        val recyclerViewDataInitDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
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
                        actionList.add(action)
                        break
                    }
                    actionSelectCursor.close()
                }
            }
            planDetailCursor.close()
            recyclerViewDataInitTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("RecyclerView Init Failed(In PastPlanDetailActivity):$e")
            MyToast(this,this.resources.getString(R.string.loading_failed)).showToast()
        }finally{
            recyclerViewDataInitTool.endTransaction()
            recyclerViewDataInitTool.close()
            recyclerViewDataInitDatabase.close()
        }
    }


    private fun getKeyInPlanDetailMap(id:Int): Action?{
        for (action in planDetailMap.keys){
            if (action.actionID == id){
                return action
            }
        }
        return null
    }


    override fun onDateCancelButtonClick() {
        SelectedItemClass.clear()
        SelectedItemClass.addItem(singlePickDate!!)
    }

    override fun onDateConfirmButtonClick() {
        targetDaysList.clear()
        for (day in SelectedItemClass.getSelectedList()){
            targetDaysList.add(day)
        }
        processDialogFragment!!.show(this.supportFragmentManager, null)
        val copyProcess = CopyToOtherDays()
        copyProcess.execute()
    }

    //模板计划到其他日期
    private fun copyToTargetDays(date:String){
        val sendDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val sendTool=sendDatabase.writableDatabase
        sendTool.beginTransaction()
        try{
            val planDetailCursor=sendTool.rawQuery("Select * From PlanDetailTable where Date=? Order By PlanOrder" ,arrayOf(date))
            while(planDetailCursor.moveToNext()){
                //向每日中插入数据
                for (day in targetDaysList){
                    val insertSql = "Insert Into PlanDetailTable (ActionID,ActionType,ActionName," +
                            "IsHadWeightUnits,Unit,Weight,Num,Done,PlanOrder,Date) Values(${planDetailCursor.getString(0)}," +
                            "${planDetailCursor.getString(1)},\"${planDetailCursor.getString(2)}\"," +
                            "${planDetailCursor.getString(3)},\"${planDetailCursor.getString(4)}\"," +
                            "${planDetailCursor.getString(5)},${planDetailCursor.getString(6)}," +
                            "0,0,\"$day\")"
                    sendTool.execSQL(insertSql)
                }
            }
            planDetailCursor.close()
            if (!templateCopyFlag){
                templateCopyFlag = true
            }
            sendTool.setTransactionSuccessful()
        }catch(e: java.lang.Exception){
            println("Plan Copy To Target Days Failed(In PastPlanDetailActivity):$e")
            if (templateCopyFlag){
                templateCopyFlag = false
            }
        }finally{
            sendTool.endTransaction()
            sendTool.close()
            sendDatabase.close()
        }
        planOrderCorrectFlag = ActionOrderInPlanDetailCorrect.correct(this,targetDaysList)
    }

    //后台保存任务
    @SuppressLint("StaticFieldLeak")
    inner class CopyToOtherDays: AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            copyToTargetDays(dateInfo!!)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (templateCopyFlag && planOrderCorrectFlag){
                processDialogFragment!!.dismiss()
                MyToast(this@PastPlanDetailActivity,this@PastPlanDetailActivity.resources.getString(R.string.add_successful)).showToast()
            }else{
                MyToast(this@PastPlanDetailActivity,this@PastPlanDetailActivity.resources.getString(R.string.add_failed)).showToast()
                processDialogFragment!!.dismiss()
            }
            SelectedItemClass.addItem(singlePickDate!!)
        }
    }

    //创建新模板
    private fun newTemplateCreate(templateName:String){
        val templateID:Int?
        val newTemplate: Template?
        val templateCreateDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val templateCreateTool=templateCreateDatabase.writableDatabase
        templateCreateTool.beginTransaction()
        try{
            //新建模板
            val insertSql = "INSERT INTO TemplateTable (TemplateName,ActionNum,MuscleGroupInclude) VALUES (" +
                    "\"$templateName\",0,\"\")"
            templateCreateTool.execSQL(insertSql)
            val idCheckCursor = templateCreateTool.rawQuery("select last_insert_rowid() from TemplateTable",null)
            idCheckCursor.moveToNext()
            templateID = idCheckCursor.getString(0).toInt()
            newTemplate = Template(templateName,0, arrayListOf(),templateID)
            idCheckCursor.close()
            //查询计划，向模板详情添加数据
            val planDetailCheckCursor = templateCreateTool.rawQuery("select * from PlanDetailTable where Date=? Order By PlanOrder",
                arrayOf(dateInfo))
            val muscleGroupList = arrayListOf<Int>()
            val actionList = arrayListOf<String>()
            while (planDetailCheckCursor.moveToNext()){
                if (!muscleGroupList.contains(planDetailCheckCursor.getString(1).toInt())){
                    muscleGroupList.add(planDetailCheckCursor.getString(1).toInt())
                }
                if (!actionList.contains(planDetailCheckCursor.getString(0))){
                    actionList.add(planDetailCheckCursor.getString(0))
                }
                val templateDetailInsertSql =  "Insert Into TemplateDetailTable (ActionID,ActionType,ActionName," +
                        "IsHadWeightUnits,Unit,Weight,Num,TemplateID,TemplateOrder) Values(${planDetailCheckCursor.getString(0)}," +
                        "${planDetailCheckCursor.getString(1)},\"${planDetailCheckCursor.getString(2)}\"," +
                        "${planDetailCheckCursor.getString(3)},\"${planDetailCheckCursor.getString(4)}\"," +
                        "${planDetailCheckCursor.getString(5)},${planDetailCheckCursor.getString(6)},$templateID," +
                        "${planDetailCheckCursor.getString(8)})"
                templateCreateTool.execSQL(templateDetailInsertSql)
            }
            planDetailCheckCursor.close()
            //更新涉及部位
            val muscleGroupNameList = this.resources.getStringArray(R.array.muscle_group)
            val muscleGroupInclude = StringBuilder()
            newTemplate.actionNum = actionList.size
            for (actionType in muscleGroupList){
                newTemplate.muscleGroupInclude.add(muscleGroupNameList[actionType])
                if (muscleGroupList.indexOf(actionType) == muscleGroupList.size-1){
                    muscleGroupInclude.append(muscleGroupNameList[actionType])
                }else{
                    muscleGroupInclude.append("${muscleGroupNameList[actionType]};")
                }
            }
            val muscleGroupUpdateSql = "Update TemplateTable Set ActionNum=${actionList.size},MuscleGroupInclude=\"$muscleGroupInclude\" Where TemplateID=$templateID"
            LibraryUpdateClass.putNewTemplate(newTemplate)
            templateCreateTool.execSQL(muscleGroupUpdateSql)
            if (!newTemplateFlag){
                newTemplateFlag = true
            }
            templateCreateTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Create Failed(In PastPlanDetailActivity):$e")
            MyToast(this as Context,resources.getString(R.string.add_failed)).showToast()
            if (newTemplateFlag){
                newTemplateFlag = false
            }
        }finally{
            templateCreateTool.endTransaction()
            templateCreateTool.close()
            templateCreateDatabase.close()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class NewTemplate(templateName: String): AsyncTask<Void, Int, Boolean>() {

        private val newTemplateName = templateName

        override fun doInBackground(vararg params: Void?): Boolean {
            newTemplateCreate(newTemplateName)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (newTemplateFlag){
                processDialogFragment!!.dismiss()
                MyToast(this@PastPlanDetailActivity,this@PastPlanDetailActivity.resources.getString(R.string.add_successful)).showToast()
            }else{
                MyToast(this@PastPlanDetailActivity,this@PastPlanDetailActivity.resources.getString(R.string.add_failed)).showToast()
                processDialogFragment!!.dismiss()
            }
        }
    }

    override fun onConfirmButtonClick() {
        if (formView!=null){
            val templateName = formView!!.findViewById<EditText>(R.id.template_name)
            if (TextUtils.isEmpty(templateName!!.text)){
                templateName.background = ContextCompat.getDrawable(this, R.drawable.incomplete_edit_text_background)
                MyToast(this,resources.getString(R.string.form_incomplete)).showToast()
                (this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(
                    VibrationEffect.createOneShot(400,4))
            }else{
                processDialogFragment!!.show(this.supportFragmentManager, null)
                val newTemplateProcess = NewTemplate(templateName.text.toString())
                newTemplateProcess.execute()
                formDialog!!.dismiss()
            }
        }
    }

}