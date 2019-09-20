package com.example.zhangjie.fitnessflow.library

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.ActionDetailInTemplate
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import java.lang.Exception

class ActionDetailAdapterInTemplateDetailActivity (private val action:Action,
                                                   private val actionDetailList:ArrayList<ActionDetailInTemplate>,
                                                   private val templateID:Int,
                                                   private val context:AppCompatActivity):
    RecyclerView.Adapter<ActionDetailAdapterInTemplateDetailActivity.RvHolder>(),MyDialogFragment.ConfirmButtonClickListener{

    private val maxSwipeDistance = -(context.resources.getDimension(R.dimen.iconSize)*2 + context.resources.getDimension(
        R.dimen.viewMargin)*5)
    private var lastItemDeleteListener:LastItemDeleteListener? = null
    private var currentPosition = 0
    private var formView:View? = null
    private var formDialog:MyDialogFragment? = null
    private var currentViewHolder:RvHolder? = null

    //viewType中1代表有重量布局，0代表无重量布局
    class RvHolder(view: View,viewType:Int):RecyclerView.ViewHolder(view){
        //相同控件
        val parentLayout = view.findViewById<FrameLayout>(R.id.item_parent_layout)!!
        val upperLayout = view.findViewById<LinearLayout>(R.id.upper_layout)!!
        val itemEditButton = view.findViewById<ImageButton>(R.id.detail_edit)!!
        val itemDeleteButton = view.findViewById<ImageButton>(R.id.del_button)!!
        val targetText = view.findViewById<TextView>(R.id.target)!!
        val animButton = view.findViewById<ImageButton>(R.id.anim_button)!!
        //不同控件
        var weightText:TextView? = null
        var unitText:TextView? = null
        init {
            when(viewType){
                0->{
                    unitText = view.findViewById(R.id.weight_unit)
                }
                1->{
                    weightText = view.findViewById(R.id.weight)
                }
            }
        }
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //根据有无重量单位返回不同布局
        return if (action.IsHadWeightUnits == 0){
            RvHolder(LayoutInflater.from(context).inflate(R.layout.action_detail_view_without_weight,p0,false),0)
        }else{
            RvHolder(LayoutInflater.from(context).inflate(R.layout.action_detail_view,p0,false),1)
        }
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionDetailList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        p0.targetText.text = actionDetailList[p1].num.toString()
        //根据有无重量单位，进行组件设置
        if (action.IsHadWeightUnits == 0){
            p0.unitText!!.text = actionDetailList[p1].unit
        }else{
            p0.weightText!!.text = actionDetailList[p1].weight.toString()
        }
        p0.animButton.setOnClickListener {
            itemSwipeAnimation(p0.upperLayout,p0)
        }
        p0.itemDeleteButton.setOnClickListener {
            itemSwipeAnimation(p0.upperLayout,p0)
            delActionDetail(action.actionID, actionDetailList[p1].ID,p1)
        }
        p0.itemEditButton.setOnClickListener {
            currentPosition = p1
            currentViewHolder = p0
            if (action.IsHadWeightUnits == 1){
                formView = View.inflate(context,R.layout.action_detail_edit_view,null)
                formView!!.findViewById<EditText>(R.id.weight).setText(actionDetailList[p1].weight.toString().toCharArray(),0,actionDetailList[p1].weight.toString().count())
                formView!!.findViewById<EditText>(R.id.target).setText(actionDetailList[p1].num.toString().toCharArray(),0,actionDetailList[p1].num.toString().count())
                formDialog = MyDialogFragment(2, Gravity.CENTER,1,formView!!)
                formDialog!!.setConfirmButtonClickListener(this)
                formDialog!!.show(context.supportFragmentManager,null)
            }else{
                formView = View.inflate(context,R.layout.action_detail_edit_view_without_weight,null)
                formView!!.findViewById<EditText>(R.id.target).setText(actionDetailList[p1].num.toString().toCharArray(),0,actionDetailList[p1].num.toString().count())
                formDialog = MyDialogFragment(2, Gravity.CENTER,1,formView!!)
                formDialog!!.setConfirmButtonClickListener(this)
                formDialog!!.show(context.supportFragmentManager,null)
            }
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    private fun itemSwipeAnimation(upperView: View, p0:RvHolder){
        if (upperView.translationX == 0f){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,maxSwipeDistance)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            val rotateAnimation = ValueAnimator.ofFloat(180f,0f)
            rotateAnimation.addUpdateListener {
                val translationRotation = it.animatedValue as Float
                p0.animButton.rotation = translationRotation
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            rotateAnimation.duration = 300
            rotateAnimation.start()
            p0.itemDeleteButton.isClickable = true
            p0.itemEditButton.isClickable = true
        }else if (upperView.translationX == maxSwipeDistance){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,0f)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            val rotateAnimation = ValueAnimator.ofFloat(0f,180f)
            rotateAnimation.addUpdateListener {
                val translationRotation = it.animatedValue as Float
                p0.animButton.rotation = translationRotation
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            rotateAnimation.duration = 300
            rotateAnimation.start()
            p0.upperLayout.translationX = 0f
            p0.itemDeleteButton.isClickable = false
            p0.itemEditButton.isClickable = false
        }
    }

    fun addActionDetail(){
        //数据库中更新添加次数,插入第一组数据
        val actionDetailAddDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionDetailAddTool=actionDetailAddDatabase.writableDatabase
        actionDetailAddTool.beginTransaction()
        try{
            val insertSql = "Insert Into TemplateDetailTable (ActionID,ActionType,ActionName," +
                    "IsHadWeightUnits,Unit,Weight,Num,TemplateID,TemplateOrder) Values(${action.actionID},${action.actionType}," +
                    "\"${action.actionName}\",${action.IsHadWeightUnits},\"${action.unit}\"" +
                    ",${(action.weightOfIncreaseProgressively + actionDetailList[actionDetailList.size-1].weight)}," +
                    "${(action.numOfIncreaseProgressively + actionDetailList[actionDetailList.size-1].num)}," +
                    "$templateID,${actionDetailList[actionDetailList.size-1].templateOrder})"
            actionDetailAddTool.execSQL(insertSql)
            val idCheckCursor = actionDetailAddTool.rawQuery("select last_insert_rowid()from TemplateDetailTable",null)
            idCheckCursor.moveToNext()
            val newDetailID = idCheckCursor.getString(0).toInt()
            idCheckCursor.close()
            actionDetailList.add(ActionDetailInTemplate(action.actionID,action.actionType,action.actionName,
                action.IsHadWeightUnits,action.unit,
                (action.weightOfIncreaseProgressively + actionDetailList[actionDetailList.size-1].weight),
                (action.numOfIncreaseProgressively + actionDetailList[actionDetailList.size-1].num),
                actionDetailList[actionDetailList.size-1].templateOrder,newDetailID))
            notifyItemInserted(actionDetailList.size -1)
            notifyItemRangeChanged(actionDetailList.size -1,1)
            actionDetailAddTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Detail Add In Action Group Failed(In ActionDetailAdapterInTemplateDetailActivity):$e")
            MyToast(context,context.resources.getString(R.string.add_failed)).showToast()
        }finally{
            actionDetailAddTool.endTransaction()
            actionDetailAddTool.close()
            actionDetailAddDatabase.close()
        }
    }

    private fun delActionDetail(actionID: Int, actionDetailID:Int,position: Int){
        if (actionDetailList.size == 1){
            if (lastItemDeleteListener!=null){
                lastItemDeleteListener!!.onLastItemDelete(actionID)
            }
        }else{
            val actionDetailDeleteDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
            val actionDetailDeleteTool=actionDetailDeleteDatabase.writableDatabase
            actionDetailDeleteTool.beginTransaction()
            try{
                val delSql = "Delete From TemplateDetailTable Where ID=$actionDetailID"
                actionDetailDeleteTool.execSQL(delSql)
                actionDetailList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,actionDetailList.size-position)
                actionDetailDeleteTool.setTransactionSuccessful()
            }catch(e:Exception){
                println("Action Detail Delete In Action Group Failed(In ActionDetailAdapterInTemplateDetailActivity):$e")
                MyToast(context,context.resources.getString(R.string.del_failed)).showToast()
            }finally{
                actionDetailDeleteTool.endTransaction()
                actionDetailDeleteTool.close()
                actionDetailDeleteDatabase.close()
            }
        }
    }

    interface LastItemDeleteListener{
        fun onLastItemDelete(actionID:Int)
    }

    fun setLastItemDeleteListener(lastItemDeleteListener:LastItemDeleteListener){
        this.lastItemDeleteListener = lastItemDeleteListener
    }

    override fun onConfirmButtonClick() {
        when(action.IsHadWeightUnits){
            1->{
                val weightEditText = formView!!.findViewById<EditText>(R.id.weight)
                val numEditText = formView!!.findViewById<EditText>(R.id.target)
                val weightValue = if (TextUtils.isEmpty(weightEditText.text)){0f}else{
                    weightEditText.text.toString().toFloat()
                }
                val numValue = if (TextUtils.isEmpty(numEditText.text)){0}else{
                    numEditText.text.toString().toInt()
                }
                actionDetailList[currentPosition].weight = weightValue
                actionDetailList[currentPosition].num = numValue
                currentViewHolder!!.weightText!!.text = weightValue.toString()
                currentViewHolder!!.targetText.text = numValue.toString()
            }
            0->{
                val numEditText = formView!!.findViewById<EditText>(R.id.target)
                val numValue = if (TextUtils.isEmpty(numEditText.text)){0}else{
                    numEditText.text.toString().toInt()
                }
                actionDetailList[currentPosition].num = numValue
                currentViewHolder!!.targetText.text = numValue.toString()
            }
        }
        formDialog!!.dismiss()
        itemSwipeAnimation(currentViewHolder!!.upperLayout,currentViewHolder!!)
    }

}
