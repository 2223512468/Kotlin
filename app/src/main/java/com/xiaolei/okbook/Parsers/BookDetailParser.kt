package com.xiaolei.okbook.Parsers

import org.jsoup.Jsoup

/**
 * 小说详情内容解析器
 */
object BookDetailParser
{
    fun parser(html: String, netName: String) = when (netName)
    {
        "小说阅读网" ->
        {
            xsydw(html)
        }
        "潇湘书院" ->
        {
            xxsy(html)
        }
        "红袖添香" ->
        {
            hxtx(html)
        }
        "言情小说吧" ->
        {
            yqxsb(html)
        }
        "起点中文网" ->
        {
            qdzww(html)
        }
        else ->
        {
            ""
        }
    }
    
    /**
     * 小说阅读网
     */
    private fun xsydw(html: String): String
    {
        val content = StringBuilder()
        val jsoup = Jsoup.parse(html)
        try
        {
            val headStr = jsoup.select(".main-text-wrap .text-head .j_chapterName").first().html()
            val contentStr = jsoup.select(".main-text-wrap .read-content").first().html()
            content.append(headStr).append("<br/><br/>").append(contentStr)
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return content.toString()
        }
    }
    
    /**
     * 潇湘书院
     */
    private fun xxsy(html: String): String
    {
        val content = StringBuilder()
        val jsoup = Jsoup.parse(html)
        try
        {
            val headStr = jsoup.select(".chapter-title").first().html()
            val contentStr = jsoup.select("#auto-chapter").first().html()
            content.append(headStr).append("<br/><br/>").append(contentStr)
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return content.toString()
        }
    }
    
    /**
     * 红袖添香
     */
    private fun hxtx(html: String): String
    {
        val content = StringBuilder()
        val jsoup = Jsoup.parse(html)
        try
        {
            val headStr = jsoup.select(".main-text-wrap .text-head .j_chapterName").first().html()
            val contentStr = jsoup.select(".main-text-wrap .read-content").first().html()
            content.append(headStr).append("<br/><br/>").append(contentStr)
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return content.toString()
        }
    }
    
    /**
     * 言情小说吧
     */
    private fun yqxsb(html: String): String
    {
        val content = StringBuilder()
        val jsoup = Jsoup.parse(html)
        try
        {
            val headStr = jsoup.select(".main-text-wrap .text-head .j_chapterName").first().html()
            val contentStr = jsoup.select(".main-text-wrap .read-content").first().html()
            content.append(headStr).append("<br/><br/>").append(contentStr)
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return content.toString()
        }
    }
    
    /**
     * 起点中文网
     */
    private fun qdzww(html: String): String
    {
        val content = StringBuilder()
        val jsoup = Jsoup.parse(html)
        try
        {
            val headStr = jsoup.select(".main-text-wrap .text-head .j_chapterName").first().html()
            val contentStr = jsoup.select(".main-text-wrap .read-content").first().html()
            content.append(headStr).append("<br/><br/>").append(contentStr)
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return content.toString()
        }
    }
}