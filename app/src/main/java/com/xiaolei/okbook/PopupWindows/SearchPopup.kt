package com.xiaolei.okbook.PopupWindows

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import com.xiaolei.okbook.Exts.hideKeyboard
import com.xiaolei.okbook.Exts.showKeyboard
import com.xiaolei.okbook.R

class SearchPopup(val activity: Activity?) : PopupWindow()
{
    private var search_et: EditText? = null
    private var search_btn: ImageView? = null
    private var onenter: ((str: String) -> Unit)? = null
    
    init
    {
        contentView = activity?.layoutInflater?.inflate(R.layout.layout_search, null)
        search_et = contentView.findViewById(R.id.search_et)
        search_et?.imeOptions = EditorInfo.IME_ACTION_SEARCH
        search_btn = contentView.findViewById(R.id.search_btn)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        setOnDismissListener {
            backgroundAlpha(1f)
            search_et?.hideKeyboard()
        }
        // 输入监听事件
        search_et?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                //处理事件  
                onenter?.invoke(search_et?.text?.toString()?.trim() ?: "")
            }
            false
        }
        // 搜索按钮
        search_btn?.setOnClickListener {
            onenter?.invoke(search_et?.text?.toString()?.trim() ?: "")
        }
    }
    
    fun show(view: View, onenter: ((str: String) -> Unit)? = null)
    {
        this.onenter = onenter
        this.showAsDropDown(view)
        search_et?.showKeyboard()
        backgroundAlpha(0.7f)
    }
    
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private fun backgroundAlpha(bgAlpha: Float)
    {
        val lp = activity?.window?.attributes
        lp?.alpha = bgAlpha
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity?.window?.attributes = lp
    }
}