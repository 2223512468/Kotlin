package com.xiaolei.okbook.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.xiaolei.okbook.Base.BaseAdapter
import com.xiaolei.okbook.Bean.ChapterBean
import com.xiaolei.okbook.Exts.gone
import com.xiaolei.okbook.Exts.resColor
import com.xiaolei.okbook.R
import java.util.*

/**
 * Created by xiaolei on 2018/4/27.
 */
class BookChapterAdapter(list: LinkedList<ChapterBean>) : BaseAdapter<ChapterBean, BaseAdapter.Holder>(list)
{
    var position = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bookchapter, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, data: ChapterBean, position: Int)
    {
        val bookName = holder.get<TextView>(R.id.bookName)
        val title_time = holder.get<TextView>(R.id.title_time)
        val title_wordnum = holder.get<TextView>(R.id.title_wordnum)

        title_time.gone()
        title_wordnum.gone()

        bookName.text = data.name

        if (this.position == position)
        {
            bookName.setTextColor(bookName.resColor(R.color.main_color))
        } else
        {
            bookName.setTextColor(bookName.resColor(R.color.main_text_color))
        }

    }
}