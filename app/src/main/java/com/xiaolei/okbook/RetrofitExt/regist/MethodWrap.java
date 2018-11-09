package com.xiaolei.okbook.RetrofitExt.regist;

import java.lang.reflect.Method;

/**
 * Created by xiaolei on 2017/12/15.
 */

public class MethodWrap
{
    public MethodWrap(Method method)
    {
        this.method = method;
    }

    private Method method;
    private Object tag;

    public void setMethod(Method method)
    {
        this.method = method;
    }

    public void setTag(Object tag)
    {
        this.tag = tag;
    }

    public Method getMethod()
    {
        return method;
    }

    public Object getTag()
    {
        return tag;
    }
}
