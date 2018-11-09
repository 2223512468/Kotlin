package com.xiaolei.okbook.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ReadPageAdapter(fm: FragmentManager, val list: List<out Fragment>) : FragmentPagerAdapter(fm)
{
    override fun getItem(position: Int) = list[position]
    
    override fun getCount() = list.size
}