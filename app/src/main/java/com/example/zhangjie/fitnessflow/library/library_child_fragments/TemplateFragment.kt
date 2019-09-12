package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.utils_class.MyDataBaseTool

class TemplateFragment : Fragment(){

    private var templateRv:RecyclerView? = null
    private var layoutManager:LinearLayoutManagerForItemSwipe? = null
    private var adapter:TemplateFragmentAdapter? = null
    private val templateList = arrayListOf<Template>()

    //视图加载
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.fragment_template,container,false)
    }

    //视图初始化
    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        templateRv = view.findViewById(R.id.template_rv)
        templateListInit(view.context)
        layoutManager = LinearLayoutManagerForItemSwipe((view.context))
        templateRv!!.layoutManager = layoutManager
        adapter = TemplateFragmentAdapter(templateList, layoutManager!!,view.context)
        templateRv!!.adapter = adapter
    }

    private fun templateListInit(context:Context){
        val templateCheckDatabase= MyDataBaseTool(context,"FitnessFlowDB",null,1)
        val templateCheckTool=templateCheckDatabase.writableDatabase
        templateCheckTool.beginTransaction()
        try{
            val templateCheckCursor= templateCheckTool.rawQuery("Select * From TemplateTable",null)
            while(templateCheckCursor.moveToNext()){
                val muscleGroupInclude = arrayListOf<String>()
                if (templateCheckCursor.getString(2).count() > 0){
                    for (muscleGroup in templateCheckCursor.getString(2).split(";")){
                        muscleGroupInclude.add(muscleGroup)
                    }
                }
                templateList.add(Template(templateCheckCursor.getString(0),templateCheckCursor.getString(1).toInt(),
                    muscleGroupInclude,templateCheckCursor.getString(3).toInt()))
            }
            templateCheckCursor.close()
            templateCheckTool.setTransactionSuccessful()
        }catch(e:Exception){
            println("Template Check Failed(In TemplateFragment):$e")
        }finally{
            templateCheckTool.endTransaction()
            templateCheckTool.close()
            templateCheckDatabase.close()
        }
    }

    fun templateAdd(template: Template){
        adapter!!.addTemplate(templateList.size,template)
    }

    override fun onResume() {
        //模板编辑完毕后刷新相应项
        if (TemplateModifyClass.getPosition() != null){
            adapter!!.updateTemplate(TemplateModifyClass.getPosition()!!,TemplateModifyClass.getTemplate()!!)
        }
        super.onResume()
    }

}