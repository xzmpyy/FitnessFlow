package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.animation.ValueAnimator
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
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class MuscleGroupItemAddFormView (context: Context,set:AttributeSet):LinearLayout(context,set){

    constructor(context: Context,set:AttributeSet,actionType:Int):this(context,set){
        this.actionType = actionType
    }

    private var actionType = 1
    private var formView: View? = null
    private val muscleGroupNameList = resources.getStringArray(R.array.muscle_group)
    private var viewPager:ViewPagerForAdditionForm? = null
    private var viewPagerAdapter = AdapterForViewPagerInAdditionForm()
    private var vibrator: Vibrator
    private var initFlag = true
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
    //表单信息是否完整
    //必填信息是否完整，名称，每组单位、重量单位
    private var informationIntegrityFlag = arrayListOf(0,0,0)
    private val unIntegrityList = arrayListOf<EditText>()
    private val editTextBackground = ContextCompat.getDrawable(context,R.drawable.edit_text_background)
    private val incompleteEditTextBackground = ContextCompat.getDrawable(context,R.drawable.incomplete_edit_text_background)


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
                    MyToast(context,formAddSuccessfulText).showToast()
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
                    MyToast(context,formAddSuccessfulText).showToast()
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

}