package com.example.zhangjie.fitnessflow.library

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.library.library_child_fragments.MuscleGroupFragment
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateFragment
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateModifyClass
import com.example.zhangjie.fitnessflow.splash.FragmentInit
import com.example.zhangjie.fitnessflow.splash.IndexViewPagerAdapter
import com.example.zhangjie.fitnessflow.splash.ViewPagerScrollerFalse
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class LibraryFragment : Fragment(){

    private var muscleGroupButtonList:List<Button>? = null
    private var currentPageNo = 0
    private var selectedColor:Int? = null
    private var unselectedColor:Int? = null
    private var muscleViewPager:ViewPagerScrollerFalse? = null
    private val muscleGroupFragmentsList = FragmentInit.getMuscleGroupFragmentsList()
    private var muscleGroupViewPagerAdapter: IndexViewPagerAdapter? = null

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_library,container,false)
    }

    //视图初始化
    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        selectedColor = ContextCompat.getColor(view.context, R.color.primaryTextColor)
        unselectedColor = ContextCompat.getColor(view.context, R.color.unSelectedTextColor)
        //导航按钮及点击切换事件
        muscleGroupButtonList = listOf(view.findViewById(R.id.Template),view.findViewById(R.id.Chest),
            view.findViewById(R.id.Shoulder),view.findViewById(R.id.BackGroup),view.findViewById(R.id.Arms),
            view.findViewById(R.id.Legs),view.findViewById(R.id.Belly),view.findViewById(R.id.Aerobics),
            view.findViewById(R.id.Others))
        for (muscleGroup in muscleGroupButtonList!!){
            muscleGroup.setOnClickListener {
                if (muscleGroupButtonList!!.indexOf(muscleGroup) != currentPageNo){
                    muscleGroup.setTextColor(selectedColor!!)
                    muscleGroupButtonList!![currentPageNo].setTextColor(unselectedColor!!)
                    currentPageNo = muscleGroupButtonList!!.indexOf(muscleGroup)
                    muscleViewPager!!.setCurrentItem(currentPageNo,false)
                }
            }
        }
        //肌群细览
        muscleViewPager = view.findViewById(R.id.muscle_vp)
        muscleViewPager!!.offscreenPageLimit = 8
        muscleGroupViewPagerAdapter = IndexViewPagerAdapter(childFragmentManager, muscleGroupFragmentsList)
        muscleViewPager!!.adapter = muscleGroupViewPagerAdapter
        muscleViewPager!!.currentItem = currentPageNo
    }

    fun getCurrentPageNo():Int{
        return currentPageNo
    }

    fun actionAdd(actionType:Int, action:Action){
        (muscleGroupFragmentsList[actionType] as MuscleGroupFragment).actionAdd(action)
    }

    fun templateAdd(templateName:String,dialog:MyDialogFragment){
        val newTemplate = newTemplate(templateName)
        if (newTemplate!=null){
            (muscleGroupFragmentsList[0] as TemplateFragment).templateAdd(newTemplate)
            dialog.dismiss()
            TemplateModifyClass.setPosition(-1)
            TemplateModifyClass.setTemplate(newTemplate)
            val intent = Intent(this.activity, TemplateDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun newTemplate(templateName: String):Template?{
        var templateID:Int? = null
        val templateCreateDatabase= MyDataBaseTool(this.activity as Context,"FitnessFlowDB",null,1)
        val templateCreateTool=templateCreateDatabase.writableDatabase
        templateCreateTool.beginTransaction()
        try{
            val insertSql = "INSERT INTO TemplateTable (TemplateName,ActionNum,MuscleGroupInclude) VALUES (" +
                    "\"$templateName\",0,\"\")"
            templateCreateTool.execSQL(insertSql)
            val idCheckCursor = templateCreateTool.rawQuery("select last_insert_rowid() from TemplateTable",null)
            idCheckCursor.moveToNext()
            templateID = idCheckCursor.getString(0).toInt()
            idCheckCursor.close()
            templateCreateTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Create Failed(In LibraryFragment):$e")
            MyToast(this.activity as Context,resources.getString(R.string.add_failed)).showToast()
        }finally{
            templateCreateTool.endTransaction()
            templateCreateTool.close()
            templateCreateDatabase.close()
        }
        return if (templateID == null){null}else{Template(templateName,0, arrayListOf(),templateID)}
    }

}
