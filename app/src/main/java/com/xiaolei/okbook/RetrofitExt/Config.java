package com.xiaolei.okbook.RetrofitExt;

import com.xiaolei.okbook.RetrofitExt.common.DefaultFailEvent;
import com.xiaolei.okbook.RetrofitExt.common.IFailEvent;
import com.xiaolei.okbook.RetrofitExt.regist.ResponseBeanRegister;
import com.xiaolei.okbook.RetrofitExt.regist.ResponseBeanRegisterTable;

/**
 * Created by xiaolei on 2017/12/13.
 */

public class Config
{
    /**
     * 设置统一的网络失败处理方式<br/>
     * 处理流程：onFail -> IFailEvent.onFail
     * 这里需要注意的是，如果你重写了SICallBack的onFail方法，并且不调用super.onFail，
     * 那么，则不会走统一的处理方式。
     * @param klass
     */
    public static void setFailedEventClass(Class<? extends IFailEvent> klass)
    {
        fiedFailEventClass = klass;
    }

    /**
     * 为你的ResponseBean注册一个路由
     * @param klass
     * @param registClass
     * @param <T>
     */
    public static  <T> void registResponseBean(Class<T> klass, Class<? extends ResponseBeanRegister<T>> registClass)
    {
        ResponseBeanRegisterTable.getInstance().regist(klass,registClass);
    }
    
    public static Class<? extends IFailEvent> fiedFailEventClass = DefaultFailEvent.class;
}
