package com.example.zhangjie.fitnessflow.splash

import android.os.Bundle
import android.util.Xml
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.zhangjie.fitnessflow.R
import com.example.zhangjie.fitnessflow.library.LibraryFragment
import com.example.zhangjie.fitnessflow.library.library_child_fragments.MuscleGroupItemAddFormView
import com.example.zhangjie.fitnessflow.navigation_bar.NavigationBarView
import com.example.zhangjie.fitnessflow.plan.PlanFragment
import com.example.zhangjie.fitnessflow.utils_class.MyDialogFragment

class IndexActivity : AppCompatActivity(),NavigationBarView.OperationButtonClickListener,
    NavigationBarView.NavigatorClickListener,MyDialogFragment.ConfirmButtonClickListener{

    private var indexViewPager: ViewPagerScrollerFalse? = null
    private var indexFragmentInViewPagerList = FragmentInit.getIndexFragmentInViewPagerList()
    private var indexViewPagerAdapter:IndexViewPagerAdapter? = null
    private var navigatorBar:NavigationBarView? = null
    private var formView: View? = null
    private var formViewType = 0

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
    }

    //OperatorButton点击事件
    override fun onOperationButtonClick(position: Int) {
        when(position){
            2->{
                when(val currentPageNo = (indexFragmentInViewPagerList[2] as LibraryFragment).getCurrentPageNo()){
                    0->{
                        formViewType = 0
                    }
                    else->{
                        formViewType = 1
                        val parser = resources.getXml(R.xml.base_linear_layout)
                        val attributes = Xml.asAttributeSet(parser)
                        formView = MuscleGroupItemAddFormView(this,attributes,currentPageNo)
                        val formDialog = MyDialogFragment(Gravity.CENTER,1,formView!!)
                        formDialog.setConfirmButtonClickListener(this)
                        formDialog.show(supportFragmentManager,null)
                    }
                }
            }
        }
    }

    override fun onNavigatorClick(position: Int) {
        indexViewPager!!.setCurrentItem(position,false)
    }

    //息屏后点亮状态恢复
    override fun onRestart() {
        (indexFragmentInViewPagerList[1] as PlanFragment).fitCalendarReStart()
        super.onRestart()
    }

    override fun onConfirmButtonClick() {
        when(formViewType){
            1->{
                if (formView!=null){
                    (formView!! as MuscleGroupItemAddFormView).onConfirmButtonClick()
                }
            }
        }
    }


}