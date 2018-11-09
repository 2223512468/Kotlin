package com.xiaolei.okbook.Utils

import android.app.Fragment
import android.content.Context

/**
 * 信号栏工具类
 * Created by xiaolei on 2017/8/17.
 */

object StateBarUtil
{
    /**
     * 获取信号栏高度
     * @return
     */
    fun getStateBarHeigh(context: Context): Int
    {
        var statusBarHeight1 = 0
        //获取status_bar_height资源的ID  
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
        {
            //根据资源ID获取响应的尺寸值  
            statusBarHeight1 = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight1
    }

    /**
     * 获取信号栏高度
     * @return
     */
    fun getStateBarHeigh(fragment: Fragment): Int
    {
        var statusBarHeight1 = 0
        //获取status_bar_height资源的ID  
        val resourceId = fragment.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
        {
            //根据资源ID获取响应的尺寸值  
            statusBarHeight1 = fragment.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight1
    }

    /**
     * 获取信号栏高度
     * @return
     */
    fun getStateBarHeigh(fragment: android.support.v4.app.Fragment): Int
    {
        var statusBarHeight1 = 0
        //获取status_bar_height资源的ID  
        val resourceId = fragment.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
        {
            //根据资源ID获取响应的尺寸值  
            statusBarHeight1 = fragment.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight1
    }
}
