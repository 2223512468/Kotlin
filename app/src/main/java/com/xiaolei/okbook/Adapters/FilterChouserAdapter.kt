package com.xiaolei.okbook.Adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.xiaolei.okbook.Bean.SupportBeanEntryParamEnum
import com.xiaolei.okbook.R

import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import java.util.*

open class FilterChouserAdapter(val datas: List<SupportBeanEntryParamEnum>) : TagAdapter<SupportBeanEntryParamEnum>(datas)
{
    override fun getView(parent: FlowLayout, position: Int, t: SupportBeanEntryParamEnum): View
    {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_filter_qidian_index, parent, false) as TextView
        view.text = t.name
        return view
    }
}
