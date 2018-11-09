package com.xiaolei.okbook.Bean

import java.io.Serializable

/**
 * Created by xiaolei on 2018/5/2.
 */
class IndexBook : Serializable
{
    var id: Int = 0
    // 链接
    var url:String = ""
    // 页总数
    var pageCount: Int = 1
    // 当前页
    var pageCurrent: Int = 1
    var list:ArrayList<Book> = arrayListOf()
}