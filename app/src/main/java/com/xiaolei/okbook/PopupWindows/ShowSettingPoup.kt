package com.xiaolei.okbook.PopupWindows

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import com.xiaolei.okbook.R

/**
 * Created by xiaolei on 2018/5/8.
 */
class ShowSettingPoup(val activity: Activity) : PopupWindow()
{
    init
    {
        contentView = activity.layoutInflater.inflate(R.layout.poup_show_setting, null)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        this.animationStyle = R.style.anim_settring_
        setOnDismissListener {
            backgroundAlpha(1f)
        }
    }
    
    override fun showAsDropDown(anchor: View?)
    {
        super.showAsDropDown(anchor)
        backgroundAlpha(0.9f)
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private fun backgroundAlpha(bgAlpha: Float)
    {
        val lp = activity.window?.attributes
        lp?.alpha = bgAlpha
        activity.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity.window?.attributes = lp
    }
}