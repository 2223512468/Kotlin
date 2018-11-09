package com.xiaolei.okjoke.Base

/**
 * status : OK
 * message : 处理成功或失败所对应的提示信息
 * url : 需要后续跳转的URL
 * callback : 需要后续执行的操作名称
 * content : 请求所需的接口输出数据
 * remark : 备注信息
 * Created by xiaolei on 2017/12/6.
 */
class ResBodyBean<T>(var status: String = "",
                     var message: String = "",
                     var url: String = "",
                     var callback: String = "",
                     var content: T? = null,
                     var remark: String = "")
{
    fun isOk(): Boolean = "OK" == status
}
