package com.xiaolei.okbook.RetrofitExt.common;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;


import com.xiaolei.okbook.RetrofitExt.Config;
import com.xiaolei.okbook.RetrofitExt.regist.MethodWrap;
import com.xiaolei.okbook.RetrofitExt.regist.ResponseBeanRegister;
import com.xiaolei.okbook.RetrofitExt.regist.ResponseBeanRegisterTable;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by xiaolei on 2017/7/9.
 */

public abstract class SICallBack<T> implements Callback<T> {
    public SoftReference<Context> context;
    private IFailEvent failEvent;

    public SICallBack(Context context) {
        this.context = new SoftReference<>(context);
        failEvent = SingleObjCache.getInstance().get(Config.fiedFailEventClass);
    }

    public SICallBack(Fragment fragment) {
        this(fragment.getActivity());
    }

    public SICallBack(android.support.v4.app.Fragment fragment) {
        this(fragment.getActivity());
    }

    public abstract void onSuccess(@NonNull T result) throws Exception;

    public void onFail(@NonNull Throwable t) {
        if (checkActivityFinish())
            return;
        if (context.get() != null) {
            t.printStackTrace();
            UnifiedFailEvent(t);
        }
    }

    public abstract void onFinally();

    public abstract void onCache(@NonNull T result) throws Exception;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (checkActivityFinish())
            return;
        try {
            if (response.isSuccessful()) {
                onNext(response.body(), response);
            } else {
                onFail(new IOException(response.code() + ""));
            }
        } catch (Exception e) {
            onFail(new IOException(e));
        } finally {
            onFinally();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (checkActivityFinish())
            return;
        try {
            onFail(t);
        } finally {
            onFinally();
        }
    }


    private void onNext(T bodyBean, Response<T> response) {
        if (checkActivityFinish())
            return;
        try {
            Class<? extends ResponseBeanRegister> regist = ResponseBeanRegisterTable.getInstance().getRegistValue(bodyBean);
            if (regist != null) {
                ResponseBeanRegister registObj = ResponseBeanRegisterTable.getInstance().getRegistObj(regist);
                if (registObj != null) {
                    String callback = registObj.filter(bodyBean);
                    if (callback != null && !callback.isEmpty()) {
                        MethodWrap methodWrap = registObj.getMethodWraps(callback);
                        Method method = methodWrap.getMethod();
                        if (method != null) {
                            Class paramTypes[] = method.getParameterTypes();//获取参数类型集合
                            Object objs[] = new Object[paramTypes.length];
                            for (int i = 0; i < paramTypes.length; i++) {
                                Class paramtype = paramTypes[i];
                                if (paramtype == Context.class)//如果写了Context参数，那么就将Context注入
                                {
                                    objs[i] = context.get();
                                } else if (paramtype.isInstance(bodyBean))//如果写了那个bean，那么将对象注入
                                {
                                    objs[i] = bodyBean;
                                } else {
                                    objs[i] = null;
                                }
                            }
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            method.invoke(registObj, objs);
                            if (methodWrap.getTag() != null
                                    && (((boolean.class.isInstance(methodWrap.getTag())) && (boolean) methodWrap.getTag())
                                    || ((Boolean.class.isInstance(methodWrap.getTag())) && (Boolean) methodWrap.getTag()))) // 标识为跳过下一步，才return跳过。
                            {
                                return;
                            }
                        }
                    }
                }
            }
            if ("from disk cache".equals(response.message())
                    || "from memory cache".equals(response.message()))//是来自缓存吗？？
            {
                onCache(bodyBean);//走缓存的Callback
            } else {
                onSuccess(bodyBean);//走成功的Callback
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查界面是否关闭了
     *
     * @return
     */
    private boolean checkActivityFinish() {
        Context mContext = context.get();
        if (mContext != null) {
            if (FragmentActivity.class.isInstance(mContext)) {
                FragmentActivity activity = (FragmentActivity) mContext;
                return activity.isFinishing();
            }

            if (Activity.class.isInstance(mContext)) {
                Activity activity = (Activity) mContext;
                return activity.isFinishing();
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 统一的错误处理方式
     */
    private void UnifiedFailEvent(Throwable e) {
        if (failEvent != null) {
            failEvent.onFail(this, e, context.get());
        }
    }
}
