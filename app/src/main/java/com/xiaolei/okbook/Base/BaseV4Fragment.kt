package com.xiaolei.okbook.Base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseV4Fragment : Fragment()
{
    private lateinit var provider: ViewModelProvider
    private lateinit var contentView: View
    lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        if (!::contentView.isInitialized)
        {
            contentView = inflater.inflate(contentViewId(), null)
            provider = ViewModelProviders.of(this)
            mContext = contentView.context
            initView()
            setListeners()
            initData()
            loadData()
        }
        return contentView
    }

    fun <T : ViewModel> getViewModel(modelClass: Class<T>): T = provider.get(modelClass)

    override fun getView() = contentView
    abstract fun contentViewId(): Int
    abstract fun initView()
    abstract fun setListeners()
    abstract fun initData()
    abstract fun loadData()
}