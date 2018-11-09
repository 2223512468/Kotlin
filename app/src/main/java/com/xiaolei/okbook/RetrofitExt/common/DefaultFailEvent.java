package com.xiaolei.okbook.RetrofitExt.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xiaolei.okbook.RetrofitExt.Config;


/**
 * 默认的统一的失败提示接口
 * Created by xiaolei on 2017/11/14.
 */
public class DefaultFailEvent implements IFailEvent
{
    private long lastToastTime = -1;
    private String klassName = DefaultFailEvent.class.getName();
    private String configKlassName = Config.class.getName();

    @Override
    public void onFail(SICallBack callBack, Throwable t, Context context)
    {
        Log.e("FailEvent", "发生异常回调处:" + klassName + "\n如果要修改，则可以调用:" + configKlassName + ".fiedFailEventClass=你的自己定义的统一失败处理方式。");
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastToastTime > 10 * 1000)// 超过十秒
        {
            Toast.makeText(context, "网络异常~请检查手机联网状态~", Toast.LENGTH_SHORT).show();
        }
    }
}
