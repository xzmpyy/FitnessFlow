package com.example.zhangjie.fitnessflow.library.library_child_fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R

class MuscleGroupFragmentAdapter (private val list:ArrayList<String>, private val layoutManager:LinearLayoutManagerForItemSwipe,
                                  private val context: Context):
    RecyclerView.Adapter<MuscleGroupFragmentAdapter.RvHolder>(){

    private val firstItemTopMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private val lastItemBottomMargin = context.resources.getDimension(R.dimen.LastBottomInRvBottom).toInt()
    private val maxSwipeDistance = -(context.resources.getDimension(R.dimen.iconSize)*2 + context.resources.getDimension(
        R.dimen.viewMargin)*5)
    private var canScrollVerticallyFlag = true

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View): RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val itemText=view.findViewById<TextView>(R.id.text)!!
        val parentLayout = view.findViewById<FrameLayout>(R.id.item_parent_layout)!!
        val upperLayout = view.findViewById<LinearLayout>(R.id.upper_layout)!!
        val editTemplateButton = view.findViewById<ImageButton>(R.id.edit_button)!!
        val deleteTemplateButton = view.findViewById<ImageButton>(R.id.delete_button)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.muscle_group_item_in_library,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return list.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(p0:RvHolder, p1:Int){
        //第一个和最后一个加top、bottom的margin
        if (p1 == 0){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.topMargin = firstItemTopMargin
            p0.parentLayout.layoutParams = layoutParams
        }
        if (p1 == list.size - 1){
            val layoutParams = FrameLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.bottomMargin = lastItemBottomMargin
            p0.parentLayout.layoutParams = layoutParams
            p0.parentLayout.background = ContextCompat.getDrawable(context,R.drawable.last_item_underline)
        }
        //向viewHolder中的View控件赋值需显示的内容
        p0.itemText.text="M" + list[p1]
        var positionX = 0f
        //item侧滑显示按钮
        p0.upperLayout.setOnTouchListener { _, event ->
            when (event!!.action){
                MotionEvent.ACTION_DOWN->{
                    positionX = event.rawX
                }
                MotionEvent.ACTION_MOVE->{
                    val targetTranslationX = p0.upperLayout.translationX + event.rawX - positionX
                    if (targetTranslationX < 0 && targetTranslationX>maxSwipeDistance){
                        if (canScrollVerticallyFlag){
                            canScrollVerticallyFlag = false
                            layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                        }
                        p0.upperLayout.translationX = targetTranslationX
                    }else if (targetTranslationX<=maxSwipeDistance && p0.upperLayout.translationX != maxSwipeDistance){
                        if (!canScrollVerticallyFlag){
                            canScrollVerticallyFlag = true
                            layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                        }
                        p0.upperLayout.translationX = maxSwipeDistance
                        p0.editTemplateButton.isClickable = true
                        p0.deleteTemplateButton.isClickable = true
                    }else if (targetTranslationX >=0 && p0.upperLayout.translationX != 0f){
                        if (!canScrollVerticallyFlag){
                            canScrollVerticallyFlag = true
                            layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
                        }
                        p0.upperLayout.translationX = 0f
                        p0.editTemplateButton.isClickable = false
                        p0.deleteTemplateButton.isClickable = false
                    }
                    positionX = event.rawX
                }
                MotionEvent.ACTION_UP->{
                    itemSwipeAnimation(p0.upperLayout,p0)
                    positionX = 0f
                }
            }
            true
        }
        //按钮点击事件
        p0.deleteTemplateButton.setOnClickListener {
            println("Delete")
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    private fun itemSwipeAnimation(upperView: View, p0:RvHolder){
        if (upperView.translationX <= maxSwipeDistance/2 && upperView.translationX!=maxSwipeDistance){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,maxSwipeDistance)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            p0.editTemplateButton.isClickable = true
            p0.deleteTemplateButton.isClickable = true
            if (!canScrollVerticallyFlag){
                canScrollVerticallyFlag = true
                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
            }
        }else if (upperView.translationX > maxSwipeDistance/2 && upperView.translationX!=0f){
            val swipeAnimation = ValueAnimator.ofFloat(upperView.translationX,0f)
            swipeAnimation.addUpdateListener {
                val translationDistance = it.animatedValue as Float
                upperView.translationX = translationDistance
            }
            swipeAnimation.duration = 300
            swipeAnimation.start()
            p0.upperLayout.translationX = 0f
            p0.editTemplateButton.isClickable = false
            p0.deleteTemplateButton.isClickable = false
            if (!canScrollVerticallyFlag){
                canScrollVerticallyFlag = true
                layoutManager.setCanScrollVerticallyFlag(canScrollVerticallyFlag)
            }
        }
    }

}