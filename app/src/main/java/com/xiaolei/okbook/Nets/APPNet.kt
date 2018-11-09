package com.xiaolei.okbook.Nets

import com.xiaolei.okhttputil.Catch.CacheHeaders
import retrofit2.Call
import retrofit2.http.*


/**
 * 网络所有的API
 * Created by xiaolei on 2017/12/7.
 */
interface APPNet
{
    /**
     * 带缓存的请求
     */
    @Headers(CacheHeaders.NORMAL)
    @GET
    fun getHtmlBy(@Url path: String): Call<String>
    
    
    /**
     * 缓存优先
     */
    @Headers(CacheHeaders.CACHE_FIRST)
    @GET
    fun getHtmlCacheFirst(@Url path: String): Call<String>
}