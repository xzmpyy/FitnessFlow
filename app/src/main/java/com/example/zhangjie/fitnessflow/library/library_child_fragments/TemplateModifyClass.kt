package com.example.zhangjie.fitnessflow.library.library_child_fragments

import com.example.zhangjie.fitnessflow.data_class.Template

object TemplateModifyClass {

    private var position:Int? = null
    private var template:Template? = null

    fun setPosition(position:Int){
        this.position = position
    }

    fun setTemplate(template:Template){
        this.template = template
    }

    fun getPosition():Int?{
        return position
    }

    fun getTemplate():Template?{
        return template
    }

    fun clear(){
        position = null
        template = null
    }

}