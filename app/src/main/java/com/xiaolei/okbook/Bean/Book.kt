package com.xiaolei.okbook.Bean

import java.io.Serializable

/**
 * 小说列表
 * Created by xiaolei on 2018/5/2.
 */
class Book : Serializable
{
    var id: Int = 0
    // 网站名称
    var netName: String = ""

    // 详情的url
    var url: String = ""
    // 封面
    var icon: String = ""
    // 书本名字
    var bookName: String = ""
    // tag
    var tag: String = ""
    // 简介
    var desc: String = ""
}