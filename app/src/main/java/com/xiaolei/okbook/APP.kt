package com.xiaolei.okbook

import android.support.multidex.MultiDexApplication
import com.tencent.bugly.crashreport.CrashReport
import com.xiaolei.okbook.Keys.KeyUtil
import com.xiaolei.okbook.Nets.BaseNetCore


/**
 * Created by xiaolei on 2018/4/20.
 */
class APP : MultiDexApplication()
{
    override fun onCreate()
    {
        registerActivityLifecycleCallbacks(LifeCycle)
        BaseNetCore.initRetrofit(this)
        CrashReport.initCrashReport(this, KeyUtil.getBuglyAppId(), false)
        super.onCreate()
    }
}