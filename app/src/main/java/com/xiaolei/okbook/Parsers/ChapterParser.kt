package com.xiaolei.okbook.Parsers

import com.google.gson.reflect.TypeToken
import com.xiaolei.okbook.Bean.ChapterBean
import com.xiaolei.okbook.Gson.Gson
import com.xiaolei.okbook.Utils.Log
import org.jsoup.Jsoup
import java.util.*


/**
 * 章节目录的解析器
 */
object ChapterParser
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
            LinkedList()
        }
    }
    
    /**
     * 小说阅读网
     */
    private fun xsydw(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".catalog-content-wrap .cf li a")
            
            lis.forEach { li ->
                val bean = ChapterBean(name = li.html(), url = "https:${li.attr("href")}")
                list.add(bean)
            }
            
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return list
        }
    }
    
    /**
     * 潇湘书院
     */
    private fun xxsy(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        val jsoup = Jsoup.parse(html)
        try
        {
            val imgUrl = jsoup.select(".bookprofile img").first().attr("src")
            val bookId = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."))
            val chapterJsoup = Jsoup.connect("http://www.xxsy.net/partview/GetChapterList?bookid=$bookId").get()
            
            val lias = chapterJsoup.select(".catalog-list li a")
            lias.forEach { lia ->
                val bean = ChapterBean(name = lia.html(), url = "http://www.xxsy.net${lia.attr("href")}")
                list.add(bean)
            }
            
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return list
        }
    }
    
    /**
     * 红袖添香
     */
    private fun hxtx(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".catalog-content-wrap .cf li a")
            
            lis.forEach { li ->
                val bean = ChapterBean(name = li.html(), url = "https:${li.attr("href")}")
                list.add(bean)
            }
            
            jsoup.empty()
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return list
        }
    }
    
    /**
     * 言情小说吧
     */
    private fun yqxsb(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        try
        {
            val jsoup = Jsoup.parse(html)
            val lis = jsoup.select(".catalog-content-wrap .cf li a")
            
            lis.forEach { li ->
                val bean = ChapterBean(name = li.html(), url = "https:${li.attr("href")}")
                list.add(bean)
            }
            
            jsoup.empty()
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            return list
        }
    }
    
    /**
     * 起点中文网
     */
    private fun qdzww(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".catalog-content-wrap .cf li a")
            
            lis.forEach { li ->
                val bean = ChapterBean(name = li.html(), url = "https:${li.attr("href")}")
                list.add(bean)
            }
            if (lis.isEmpty())
            {
                list.addAll(qdzwwVip(html))
            }
            return list
        } catch (e: Exception) // 正常解析失败，则尝试解析VIP章节
        {
            Log.e("XIAOLEI", "解析失败，尝试VIP章节解析方式")
            return qdzwwVip(html)
        } finally
        {
            jsoup.empty()
        }
    }
    
    private fun qdzwwVip(html: String): LinkedList<ChapterBean>
    {
        val list = LinkedList<ChapterBean>()
        val jsoup = Jsoup.parse(html)
        try
        {
            val bookId = jsoup.select("#bookImg").attr("data-bid")
            
            val htm0 = Jsoup.connect("https://m.qidian.com/book/$bookId/catalog").get().html()
            val html2 = htm0.substring(htm0.indexOf("g_data.volumes"))
            val html3 = html2.substring(0, html2.indexOf("</script>"))
            val json = html3.substring(html3.indexOf("["), html3.lastIndexOf("]") + 1)
            val list2 = Gson.gson.fromJson<List<vome>>(json, object : TypeToken<List<vome>>()
            {}.type)
            val list3 = list2.filter { vome -> vome.vN.contains("正文", true) || vome.vN.contains("vip", true) }
            list3.forEach { vome ->
                vome.cs.forEach { charlog ->
                    list.add(ChapterBean(url = "https://read.qidian.com/chapter/$bookId/${charlog.id}", name = charlog.cN))
                }
            }
            
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return list
        }
    }
    
    class vome(var vN: String, var cs: List<charlog>)
    class charlog(var cN: String, var id: Int)
}