package com.xiaolei.okbook.Bean

import java.io.Serializable

/**
 * @param name 网站名称
 * @param url 网站链接
 * @param icon 网站icon
 * @param pageKey 翻页关键字
 * @param entry 几种性别
 * @param searchUrl 搜索链接
 * @param searchKey 搜索关键字
 * Created by xiaolei on 2018/4/27.
 */
class SupportBean(var name: String
                  , var url: String
                  , var icon: String
                  , var searchUrl: String? = null
                  , var searchKey: String? = null
                  , var pageKey: String
                  , var entry: List<SupportBeanEntry>) : Serializable

class SupportBeanEntry(var gender: String
                       , var desc: String
                       , var rootUrl: String
                       , var params: List<SupportBeanEntryParam>) : Serializable

class SupportBeanEntryParam(var name: String
                            , var key: String
                            , var enum: List<SupportBeanEntryParamEnum>) : Serializable

class SupportBeanEntryParamEnum(var value: String, var name: String) : Serializable