package com.xiaolei.okbook.Nets


import android.content.Context
import com.xiaolei.okbook.BuildConfig
import com.xiaolei.okbook.Configs.Globals.SERVER_ADDRESS
import com.xiaolei.okbook.RetrofitExt.Config
import com.xiaolei.okbook.Utils.Log
import com.xiaolei.okhttputil.Cookie.CookieJar
import com.xiaolei.okhttputil.OkHttpUtilConfig
import com.xiaolei.okhttputil.interceptor.CacheInterceptor
import com.xiaolei.okhttputil.interceptor.SSessionInterceptor
import com.xiaolei.okjoke.Base.ResBodyBean
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


/**
 * Created by xiaolei on 2017/5/10.
 */

object BaseNetCore
{
    lateinit var okHttpClient: OkHttpClient
    fun initRetrofit(context: Context)
    {
        okHttpClient = OkHttpClient.Builder()
                .addInterceptor(CacheInterceptor(context, CacheInterceptor.Type.SQLITE))
                .addInterceptor(loggingInterceptor)
                .addInterceptor(SSessionInterceptor(mapOf(Pair("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"))))
                .retryOnConnectionFailure(true) //失败重连
                .connectTimeout(15, TimeUnit.SECONDS) //网络请求超时时间单位为秒
                .writeTimeout(15, TimeUnit.SECONDS) //写超时
                .readTimeout(15, TimeUnit.SECONDS) //读超时
                .cookieJar(CookieJar(context))
                .build()
        okHttpClient.dispatcher().maxRequestsPerHost = 16 // 每个主机最大请求数为16
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Log.d("HttpRetrofit", message + "")
        }
        ).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()  //01:获取Retrofit对象
                .baseUrl(SERVER_ADDRESS) //02采用链式结构绑定Base url
                .addConverterFactory(ScalarsConverterFactory.create()) //首先判断是否需要转换成字符串，简单类型
                .addConverterFactory(GsonConverterFactory.create()) //再将转换成bean
                .client(okHttpClient)
                .build() //03执行操作
    }

    init
    {
        OkHttpUtilConfig.DEBUG = BuildConfig.DEBUG
        Config.registResponseBean(ResBodyBean::class.java, ResBodyRegister::class.java)
        Config.fiedFailEventClass = OnFaileEvent::class.java
    }

    fun <T> create(klass: Class<T>): T = retrofit.create(klass)

}
