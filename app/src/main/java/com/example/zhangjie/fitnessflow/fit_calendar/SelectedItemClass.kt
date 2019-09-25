package com.example.zhangjie.fitnessflow.fit_calendar

object SelectedItemClass {

    private val selectedItemList = arrayListOf<String>()

    fun addItem(item:String){
        if (!selectedItemList.contains(item)){
            selectedItemList.add(item)
        }
    }

    fun removeItem(item:String){
        if (selectedItemList.contains(item)){
            selectedItemList.remove(item)
        }
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