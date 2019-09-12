package com.example.zhangjie.fitnessflow.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Template
import com.example.zhangjie.fitnessflow.library.library_child_fragments.TemplateModifyClass
import com.example.zhangjie.fitnessflow.splash.IndexActivity

class TemplateDetailActivity : AppCompatActivity() {

    private var template:Template?=null
    private var backButton:Button? = null
    private var saveButton:Button? = null
    private var templateName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_detail)
        template = TemplateModifyClass.getTemplate()!!
        //相关视图
        backButton = findViewById(R.id.back)
        saveButton = findViewById(R.id.save)
        templateName = findViewById(R.id.template_name)
        backButton!!.setOnClickListener {
            TemplateModifyClass.clear()
            finish()
        }
        saveButton!!.setOnClickListener {
            finish()
        }
        templateName!!.text = template!!.templateName
    }
}
