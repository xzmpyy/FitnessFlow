package com.example.zhangjie.fitnessflow.mine

import android.annotation.SuppressLint
import android.app.Service
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import java.math.RoundingMode
import java.text.DecimalFormat

class MineFragment : Fragment(), MyDialogFragment.ConfirmButtonClickListener {

    private var weightText:TextView? = null
    private var fatText:TextView? = null
    private var bmiText:TextView? = null
    private var weightUnit:String? = null
    private var daysCount:TextView? = null
    private val todayString = GetMonthInfo.getTodayString()
    private var formView:View? = null
    private var dialogFragment:MyDialogFragment? = null
    private var weight:Float? = null
    private var fat:Float? = null
    private var bmi:Float? = null
    private var age:Int? = null
    private var stature:Float? = null
    //0女1男
    private var sex=0
    private var editTextBackground:Drawable? = null
    private var incompleteEditTextBackground:Drawable? = null


    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_mine,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        weightText = view.findViewById(R.id.weight)
        fatText = view.findViewById(R.id.body_fat)
        bmiText = view.findViewById(R.id.bmi)
        daysCount = view.findViewById(R.id.days)
        if (weightUnit == null){
            weightUnit = view.context.resources.getString(R.string.KG)
        }
        editTextBackground = ContextCompat.getDrawable(view.context,R.drawable.edit_text_background)
        incompleteEditTextBackground = ContextCompat.getDrawable(view.context,R.drawable.incomplete_edit_text_background)
        dataInit()
    }

    fun personalDataEdit(){
        formView = View.inflate(view!!.context,R.layout.personal_data_form_view,null)
        dialogFragment = MyDialogFragment(2,Gravity.CENTER,1,formView!!)
        dialogFragment!!.setConfirmButtonClickListener(this)
        if (stature!=null){
            formView!!.findViewById<EditText>(R.id.stature).setText(stature.toString().toCharArray(),0,stature.toString().count())
            formView!!.findViewById<EditText>(R.id.age).setText(age.toString().toCharArray(),0,age.toString().count())
            formView!!.findViewById<EditText>(R.id.weight).setText(weight.toString().toCharArray(),0,weight.toString().count())
            if (sex == 1){
                formView!!.findViewById<Switch>(R.id.sex_switch).isChecked = true
                formView!!.findViewById<TextView>(R.id.sex).text = view!!.context.getString(R.string.male)
            }
        }
        formView!!.findViewById<Switch>(R.id.sex_switch).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                formView!!.findViewById<TextView>(R.id.sex).text = view!!.context.getString(R.string.male)
            }else{
                formView!!.findViewById<TextView>(R.id.sex).text = view!!.context.getString(R.string.female)
            }
        }
        dialogFragment!!.show(this.childFragmentManager,null)
    }

    @SuppressLint("SetTextI18n")
    private fun dataInit(){
        val myDatabase= MyDataBaseTool(view!!.context,"FitnessFlowDB",null,1)
        val myDataBaseTool=myDatabase.writableDatabase
        myDataBaseTool.beginTransaction()
        try{
            val cursor=myDataBaseTool.rawQuery("Select * From PersonalData Order By DataID Desc",null)
            while(cursor.moveToNext()){
                weightText!!.text = "${cursor.getString(4)}$weightUnit"
                fatText!!.text = "${cursor.getString(5)}%"
                bmiText!!.text = cursor.getString(6).toString()
                weight = cursor.getString(4).toFloat()
                fat = cursor.getString(5).toFloat()
                bmi = cursor.getString(6).toFloat()
                age =cursor.getString(2).toInt()
                stature = cursor.getString(3).toFloat()
                sex = cursor.getString(1).toInt()
                break
            }
            cursor.close()
            val daysCursor = myDataBaseTool.rawQuery("Select * From PlanRecord",null)
            if (daysCursor.count > 0){
                daysCount!!.text = daysCount!!.text.toString().replace("count",daysCursor.count.toString())
            }else{
                daysCount!!.text = daysCount!!.text.toString().replace("count","0")
            }
            daysCursor.close()
            myDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Data Init Failed(In MineFragment):$e")
        }finally{
            myDataBaseTool.endTransaction()
            myDataBaseTool.close()
            myDatabase.close()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onConfirmButtonClick() {
        val formFlag = arrayListOf(true,true,true)
        if (TextUtils.isEmpty(formView!!.findViewById<EditText>(R.id.stature)!!.text)){
            if (formFlag[0]){
                formView!!.findViewById<EditText>(R.id.stature)!!.background = incompleteEditTextBackground
                formFlag[0] = false
            }
        }else{
            stature = formView!!.findViewById<EditText>(R.id.stature)!!.text.toString().toFloat()
            if(!formFlag[0]){
                formView!!.findViewById<EditText>(R.id.stature)!!.background = editTextBackground
                formFlag[0] = true
            }
        }
        if (TextUtils.isEmpty(formView!!.findViewById<EditText>(R.id.age)!!.text)){
            if (formFlag[1]){
                formView!!.findViewById<EditText>(R.id.age)!!.background = incompleteEditTextBackground
                formFlag[1] = false
            }
        }else{
            age = formView!!.findViewById<EditText>(R.id.age)!!.text.toString().toInt()
            if(!formFlag[1]){
                formView!!.findViewById<EditText>(R.id.age)!!.background = editTextBackground
                formFlag[1] = true
            }
        }
        if (TextUtils.isEmpty(formView!!.findViewById<EditText>(R.id.weight)!!.text)){
            if (formFlag[2]){
                formView!!.findViewById<EditText>(R.id.weight)!!.background = incompleteEditTextBackground
                formFlag[2] = false
            }
        }else{
            weight = formView!!.findViewById<EditText>(R.id.weight)!!.text.toString().toFloat()
            if(!formFlag[2]){
                formView!!.findViewById<EditText>(R.id.weight)!!.background = editTextBackground
                formFlag[2] = true
            }
        }
        if (formFlag[0]&&formFlag[1]&&formFlag[2]){
            if (formView!!.findViewById<Switch>(R.id.sex_switch).isChecked){
                sex = 1
            }
            bmi = if (TextUtils.isEmpty(formView!!.findViewById<EditText>(R.id.bmi)!!.text)){
                getBMI(stature!!,weight!!)
            }else{
                formView!!.findViewById<EditText>(R.id.bmi)!!.text.toString().toFloat()
            }
            fat = if (TextUtils.isEmpty(formView!!.findViewById<EditText>(R.id.fat)!!.text)){
                getFat(age!!,bmi!!,sex)
            }else{
                formView!!.findViewById<EditText>(R.id.fat)!!.text.toString().toFloat()
            }
            val personalDataUpdate=MyDataBaseTool(view!!.context,"FitnessFlowDB",null,1)
            val personalDataUpdateTool=personalDataUpdate.writableDatabase
            personalDataUpdateTool.beginTransaction()
            try{
                val checkCursor = personalDataUpdateTool.rawQuery("Select * From PersonalData Where Date=?",
                    arrayOf(todayString))
                if (checkCursor.count == 0){
                    val insertSql = "INSERT INTO PersonalData (Date,Sex,Age,Stature,Weight,BMI,Fat) VALUES(\"$todayString\",$sex,$age,$stature,$weight,$bmi,$fat)"
                    personalDataUpdateTool.execSQL(insertSql)
                }else{
                    checkCursor.moveToNext()
                    val updateSql = "Update PersonalData Set Sex=$sex,Age=$age,Stature=$stature,Weight=$weight,BMI=$bmi,Fat=$fat Where DataID=${checkCursor.getString(7)}"
                    personalDataUpdateTool.execSQL(updateSql)
                }
                checkCursor.close()
                weightText!!.text = weight.toString()+weightUnit
                fatText!!.text = "${fat}%"
                bmiText!!.text = bmi.toString()
                MyToast(view!!.context,view!!.context.resources.getString(R.string.add_successful)).showToast()
                personalDataUpdateTool.setTransactionSuccessful()
            }catch(e:Exception){
                println("Personal Data Update Failed(In MineFragment):$e")
                MyToast(view!!.context,view!!.context.resources.getString(R.string.add_failed)).showToast()
            }finally{
                personalDataUpdateTool.endTransaction()
                personalDataUpdateTool.close()
                personalDataUpdate.close()
            }
            dialogFragment!!.dismiss()
        }else{
            MyToast(view!!.context,view!!.context.resources.getString(R.string.form_incomplete)).showToast()
            (view!!.context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(400,4))
        }
    }

    private fun floatSave(value:Float):Float{
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(value.toBigDecimal()).toFloat()
    }

    private fun getBMI(height:Float, weight:Float):Float{
        return floatSave(weight/(height*height))
    }

    private fun getFat(age:Int, bmi:Float, sex:Int):Float{
        val fatPercentage = (1.2f*bmi) + 0.23f*(age.toFloat()) -10.8f*(sex.toFloat())
        return floatSave(fatPercentage)
    }

}