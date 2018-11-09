package com.xiaolei.okbook.PopupWindows

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import com.xiaolei.okbook.R

class ShowDeletePop(val activity: Activity) : PopupWindow()
{
    private var activityParent: View = (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
    private val cancle_btn: Button
    private val delete_btn: Button
    private var ondelete: (() -> Unit)? = null

    init
    {
        contentView = activity.layoutInflater.inflate(R.layout.layout_delete_pop, null)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        this.animationStyle = R.style.anim_licai_detial_bottombar
        setBackgroundDrawable(BitmapDrawable(activity.resources, null as Bitmap?))
        setOnDismissListener {
            backgroundAlpha(1f)
        }
        delete_btn = contentView.findViewById(R.id.delete_btn)
        cancle_btn = contentView.findViewById(R.id.cancle_btn)
        delete_btn.setOnClickListener {
            try
            {
                ondelete?.invoke()
            } finally
            {
                dismiss()
            }
        }
        cancle_btn.setOnClickListener {
            dismiss()
        }
    }

    fun showOnBottom(ondelete: (() -> Unit)? = null)
    {
        this.ondelete = ondelete
        this.showAtLocation(activityParent, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0) //设置layout在PopupWindow中显示的位置
        backgroundAlpha(0.9f)
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    fun backgroundAlpha(bgAlpha: Float)
    {
        val lp = activity.window.attributes
        lp.alpha = bgAlpha
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity.window.attributes = lp
    }
}