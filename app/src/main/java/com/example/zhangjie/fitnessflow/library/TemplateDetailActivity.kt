package com.example.zhangjie.fitnessflow.library

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateModifyClass
import com.example.zhangjie.fitnessflow.utils_class.*
import com.example.zhangjie.fitnessflow.utils_class.action_pick.ActionPickDialog

class TemplateDetailActivity : AppCompatActivity() ,MyDialogFragment.ConfirmButtonClickListener,
    ActionPickDialog.AddButtonClickListener{

    private var template:Template?=null
    private var backButton:Button? = null
    private var saveButton:Button? = null
    private var templateName: TextView? = null
    private var editType = 0
    private var processDialogFragment: ProcessDialogFragment? = null
    //RecyclerView相关
    private var actionGroupRv:RecyclerViewForItemSwap? = null
    private val actionGroupLayoutManager = LinearLayoutManager(this)
    private var actionGroupRvAdapter:ActionGroupAdapterInTemplateDetailActivity? = null
    private var myTouchHelperCallback:MyItemTouchHelperCallback? = null
    //编辑相关
    private var editDialog:MyDialogFragment? = null
    private var templateEditView:View? = null
    private var templateNameEditButton:ImageButton? = null
    private var editTextParent:LinearLayout? = null
    private var saveFlag = true
    private var detailSaveFlag = true
    private var muscleGroupInclude = StringBuilder()
    private var muscleGroupIncludeList = arrayListOf<String>()
    private var addButton:ImageButton? = null
    private val templateDetailMap = mutableMapOf<Action,ArrayList<ActionDetailInTemplate>>()
    private val actionIDListInTemplateDetail = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_detail)
        template = TemplateModifyClass.getTemplate()!!
        //相关视图
        backButton = this.findViewById(R.id.back)
        saveButton = this.findViewById(R.id.save)
        templateName = this.findViewById(R.id.template_name)
        templateNameEditButton = this.findViewById(R.id.template_name_edit)
        processDialogFragment = ProcessDialogFragment(this.resources.getString(R.string.save_process))
        backButton!!.setOnClickListener {
            TemplateModifyClass.clear()
            finish()
        }
        saveButton!!.setOnClickListener {
            processDialogFragment!!.show(supportFragmentManager, null)
            val dataSaving = DataSaving()
            dataSaving.execute()
        }
        templateNameEditButton!!.setOnClickListener {
            templateNameEditDialog()
        }
        templateName!!.text = template!!.templateName
        addButton = findViewById(R.id.add_button)
        addButton!!.setOnClickListener {
            val actionPickDialog = ActionPickDialog(actionIDListInTemplateDetail,this)
            actionPickDialog.setAddButtonClickListener(this)
            actionPickDialog.show(supportFragmentManager,null)
        }
        //RecyclerView
        recyclerViewDataInit()
        actionGroupRv = this.findViewById(R.id.action_group_rv)
        actionGroupRv!!.layoutManager = actionGroupLayoutManager
        actionGroupRvAdapter = ActionGroupAdapterInTemplateDetailActivity(template!!.templateID,templateDetailMap,actionIDListInTemplateDetail,this)
        actionGroupRv!!.adapter = actionGroupRvAdapter
        //RecyclerView中的移动换位
        myTouchHelperCallback = MyItemTouchHelperCallback(templateDetailMap,actionIDListInTemplateDetail,actionGroupRvAdapter!!)
        val helper = ItemTouchHelper(myTouchHelperCallback!!)
        helper.attachToRecyclerView(actionGroupRv)
        actionGroupRv!!.setHelper(helper)
        actionGroupRvAdapter!!.setDragListener(actionGroupRv!!)
    }

    //模板名称编辑
    private fun templateNameEditDialog(){
        templateEditView = View.inflate(this,R.layout.template_create_dialog,null)
        editTextParent = templateEditView!!.findViewById(R.id.parent_view)
        val templateNameEdit = templateEditView!!.findViewById<EditText>(R.id.template_name)
        templateNameEdit.setText(template!!.templateName.toCharArray(), 0, template!!.templateName.count())
        editDialog = MyDialogFragment(2,Gravity.CENTER,1,templateEditView!!)
        editDialog!!.setConfirmButtonClickListener(this)
        //延时弹出键盘
        Handler().postDelayed({ editTextGetFocus(templateNameEdit) },100)
        editDialog!!.show(supportFragmentManager,null)
    }

    override fun onConfirmButtonClick() {
        editTextParent!!.focusable = View.FOCUSABLE
        editTextParent!!.isFocusableInTouchMode = true
        editTextParent!!.requestFocus()
        //收起键盘
        val imm: InputMethodManager = editTextParent!!.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTextParent!!.windowToken, 0)
        when (editType){
            //模板编辑
            0->{
                val templateName = editTextParent!!.findViewById<EditText>(R.id.template_name)
                if (TextUtils.isEmpty(templateName!!.text)){
                    templateName.background = ContextCompat.getDrawable(this,R.drawable.incomplete_edit_text_background)
                    MyToast(this,resources.getString(R.string.form_incomplete)).showToast()
                    (this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(
                        VibrationEffect.createOneShot(400,4))
                }else{
                    templateNameUpdate(templateName.text.toString())
                    editDialog!!.dismiss()
                }
            }
        }
    }

    //EditText自动获取焦点并弹出键盘
    private fun editTextGetFocus(editText:EditText){
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        editText.setSelection(editText.text.toString().length)
        val inputManager:InputMethodManager  = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }

    private fun templateNameUpdate(newName:String){
        template!!.templateName = newName
        templateName!!.text = newName
    }

    private fun templateMuscleGroupIncludeUpdate():String{
        val muscleGroupNameList = this.resources.getStringArray(R.array.muscle_group)
        val includeTypeList = arrayListOf<Int>()
        for (actionID in actionIDListInTemplateDetail){
            if (!includeTypeList.contains(getKeyInTemplateDetailMap(actionID)!!.actionType)){
                includeTypeList.add(getKeyInTemplateDetailMap(actionID)!!.actionType)
            }
        }
        for (actionType in includeTypeList){
            if (includeTypeList.indexOf(actionType) == includeTypeList.size-1){
                muscleGroupInclude.append(muscleGroupNameList[actionType])
            }else{
                muscleGroupInclude.append("${muscleGroupNameList[actionType]};")
            }
            muscleGroupIncludeList.add(muscleGroupNameList[actionType])
        }
        return muscleGroupInclude.toString()
    }

    private fun templateDetailUpdate(){
        val templateDetailUpdateDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val templateDetailUpdateDataBaseTool=templateDetailUpdateDatabase.writableDatabase
        templateDetailUpdateDataBaseTool.beginTransaction()
        try{
            for (key in templateDetailMap.keys){
                for (detail in templateDetailMap[key]!!){
                    val updateSql = "Update TemplateDetailTable Set ActionID=${detail.actionID},ActionType=${detail.actionType}," +
                            "ActionName=\"${detail.actionName}\",IsHadWeightUnits=${detail.isHadWeightUnits}," +
                            "Unit=\"${detail.unit}\",Weight=${detail.weight},Num=${detail.num}," +
                            "TemplateID=${template!!.templateID},TemplateOrder=${detail.templateOrder} WHERE ID=${detail.ID}"
                    templateDetailUpdateDataBaseTool.execSQL(updateSql)
                }
            }
            if (!detailSaveFlag){
                detailSaveFlag = true
            }
            templateDetailUpdateDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Detail Update Failed(In TemplateDetailActivity):$e")
            if (detailSaveFlag){
                detailSaveFlag = false
            }
        }finally{
            templateDetailUpdateDataBaseTool.endTransaction()
            templateDetailUpdateDataBaseTool.close()
            templateDetailUpdateDatabase.close()
        }
    }

    private fun templateUpdate(){
        val templateUpdateDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val templateUpdateDataBaseTool=templateUpdateDatabase.writableDatabase
        template!!.actionNum = actionIDListInTemplateDetail.size
        template!!.muscleGroupInclude = muscleGroupIncludeList
        templateUpdateDataBaseTool.beginTransaction()
        try{
            val updateSql = "Update TemplateTable Set TemplateName=\"${template!!.templateName}\",ActionNum=${actionIDListInTemplateDetail.size}," +
                    "MuscleGroupInclude=\"${templateMuscleGroupIncludeUpdate()}\" Where TemplateID=${template!!.templateID}"
            templateUpdateDataBaseTool.execSQL(updateSql)
            if (!saveFlag){
                saveFlag = true
            }
            templateUpdateDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Update Failed(In TemplateDetailActivity):$e")
            if (saveFlag){
                saveFlag = false
            }
        }finally{
            templateUpdateDataBaseTool.endTransaction()
            templateUpdateDataBaseTool.close()
            templateUpdateDatabase.close()
        }
    }


    @SuppressLint("StaticFieldLeak")
    inner class DataSaving: AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            templateDetailUpdate()
            templateUpdate()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (saveFlag && detailSaveFlag){
                TemplateModifyClass.setTemplate(template!!)
                processDialogFragment!!.dismiss()
                finish()
            }else{
                MyToast(this@TemplateDetailActivity,resources.getString(R.string.save_failed)).showToast()
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
            val templateDetailCursor=recyclerViewDataInitTool.rawQuery("Select * From TemplateDetailTable where TemplateID=? Order By TemplateOrder",arrayOf(template!!.templateID.toString()))
            while(templateDetailCursor.moveToNext()){
                val actionDetailInTemplate = ActionDetailInTemplate(templateDetailCursor.getString(0).toInt(),
                    templateDetailCursor.getString(1).toInt(),templateDetailCursor.getString(2),
                    templateDetailCursor.getString(3).toInt(),templateDetailCursor.getString(4),
                    templateDetailCursor.getString(5).toFloat(),templateDetailCursor.getString(6).toInt(),
                    templateDetailCursor.getString(8).toInt(),templateDetailCursor.getString(9).toInt())
                if (actionIDListInTemplateDetail.contains(templateDetailCursor.getString(0).toInt())){
                    templateDetailMap[getKeyInTemplateDetailMap(actionDetailInTemplate.actionID)]!!.add(actionDetailInTemplate)
                }else{
                    actionIDListInTemplateDetail.add(templateDetailCursor.getString(0).toInt())
                    //生成动作类，插入新键值
                    val actionSelectCursor=recyclerViewDataInitTool.rawQuery("Select * From ActionTable where ActionID=?",arrayOf(templateDetailCursor.getString(0)))
                    while(actionSelectCursor.moveToNext()){
                        val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                            actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                            actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                            actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                        templateDetailMap[action] = arrayListOf(actionDetailInTemplate)
                        break
                    }
                    actionSelectCursor.close()
                }
            }
            templateDetailCursor.close()
            recyclerViewDataInitTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("RecyclerView Init Failed(In TemplateDetailActivity):$e")
            MyToast(this,this.resources.getString(R.string.loading_failed))
        }finally{
            recyclerViewDataInitTool.endTransaction()
            recyclerViewDataInitTool.close()
            recyclerViewDataInitDatabase.close()
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

    //实现动作添加的监听接口
    override fun onAddButtonClick(action: Action) {
        actionGroupRvAdapter!!.addAction(action, actionIDListInTemplateDetail.size)
    }

}
