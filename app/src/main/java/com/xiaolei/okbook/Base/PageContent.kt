package com.xiaolei.okjoke.Base

/**
 * 翻页所需要的
 * current : 1
 * size : 10
 * total : 113
 * pageCount : 12
 * list : []
 * Created by xiaolei on 2017/12/6.
 */
class PageContent<T>(var current: Int = 0,
                     var size: Int = 0,
                     var total: Int = 0,
                     var pageCount: Int = 1,
                     var list: List<T>)