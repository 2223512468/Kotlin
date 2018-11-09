package com.xiaolei.okbook.Activitys

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.transition.Fade
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.xiaolei.okbook.Base.BaseActivity
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Base.FragmentPagerAdapter
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Exts.edit
import com.xiaolei.okbook.Exts.get
import com.xiaolei.okbook.Exts.gone
import com.xiaolei.okbook.Fragments.MarketFragment
import com.xiaolei.okbook.Fragments.BookShelfFragment
import com.xiaolei.okbook.LifeCycle
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.PermisstionUtil
import com.xiaolei.okbook.Exts.observeNotNull
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_guide_.*
import java.util.*

/**
 * Created by xiaolei on 2018/4/20.
 */
class MainActivity : BaseActivity()
{
    private val viewModel by lazy {
        getViewModel(MainViewModel::class.java)
    }
    private val sp by lazy {
        getSharedPreferences(Globals.config, Context.MODE_PRIVATE)
    }
    private val fragmentList = ArrayList<BaseV4Fragment>()
    private val adapter by lazy {
        FragmentPagerAdapter(supportFragmentManager, fragmentList)
    }
    private var clickTime: Long = 0

    private var animatorSet: AnimatorSet? = null

    private val randomStrArray = arrayOf("继续滑呀，还有~"
            , "努力，还有~"
            , "加油，快没了~"
            , "哼！还有呢、"
            , "继续啊~"
            , "还没完呢~")
    private val isFirstRun = "isFirstRun_MainActivity"
    override fun onCreate(savedInstanceState: Bundle?)
    {
        window.enterTransition = Fade().setDuration(300)
        window.exitTransition = Fade().setDuration(300)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initObj()
    {

    }

    override fun initData()
    {
        fragmentList.add(BookShelfFragment()) // 收藏夹
        val name_list = assets.list("support")
        name_list.forEach { name ->
            val fragment = MarketFragment()
            val bundle = Bundle()
            bundle.putString("support_name", name)
            fragment.arguments = bundle
            fragmentList.add(fragment)
        }
    }

    override fun initView()
    {

        val firstRun = sp.getBoolean(isFirstRun, true)
        if (firstRun)
        {
            view_guide.inflate()

            animatorSet = AnimatorSet()
            val slideAnimator = ObjectAnimator.ofFloat(slideRight, "translationX", 0f, -100f)
            slideAnimator.duration = 1000
            slideAnimator.repeatCount = -1
            val textAnimator = slideAnimator.clone().apply {
                target = tip_text
            }
            animatorSet?.play(slideAnimator)?.with(textAnimator)
            animatorSet?.start()
        }
    }

    override fun onResume()
    {
        animatorSet?.resume()
        super.onResume()
    }

    override fun onPause()
    {
        animatorSet?.pause()
        super.onPause()
    }

    override fun onDestroy()
    {
        animatorSet?.cancel()
        animatorSet = null
        super.onDestroy()
    }

    override fun setListener()
    {
        // 接受异步的消息，选择到哪个页面
        viewModel.current.observeNotNull(this, { current ->
            view_pager.currentItem = current
        })
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = radio_group.findViewById<RadioButton>(checkedId)
            val position = radio_group.indexOfChild(radioButton)
            view_pager.currentItem = position
        }
        view_pager.adapter = adapter
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener()
        {
            override fun onPageSelected(position: Int)
            {
                when (position)
                {
                    0 ->
                    {
                        radio_group.get(position).isChecked = true
                    }
                    else ->
                    {
                        val tip_text = findViewById<TextView>(R.id.tip_text)
                        val guide_content = findViewById<View>(R.id.guide_content)
                        tip_text?.let { tip_text ->
                            if (position == adapter.count - 1)
                            {
                                sp.edit {
                                    putBoolean(isFirstRun, false)
                                }
                                animatorSet?.cancel()
                                animatorSet = null
                                guide_content.gone()
                            } else
                            {
                                tip_text.text = randomStrArray[position]
                            }
                        }
                        val radioButton = radio_group.getChildAt(position) as? RadioButton?
                        radioButton?.isChecked = true
                    }
                }
            }
        })
    }

    override fun loadData()
    {
        radio_group.get(0).isChecked = true
        PermisstionUtil.checkPermisstion(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (System.currentTimeMillis() - clickTime <= 2 * 1000)
            {
                LifeCycle.finishAll()
                LifeCycle.exit()
            } else
            {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                clickTime = System.currentTimeMillis()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    class MainViewModel : ViewModel()
    {
        val current = MutableLiveData<Int>()
    }
}