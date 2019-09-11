package com.example.zhangjie.fitnessflow.utils_class

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDataBaseTool(context: Context, DB_NAME:String, factory: SQLiteDatabase.CursorFactory?, version:Int)
    : SQLiteOpenHelper(context,DB_NAME,factory,version){

    override fun onCreate(db:SQLiteDatabase?){
        //动作表
        val actionTableSql1 = "CREATE TABLE \"ActionTable\" (\n" +
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
        val actionTableSql2 = "CREATE UNIQUE INDEX \"ActionID\"\n" +
                "ON \"ActionTable\" (\n" +
                "  \"ActionID\" ASC\n" +
                ");"
        val actionTableSql3 = "CREATE INDEX \"ActionType\"\n" +
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
        db!!.execSQL(actionTableSql1)
        //执行语句
        db.run{
            execSQL(actionTableSql2)
            execSQL(actionTableSql3)
            execSQL(templateString1)
            execSQL(templateString2)
        }
    }

    override fun onUpgrade(db:SQLiteDatabase?,oldVersion:Int,newVersion:Int){

    }


}