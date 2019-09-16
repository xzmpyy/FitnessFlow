package com.example.zhangjie.fitnessflow.library

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateModifyClass
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import com.example.zhangjie.fitnessflow.utils_class.ProcessDialogFragment

class TemplateDetailActivity : AppCompatActivity() ,MyDialogFragment.ConfirmButtonClickListener{

    private var template:Template?=null
    private var backButton:Button? = null
    private var saveButton:Button? = null
    private var templateName: TextView? = null
    private var editType = 0
    private var processDialogFragment: ProcessDialogFragment? = null
    //编辑相关
    private var editDialog:MyDialogFragment? = null
    private var templateEditView:View? = null
    private var templateNameEditButton:ImageButton? = null
    private var editTextParent:LinearLayout? = null
    private var saveFlag = true
    private var muscleGroupInclude = StringBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_detail)
        template = TemplateModifyClass.getTemplate()!!
        //相关视图
        backButton = findViewById(R.id.back)
        saveButton = findViewById(R.id.save)
        templateName = findViewById(R.id.template_name)
        templateNameEditButton = findViewById(R.id.template_name_edit)
        processDialogFragment = ProcessDialogFragment(this.resources.getString(R.string.save_process))
        backButton!!.setOnClickListener {
            TemplateModifyClass.clear()
            finish()
        }
        saveButton!!.setOnClickListener {
            TemplateModifyClass.setTemplate(template!!)
            processDialogFragment!!.show(supportFragmentManager, null)
            val dataSaving = DataSaving()
            dataSaving.execute()
        }
        templateNameEditButton!!.setOnClickListener {
            templateEditDialog(0)
        }
        templateName!!.text = template!!.templateName
    }

    //0是模板名称编辑，1是有重量编辑，2是无重量编辑
    private fun templateEditDialog(type:Int){
        when(type){
            0->{
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
        }
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

    private fun templateUpdate(){
        for (muscleGroup in template!!.muscleGroupInclude){
            if (template!!.muscleGroupInclude.indexOf(muscleGroup) == template!!.muscleGroupInclude.count()-1){
                muscleGroupInclude.append(muscleGroup)
            }else{
                muscleGroupInclude.append("$muscleGroup;")
            }
        }
        val templateUpdateDatabase= MyDataBaseTool(this,"FitnessFlowDB",null,1)
        val templateUpdateDataBaseTool=templateUpdateDatabase.writableDatabase
        templateUpdateDataBaseTool.beginTransaction()
        try{
            val updateSql = "Update TemplateTable Set TemplateName=\"${template!!.templateName}\",ActionNum=${template!!.actionNum}," +
                    "MuscleGroupInclude=\"$muscleGroupInclude\" Where TemplateID=${template!!.templateID}"
            templateUpdateDataBaseTool.execSQL(updateSql)
            templateUpdateDataBaseTool.setTransactionSuccessful()
            if (!saveFlag){
                saveFlag = true
            }
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
            templateUpdate()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (saveFlag){
                processDialogFragment!!.dismiss()
                finish()
            }else{
                MyToast(this@TemplateDetailActivity,resources.getString(R.string.save_failed)).showToast()
                processDialogFragment!!.dismiss()
            }
        }

    }

}
