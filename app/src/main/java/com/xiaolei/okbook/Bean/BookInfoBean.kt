package com.xiaolei.okbook.Bean

import java.io.Serializable

/**
 * @param bookName 书名
 * @param imgUrl 封面
 * @param author 作者
 * @param netName 网站名字
 * @param tag 标签
 * @param wordCount 总字数
 * @param catalogCount 总章节数
 * @param desc 简介
 */
class BookInfoBean(var bookName: String = ""
                   , var imgUrl: String = ""
                   , var author: String = ""
                   , var netName: String = ""
                   , var tag: String = ""
                   , var wordCount: String = ""
                   , var catalogCount: String = ""
                   , var desc: String = ""):Serializable