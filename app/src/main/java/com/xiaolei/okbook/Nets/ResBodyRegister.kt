package com.xiaolei.okbook.Nets

import com.xiaolei.okbook.RetrofitExt.regist.ResponseBeanRegister
import com.xiaolei.okjoke.Base.ResBodyBean

/**
 * Created by xiaolei on 2017/7/21.
 */

class ResBodyRegister : ResponseBeanRegister<ResBodyBean<*>>()
{
    override fun filter(data: ResBodyBean<*>): String?
    {
        return data.callback
    }
}
