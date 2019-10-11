package com.example.zhangjie.fitnessflow.mine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R

class TrendView (context: Context, set: AttributeSet): View(context, set) {
    private val paint = Paint()
    private val redColor = ContextCompat.getColor(context, R.color.primaryRed)
    private val greenColor = ContextCompat.getColor(context, R.color.primaryGreen)
    private val purpleColor = ContextCompat.getColor(context, R.color.primaryButtonBackground)
    private val strokeWidth = 12f
    private var weightList:List<Float>? = null
    private var fatList:List<Float>? = null
    private var bmiList:List<Float>? = null
    private var weightCircleList= arrayListOf<Float>()
    private var fatCircleList= arrayListOf<Float>()
    private var bmiCircleList= arrayListOf<Float>()
    private var circleXList = arrayListOf<Float>()
    private var maxWeight = 0f
    private var maxFat = 0f
    private var maxBMI = 0f
    private var minWeight = 0f
    private var minFat = 0f
    private var minBMI = 0f
    private var circleRadius = 20f
    private var viewHeight = 0f
    private var widthSize = 0
    private val padding = context.resources.getDimension(R.dimen.viewPaddingHorizontal)

    constructor(context: Context, set: AttributeSet,weightList:List<Float>,fatList:List<Float>,bmiList:List<Float>): this(context, set){
        this.weightList = weightList
        this.fatList = fatList
        this.bmiList = bmiList
    }

    init {
        //去锯齿
        paint.isAntiAlias = true
        //填充风格
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = strokeWidth
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取宽高-测量规则的模式和大小
        //获取宽高-测量规则的模式和大小
        widthSize = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = widthSize.toFloat()/4f
        if (viewHeight<=padding*8){
            viewHeight = padding*10
        }
        setMeasuredDimension(widthSize, viewHeight.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        getMaxData()
        getCircleX()
        getCircleY()
        for (index in weightList!!.indices){
            paint.color = redColor
            if (index!=0){
                canvas!!.drawLine(circleXList[index-1],weightCircleList[index-1],circleXList[index],weightCircleList[index],paint)
            }
            canvas!!.drawCircle(circleXList[index],weightCircleList[index],circleRadius,paint)
        }
        for (index in weightList!!.indices){
            paint.color = greenColor
            if (index!=0){
                canvas!!.drawLine(circleXList[index-1],fatCircleList[index-1],circleXList[index],fatCircleList[index],paint)
            }
            canvas!!.drawCircle(circleXList[index],fatCircleList[index],circleRadius,paint)
        }
        for (index in weightList!!.indices){
            paint.color = purpleColor
            if (index!=0){
                canvas!!.drawLine(circleXList[index-1],bmiCircleList[index-1],circleXList[index],bmiCircleList[index],paint)
            }
            canvas!!.drawCircle(circleXList[index],bmiCircleList[index],circleRadius,paint)
        }

    }

    private fun getMaxData(){
        var maxNum = 0f
        var minNum = 0f
        for (weight in weightList!!){
            if (weight>maxNum){
                maxNum = weight
            }
            if (weightList!!.indexOf(weight) == 0 && weightList!!.size>1){
                minNum = weight
            }else{
                if (weight<minNum){
                    minNum = weight
                }
            }
        }
        maxWeight = maxNum-minNum
        minWeight = minNum
        maxNum = 0f
        minNum = 0f
        for (fat in fatList!!){
            if (fat>maxNum){
                maxNum = fat
            }
            if (fatList!!.indexOf(fat) == 0 && fatList!!.size>1){
                minNum = fat
            }else{
                if (fat<minNum){
                    minNum = fat
                }
            }
        }
        maxFat = maxNum-minNum
        minFat = minNum
        maxNum = 0f
        minNum = 0f
        for (bmi in bmiList!!){
            if (bmi>maxNum){
                maxNum = bmi
            }
            if (bmiList!!.indexOf(bmi) == 0 && bmiList!!.size>1){
                minNum = bmi
            }else{
                if (bmi<minNum){
                    minNum = bmi
                }
            }
        }
        maxBMI = maxNum-minNum
        minBMI = minNum
    }

    private fun getCircleY(){
        for (weight in weightList!!){
            val percentage = (weight-minWeight)/maxWeight
            val positionY = if (percentage == 0f){
                viewHeight - padding*3 -circleRadius
            }else{
                viewHeight-percentage*(viewHeight-padding*2) + padding
            }
            weightCircleList.add(positionY)
        }
        for (fat in fatList!!){
            val percentage = (fat-minFat)/maxFat
            val positionY = if (percentage == 0f){
                viewHeight - padding*2 -circleRadius
            }else{
                viewHeight-percentage*(viewHeight-padding*2) + padding*2
            }
            fatCircleList.add(positionY)
        }
        for (bmi in bmiList!!){
            val percentage = (bmi-minBMI)/maxBMI
            val positionY = if (percentage == 0f){
                viewHeight - padding -circleRadius
            }else{
                viewHeight-percentage*(viewHeight-padding*2) + padding*3
            }
            bmiCircleList.add(positionY)
        }
    }

    private fun getCircleX(){
        val xPosition = widthSize.toFloat()/(weightList!!.size+1)
        for (i in weightList!!.indices){
            circleXList.add(xPosition + xPosition*i.toFloat())
        }
    }

}