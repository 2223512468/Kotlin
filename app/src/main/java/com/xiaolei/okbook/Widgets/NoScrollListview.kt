package com.xiaolei.okbook.Widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ListView

/**
 * 禁止滚动的ListView
 * Created by Administrator on 2015/10/21 0021.
 */
class NoScrollListview(context: Context, attrs: AttributeSet) : ListView(context, attrs)
{
    /**
     * 设置不滚动
     */
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
    
}