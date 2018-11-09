package com.xiaolei.okbook.Base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

import java.util.LinkedList

/**
 * 统一的 FragmentPagerAdapter
 * Created by admin on 2017/7/19.
 */

class FragmentPagerAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) : android.support.v4.app.FragmentPagerAdapter(fm)
{
    private val pages = LinkedList<Fragment?>()

    override fun getItem(position: Int): Fragment?
    {
        var page: Fragment? = null
        if (pages.size > position)
        {
            page = pages[position]
            if (page != null)
            {
                return page
            }
        }
        while (position >= pages.size)
        {
            pages.add(null)
        }
        page = fragmentList[position]
        pages[position] = page
        return page
    }

    override fun getCount(): Int
    {
        return fragmentList.size
    }
}
