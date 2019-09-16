package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Xml
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.utils_class.MyAlertFragment
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import java.lang.Exception

class MuscleGroupFragmentAdapter (private var actionList:ArrayList<Action>, private val layoutManager:LinearLayoutManagerForItemSwipe,
                                  private val context: AppCompatActivity
): RecyclerView.Adapter<MuscleGroupFragmentAdapter.RvHolder>(),MyDialogFragment.ConfirmButtonClickListener,
    MuscleGroupItemAddFormView.SubmitListener,MyAlertFragment.ConfirmButtonClickListener{

    private val firstItemTopMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()
    private val maxSwipeDistance = -(context.resources.getDimension(R.dimen.iconSize)*2 + context.resources.getDimension(
        R.dimen.viewMargin)*5)
    private var canScrollVerticallyFlag = true
    private val addTimesText = context.resources.getString(R.string.add_times_text)
    private var formView:View? = null
    private var currentItemPosition = 0
    private var currentViewHolder:RvHolder? = null

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View): RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val actionName=view.findViewById<TextView>(R.id.action_name)!!
        val parentLayout = view.findViewById<FrameLayout>(R.id.item_parent_layout)!!
        val upperLayout = view.findViewById<LinearLayout>(R.id.upper_layout)!!
        val editActionButton = view.findViewById<ImageButton>(R.id.edit_button)!!
        val deleteActionButton = view.findViewById<ImageButton>(R.id.delete_button)!!
        val addTimes = view.findViewById<TextView>(R.id.add_times)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.muscle_group_item_in_library,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return actionList.size
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        //第一个和最后一个加top、bottom的margin
        if (p1 == 0){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.topMargin = firstItemTopMargin
            p0.parentLayout.layoutParams = layoutParams
        }
        if (p1 == actionList.size - 1){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
            p0.parentLayout.background = ContextCompat.getDrawable(context,R.drawable.last_item_underline)
        }
        //向viewHolder中的View控件赋值需显示的内容
        p0.actionName.text= actionList[p1].actionName
        p0.addTimes.text = addTimesText + actionList[p1].addTimes.toString()
        var positionX = 0f
        //item侧滑显示按钮
        p0.upperLayout.setOnTouchListener { _, event ->
            try {
                when (event!!.action){
                    MotionEvent.ACTION_DOWN->{
                        positionX = event.rawX
                    }
                    MotionEvent.ACTION_MOVE->{
                        val targetTranslationX = p0.upperLayout.translationX + event.rawX - positionX
                        if (targetTranslationX < 0 && targetTranslationX>maxSwipeDistance){
                            if (canScrollVerticallyFlag){
                                canScrollVerticallyFlag = false
                                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                            }
                            p0.upperLayout.translationX = targetTranslationX
                        }else if (targetTranslationX<=maxSwipeDistance && p0.upperLayout.translationX != maxSwipeDistance){
                            if (!canScrollVerticallyFlag){
                                canScrollVerticallyFlag = true
                                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                            }
                            p0.upperLayout.translationX = maxSwipeDistance
                            p0.editActionButton.isClickable = true
                            p0.deleteActionButton.isClickable = true
                        }else if (targetTranslationX >=0 && p0.upperLayout.translationX != 0f){
                            if (!canScrollVerticallyFlag){
                                canScrollVerticallyFlag = true
                                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                            }
                            p0.upperLayout.translationX = 0f
                            p0.editActionButton.isClickable = false
                            p0.deleteActionButton.isClickable = false
                        }
                        positionX = event.rawX
                    }
                    MotionEvent.ACTION_UP->{
                        itemSwipeAnimation(p0.upperLayout,p0)
                        positionX = 0f
                    }
                }
            }
            catch (exception: Exception){
                println(exception)
                itemSwipeAnimation(p0.upperLayout,p0)
                positionX = 0f
            }
            true
        }
        //按钮点击事件
        p0.deleteActionButton.setOnClickListener {
            currentItemPosition = p1
            val alertView = View.inflate(it.context,R.layout.alert_text_view, null)
            alertView.findViewById<TextView>(R.id.alert_text).text = it.context.resources.getString(R.string.confirm_to_delete)
            val alertFragment = MyAlertFragment(alertView)
            alertFragment.setConfirmButtonClickListener(this)
            alertFragment.show(context.supportFragmentManager, null)
            currentViewHolder = p0
        }
        p0.editActionButton.setOnClickListener {
            currentItemPosition = p1
            val parser = it.context.resources.getXml(R.xml.base_linear_layout)
            val attributes = Xml.asAttributeSet(parser)
            formView = MuscleGroupItemAddFormView(it.context,attributes,actionList[p1].actionType,actionInfo=actionList[p1])
            (formView!! as MuscleGroupItemAddFormView).setSubmitListener(this)
            val formDialog = MyDialogFragment(1, Gravity.CENTER,1,formView!!)
            formDialog.setConfirmButtonClickListener(this)
            formDialog.show(context.supportFragmentManager,null)
            currentViewHolder = p0
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    private fun itemEditEndAnimation(upperView: View, p0:RvHolder){
        val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,0f)
        swipeAnimation.addUpdateListener {
            val translationDistance = it.animatedValue as Float
            upperView.translationX = translationDistance
        }
        swipeAnimation.duration = 300
        swipeAnimation.start()
        p0.upperLayout.translationX = 0f
        p0.editActionButton.isClickable = false
        p0.deleteActionButton.isClickable = false
        if (!canScrollVerticallyFlag){
            canScrollVerticallyFlag = true
            layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
        }
    }

    private fun itemSwipeAnimation(upperView: View, p0:RvHolder){
        if (upperView.translationX <= maxSwipeDistance/2 && upperView.translationX!=maxSwipeDistance){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,maxSwipeDistance)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            p0.editActionButton.isClickable = true
            p0.deleteActionButton.isClickable = true
            if (!canScrollVerticallyFlag){
                canScrollVerticallyFlag = true
                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
            }
        }else if (upperView.translationX > maxSwipeDistance/2 && upperView.translationX!=0f){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,0f)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            p0.upperLayout.translationX = 0f
            p0.editActionButton.isClickable = false
            p0.deleteActionButton.isClickable = false
            if (!canScrollVerticallyFlag){
                canScrollVerticallyFlag = true
                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
            }
        }
    }

    fun addAction(position:Int,action: Action){
        actionList.add(position,action)
        notifyItemInserted(position)
        if (position == 0){
            notifyItemRangeChanged(position,actionList.size-position)
        }else{
            notifyItemRangeChanged(position-1,actionList.size-position+1)
        }
    }

    private fun delAction(position:Int){
        actionList.removeAt(position)
        notifyItemRemoved(position)
        if (position != actionList.size){
            notifyItemRangeChanged(position,actionList.size-position)
        }else{
            notifyItemRangeChanged(position-1,actionList.size-position+1)
        }
    }

    override fun onConfirmButtonClick() {
        (formView!! as MuscleGroupItemAddFormView).onConfirmButtonClick()
    }

    override fun onSubmit(actionType: Int, action: Action) {
        actionList[currentItemPosition] = action
        notifyDataSetChanged()
        itemEditEndAnimation(currentViewHolder!!.upperLayout, currentViewHolder!!)
        currentViewHolder = null
    }

    private fun actionDeleteInDataBase(){
        val actionDeleteDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val actionDeleteDataBaseTool=actionDeleteDatabase.writableDatabase
        actionDeleteDataBaseTool.beginTransaction()
        try{
            val delSql: String = if (actionList[currentItemPosition].addTimes == 0){
                "DELETE FROM ActionTable WHERE ActionID=${actionList[currentItemPosition].actionID}"
            }else{
                "UPDATE ActionTable SET IsShow=0 WHERE ActionID=${actionList[currentItemPosition].actionID}"
            }
            val delSql2 = "DELETE FROM TemplateDetailTable WHERE ActionID=${actionList[currentItemPosition].actionID}"
            actionDeleteDataBaseTool.execSQL(delSql)
            actionDeleteDataBaseTool.execSQL(delSql2)
            itemEditEndAnimation(currentViewHolder!!.upperLayout, currentViewHolder!!)
            currentViewHolder = null
            delAction(currentItemPosition)
            actionDeleteDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Delete Failed(In MuscleGroupFragmentAdapter):$e")
            MyToast(context, context.resources.getString(R.string.del_failed)).showToast()
        }finally{
            actionDeleteDataBaseTool.endTransaction()
            actionDeleteDataBaseTool.close()
            actionDeleteDatabase.close()
        }
    }

    override fun onAlertConfirmButtonClick() {
        actionDeleteInDataBase()
    }

}