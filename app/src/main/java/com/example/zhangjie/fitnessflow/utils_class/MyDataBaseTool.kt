package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDataBaseTool(context: Context, DB_NAME:String, factory: SQLiteDatabase.CursorFactory?, version:Int)
    : SQLiteOpenHelper(context,DB_NAME,factory,version){

    override fun onCreate(db:SQLiteDatabase?){
        //动作表
        val actionTableString1 = "CREATE TABLE \"ActionTable\" (\n" +
                "  \"ActionType\" integer,\n" +
                "  \"ActionID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"ActionName\" text,\n" +
                "  \"IsHadWeightUnits\" integer,\n" +
                "  \"AddTimes\" integer,\n" +
                "  \"Unit\" text,\n" +
                "  \"InitWeight\" real,\n" +
                "  \"InitNum\" integer,\n" +
                "  \"WeightOfIncreaseProgressively\" real,\n" +
                "  \"NumOfIncreaseProgressively\" integer,\n" +
                "  \"IsShow\" integer,\n" +
                "  CONSTRAINT \"ActionID\" UNIQUE (\"ActionID\" ASC)\n" +
                ");"
        val actionTableString2 = "CREATE UNIQUE INDEX \"ActionID\"\n" +
                "ON \"ActionTable\" (\n" +
                "  \"ActionID\" ASC\n" +
                ");"
        val actionTableString3 = "CREATE INDEX \"ActionType\"\n" +
                "ON \"ActionTable\" (\n" +
                "  \"ActionType\" ASC\n" +
                ");"
        //模板表
        val templateString1 = "CREATE TABLE \"TemplateTable\" (\n" +
                "  \"TemplateName\" text,\n" +
                "  \"ActionNum\" integer,\n" +
                "  \"MuscleGroupInclude\" text,\n" +
                "  \"TemplateID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  CONSTRAINT \"TemplateID\" UNIQUE (\"TemplateID\")\n" +
                ");"
        //索引
        val templateString2="CREATE UNIQUE INDEX \"TemplateID\"\n" +
                "ON \"TemplateTable\" (\n" +
                "  \"TemplateID\" ASC\n" +
                ");"
        //模板详情表
        val templateDetailString1 = "CREATE TABLE \"TemplateDetailTable\" (\n" +
                "  \"ActionID\" integer,\n" +
                "  \"ActionType\" integer,\n" +
                "  \"ActionName\" text,\n" +
                "  \"IsHadWeightUnits\" integer,\n" +
                "  \"Unit\" text,\n" +
                "  \"Weight\" real,\n" +
                "  \"Num\" integer,\n" +
                "  \"TemplateID\" integer,\n" +
                "  \"TemplateOrder\" integer,\n" +
                "  \"ID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  CONSTRAINT \"ID\" UNIQUE (\"ID\")\n" +
                ");"
        //索引
        val templateDetailString2 = "CREATE INDEX \"TemplateIDInTemplateDetail\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"TemplateID\" ASC\n" +
                ");"
        val templateDetailString3="CREATE UNIQUE INDEX \"IDInTemplateDetail\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"ID\" ASC\n" +
                ");"
        val templateDetailString4 = "CREATE INDEX \"ActionIDInTemplateDetail\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"ActionID\" ASC\n" +
                ");"
        //今日计划详情表
        val planDetailString1 = "CREATE TABLE \"PlanDetailTable\" (\n" +
                "  \"ActionID\" integer,\n" +
                "  \"ActionType\" integer,\n" +
                "  \"ActionName\" text,\n" +
                "  \"IsHadWeightUnits\" integer,\n" +
                "  \"Unit\" text,\n" +
                "  \"Weight\" real,\n" +
                "  \"Num\" integer,\n" +
                "  \"Done\" integer,\n" +
                "  \"PlanOrder\" integer,\n" +
                "  \"Date\" text,\n" +
                "  \"ID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  CONSTRAINT \"ID\" UNIQUE (\"ID\")\n" +
                ");"
        //索引
        val planDetailString2="CREATE UNIQUE INDEX \"IDInPlanDetail\"\n" +
                "ON \"PlanDetailTable\" (\n" +
                "  \"ID\" ASC\n" +
                ");"
        val planDetailString3 = "CREATE INDEX \"ActionIDInPlanDetail\"\n" +
                "ON \"PlanDetailTable\" (\n" +
                "  \"ActionID\" ASC\n" +
                ");"
        val planDetailString4 = "CREATE INDEX \"Date\"\n" +
                "ON \"PlanDetailTable\" (\n" +
                "  \"Date\" ASC\n" +
                ");"
        val personalDataString1 = "CREATE TABLE \"PersonalData\" (\n" +
                "  \"Date\" text,\n" +
                "  \"Sex\" integer,\n" +
                "  \"Age\" integer,\n" +
                "  \"Stature\" real,\n" +
                "  \"Weight\" real,\n" +
                "  \"BMI\" real,\n" +
                "  \"Fat\" real,\n" +
                "  \"DataID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  CONSTRAINT \"DataID\" UNIQUE (\"DataID\")\n" +
                ");"
        //索引
        val personalDataString2 = "CREATE INDEX \"DateInPersonalData\"\n" +
                "ON \"PersonalData\" (\n" +
                "  \"Date\" ASC\n" +
                ");"
        val personalDataString3 = "CREATE INDEX \"DataID\"\n" +
                "ON \"PersonalData\" (\n" +
                "  \"DataID\" ASC\n" +
                ");"
        db!!.execSQL(actionTableString1)
        //执行语句
        db.run{
            execSQL(actionTableString2)
            execSQL(actionTableString3)
            execSQL(templateString1)
            execSQL(templateString2)
            execSQL(templateDetailString1)
            execSQL(templateDetailString2)
            execSQL(templateDetailString3)
            execSQL(templateDetailString4)
            execSQL(planDetailString1)
            execSQL(planDetailString2)
            execSQL(planDetailString3)
            execSQL(planDetailString4)
            execSQL(personalDataString1)
            execSQL(personalDataString2)
            execSQL(personalDataString3)
        }
    }

    override fun onUpgrade(db:SQLiteDatabase?,oldVersion:Int,newVersion:Int){

    }


}