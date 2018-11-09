package com.xiaolei.okbook.Base

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.xiaolei.okbook.R
import java.util.*

/**
 * Created by xiaolei on 2018/1/2.
 */
abstract class RecyclerAdapter<T>(val list: LinkedList<T>) : RecyclerView.Adapter<BaseHolder>()
{
    private var onclick: ((view: View, position: Int) -> Unit)? = null
    private val listener by lazy {
        View.OnClickListener { v ->
            onclick?.invoke(v, v.getTag(R.id.recycleview_position) as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder
    {
        val holder = onCreate(parent, viewType)
        if (!holder.rootView.hasOnClickListeners())
        {
            holder.rootView.setOnClickListener(listener)
        }
        return holder
    }

    abstract fun onCreate(parent: ViewGroup, viewType: Int): BaseHolder
    abstract fun onBindView(holder: BaseHolder, position: Int)

    override fun onBindViewHolder(holder: BaseHolder, position: Int)
    {
        holder.rootView.setTag(R.id.recycleview_position, position)
        onBindView(holder, position)
    }

    override fun getItemCount() = list.size

    public fun onItemClick(block: (view: View, position: Int) -> Unit)
    {
        this.onclick = block
    }
}