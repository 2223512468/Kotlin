package com.xiaolei.okbook.Nets

import android.content.Context
import android.widget.Toast
import com.xiaolei.okbook.RetrofitExt.common.IFailEvent
import com.xiaolei.okbook.RetrofitExt.common.SICallBack

/**
 * Created by xiaolei on 2017/12/18.
 */
class OnFaileEvent : IFailEvent
{
    private var lastTime = 0L
    override fun onFail(callBack: SICallBack<*>?, t: Throwable?, context: Context?)
    {
        context?.let {
            if (System.currentTimeMillis() - lastTime >= 10 * 1000)
            {
                Toast.makeText(context, "哇哦~网络异常了耶~", Toast.LENGTH_SHORT).show()
                lastTime = System.currentTimeMillis()
            }
        }
    }
}