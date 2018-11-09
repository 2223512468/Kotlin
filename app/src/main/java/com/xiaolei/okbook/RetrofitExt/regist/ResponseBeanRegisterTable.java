package com.xiaolei.okbook.RetrofitExt.regist;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注册表，关于
 * 网络请求得到的数据，与解析结果的执行器
 * Created by xiaolei on 2017/7/12.
 */

public class ResponseBeanRegisterTable
{
    private static ResponseBeanRegisterTable instance;
    private Map<Class, Class<? extends ResponseBeanRegister>> map = new ArrayMap<>();
    private Map<Class<? extends ResponseBeanRegister>, Object> registObj = new ArrayMap<>();

    private ResponseBeanRegisterTable()
    {
    }

    public static synchronized ResponseBeanRegisterTable getInstance()
    {
        if (instance == null)
        {
            instance = new ResponseBeanRegisterTable();
        }
        return instance;
    }

    public <T> void regist(Class<T> klass, Class<? extends ResponseBeanRegister<T>> registClass)
    {
        Object object = null;
        try
        {
            object = registClass.newInstance();
            map.put(klass, registClass);
            registObj.put(registClass, object);
        } catch (Exception e)
        {
            new Exception(registClass + "类需要至少一个Public无参构造函数").printStackTrace();
        }
    }

    public Class[] getRegistKeys()
    {
        List<Class> list = new ArrayList<>();
        for (Map.Entry<Class, Class<? extends ResponseBeanRegister>> entry : map.entrySet())
        {
            list.add(entry.getKey());
        }
        return list.toArray(new Class[list.size()]);
    }

    public Class<? extends ResponseBeanRegister> getRegistValue(Object object)
    {
        for (Map.Entry<Class, Class<? extends ResponseBeanRegister>> entry : map.entrySet())
        {
            Class key = entry.getKey();
            if (key.isInstance(object))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    public ResponseBeanRegister getRegistObj(Class<? extends ResponseBeanRegister> klass)
    {
        return (ResponseBeanRegister) registObj.get(klass);
    }
}
