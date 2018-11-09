package com.xiaolei.okbook.Widgets

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.xiaolei.okbook.Adapters.ReadPageAdapter
import com.xiaolei.okbook.Utils.Log

class AutoViewPager @JvmOverloads constructor(context: Context, attrt: AttributeSet?) : ViewPager(context, attrt)
{
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        adapter?.let {
            val ada = adapter as? ReadPageAdapter
            val fragment = ada?.getItem(currentItem)
            val childView = fragment?.view
            childView?.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            setMeasuredDimension(measuredWidth, childView?.measuredHeight ?: measuredHeight)
        }
    }
    
    override fun onTouchEvent(ev: MotionEvent?) = false
    override fun onInterceptTouchEvent(ev: MotionEvent?) = false
}