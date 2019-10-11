package com.example.zhangjie.fitnessflow.mine

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Xml
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.splash.SplashActivity
import com.example.zhangjie.fitnessflow.utils_class.GetPlanCountsList
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
    private var noDataRecord:TextView? = null
    private var trendLayout:LinearLayout? = null
    private val todayString = GetMonthInfo.getTodayString()
    private var formView:View? = null
    private var dialogFragment:MyDialogFragment? = null
    private var weight:Float? = null
    private var fat:Float? = null
    private var bmi:Float? = null
    private var age:Int? = null
    private var stature:Float? = null
    private val recordList = arrayListOf<Int>()
    private var trendView:TrendView? = null
    //0女1男
    private var sex=0
    private var editTextBackground:Drawable? = null
    private var incompleteEditTextBackground:Drawable? = null
    private var weightList = listOf<Float>()
    private var fatList= listOf<Float>()
    private var bmiList= listOf<Float>()
    private var dialogType = 0
    private var selectDrawable:Drawable? = null
    private var unSelectDrawable:Drawable? = null

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
        noDataRecord = view.findViewById(R.id.no_data_record)
        trendLayout = view.findViewById(R.id.trend)
        selectDrawable = ContextCompat.getDrawable(view.context,R.drawable.all_done_green)
        unSelectDrawable = ContextCompat.getDrawable(view.context,R.drawable.all_done_gray)
        if (weightUnit == null){
            weightUnit = view.context.resources.getString(R.string.KG)
        }
        editTextBackground = ContextCompat.getDrawable(view.context,R.drawable.edit_text_background)
        incompleteEditTextBackground = ContextCompat.getDrawable(view.context,R.drawable.incomplete_edit_text_background)
        for (i in 1..30){
            recordList.add(-1)
        }
        dataInit()
        val mapRecyclerView = view.findViewById<RecyclerView>(R.id.map)
        mapRecyclerView.layoutManager = GridLayoutManager(view.context,10)
        mapRecyclerView.adapter = AdapterForMap(recordList,view.context)
        view.findViewById<ImageButton>(R.id.instruction).setOnClickListener {
            formView = View.inflate(view.context,R.layout.instruction_view,null)
            dialogFragment = MyDialogFragment(0,Gravity.CENTER,1,formView!!)
            dialogFragment!!.show(this.childFragmentManager,null)
        }
        view.findViewById<ImageButton>(R.id.data_clear).setOnClickListener {
            dialogType = 1
            formView = View.inflate(view.context,R.layout.data_clear_view,null)
            dialogFragment = MyDialogFragment(2,Gravity.CENTER,1,formView!!)
            dialogFragment!!.setConfirmButtonClickListener(this)
            formView!!.findViewById<ImageButton>(R.id.plan_data).setOnClickListener {
                if (formView!!.findViewById<ImageButton>(R.id.plan_data).tag.toString().toInt() == 0){
                    formView!!.findViewById<ImageButton>(R.id.plan_data).setImageDrawable(selectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.plan_data).tag=1
                }else{
                    formView!!.findViewById<ImageButton>(R.id.plan_data).setImageDrawable(unSelectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.plan_data).tag=0
                }
            }
            formView!!.findViewById<ImageButton>(R.id.action_data).setOnClickListener {
                if (formView!!.findViewById<ImageButton>(R.id.action_data).tag.toString().toInt() == 0){
                    formView!!.findViewById<ImageButton>(R.id.action_data).setImageDrawable(selectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.action_data).tag=1
                }else{
                    formView!!.findViewById<ImageButton>(R.id.action_data).setImageDrawable(unSelectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.action_data).tag=0
                }
            }
            formView!!.findViewById<ImageButton>(R.id.personal_data).setOnClickListener {
                if (formView!!.findViewById<ImageButton>(R.id.personal_data).tag.toString().toInt() == 0){
                    formView!!.findViewById<ImageButton>(R.id.personal_data).setImageDrawable(selectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.personal_data).tag=1
                }else{
                    formView!!.findViewById<ImageButton>(R.id.personal_data).setImageDrawable(unSelectDrawable)
                    formView!!.findViewById<ImageButton>(R.id.personal_data).tag=0
                }
            }
            dialogFragment!!.show(this.childFragmentManager,null)
        }
        view.findViewById<ImageButton>(R.id.recommendation_feedback).setOnClickListener {
            formView = View.inflate(view.context,R.layout.recommendation_feedback_view,null)
            dialogFragment = MyDialogFragment(0,Gravity.CENTER,1,formView!!)
            dialogFragment!!.show(this.childFragmentManager,null)
        }
        view.findViewById<ImageButton>(R.id.about).setOnClickListener {
            formView = View.inflate(view.context,R.layout.about_view,null)
            dialogFragment = MyDialogFragment(0,Gravity.CENTER,1,formView!!)
            dialogFragment!!.show(this.childFragmentManager,null)
        }
    }

    fun personalDataEdit(){
        dialogType = 0
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
            val cursor=myDataBaseTool.rawQuery("Select * From PersonalData Order By DataID Desc Limit 8 ",null)
            val weightArrayList = arrayListOf<Float>()
            val fatArrayList = arrayListOf<Float>()
            val bmiArrayList = arrayListOf<Float>()
            while(cursor.moveToNext()){
                if (stature == null){
                    weightText!!.text = "${cursor.getString(4)}$weightUnit"
                    fatText!!.text = "${cursor.getString(6)}%"
                    bmiText!!.text = cursor.getString(5).toString()
                    weight = cursor.getString(4).toFloat()
                    fat = cursor.getString(6).toFloat()
                    bmi = cursor.getString(5).toFloat()
                    age =cursor.getString(2).toInt()
                    stature = cursor.getString(3).toFloat()
                    sex = cursor.getString(1).toInt()
                }
                weightArrayList.add(cursor.getString(4).toFloat())
                fatArrayList.add(cursor.getString(6).toFloat())
                bmiArrayList.add(cursor.getString(5).toFloat())
            }
            weightList = weightArrayList.reversed()
            fatList = fatArrayList.reversed()
            bmiList = bmiArrayList.reversed()
            //趋势图
            if (weightList.isNotEmpty()){
                noDataRecord!!.visibility = LinearLayout.GONE
                val parser = resources.getXml(R.xml.trend_view)
                val attributesForTrend = Xml.asAttributeSet(parser)
                trendView = TrendView(view!!.context,attributesForTrend,weightList,fatList,bmiList)
                trendLayout!!.addView(trendView)
            }
            cursor.close()
            val daysCursor = myDataBaseTool.rawQuery("Select * From PlanRecord",null)
            if (daysCursor.count > 0){
                daysCount!!.text = daysCount!!.text.toString().replace("count",daysCursor.count.toString())
            }else{
                daysCount!!.text = daysCount!!.text.toString().replace("count","0")
            }
            daysCursor.close()
            val dayList = GetPlanCountsList.getDayList()
            for (dayIndex in 0..29){
                val recordCheckCursor = myDataBaseTool.rawQuery("Select * From PlanRecord Where Date=?",
                    arrayOf(dayList[dayIndex]))
                if (recordCheckCursor.count>0){
                    recordCheckCursor.moveToNext()
                    recordList[dayIndex] = recordCheckCursor.getString(0).toInt()
                }
                recordCheckCursor.close()
            }
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
        when(dialogType){
            0->{
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
                        //更新趋势图
                        val parser = resources.getXml(R.xml.trend_view)
                        val attributesForTrend = Xml.asAttributeSet(parser)
                        if (weightList.isEmpty()){
                            weightList = listOf(weight!!)
                            fatList = listOf(fat!!)
                            bmiList = listOf(bmi!!)
                            noDataRecord!!.visibility = LinearLayout.GONE
                            trendView = TrendView(view!!.context,attributesForTrend,weightList,fatList,bmiList)
                            trendLayout!!.addView(trendView)
                        }else{
                            val trendUpdateCursor=personalDataUpdateTool.rawQuery("Select * From PersonalData Order By DataID Desc Limit 8 ",null)
                            val weightArrayList = arrayListOf<Float>()
                            val fatArrayList = arrayListOf<Float>()
                            val bmiArrayList = arrayListOf<Float>()
                            while(trendUpdateCursor.moveToNext()){
                                weightArrayList.add(trendUpdateCursor.getString(4).toFloat())
                                fatArrayList.add(trendUpdateCursor.getString(6).toFloat())
                                bmiArrayList.add(trendUpdateCursor.getString(5).toFloat())
                            }
                            weightList = weightArrayList.reversed()
                            fatList = fatArrayList.reversed()
                            bmiList = bmiArrayList.reversed()
                            trendUpdateCursor.close()
                            trendLayout!!.removeAllViews()
                            trendView = TrendView(view!!.context,attributesForTrend,weightList,fatList,bmiList)
                            trendLayout!!.addView(trendView)
                        }
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
            1->{
                val dataClear=MyDataBaseTool(view!!.context,"FitnessFlowDB",null,1)
                val dataClearTool=dataClear.writableDatabase
                dataClearTool.beginTransaction()
                try {
                    val planTag = formView!!.findViewById<ImageButton>(R.id.plan_data).tag.toString().toInt()
                    val actionTag = formView!!.findViewById<ImageButton>(R.id.action_data).tag.toString().toInt()
                    val personalTag = formView!!.findViewById<ImageButton>(R.id.personal_data).tag.toString().toInt()
                    if ((planTag+personalTag+actionTag)>0){
                        if (planTag==1){
                            val deleteSql = "Delete From PlanDetailTable"
                            val deleteSql2 = "Delete From PlanRecord"
                            dataClearTool.execSQL(deleteSql)
                            dataClearTool.execSQL(deleteSql2)
                        }
                        if (actionTag==1){
                            val deleteSql = "Delete From ActionTable"
                            val deleteSql2 = "Delete From TemplateTable"
                            val deleteSql3 = "Delete From TemplateDetailTable"
                            dataClearTool.execSQL(deleteSql)
                            dataClearTool.execSQL(deleteSql2)
                            dataClearTool.execSQL(deleteSql3)
                        }
                        if (personalTag==1){
                            val deleteSql = "Delete From PersonalData"
                            dataClearTool.execSQL(deleteSql)
                        }
                    }
                    dataClearTool.setTransactionSuccessful()
                    if ((planTag+personalTag+actionTag)>0){
                        //重启应用
                        val reStartIntent = Intent(this@MineFragment.context, SplashActivity().javaClass)
                        startActivity(reStartIntent)
                    }
                }catch (e:Exception){
                    println("Data Clear Failed(In MineFragment):$e")
                    MyToast(view!!.context,view!!.context.resources.getString(R.string.del_failed)).showToast()
                }finally {
                    dataClearTool.endTransaction()
                    dataClearTool.close()
                    dataClear.close()
                }
                dialogFragment!!.dismiss()
            }
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