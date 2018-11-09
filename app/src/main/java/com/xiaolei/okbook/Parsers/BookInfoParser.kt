package com.xiaolei.okbook.Parsers

import com.xiaolei.okbook.Bean.BookInfoBean
import org.jsoup.Jsoup

/**
 * 图书详情解析器
 * Created by xiaolei on 2018/4/28.
 */
object BookInfoParser
{

    fun parser(html: String, netName: String) = when (netName)
    {
        "小说阅读网" ->
        {
            xsydw(html, netName)
        }
        "潇湘书院" ->
        {
            xxsy(html, netName)
        }
        "红袖添香" ->
        {
            hxtx(html, netName)
        }
        "言情小说吧" ->
        {
            yqxsb(html, netName)
        }
        "起点中文网" ->
        {
            qdzww(html, netName)
        }
        else ->
        {
            BookInfoBean()
        }
    }.apply {
        this.netName = netName
    }


    /**
     * 小说阅读网
     */
    private fun xsydw(html: String, netName: String): BookInfoBean
    {
        val bean = BookInfoBean()
        val jsoup = Jsoup.parse(html)
        try
        {
            val bookinfoE = jsoup.select(".book-info")

            val bookName = bookinfoE.select("h1 em").first().html()
            val author = bookinfoE.select(".writer").first().html()
            val bookImg = jsoup.select("#bookImg img").first().attr("src")
            val tagE = bookinfoE.select(".tag").first().children()

            val tag = StringBuilder()

            tagE.forEach { t ->
                tag.append(t.html()).append(" ")
            }
            val wordCountE = bookinfoE.select(".total").first().children()

            val wordCStr = StringBuilder()

            wordCountE.forEach { e ->
                wordCStr.append(e.html()).append(" ")
            }

            val desc = bookinfoE.select(".intro").first().html()
            val catalogCount = jsoup.select("#J-catalogCount").html()


            bean.imgUrl = "https:${bookImg.replace("\r", "")}"
            bean.bookName = bookName
            bean.author = author
            bean.tag = tag.toString()
            bean.wordCount = wordCStr.toString()
            bean.desc = desc
            bean.catalogCount = catalogCount

        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return bean
        }
    }

    /**
     * 潇湘书院
     */
    private fun xxsy(html: String, netName: String): BookInfoBean
    {
        val bean = BookInfoBean()
        val jsoup = Jsoup.parse(html)
        try
        {
            val bookprofile = jsoup.select(".bookprofile").first()

            val bookImg = bookprofile.select("img").first().attr("src")
            val bookName = bookprofile.select(".title h1").first().html()
            val author = bookprofile.select(".title span a").first().html()
            val tagE = bookprofile.select(".sub-tags a")
            val tag = StringBuilder()
            tagE.forEach { e ->
                tag.append(e.html()).append(" ")
            }
            val wordCStr = bookprofile.selectFirst(".sub-data span em").html()
            val desc = jsoup.select(".introcontent").html()

            bean.imgUrl = "http:$bookImg"
            bean.bookName = bookName
            bean.author = author
            bean.tag = tag.toString()
            bean.wordCount = wordCStr.toString()
            bean.desc = desc

            val bookId = bookImg.substring(bookImg.lastIndexOf("/") + 1, bookImg.lastIndexOf("."))

            val document = Jsoup.connect("http://www.xxsy.net/partview/GetChapterList?bookid=$bookId").get()

            val countSize = document.select("#chapter li").size
            bean.catalogCount = "(${countSize}章)" // 获取不到章节总数

        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return bean
        }
    }

    /**
     * 红袖添香
     */
    private fun hxtx(html: String, netName: String): BookInfoBean
    {
        val bean = BookInfoBean()
        val jsoup = Jsoup.parse(html)
        try
        {

            val bookinfoE = jsoup.select(".book-info")

            val bookName = bookinfoE.select("h1 em").first().html()
            val author = bookinfoE.select(".writer").first().html()
            val bookImg = jsoup.select("#bookImg img").first().attr("src")
            val tagE = bookinfoE.select(".tag").first().children()

            val tag = StringBuilder()

            tagE.forEach { t ->
                tag.append(t.html()).append(" ")
            }
            val wordCountE = bookinfoE.select(".total").first().children()

            val wordCStr = StringBuilder()

            wordCountE.forEach { e ->
                wordCStr.append(e.html()).append(" ")
            }

            val desc = bookinfoE.select(".intro").first().html()
            val catalogCount = jsoup.select("#J-catalogCount").html()


            bean.imgUrl = "https:${bookImg.replace("\r", "")}"
            bean.bookName = bookName
            bean.author = author
            bean.tag = tag.toString()
            bean.wordCount = wordCStr.toString()
            bean.desc = desc
            bean.catalogCount = catalogCount

        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return bean
        }
    }

    /**
     * 言情小说吧
     */
    private fun yqxsb(html: String, netName: String): BookInfoBean
    {
        val bean = BookInfoBean()
        val jsoup = Jsoup.parse(html)
        try
        {


            val bookinfoE = jsoup.select(".book-info")

            val bookName = bookinfoE.select("h1 em").first().html()
            val author = bookinfoE.select(".writer").first().html()
            val bookImg = jsoup.select("#bookImg img").first().attr("src")
            val tagE = bookinfoE.select(".tag").first().children()

            val tag = StringBuilder()

            tagE.forEach { t ->
                tag.append(t.html()).append(" ")
            }
            val wordCountE = bookinfoE.select(".total").first().children()

            val wordCStr = StringBuilder()

            wordCountE.forEach { e ->
                wordCStr.append(e.html()).append(" ")
            }

            val desc = bookinfoE.select(".intro").first().html()
            val catalogCount = jsoup.select("#J-catalogCount").html()


            bean.imgUrl = "https:${bookImg.replace("\r", "")}"
            bean.bookName = bookName
            bean.author = author
            bean.tag = tag.toString()
            bean.wordCount = wordCStr.toString()
            bean.desc = desc
            bean.catalogCount = catalogCount


        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return bean
        }
    }

    /**
     * 起点中文网
     */
    private fun qdzww(html: String, netName: String): BookInfoBean
    {
        val bean = BookInfoBean()
        val jsoup = Jsoup.parse(html)
        try
        {

            val e_book_information = jsoup.select(".book-information")
            val e_book_info = e_book_information.select(".book-info")
            val e_content_nav_wrap = jsoup.select(".content-nav-wrap")
            val e_tag = e_book_info.select(".tag").first().children()
            val e_catalog_content_wrap = jsoup.select(".catalog-content-wrap").first()

            val bookImg = e_book_information.select(".book-img img").first().attr("src")
            val bookName = e_book_info.select("h1 em").first().html()
            val author = e_book_info.select("h1 a").first().html()
            val tag = StringBuilder()
            e_tag.forEach { e -> tag.append(e.html()).append(" ") }
            val intro = e_book_info.select(".intro").first().html()
            val catalogCount = e_content_nav_wrap.select("#J-catalogCount").html()
            val bookIntro = jsoup.select(".book-info-detail .book-intro").first().html()
            val wordCount = e_catalog_content_wrap.select(".count cite").first().html()


            bean.imgUrl = "https:${bookImg.replace("\r", "")}"
            bean.bookName = bookName
            bean.author = author
            bean.tag = tag.toString()
            bean.wordCount = wordCount
            bean.desc = intro
            bean.catalogCount = catalogCount


        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return bean
        }
    }
}