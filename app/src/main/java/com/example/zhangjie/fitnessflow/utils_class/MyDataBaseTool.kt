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
                "  \"InitWeight\" text,\n" +
                "  \"InitNum\" text,\n" +
                "  \"WeightOfIncreaseProgressively\" integer,\n" +
                "  \"NumOfIncreaseProgressively\" integer,\n" +
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
        db!!.execSQL(actionTableSql1)
        //执行语句
        db.run{
            execSQL(actionTableSql2)
            execSQL(actionTableSql3)
        }
    }

    override fun onUpgrade(db:SQLiteDatabase?,oldVersion:Int,newVersion:Int){

    }


}