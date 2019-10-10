package com.example.zhangjie.fitnessflow.mine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangjie.fitnessflow.R

class AdapterForMap (private val recordList:ArrayList<Int>, private val context: Context):
    RecyclerView.Adapter<AdapterForMap.RvHolder>(){

    private val redColor = ContextCompat.getColor(context, R.color.primaryRed)
    private val done20 = ContextCompat.getColor(context, R.color.done20)
    private val done40 = ContextCompat.getColor(context, R.color.done40)
    private val done60 = ContextCompat.getColor(context, R.color.done60)
    private val greenColor = ContextCompat.getColor(context, R.color.primaryGreen)

    //控件类，代表了每一个Item的布局
    class RvHolder(view: View):RecyclerView.ViewHolder(view){
        //找到加载的布局文件中需要进行设置的各项控件
        val block=view.findViewById<LinearLayout>(R.id.block)!!
    }

    //复写控件类的生成方法
    override fun onCreateViewHolder(p0: ViewGroup, p1:Int):RvHolder{
        //创建一个ViewHolder，获得ViewHolder关联的layout文件,返回一个加载了layout的控件类
        //inflate三个参数为需要填充的View的资源id、附加到resource中的根控件、是否将root附加到布局文件的根视图上
        return RvHolder(LayoutInflater.from(context).inflate(R.layout.block_view,p0,false))
    }

    //获取Item个数的方法
    override fun getItemCount():Int{
        //返回列表长度
        return 30
    }

    override fun onBindViewHolder(p0:RvHolder,p1:Int){
        //向viewHolder中的View控件赋值需显示的内容
        if (recordList[p1] in 0..19){
            p0.block.setBackgroundColor(redColor)
        }
        if (recordList[p1] in 20..39){
            p0.block.setBackgroundColor(done20)
        }
        if (recordList[p1] in 40..59){
            p0.block.setBackgroundColor(done40)
        }
        if (recordList[p1] in 60..79){
            p0.block.setBackgroundColor(done60)
        }
        if (recordList[p1] >= 80){
            p0.block.setBackgroundColor(greenColor)
        }
    }

    //onBindViewHolder只有在getItemViewType返回值不同时才调用，当有多种布局的Item时不重写会导致复用先前的条目，数据容易错乱
    override fun getItemViewType(position:Int):Int{
        return position
    }


}
