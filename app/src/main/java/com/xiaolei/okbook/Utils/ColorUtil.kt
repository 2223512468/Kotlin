package com.xiaolei.okbook.Utils

import android.graphics.Color

/**
 * Created by xiaolei on 2018/5/7.
 */
object ColorUtil
{
    fun toHexEncoding(color: Int): String
    {
        var R: String
        var G: String
        var B: String
        val sb = StringBuffer()
        R = Integer.toHexString(Color.red(color))
        G = Integer.toHexString(Color.green(color))
        B = Integer.toHexString(Color.blue(color))
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = if (R.length == 1) "0$R" else R
        G = if (G.length == 1) "0$G" else G
        B = if (B.length == 1) "0$B" else B
        sb.append("#")
        sb.append(R)
        sb.append(G)
        sb.append(B)
        return sb.toString()
    }
}