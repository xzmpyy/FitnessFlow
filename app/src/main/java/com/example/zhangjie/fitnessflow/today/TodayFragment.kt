package com.example.zhangjie.fitnessflow.today

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.*
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.library.LibraryUpdateClass
import com.example.zhangjie.fitnessflow.plan.PlanDetailActivity
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import com.example.zhangjie.fitnessflow.utils_class.ProcessDialogFragment
import java.lang.Exception

class TodayFragment : Fragment(){

    private val toDayString = GetMonthInfo.getTodayString()
    private var noPlanPrompt: LinearLayout? = null
    private var addPlanButton:ImageButton?  = null
    private var actionDetailRecyclerView:RecyclerView? = null
    private var isHadPlanTodayFlag = false
    private val actionList = arrayListOf<Action>()
    private val actionDetailMap = mutableMapOf<Action, ArrayList<ActionDetailInPlan>>()
    private val actionIdList = arrayListOf<Int>()
    private var layoutManager:LinearLayoutManager? = null
    private var adapter:AdapterForTodayFragment? = null
    private var foldFlag = true
    private var processDialogFragment: ProcessDialogFragment? = null
    private var onDataRefresh:OnDataRefresh?=null

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_today,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val dayInfoList = toDayString.split("-")
        val monthAbbreviation = view.context.resources.getStringArray(R.array.month)
        view.findViewById<TextView>(R.id.year).text = dayInfoList[0]
        view.findViewById<TextView>(R.id.month).text = monthAbbreviation[dayInfoList[1].toInt() - 1]
        view.findViewById<TextView>(R.id.day).text = dayInfoList[2].toInt().toString()
        processDialogFragment = ProcessDialogFragment(this.resources.getString(R.string.save_process))
        noPlanPrompt = view.findViewById(R.id.no_plan)
        addPlanButton = view.findViewById(R.id.add_plan)
        actionDetailRecyclerView = view.findViewById(R.id.action_detail)
        addPlanButton!!.setOnClickListener {
            val intent = Intent(this.activity,PlanDetailActivity::class.java)
            intent.putExtra("Date",toDayString)
            startActivity(intent)
        }
        layoutManager = LinearLayoutManager(view.context)
        actionDetailRecyclerView!!.layoutManager = layoutManager
    }

    override fun onResume() {
        if (IsScreenRestart.checkFlag()){
            IsScreenRestart.setFlag(false)
        }else{
            dataRefresh()
        }
        super.onResume()
    }

    fun dataRefresh(){
        //加载数据
        actionIdList.clear()
        actionList.clear()
        actionDetailMap.clear()
        val planCheckDatabase= MyDataBaseTool(view!!.context,"FitnessFlowDB",null,1)
        val planCheckTool=planCheckDatabase.writableDatabase
        planCheckTool.beginTransaction()
        try{
            val planDetailCursor=planCheckTool.rawQuery("Select * From PlanDetailTable where Date=\"$toDayString\" Order By PlanOrder",null)
            while(planDetailCursor.moveToNext()){
                if (planDetailCursor.getString(5).toInt()>=0 && planDetailCursor.getString(6).toInt()>0){
                    val actionDetailInPlan = ActionDetailInPlan(planDetailCursor.getString(0).toInt(),
                        planDetailCursor.getString(1).toInt(),planDetailCursor.getString(2),
                        planDetailCursor.getString(3).toInt(),planDetailCursor.getString(4),
                        planDetailCursor.getString(5).toFloat(),planDetailCursor.getString(6).toInt(),
                        planDetailCursor.getString(7).toInt(),planDetailCursor.getString(8).toInt(),
                        planDetailCursor.getString(10).toInt())
                    if (actionIdList.contains(planDetailCursor.getString(0).toInt())){
                        actionDetailMap[getKeyInPlanDetailMap(actionDetailInPlan.actionID)]!!.add(actionDetailInPlan)
                    }else{
                        actionIdList.add(planDetailCursor.getString(0).toInt())
                        //生成动作类，插入新键值
                        val actionSelectCursor=planCheckTool.rawQuery("Select * From ActionTable where ActionID=?",arrayOf(planDetailCursor.getString(0)))
                        while(actionSelectCursor.moveToNext()){
                            val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                                actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                                actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                                actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                            actionList.add(action)
                            actionDetailMap[action] = arrayListOf(actionDetailInPlan)
                            break
                        }
                        actionSelectCursor.close()
                    }
                }
            }
            planDetailCursor.close()
            if (LibraryUpdateClass.checkTodayDataUpdateFlag()){
                LibraryUpdateClass.setTodayDataUpdateFlag(false)
            }
            planCheckTool.setTransactionSuccessful()
        }catch(e: Exception){
            println("$toDayString Data Check Failed(In TodayFragment):$e")
            MyToast(view!!.context,view!!.context.resources.getString(R.string.loading_failed)).showToast()
        }finally{
            planCheckTool.endTransaction()
            planCheckTool.close()
            planCheckDatabase.close()
        }
        //添加Adapter
        adapter = AdapterForTodayFragment(actionList,actionDetailMap,view!!.context)
        actionDetailRecyclerView!!.adapter = adapter!!
        if (actionList.size == 0){
            if (isHadPlanTodayFlag){
                isHadPlanTodayFlag = false
            }
            TodayPlanNum.setFlag(isHadPlanTodayFlag)
        }else{
            if (!isHadPlanTodayFlag){
                isHadPlanTodayFlag = true
            }
            TodayPlanNum.setFlag(isHadPlanTodayFlag)
        }
        if (!isHadPlanTodayFlag){
            noPlanPrompt!!.visibility = LinearLayout.VISIBLE
            actionDetailRecyclerView!!.visibility = LinearLayout.GONE
        }else{
            noPlanPrompt!!.visibility = LinearLayout.GONE
            actionDetailRecyclerView!!.visibility = LinearLayout.VISIBLE
        }
        if (onDataRefresh!=null){
            onDataRefresh!!.onDataRefresh()
        }
        foldFlag = true
        if (!FoldState.checkFlag()){
            FoldState.setFlag(foldFlag)
        }
    }

    private fun getKeyInPlanDetailMap(id:Int):Action?{
        for (action in actionDetailMap.keys){
            if (action.actionID == id){
                return action
            }
        }
        return null
    }

    private fun fold(){
        adapter!!.fold()
        foldFlag = true
        processDialogFragment!!.show(this.activity!!.supportFragmentManager, null)
        val dataSaving = DataSaving()
        dataSaving.execute()
    }

    private fun unfold(){
        if (actionList.size == 0){
            MyToast(view!!.context,view!!.context.resources.getString(R.string.can_not_unfold)).showToast()
        }else{
            adapter!!.unfold()
            foldFlag = false
        }
    }

    fun onOperatorClick(){
        if (foldFlag){
            unfold()
        }else{
            fold()
        }
    }

    private fun updateData(){
        val updateDataDatabase=MyDataBaseTool(view!!.context,"FitnessFlowDB",null,1)
        val updateDataDataBaseTool=updateDataDatabase.writableDatabase
        updateDataDataBaseTool.beginTransaction()
        val actionCompletion = arrayListOf<Float>()
        try{
            for (action in actionList){
                var doneSum = 0
                var targetSum = 0
                for (detail in actionDetailMap[action]!!){
                    val updateSql = "Update PlanDetailTable Set Done=${detail.done} Where ID=${detail.ID}"
                    doneSum += detail.done
                    targetSum += detail.num
                    updateDataDataBaseTool.execSQL(updateSql)
                }
                actionCompletion.add((doneSum.toFloat()/targetSum.toFloat())*100f)
            }
            var recordSum = 0f
            for (completion in actionCompletion){
                recordSum += completion
            }
            val record = (recordSum/(actionList.size.toFloat())).toInt()
            val recordCursor = updateDataDataBaseTool.rawQuery("Select * From PlanRecord Where Date=?",
                arrayOf(toDayString))
            if (recordCursor.count>0){
                recordCursor.moveToNext()
                val recordUpdateSql = "Update PlanRecord Set Record=$record Where PlanRecordID=${recordCursor.getString(2)} "
                updateDataDataBaseTool.execSQL(recordUpdateSql)
            }else{
                val recordInsertSql = "INSERT INTO PlanRecord (Record,Date) VALUES($record,\"$toDayString\")"
                updateDataDataBaseTool.execSQL(recordInsertSql)
            }
            recordCursor.close()
            updateDataDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Data Save Failed(In TodayFragment):$e")
            MyToast(view!!.context,view!!.context.resources.getString(R.string.save_failed))
        }finally{
            updateDataDataBaseTool.endTransaction()
            updateDataDataBaseTool.close()
            updateDataDatabase.close()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DataSaving: AsyncTask<Void, Int, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            updateData()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            processDialogFragment!!.dismiss()
        }
    }

    interface OnDataRefresh{
        fun onDataRefresh()
    }

    fun setOnDataRefresh(onDataRefresh:OnDataRefresh){
        this.onDataRefresh = onDataRefresh
    }

}