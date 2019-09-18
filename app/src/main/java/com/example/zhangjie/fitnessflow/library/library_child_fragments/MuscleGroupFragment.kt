package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class MuscleGroupFragment : Fragment(){

    companion object{
        fun getMuscleGroupFragmentWithType(type:Int):MuscleGroupFragment{
            val fragment = MuscleGroupFragment()
            fragment.muscleGroupType = type
            return fragment
        }
    }

    private var muscleGroupRv: RecyclerView? = null
    private var layoutManager:LinearLayoutManagerForItemSwipe? = null
    private var adapter:MuscleGroupFragmentAdapter? = null
    private var muscleGroupType = 0
    //测试数据
    private val actionList = arrayListOf<Action>()

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_muscle_group,container,false)
    }

    //视图初始化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        muscleGroupRv = view.findViewById(R.id.muscle_group_rv)
        //加载动作数据
        val actionSelectDatabase= MyDataBaseTool(view.context,"FitnessFlowDB",null,1)
        val actionSelectDataBaseTool=actionSelectDatabase.writableDatabase
        actionSelectDataBaseTool.beginTransaction()
        try{
            val actionSelectCursor=actionSelectDataBaseTool.rawQuery("Select * From ActionTable where ActionType=? And IsShow=? ORDER BY AddTimes",arrayOf(muscleGroupType.toString(), "1"))
            while(actionSelectCursor.moveToNext()){
                val action = Action(actionSelectCursor.getString(0).toInt(),actionSelectCursor.getString(1).toInt(),
                    actionSelectCursor.getString(2),actionSelectCursor.getString(3).toInt(),actionSelectCursor.getString(4).toInt(),
                    actionSelectCursor.getString(5),actionSelectCursor.getString(6).toFloat(),actionSelectCursor.getString(7).toInt(),
                    actionSelectCursor.getString(8).toFloat(),actionSelectCursor.getString(9).toInt(),actionSelectCursor.getString(10).toInt())
                actionList.add(action)
            }
            actionSelectCursor.close()
            actionSelectDataBaseTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Action Select Failed(In MuscleGroupFragment):$e")
            MyToast(context!!,context!!.resources.getString(R.string.loading_failed))
        }finally{
            actionSelectDataBaseTool.endTransaction()
            actionSelectDataBaseTool.close()
            actionSelectDatabase.close()
        }

        layoutManager = LinearLayoutManagerForItemSwipe((view.context))
        muscleGroupRv!!.layoutManager = layoutManager
        adapter = MuscleGroupFragmentAdapter(actionList, layoutManager!!,view.context as AppCompatActivity)
        muscleGroupRv!!.adapter = adapter
    }

    fun actionAdd(action: Action){
        adapter!!.addAction(actionList.size,action)
        muscleGroupRv!!.scrollToPosition(actionList.size-1)
    }

    fun actionAddTimesUpdate(actionIDList:ArrayList<Int>){
        adapter!!.updateActionAddTimes(actionIDList,muscleGroupType)
    }

}