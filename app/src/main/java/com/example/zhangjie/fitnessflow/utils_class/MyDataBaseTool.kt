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
                "  \"Weight\" integer,\n" +
                "  \"Num\" integer,\n" +
                "  \"TemplateID\" integer,\n" +
                "  \"ActionOrder\" integer,\n" +
                "  \"TemplateOrder\" integer,\n" +
                "  \"Num\" integer,\n" +
                "  \"ID\" integer PRIMARY KEY AUTOINCREMENT,\n" +
                "  CONSTRAINT \"TemplatesItemID\" UNIQUE (\"TemplatesItemID\")\n" +
                ");"
        //索引
        val templateDetailString2 = "CREATE INDEX \"TemplateID\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"TemplateID\" ASC\n" +
                ");"
        val templateDetailString3="CREATE UNIQUE INDEX \"ID\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"ID\" ASC\n" +
                ");"
        val templateDetailString4 = "CREATE INDEX \"ActionID\"\n" +
                "ON \"TemplateDetailTable\" (\n" +
                "  \"ActionID\" ASC\n" +
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
        }
    }

    override fun onUpgrade(db:SQLiteDatabase?,oldVersion:Int,newVersion:Int){

    }


}