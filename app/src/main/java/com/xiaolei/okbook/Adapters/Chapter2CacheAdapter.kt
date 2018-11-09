package com.xiaolei.okbook.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.xiaolei.okbook.Base.BaseAdapter
import com.xiaolei.okbook.Activitys.Chapter2CacheActivity.ChapterWrap
import com.xiaolei.okbook.Exts.gone
import com.xiaolei.okbook.Exts.show
import com.xiaolei.okbook.R
import java.util.*

/**
 * Created by xiaolei on 2018/5/4.
 */
class Chapter2CacheAdapter(list: LinkedList<ChapterWrap>) : BaseAdapter<ChapterWrap, BaseAdapter.Holder>(list)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chapter_cache, parent, false)
        return Holder(view)
    }
    
    override fun onBindViewHolder(holder: Holder, data: ChapterWrap, position: Int)
    {
        val chapter = data.chapter
        
        val chapterName = holder.get<TextView>(R.id.chapter_name)
        val has_been_cached = holder.get<TextView>(R.id.has_been_cached)
        val checkbox = holder.get<CheckBox>(R.id.checkbox)
        
        
        if (data.isCached)
        {
            has_been_cached.show()
            checkbox.gone()
        } else
        {
            has_been_cached.gone()
            checkbox.show()
        }
        
        
        checkbox.isChecked = data.isChecked
        chapterName.text = chapter.name
    }
}