package com.xiaolei.okbook.Adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xiaolei.okbook.Base.BaseAdapter
import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.R

/**
 * Created by xiaolei on 2018/4/23.
 */
class IndexAdapter(val list: ArrayList<Book>) : BaseAdapter<Book, BaseAdapter.Holder>(list)
{
    private val requestOptions = RequestOptions().apply {
        error(R.drawable.icon_no_cover)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        val inflter = LayoutInflater.from(parent.context)
        val view = inflter.inflate(R.layout.item_market_index, parent, false)
        return Holder(view)
    }
    
    override fun onBindViewHolder(holder: Holder, data: Book, position: Int)
    {
        val icon = holder.get<ImageView>(R.id.icon)
        val title = holder.get<TextView>(R.id.title)
        val author = holder.get<TextView>(R.id.author)
        val desc = holder.get<TextView>(R.id.desc)
        
        Glide.with(holder.rootView)
                .load(data.icon)
                .apply(requestOptions)
                .into(icon)
        title.text = Html.fromHtml(data.bookName)
        author.text = data.tag
        desc.text = Html.fromHtml(data.desc)
    }
}