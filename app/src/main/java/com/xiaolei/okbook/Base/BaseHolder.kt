package com.xiaolei.okbook.Base

import android.support.annotation.IdRes
import android.support.v4.util.ArrayMap
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by xiaolei on 2017/12/22.
 */
class BaseHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)
{
    private val arraymap = ArrayMap<Int, View>()
    fun <T : View> get(@IdRes id: Int): T
    {
        var view:View? = arraymap[id]
        view?:let {
            val findView = rootView.findViewById<View>(id)
            arraymap.put(id,findView)
            view = findView
        }
        return view!! as T
    }
}