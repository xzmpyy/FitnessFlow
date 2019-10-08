package com.example.zhangjie.fitnessflow.splash

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Xml
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.data_class.Action
import com.example.zhangjie.fitnessflow.fit_calendar.GetMonthInfo
import com.example.zhangjie.fitnessflow.fit_calendar.SelectedItemClass
import com.example.zhangjie.fitnessflow.library.LibraryFragment
import com.example.zhangjie.fitnessflow.library.LibraryUpdateClass
import com.example.zhangjie.fitnessflow.library.library_child_fragments.MuscleGroupItemAddFormView
import com.example.zhangjie.fitnessflow.navigation_bar.NavigationBarView
import com.example.zhangjie.fitnessflow.plan.PlanDetailActivity
import com.example.zhangjie.fitnessflow.plan.PlanFragment
import com.example.zhangjie.fitnessflow.today.TodayFragment
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment
import com.example.zhangjie.fitnessflow.utils_class.MyToast

class IndexActivity : AppCompatActivity(),NavigationBarView.OperationButtonClickListener,
    NavigationBarView.NavigatorClickListener,MyDialogFragment.ConfirmButtonClickListener,
    MuscleGroupItemAddFormView.SubmitListener,TodayFragment.OnDataRefresh{

    private var indexViewPager: ViewPagerScrollerFalse? = null
    private var indexFragmentInViewPagerList = FragmentInit.getIndexFragmentInViewPagerList()
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null
    private var navigatorBar:NavigationBarView? = null
    private var formView: View? = null
    private var formViewType = 0
    private var formDialog:MyDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        indexViewPager = findViewById(R.id.index_view_pager)
        navigatorBar = findViewById(R.id.navigator)
        indexViewPager!!.offscreenPageLimit =3
        navigatorBar!!.setOperationButtonClickListener(this)
        navigatorBar!!.setNavigatorClickListener(this)
        indexViewPagerAdapter = IndexViewPagerAdapter(supportFragmentManager, indexFragmentInViewPagerList)
        indexViewPager!!.adapter = indexViewPagerAdapter
        indexViewPager!!.currentItem = 1
        (indexFragmentInViewPagerList[0] as TodayFragment).setOnDataRefresh(this)
    }

    //OperatorButton点击事件
    override fun onOperationButtonClick(position: Int) {
        when(position){
            2->{
                when(val currentPageNo = (indexFragmentInViewPagerList[2] as LibraryFragment).getCurrentPageNo()){
                    0->{
                        formViewType = 0
                        formView = View.inflate(this,R.layout.template_create_dialog,null)
                        formDialog = MyDialogFragment(2,Gravity.CENTER,1,formView!!)
                        formDialog!!.setConfirmButtonClickListener(this)
                        formDialog!!.show(supportFragmentManager,null)
                    }
                    else->{
                        formViewType = 1
                        val parser = resources.getXml(R.xml.base_linear_layout)
                        val attributes = Xml.asAttributeSet(parser)
                        formView = MuscleGroupItemAddFormView(this,attributes,currentPageNo,actionInfo = null)
                        (formView!! as MuscleGroupItemAddFormView).setSubmitListener(this)
                        formDialog = MyDialogFragment(1,Gravity.CENTER,1,formView!!)
                        formDialog!!.setConfirmButtonClickListener(this)
                        formDialog!!.show(supportFragmentManager,null)
                    }
                }
            }
            1->{
                val date=SelectedItemClass.getSelectedList()[0]
                when (GetMonthInfo.compareDate(date,GetMonthInfo.getTodayString())){
                    0->{

                    }
                    else->{
                        val intentToPlanDetailActivity = Intent(this@IndexActivity,PlanDetailActivity::class.java)
                        intentToPlanDetailActivity.putExtra("Date",date)
                        startActivity(intentToPlanDetailActivity)
                    }
                }
            }
            0->{
                (indexFragmentInViewPagerList[0] as TodayFragment).onOperatorClick()
            }
            4->{
                (indexFragmentInViewPagerList[0] as TodayFragment).onOperatorClick()
            }
        }
    }

    override fun onNavigatorClick(position: Int) {
        when(position){
            2->{
                (indexFragmentInViewPagerList[2] as LibraryFragment).updateActionAddTimes()
            }
            1->{
                (indexFragmentInViewPagerList[1] as PlanFragment).updateTodayDetail()
                (indexFragmentInViewPagerList[1] as PlanFragment).updateDefaultSelectedList()
            }
            0->{
                if (LibraryUpdateClass.checkTodayDataUpdateFlag()){
                    (indexFragmentInViewPagerList[0] as TodayFragment).dataRefresh()
                }else{
                    navigatorBar!!.resetOperationButtonInTodayPageClickFlag()
                }
            }
        }
        indexViewPager!!.setCurrentItem(position,false)
    }

    //息屏后点亮状态恢复
    override fun onRestart() {
        (indexFragmentInViewPagerList[1] as PlanFragment).fitCalendarReStart()
        super.onRestart()
    }

    override fun onConfirmButtonClick() {
        when(formViewType){
            0->{
                if (formView!=null){
                    val templateName = formView!!.findViewById<EditText>(R.id.template_name)
                    if (TextUtils.isEmpty(templateName!!.text)){
                        templateName.background = ContextCompat.getDrawable(this,R.drawable.incomplete_edit_text_background)
                        MyToast(this,resources.getString(R.string.form_incomplete)).showToast()
                        (this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(
                            VibrationEffect.createOneShot(400,4))
                    }else{
                        (indexFragmentInViewPagerList[2] as LibraryFragment).templateAdd(templateName.text.toString(),formDialog!!)
                    }
                }
            }
            1->{
                if (formView!=null){
                    (formView!! as MuscleGroupItemAddFormView).onConfirmButtonClick()
                }
            }
        }
    }

    //收藏添加动作
    override fun onSubmit(actionType:Int,action: Action) {
        (indexFragmentInViewPagerList[2] as LibraryFragment).actionAdd(actionType,action)
    }

    //TodayFragment的数据更新监听
    override fun onDataRefresh() {
        navigatorBar!!.todayDataRefresh()
    }

}