package com.example.fitnessflow.utils_class

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

object ScreenInfoClass {

    fun getScreenWidthDP(context: Context):Int{
        val wm: WindowManager =context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val windowWidth=dm.widthPixels
        val density=dm.densityDpi
        //dp=px/(dpi/160)
        return (windowWidth*DisplayMetrics.DENSITY_DEFAULT)/density
        //px=dp*(dpi/160)
        //sWidth=sWidthDp*density/DisplayMetrics.DENSITY_DEFAULT
    }

    fun getScreenWidthPx(context: Context):Int{
        val wm: WindowManager =context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun dp2px(dp:Int, context: Context):Int{
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(),context.resources.displayMetrics).toInt()
    }
    fun sp2px(sp:Int, context: Context):Int{
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun sp2dp(sp:Int, context: Context):Int {
        val px = sp2px(sp,context)
        val wm: WindowManager =context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val density=dm.densityDpi
        //dp=px/(dpi/160)
        return (px*DisplayMetrics.DENSITY_DEFAULT)/density
    }
}