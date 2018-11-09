package com.xiaolei.okbook.Base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.githang.statusbar.StatusBarCompat

/**
 * Created by xiaolei on 2018/4/20.
 */
abstract class BaseActivity : AppCompatActivity()
{
    private var hasPostCreate = false
    private var hasResume = false
    private lateinit var provider: ViewModelProvider
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        setStatusBar(true)
        super.onCreate(savedInstanceState)
        provider = ViewModelProviders.of(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState)
        if (!hasPostCreate)
        {
            initView()
            setListener()
            hasPostCreate = true
        }
    }

    override fun setContentView(view: View)
    {
        initObj()
        initData()
        super.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams)
    {
        initObj()
        initData()
        super.setContentView(view, params)
    }

    override fun setContentView(layoutResID: Int)
    {
        initObj()
        initData()
        super.setContentView(layoutResID)
    }

    override fun onResume()
    {
        super.onResume()
        if (!hasResume)
        {
            loadData()
            hasResume = true
        }
    }

    fun <T : ViewModel> getViewModel(modelClass: Class<T>): T = provider.get(modelClass)

    fun setStatusBar(lightStatusBar: Boolean)
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
            StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, lightStatusBar)
        }
    }

    abstract fun initObj()

    abstract fun initData()

    abstract fun initView()

    abstract fun setListener()

    abstract fun loadData()
}