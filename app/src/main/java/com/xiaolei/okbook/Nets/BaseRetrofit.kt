package com.xiaolei.okbook.Nets


/**
 * Created by xiaolei on 2017/3/1.
 */

object BaseRetrofit
{
    fun <T> create(klass: Class<T>): T
    {
        return BaseNetCore.create(klass)
    }

    /**
     * COOKIE的操作类
     */
    enum class COOKIE
    {
        INSTANCE;
    }
}
