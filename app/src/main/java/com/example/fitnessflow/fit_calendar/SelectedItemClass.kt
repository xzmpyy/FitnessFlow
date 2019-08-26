package com.example.fitnessflow.fit_calendar

object SelectedItemClass {

    private val selectedItemList = arrayListOf<String>()

    fun addItem(item:String){
        selectedItemList.add(item)
    }

    fun removeItem(item:String){
        selectedItemList.remove(item)
    }

    fun checkItem(item:String):Boolean{
        return selectedItemList.contains(item)
    }

    fun getSelectedList():ArrayList<String>{
        return selectedItemList
    }

    fun clear(){
        selectedItemList.clear()
    }

    fun getListCount():Int{
        return selectedItemList.count()
    }

}