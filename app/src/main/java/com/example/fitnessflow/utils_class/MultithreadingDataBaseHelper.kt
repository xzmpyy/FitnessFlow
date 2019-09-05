package com.example.fitnessflow.utils_class

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object MultithreadingDataBaseHelper{
    private var myDatabaseTool:MyDataBaseTool?=null
    private var myDatabaseHelper: SQLiteDatabase?=null
    private var threadCount=0

    //生成单例的SQLiteOpenHelper
    @Synchronized private fun getSQLiteOpenHelper(context: Context, DB_NAME:String): SQLiteOpenHelper {
        if(myDatabaseTool==null){
            myDatabaseTool=MyDataBaseTool(context,DB_NAME,null,1)
        }
        return myDatabaseTool!!
    }

    //获取SQLiteDatabase
    @Synchronized fun getSQLiteDatabase(context:Context,DB_NAME:String):SQLiteDatabase{
        if(threadCount==0){
            myDatabaseHelper=getSQLiteOpenHelper(context,DB_NAME).writableDatabase
        }
        threadCount+=1
        return myDatabaseHelper!!
    }

    @Synchronized fun closeSQLiteDatabase(databaseHelper:SQLiteDatabase){
        threadCount-=1
        if(threadCount==0){
            databaseHelper.close()
        }
    }

}