package com.example.zhangjie.fitnessflow.utils_class.action_pick

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast
import com.example.zhangjie.fitnessflow.utils_class.ScreenInfoClass

class ActionPickDialog (private val actionIDList:ArrayList<Int>,context:Context): DialogFragment(),
    MuscleGroupAdapterInActionPick.MuscleGroupClickListener{

    private var actionList = arrayListOf<Action>()
    private var muscleGroupRv:RecyclerView? = null
    private var actionListRv:RecyclerView? = null
    private val selectTextColor = ContextCompat.getColor(context,R.color.primaryTextColor)
    private val unSelectTextColor = ContextCompat.getColor(context,R.color.unSelectedTextColor)
    private var muscleGroupAdapter:MuscleGroupAdapterInActionPick? = null
    private val muscleGroupLayoutManager = LinearLayoutManager(context)
    private val muscleGroupNameList = arrayListOf<String>()
    private var actionListAdapter:ActionListAdapterInActionPick? = null
    private val actionListLayoutManager = LinearLayoutManager(context)

    //设置Fragment宽高
    override fun onStart() {
        super.onStart()
        val dialogWindow = dialog.window
        //加上这一行才能去掉四周空白
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0x000000))
        val windowWidth=
            ScreenInfoClass.getScreenWidthDP(this.context!!)
        val dialogHeight = windowWidth*3
        dialogWindow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dialogHeight)
        //dialog的位置
        dialogWindow.setGravity(Gravity.BOTTOM)
    }

    //视图加载
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //设置动画
        dialog.window!!.setWindowAnimations(R.style.dialog_pop)
        return inflater.inflate(R.layout.action_pick_view, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        muscleGroupRv = view.findViewById(R.id.muscle_group_rv)
        actionListRv = view.findViewById(R.id.action_rv)
        for (muscleGroupName in context!!.resources.getStringArray(R.array.muscle_group)){
            if (context!!.resources.getStringArray(R.array.muscle_group).indexOf(muscleGroupName) != 0){
                muscleGroupNameList.add(muscleGroupName)
            }
        }
        //左侧导航
        muscleGroupAdapter = MuscleGroupAdapterInActionPick(muscleGroupNameList,view.context)
        muscleGroupAdapter!!.setMuscleGroupClickListener(this)
        muscleGroupRv!!.layoutManager = muscleGroupLayoutManager
        muscleGroupRv!!.adapter = muscleGroupAdapter
        getActionList(1)
        actionListRv!!.layoutManager = actionListLayoutManager
        actionListAdapter = ActionListAdapterInActionPick(actionList,view.context)
        actionListRv!!.adapter = actionListAdapter
    }

    //左侧部位导航点击事件
    override fun onMuscleGroupClick(oldPosition: Int, newPosition: Int) {
        muscleGroupAdapter!!.setMuscleGroupNameText(oldPosition,unSelectTextColor)
        muscleGroupAdapter!!.setMuscleGroupNameText(newPosition,selectTextColor)
        getActionList(newPosition + 1)
        actionListAdapter!!.listUpdate()
    }

    private fun getActionList(type:Int){
        actionList.clear()
        val actionListDatabase= MyDataBaseTool(context!!,"FitnessFlowDB",null,1)
        val actionListDataBaseTool=actionListDatabase.writableDatabase
        actionListDataBaseTool.beginTransaction()
        try{
            val actionSelectCursor=actionListDataBaseTool.rawQuery("Select * From ActionTable where ActionType=? And IsShow=? ORDER BY AddTimes",arrayOf(type.toString(), "1"))
            while(actionSelectCursor.moveToNext()){
                if (!actionIDList.contains(actionSelectCursor.getString(1).toInt())){
                    val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                        actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                        actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                        actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                    actionList.add(action)
                }
            }
            actionSelectCursor.close()
            actionListDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Check Failed(In ActionPickDialog):$e")
            MyToast(context!!,context!!.resources.getString(R.string.loading_failed))
        }finally{
            actionListDataBaseTool.endTransaction()
            actionListDataBaseTool.close()
            actionListDatabase.close()
        }
    }

}