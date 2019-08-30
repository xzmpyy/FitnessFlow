package com.example.fitnessflow.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.example.fitnessflow.R


class SplashActivity : AppCompatActivity() {

    private var transitionView:TransitionView? = null
    private var duration = 1500L
    private var startActivityFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        transitionView=findViewById(R.id.process)
        val dataLoading= DataLoading()
        dataLoading.execute()
        viewScale(transitionView!! as View, duration)
        //保证动画执行完再跳转
        Handler().postDelayed({
            if (startActivityFlag){
                val intent = Intent(this@SplashActivity, IndexActivity::class.java)
                startActivity(intent)
            }else{
                startActivityFlag = true
            }
        }, duration)
    }

    @SuppressLint("StaticFieldLeak")
    inner class DataLoading: AsyncTask<Void, Int, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            //预加载任务
            FragmentInit.init()
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            if (startActivityFlag){
                val intent = Intent(this@SplashActivity, IndexActivity::class.java)
                startActivity(intent)
            }else{
                startActivityFlag = true
            }
        }

    }


    //控件动画，拉伸
    private fun viewScale(view:View, time:Long){
        val scale = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        //时长
        scale.duration = time
        //线性变化
        scale.interpolator = object : AccelerateInterpolator(){}
        //起始点
        view.pivotX = 0f
        view.pivotY = 0f
        scale.start()
    }

}
