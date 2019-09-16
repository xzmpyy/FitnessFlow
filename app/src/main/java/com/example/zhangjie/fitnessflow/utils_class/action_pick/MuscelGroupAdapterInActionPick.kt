package com.example.zhangjie.fitnessflow.utils_class.action_pick

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R

class MuscleGroupAdapterInActionPick (private val muscleGroupList:ArrayList<String>, private val context: Context):
    RecyclerView.Adapter<MuscleGroupAdapterInActionPick.RvHolder>(){

    private val firstItemTopMargin = context.resources.getDimension(R.dimen.viewMargin).toInt()
    private val selectTextColor = ContextCompat.getColor(context,R.color.primaryTextColor)
    private var muscleGroupClickListener:MuscleGroupClickListener? = null
    private var currentItem = 0
    private var muscleGroupTextList = arrayListOf<TextView>()

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val muscleGroupName=view.findViewById<TextView>(R.id.muscle_group_name)!!
        val parentLayout = view.findViewById<LinearLayout>(R.id.item_parent_layout)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.muscle_group_name_in_action_pick,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return muscleGroupList.size
    }

    override fun onBindViewHolder(p0:RvHolder,p1:Int){
        if (p1 == 0){
            val layoutParams = LinearLayout.LayoutParams(p0.parentLayout.layoutParams)
            layoutParams.topMargin = firstItemTopMargin
            p0.parentLayout.layoutParams = layoutParams
            p0.muscleGroupName.setTextColor(selectTextColor)
        }
        if (muscleGroupTextList.count()-1<p1){
            muscleGroupTextList.add(p0.muscleGroupName)
        }
        //向viewHolder中的View控件赋值需显示的内容
        p0.muscleGroupName.text=muscleGroupList[p1]
        p0.muscleGroupName.setOnClickListener {
            if (p1 != currentItem){
                if (muscleGroupClickListener!=null){
                    muscleGroupClickListener!!.onMuscleGroupClick(currentItem, p1)
                }
                currentItem = p1
            }
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }

    interface MuscleGroupClickListener{
        fun onMuscleGroupClick(oldPosition:Int, newPosition:Int)
    }

    fun setMuscleGroupClickListener(muscleGroupClickListener:MuscleGroupClickListener){
        this.muscleGroupClickListener = muscleGroupClickListener
    }

    fun setMuscleGroupNameText(position:Int, color:Int){
        muscleGroupTextList[position].setTextColor(color)
    }

}
