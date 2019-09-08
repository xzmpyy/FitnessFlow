package com.example.zhangjie.fitnessflow.utils_class

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

class OverturnAnimation(private val duration:Int): Animation(){
    private val camera= Camera()
    private var degrees=180f
    private var scale=0f
    private var centerX=0f
    private var centerY=0f
    private var listener:InterpolatedTimeListener?=null

    override fun initialize(width:Int,height:Int,parentWidth:Int,parentHeight:Int){
        super.initialize(width,height,parentWidth,parentHeight)
        //动画持续时间
        setDuration(duration.toLong())
        //效果保留
        fillAfter=true
        //线性变化
        interpolator= LinearInterpolator()
    }

    override fun applyTransformation(interpolatedTime:Float,t: Transformation?){
        camera.save()
        if(listener!=null){
            listener!!.doInHalfRotateTime(interpolatedTime)
        }
        //设置反转角度，使反转90度时改变反转角度，让正面继续朝上，这样内容不会出现镜像
        val degreesNow=if(interpolatedTime>0.5f){degrees*interpolatedTime-180}else{degrees*interpolatedTime}
        //沿Y轴旋转
        camera.rotateY(degreesNow)
        val matrix=t!!.matrix
        camera.getMatrix(matrix)
        camera.restore()
        //修正旋转时的缩放失真
        val myValues=FloatArray(9)
        matrix.getValues(myValues)
        //scale时像素密度
        myValues[6]=myValues[6]/scale//数值修正
        myValues[7]=myValues[7]/scale//数值修正
        matrix.setValues(myValues)
        //设置旋转中心
        matrix.preTranslate(-centerX,-centerY)
        matrix.postTranslate(centerX,centerY)
    }

    fun baseSet(scale:Float,centerX:Float,centerY:Float){
        this.scale=scale
        this.centerX=centerX
        this.centerY=centerY
    }

    //调用者实现的接口，用于监听在旋转90度时，处理事务
    interface InterpolatedTimeListener{
        fun doInHalfRotateTime(time:Float)
    }

    //调用者设置监听器
    fun setInterpolatedTimeListener(listener:InterpolatedTimeListener){
        this.listener=listener
    }

}
