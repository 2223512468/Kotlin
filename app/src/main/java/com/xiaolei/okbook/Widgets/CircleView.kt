package com.xiaolei.okbook.Widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.xiaolei.okbook.R

class CircleView @JvmOverloads constructor(con: Context, attr: AttributeSet? = null, styAttr: Int = 0) : View(con, attr, styAttr)
{
    var mHeight: Float = 0f
    var mWidth: Float = 0f
    val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.MAGENTA
        isAntiAlias = true
    }

    init
    {
        val array = context.obtainStyledAttributes(attr, R.styleable.CircleView)
        val defColor = array.getColor(R.styleable.CircleView_color, Color.MAGENTA)
        paint.color = defColor
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setColor(color: Int)
    {
        paint.color = color
        invalidate()
    }
    fun getColor():Int = paint.color
    
    override fun onDraw(canvas: Canvas)
    {
        canvas.drawCircle(mWidth / 2, mHeight / 2, Math.min(mHeight, mWidth) / 2, paint)
    }
}