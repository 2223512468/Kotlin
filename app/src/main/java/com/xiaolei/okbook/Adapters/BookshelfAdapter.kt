package com.xiaolei.okbook.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xiaolei.okbook.Base.BaseAdapter
import com.xiaolei.okbook.Entitys.BookShelfBean
import com.xiaolei.okbook.Exts.gone
import com.xiaolei.okbook.Exts.show
import com.xiaolei.okbook.R
import java.util.*

/**
 * Created by xiaolei on 2018/4/26.
 */
class BookshelfAdapter(val list: LinkedList<BookShelfBean>) : BaseAdapter<BookShelfBean, BaseAdapter.Holder>(list)
{
    private val requestOption = RequestOptions().apply {
        this.error(R.drawable.icon_no_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bookshelf, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, data: BookShelfBean, position: Int)
    {
        val data_content = holder.get<View>(R.id.data_content)
        val add_btn = holder.get<View>(R.id.add_btn)
        add_btn.gone()
        data_content.gone()

        if (data.id == -1)
        {
            add_btn.show()

        } else
        {
            data_content.show()
            val bookName = holder.get<TextView>(R.id.bookName)
            val imgUrl = holder.get<ImageView>(R.id.imgUrl)
            bookName.text = data.bookName

            val bookIcon = if (data.bookIcon?.startsWith("http") == true)
            {
                data.bookIcon
            } else
            {
                "http:${data.bookIcon}"
            }

            Glide.with(holder.rootView)
                    .load(bookIcon)
                    .apply(requestOption)
                    .into(imgUrl)
        }
    }
}