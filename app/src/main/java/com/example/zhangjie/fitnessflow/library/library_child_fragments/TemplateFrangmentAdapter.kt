package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.fit_calendar.SelectedItemClass
import com.example.zhangjie.fitnessflow.library.TemplateDetailActivity
import com.example.zhangjie.fitnessflow.utils_class.*
import java.lang.Exception

class TemplateFragmentAdapter (private val templateList:ArrayList<Template>, private val layoutManager:LinearLayoutManagerForItemSwipe,
                               private val context: AppCompatActivity
):
    RecyclerView.Adapter<TemplateFragmentAdapter.RvHolder>(), MyAlertFragment.ConfirmButtonClickListener,
    CalendarDialog.DateSelectedListener{

    private val firstItemTopMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()
    private val maxSwipeDistance = -(context.resources.getDimension(R.dimen.iconSize)*3 + context.resources.getDimension(R.dimen.viewMargin)*7)
    private var canScrollVerticallyFlag = true
    private val actionNum = context.resources.getString(R.string.action_num)
    private var currentItemPosition = 0
    private var currentViewHolder:RvHolder? = null
    private var singlePickDate:String? = null
    private var processDialogFragment: ProcessDialogFragment? = null
    private var templateCopyFlag = true
    private var planOrderCorrectFlag = true
    private val targetDaysList = arrayListOf<String>()

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View): RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val templateName=view.findViewById<TextView>(R.id.template_name)!!
        val parentLayout = view.findViewById<FrameLayout>(R.id.item_parent_layout)!!
        val upperLayout = view.findViewById<LinearLayout>(R.id.upper_layout)!!
        val includeNum = view.findViewById<TextView>(R.id.include_num)!!
        val includeRv = view.findViewById<RecyclerViewCanNotTouch>(R.id.muscle_group_include_rv)!!
        val sendTemplateButton = view.findViewById<ImageButton>(R.id.date_button)!!
        val editTemplateButton = view.findViewById<ImageButton>(R.id.edit_button)!!
        val deleteTemplateButton = view.findViewById<ImageButton>(R.id.delete_button)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.template_item_in_library,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return templateList.size
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        //第一个和最后一个加top、bottom的margin
        if (p1 == 0){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.topMargin = firstItemTopMargin
            p0.parentLayout.layoutParams = layoutParams
        }
        if (p1 == templateList.size - 1){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
            p0.parentLayout.background = ContextCompat.getDrawable(context,R.drawable.last_item_underline)
        }
        processDialogFragment = ProcessDialogFragment(context.resources.getString(R.string.save_process))
        //向viewHolder中的View控件赋值需显示的内容
        p0.templateName.text = templateList[p1].templateName
        p0.includeNum.text = actionNum + templateList[p1].actionNum.toString()
        val muscleGroupAdapter = AdapterForShowIncludeMuscleGroup(templateList[p1].muscleGroupInclude,context)
        val gridLayoutManager = GridLayoutManager(context,8)
        p0.includeRv.layoutManager = gridLayoutManager
        p0.includeRv.adapter = muscleGroupAdapter
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
                            p0.sendTemplateButton.isClickable = true
                            p0.editTemplateButton.isClickable = true
                            p0.deleteTemplateButton.isClickable = true
                        }else if (targetTranslationX >=0 && p0.upperLayout.translationX != 0f){
                            if (!canScrollVerticallyFlag){
                                canScrollVerticallyFlag = true
                                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                            }
                            p0.upperLayout.translationX = 0f
                            p0.sendTemplateButton.isClickable = false
                            p0.editTemplateButton.isClickable = false
                            p0.deleteTemplateButton.isClickable = false
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
        p0.deleteTemplateButton.setOnClickListener {
            currentItemPosition = p1
            val alertView = View.inflate(it.context,R.layout.alert_text_view, null)
            alertView.findViewById<TextView>(R.id.alert_text).text = it.context.resources.getString(R.string.confirm_to_delete)
            alertView.findViewById<TextView>(R.id.delete_item).text = templateList[p1].templateName
            val alertFragment = MyAlertFragment(alertView)
            alertFragment.setConfirmButtonClickListener(this)
            alertFragment.show(context.supportFragmentManager, null)
            currentViewHolder = p0
        }
        p0.editTemplateButton.setOnClickListener {
            currentItemPosition = p1
            TemplateModifyClass.setPosition(p1)
            TemplateModifyClass.setTemplate(templateList[p1])
            itemEditEndAnimation(p0.upperLayout,p0)
            val intent = Intent(this.context, TemplateDetailActivity::class.java)
            this.context.startActivity(intent)
        }
        p0.sendTemplateButton.setOnClickListener {
            currentItemPosition = p1
            singlePickDate = SelectedItemClass.getSelectedList()[0]
            SelectedItemClass.clear()
            val calendarDialog = CalendarDialog()
            calendarDialog.setDateSelectedListener(this)
            calendarDialog.show(context.supportFragmentManager,null)
            currentViewHolder = p0
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    private fun itemEditEndAnimation(upperView: View, p0: RvHolder){
        val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,0f)
        swipeAnimation.addUpdateListener {
            val translationDistance = it.animatedValue as Float
            upperView.translationX = translationDistance
        }
        swipeAnimation.duration = 300
        swipeAnimation.start()
        p0.upperLayout.translationX = 0f
        p0.editTemplateButton.isClickable = false
        p0.deleteTemplateButton.isClickable = false
        if (!canScrollVerticallyFlag){
            canScrollVerticallyFlag = true
            layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
        }
    }

    private fun itemSwipeAnimation(upperView:View,p0:RvHolder){
        if (upperView.translationX <= maxSwipeDistance/2 && upperView.translationX!=maxSwipeDistance){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,maxSwipeDistance)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            p0.sendTemplateButton.isClickable = true
            p0.editTemplateButton.isClickable = true
            p0.deleteTemplateButton.isClickable = true
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
            p0.sendTemplateButton.isClickable = false
            p0.editTemplateButton.isClickable = false
            p0.deleteTemplateButton.isClickable = false
            if (!canScrollVerticallyFlag){
                canScrollVerticallyFlag = true
                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
            }
        }
    }

    fun addTemplate(position: Int, template:Template){
        templateList.add(position,template)
        notifyItemInserted(position)
        if (position == 0){
            notifyItemRangeChanged(position,templateList.size-position)
        }else{
            notifyItemRangeChanged(position-1,templateList.size-position+1)
        }
    }

    fun updateTemplate(position: Int,template:Template){
        if (position == -1){
            templateList[templateList.size - 1] = template
        }else{
            templateList[position] = template
        }
        notifyDataSetChanged()
        TemplateModifyClass.clear()
    }

    private fun delTemplate(position:Int){
        templateList.removeAt(position)
        notifyItemRemoved(position)
        if (position != templateList.size){
            notifyItemRangeChanged(position,templateList.size-position)
        }else{
            notifyItemRangeChanged(position-1,templateList.size-position+1)
        }
    }

    private fun templateDeleteInDataBase(){
        val templateDeleteDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val templateDeleteDataBaseTool=templateDeleteDatabase.writableDatabase
        templateDeleteDataBaseTool.beginTransaction()
        try{
            val delSql1 = "DELETE FROM TemplateTable WHERE TemplateID=${templateList[currentItemPosition].templateID}"
            val delSql2 = "DELETE FROM TemplateDetailTable WHERE TemplateID=${templateList[currentItemPosition].templateID}"
            templateDeleteDataBaseTool.execSQL(delSql1)
            templateDeleteDataBaseTool.execSQL(delSql2)
            itemEditEndAnimation(currentViewHolder!!.upperLayout, currentViewHolder!!)
            currentViewHolder = null
            delTemplate(currentItemPosition)
            templateDeleteDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Delete Failed(In TemplateFragmentAdapter):$e")
            MyToast(context, context.resources.getString(R.string.del_failed)).showToast()
        }finally{
            templateDeleteDataBaseTool.endTransaction()
            templateDeleteDataBaseTool.close()
            templateDeleteDatabase.close()
        }
    }

    override fun onAlertConfirmButtonClick() {
        templateDeleteInDataBase()
    }

    //模板复制到计划中
    private fun copyToTargetDays(templateID:Int){
        val sendDatabase=MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val sendTool=sendDatabase.writableDatabase
        sendTool.beginTransaction()
        try{
            val templateDetailCursor=sendTool.rawQuery("Select * From TemplateDetailTable where TemplateID=?",arrayOf(templateID.toString()))
            while(templateDetailCursor.moveToNext()){
                //向每日中插入数据
                for (day in targetDaysList){
                    val insertSql = "Insert Into PlanDetailTable (ActionID,ActionType,ActionName," +
                    "IsHadWeightUnits,Unit,Weight,Num,Done,PlanOrder,Date) Values(${templateDetailCursor.getString(0)}," +
                            "${templateDetailCursor.getString(1)},\"${templateDetailCursor.getString(2)}\"," +
                            "${templateDetailCursor.getString(3)},\"${templateDetailCursor.getString(4)}\"," +
                            "${templateDetailCursor.getString(5)},${templateDetailCursor.getString(6)}," +
                            "0,0,\"$day\")"
                    sendTool.execSQL(insertSql)
                }
            }
            templateDetailCursor.close()
            if (!templateCopyFlag){
                templateCopyFlag = true
            }
            sendTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Copy To Target Days Failed(In TemplateFragmentAdapter):$e")
            if (templateCopyFlag){
                templateCopyFlag = false
            }
        }finally{
            sendTool.endTransaction()
            sendTool.close()
            sendDatabase.close()
        }
        planOrderCorrectFlag = ActionOrderInPlanDetailCorrect.correct(context,targetDaysList)
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
        println(targetDaysList)
        processDialogFragment!!.show(context.supportFragmentManager, null)
        val dataSaving = DataSaving()
        dataSaving.execute()
        itemEditEndAnimation(currentViewHolder!!.upperLayout,currentViewHolder!!)
    }

    //后台保存任务
    @SuppressLint("StaticFieldLeak")
    inner class DataSaving: AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            copyToTargetDays(templateList[currentItemPosition].templateID)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (templateCopyFlag && planOrderCorrectFlag){
                processDialogFragment!!.dismiss()
            }else{
                MyToast(context,context.resources.getString(R.string.add_failed)).showToast()
                processDialogFragment!!.dismiss()
            }
        }

    }

}