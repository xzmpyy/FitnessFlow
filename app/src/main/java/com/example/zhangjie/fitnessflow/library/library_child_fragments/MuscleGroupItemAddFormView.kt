package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.app.Service
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class MuscleGroupItemAddFormView (context: Context,set:AttributeSet):LinearLayout(context,set){

    constructor(context: Context,set:AttributeSet,actionType:Int,actionInfo:Action?):this(context,set){
        this.actionType = actionType
        this.actionInfo = actionInfo
    }

    private var actionType = 1
    private var formView: View? = null
    private val muscleGroupNameList = resources.getStringArray(R.array.muscle_group)
    private var viewPager:ViewPagerForAdditionForm? = null
    private var viewPagerAdapter = AdapterForViewPagerInAdditionForm()
    private var vibrator: Vibrator
    private var initFlag = true
    private var actionInfo:Action? = null
    private var actionID = 0
    //表单相关
    private var actionName:EditText? = null
    private var formTypeSwitch:Switch? = null
    private var weightUnit:EditText? = null
    private var initWeight:EditText? = null
    private var initNum:EditText? = null
    private var weightIncrease:EditText? = null
    private var numIncrease:EditText? = null
    private var otherUnit:EditText? = null
    private var initNumWithoutWeight:EditText? = null
    private var numIncreaseWithoutWeight:EditText? = null
    private val formIncompleteText = context.resources.getString(R.string.form_incomplete)
    private val formAddSuccessfulText = context.resources.getString(R.string.add_successful)
    private val formAddFailedText = context.resources.getString(R.string.add_failed)
    private val formModifySuccessfulText = context.resources.getString(R.string.modify_successful)
    private val formModifyFailedText = context.resources.getString(R.string.modify_failed)
    private var addFlag = true
    //表单信息是否完整
    //必填信息是否完整，名称，每组单位、重量单位
    private var informationIntegrityFlag = arrayListOf(0,0,0)
    private val unIntegrityList = arrayListOf<EditText>()
    private val editTextBackground = ContextCompat.getDrawable(context,R.drawable.edit_text_background)
    private val incompleteEditTextBackground = ContextCompat.getDrawable(context,R.drawable.incomplete_edit_text_background)
    //接口
    private var submitListener:SubmitListener? = null
    private var dialogDismissListener:DialogDismissListener? = null

    init {
        formView = LayoutInflater.from(context).inflate(R.layout.action_addition_form, this,false)
        actionName = formView!!.findViewById(R.id.action_name)
        viewPager = formView!!.findViewById(R.id.form_type_page)
        viewPager!!.adapter = viewPagerAdapter
        viewPager!!.currentItem = 1
        formTypeSwitch = formView!!.findViewById(R.id.form_type_switch)
        formTypeSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                viewPager!!.currentItem = 1
            }else{
                viewPager!!.currentItem = 0
            }
        }
        this.addView(formView)
        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (initFlag){
            formView!!.findViewById<TextView>(R.id.muscle_group).text = muscleGroupNameList[actionType]
            weightUnit = viewPager!!.getChildAt(1).findViewById(R.id.weight_unit)
            initWeight = viewPager!!.getChildAt(1).findViewById(R.id.init_weight)
            initNum = viewPager!!.getChildAt(1).findViewById(R.id.init_unm)
            weightIncrease = viewPager!!.getChildAt(1).findViewById(R.id.weight_increase)
            numIncrease = viewPager!!.getChildAt(1).findViewById(R.id.num_increase)
            otherUnit = viewPager!!.findViewById(R.id.other_unit)
            initNumWithoutWeight = viewPager!!.findViewById(R.id.init_unm_without_weight)
            numIncreaseWithoutWeight = viewPager!!.findViewById(R.id.num_increase_without_weight)
            initFlag = false
            if (actionInfo!=null){
                actionName!!.setText(actionInfo!!.actionName.toCharArray(),0,actionInfo!!.actionName.count())
                viewPager!!.currentItem = actionInfo!!.IsHadWeightUnits
                formTypeSwitch!!.isChecked = actionInfo!!.IsHadWeightUnits != 0
                when(actionInfo!!.IsHadWeightUnits){
                    //无重量单位
                    0->{
                        otherUnit!!.setText(actionInfo!!.unit.toCharArray(),0,actionInfo!!.unit.count())
                        initNumWithoutWeight!!.setText(actionInfo!!.initNum.toString().toCharArray(),0,actionInfo!!.initNum.toString().count())
                        numIncreaseWithoutWeight!!.setText(actionInfo!!.numOfIncreaseProgressively.toString().toCharArray(),0,actionInfo!!.numOfIncreaseProgressively.toString().count())
                    }
                    //有重量单位
                    else->{
                        weightUnit!!.setText(actionInfo!!.unit.toCharArray(),0,actionInfo!!.unit.count())
                        initWeight!!.setText(actionInfo!!.initWeight.toString().toCharArray(),0,actionInfo!!.initWeight.toString().count())
                        initNum!!.setText(actionInfo!!.initNum.toString().toCharArray(),0,actionInfo!!.initNum.toString().count())
                        weightIncrease!!.setText(actionInfo!!.weightOfIncreaseProgressively.toString().toCharArray(),0,actionInfo!!.weightOfIncreaseProgressively.toString().count())
                        numIncrease!!.setText(actionInfo!!.numOfIncreaseProgressively.toString().toCharArray(),0,actionInfo!!.numOfIncreaseProgressively.toString().count())
                    }
                }
            }
        }
        super.onWindowFocusChanged(hasWindowFocus)
    }

    fun onConfirmButtonClick(){
        if (TextUtils.isEmpty(actionName!!.text)){
            if (!unIntegrityList.contains(actionName!!)){
                unIntegrityList.add(actionName!!)
            }
            informationIntegrityFlag[0] = 0
        }else{
            informationIntegrityFlag[0] = 1
            if (unIntegrityList.contains(actionName!!)){
                unIntegrityList.remove(actionName!!)
                actionName!!.background = editTextBackground
            }
        }
        when(viewPager!!.currentItem){
            0->{
                if (TextUtils.isEmpty(otherUnit!!.text)){
                    if (!unIntegrityList.contains(otherUnit!!)){
                        unIntegrityList.add(otherUnit!!)
                    }
                    informationIntegrityFlag[1] = 0
                }else{
                    informationIntegrityFlag[1] = 1
                    if (unIntegrityList.contains(otherUnit!!)){
                        unIntegrityList.remove(otherUnit!!)
                        otherUnit!!.background = editTextBackground
                    }
                }
                if (informationIntegrityFlag[0] == 1 && informationIntegrityFlag[1] == 1){
                    if (actionInfo == null){
                        //数据库添加数据
                        insertIntoDataBase()
                        if (addFlag){
                            val newAction = generateActionClass()
                            if (submitListener!=null){
                                submitListener!!.onSubmit(actionType,newAction)
                            }
                            if (dialogDismissListener!=null){
                                dialogDismissListener!!.dialogDismiss()
                            }
                            MyToast(context,formAddSuccessfulText).showToast()
                        }else{
                            MyToast(context,formAddFailedText).showToast()
                        }
                    }else{
                        updateActionInfo()
                        if (addFlag){
                            val newAction = generateActionClass()
                            if (submitListener!=null){
                                submitListener!!.onSubmit(actionType,newAction)
                            }
                            if (dialogDismissListener!=null){
                                dialogDismissListener!!.dialogDismiss()
                            }
                            MyToast(context,formModifySuccessfulText).showToast()
                        }else{
                            MyToast(context,formModifyFailedText).showToast()
                        }
                    }
                }else{
                    for (unIntegrity in unIntegrityList){
                        unIntegrity.background = incompleteEditTextBackground
                    }
                    MyToast(context,formIncompleteText).showToast()
                    vibrator.vibrate(VibrationEffect.createOneShot(400,4))
                }
            }
            1->{
                if (TextUtils.isEmpty(weightUnit!!.text)){
                    if (!unIntegrityList.contains(weightUnit!!)){
                        unIntegrityList.add(weightUnit!!)
                    }
                    informationIntegrityFlag[2] = 0
                }else{
                    informationIntegrityFlag[2] = 1
                    if (unIntegrityList.contains(weightUnit!!)){
                        unIntegrityList.remove(weightUnit!!)
                        weightUnit!!.background = editTextBackground
                    }
                }
                if (informationIntegrityFlag[0] == 1 && informationIntegrityFlag[2] == 1){
                    if (actionInfo == null){
                        //数据库添加数据
                        insertIntoDataBase()
                        if (addFlag){
                            val newAction = generateActionClass()
                            if (submitListener!=null){
                                submitListener!!.onSubmit(actionType,newAction)
                            }
                            if (dialogDismissListener!=null){
                                dialogDismissListener!!.dialogDismiss()
                            }
                            MyToast(context,formAddSuccessfulText).showToast()
                        }else{
                            MyToast(context,formAddFailedText).showToast()
                        }
                    }else{
                        updateActionInfo()
                        if (addFlag){
                            val newAction = generateActionClass()
                            if (submitListener!=null){
                                submitListener!!.onSubmit(actionType,newAction)
                            }
                            if (dialogDismissListener!=null){
                                dialogDismissListener!!.dialogDismiss()
                            }
                            MyToast(context,formModifySuccessfulText).showToast()
                        }else{
                            MyToast(context,formModifyFailedText).showToast()
                        }
                    }
                }else{
                    for (unIntegrity in unIntegrityList){
                        unIntegrity.background = incompleteEditTextBackground
                    }
                    MyToast(context,formIncompleteText).showToast()
                    vibrator.vibrate(VibrationEffect.createOneShot(400,4))
                }
            }
        }

    }

    private fun generateActionClass():Action{
        return if (viewPager!!.currentItem == 0){
            val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncreaseWithoutWeight!!.text)){
                0
            }else{
                numIncreaseWithoutWeight!!.text.toString().toInt()
            }
            val initNumForAdd = if (TextUtils.isEmpty(initNumWithoutWeight!!.text)){
                0
            }else{
                initNumWithoutWeight!!.text.toString().toInt()
            }
            Action(actionType,actionID,actionName!!.text.toString(),0,0,otherUnit!!.text.toString(),
                0f,initNumForAdd,0f,numOfIncreaseProgressively,1)
        }else{
            val initWeightForAdd = if (TextUtils.isEmpty(initWeight!!.text)){
                0f
            }else{
                initWeight!!.text.toString().toFloat()
            }
            val initNumForAdd = if (TextUtils.isEmpty(initNum!!.text)){
                0
            }else{
                initNum!!.text.toString().toInt()
            }
            val weightOfIncreaseProgressively = if (TextUtils.isEmpty(weightIncrease!!.text)){
                0f
            }else{
                weightIncrease!!.text.toString().toFloat()
            }
            val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncrease!!.text)){
                0
            }else{
                numIncrease!!.text.toString().toInt()
            }
            Action(actionType,actionID,actionName!!.text.toString(),1,0,weightUnit!!.text.toString(),
                initWeightForAdd,initNumForAdd,weightOfIncreaseProgressively,numOfIncreaseProgressively,1)
        }
    }

    private fun insertIntoDataBase(){
        val actionInsertDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionInsertDataBaseTool=actionInsertDatabase.writableDatabase
        actionInsertDataBaseTool.beginTransaction()
        try{
            val insertSql: String
            when(viewPager!!.currentItem){
                0->{
                    val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncreaseWithoutWeight!!.text)){
                        0
                    }else{
                        numIncreaseWithoutWeight!!.text.toString().toInt()
                    }
                    val initNumForAdd = if (TextUtils.isEmpty(initNumWithoutWeight!!.text)){
                        0
                    }else{
                        initNumWithoutWeight!!.text.toString().toInt()
                    }
                    insertSql = "INSERT INTO ActionTable (ActionType,ActionName,IsHadWeightUnits,AddTimes,Unit,InitWeight," +
                            "InitNum,WeightOfIncreaseProgressively,NumOfIncreaseProgressively,IsShow) VALUES ($actionType,\"${actionName!!.text}\"," +
                            "0,0,\"${otherUnit!!.text}\",0,$initNumForAdd,0,$numOfIncreaseProgressively,1)"
                }
                else->{
                    val initWeightForAdd = if (TextUtils.isEmpty(initWeight!!.text)){
                        0f
                    }else{
                        initWeight!!.text.toString().toFloat()
                    }
                    val initNumForAdd = if (TextUtils.isEmpty(initNum!!.text)){
                        0
                    }else{
                        initNum!!.text.toString().toInt()
                    }
                    val weightOfIncreaseProgressively = if (TextUtils.isEmpty(weightIncrease!!.text)){
                        0f
                    }else{
                        weightIncrease!!.text.toString().toFloat()
                    }
                    val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncrease!!.text)){
                        0
                    }else{
                        numIncrease!!.text.toString().toInt()
                    }
                    insertSql = "INSERT INTO ActionTable (ActionType,ActionName,IsHadWeightUnits,AddTimes,Unit,InitWeight," +
                            "InitNum,WeightOfIncreaseProgressively,NumOfIncreaseProgressively,IsShow) VALUES ($actionType,\"${actionName!!.text}\"," +
                            "1,0,\"${weightUnit!!.text}\",$initWeightForAdd,$initNumForAdd,$weightOfIncreaseProgressively,$numOfIncreaseProgressively,1)"
                }
            }
            actionInsertDataBaseTool.execSQL(insertSql)
            val idCheckCursor = actionInsertDataBaseTool.rawQuery("select last_insert_rowid() from ActionTable",null)
            idCheckCursor.moveToNext()
            actionID = idCheckCursor.getString(0).toInt()
            idCheckCursor.close()
            actionInsertDataBaseTool.setTransactionSuccessful()
            if (!addFlag){
                addFlag = true
            }
        }catch(e:Exception){
            addFlag = false
            println("Action Insert Failed(In MuscleGroupItemAddFormView):$e")
        }finally{
            actionInsertDataBaseTool.endTransaction()
            actionInsertDataBaseTool.close()
            actionInsertDatabase.close()
        }
    }

    private fun updateActionInfo(){
        val actionUpdateDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionUpdateDataBaseTool=actionUpdateDatabase.writableDatabase
        actionUpdateDataBaseTool.beginTransaction()
        try{
            val updateSql: String
            when(viewPager!!.currentItem){
                0->{
                    val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncreaseWithoutWeight!!.text)){
                        0
                    }else{
                        numIncreaseWithoutWeight!!.text.toString().toInt()
                    }
                    val initNumForAdd = if (TextUtils.isEmpty(initNumWithoutWeight!!.text)){
                        0
                    }else{
                        initNumWithoutWeight!!.text.toString().toInt()
                    }
                    updateSql = "UPDATE ActionTable SET ActionName=\"${actionName!!.text}\",IsHadWeightUnits=0,Unit=\"${otherUnit!!.text}\"" +
                            ",InitWeight=0,InitNum=$initNumForAdd,WeightOfIncreaseProgressively=0,NumOfIncreaseProgressively=$numOfIncreaseProgressively" +
                            " WHERE ActionID=${actionInfo!!.actionID}"
                }
                else->{
                    val initWeightForAdd = if (TextUtils.isEmpty(initWeight!!.text)){
                        0f
                    }else{
                        initWeight!!.text.toString().toFloat()
                    }
                    val initNumForAdd = if (TextUtils.isEmpty(initNum!!.text)){
                        0
                    }else{
                        initNum!!.text.toString().toInt()
                    }
                    val weightOfIncreaseProgressively = if (TextUtils.isEmpty(weightIncrease!!.text)){
                        0f
                    }else{
                        weightIncrease!!.text.toString().toFloat()
                    }
                    val numOfIncreaseProgressively = if (TextUtils.isEmpty(numIncrease!!.text)){
                        0
                    }else{
                        numIncrease!!.text.toString().toInt()
                    }
                    updateSql = "UPDATE ActionTable SET ActionName=\"${actionName!!.text}\",IsHadWeightUnits=1,Unit=\"${weightUnit!!.text}\"," +
                            "InitWeight=$initWeightForAdd,InitNum=$initNumForAdd,WeightOfIncreaseProgressively=$weightOfIncreaseProgressively," +
                            "NumOfIncreaseProgressively=$numOfIncreaseProgressively WHERE ActionID=${actionInfo!!.actionID}"
                }
            }
            actionUpdateDataBaseTool.execSQL(updateSql)
            actionID = actionInfo!!.actionID
            actionUpdateDataBaseTool.setTransactionSuccessful()
            if (!addFlag){
                addFlag = true
            }
        }catch(e:Exception){
            addFlag = false
            println("Action Update Failed(In MuscleGroupItemAddFormView):$e")
        }finally{
            actionUpdateDataBaseTool.endTransaction()
            actionUpdateDataBaseTool.close()
            actionUpdateDatabase.close()
        }
    }

    //数据提交监听
    interface SubmitListener{
        fun onSubmit(actionType:Int,action:Action)
    }

    fun setSubmitListener(submitListener: SubmitListener){
        this.submitListener = submitListener
    }

    //页面关闭监听
    interface DialogDismissListener{
        fun dialogDismiss()
    }

    fun setDialogDismissListener(dialogDismissListener: DialogDismissListener){
        this.dialogDismissListener = dialogDismissListener
    }

}