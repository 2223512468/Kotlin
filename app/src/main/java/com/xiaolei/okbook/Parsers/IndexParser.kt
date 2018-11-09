package com.xiaolei.okbook.Parsers

import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.Bean.IndexBook
import org.jsoup.Jsoup

/**
 * 首页列表解析器
 * Created by xiaolei on 2018/4/28.
 */
object IndexParser
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
            IndexBook()
        }
    }

    /**
     * 小说阅读网
     */
    fun xsydw(html: String, netName: String): IndexBook
    {
        val bean = IndexBook()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".right-book-list ul li")
            lis.forEach { li ->
                val bookBean = Book()
                val img = li.select(".book-img a img").first().attr("src")
                val url = "https://www.readnovel.com" + li.select(".book-img a").first().attr("href")
                val bookInfo = li.select(".book-info")

                val name = bookInfo.select("h3 a").first().html()
                val author = bookInfo.select("h4 a").first().html()
                val tag = StringBuilder()
                val tags = bookInfo.select(".tag span")
                tags.forEach { tagE ->
                    tag.append(tagE.html()).append(" ")
                }
                val desc = bookInfo.select(".intro").html().trim()

                bookBean.icon = "https:$img"
                bookBean.bookName = name
                bookBean.tag = author + tag.toString()
                bookBean.desc = desc
                bookBean.url = url
                bookBean.netName = netName

                bean.list.add(bookBean)
            }

            val pageCount = jsoup.select("#page-container").attr("data-total")
            val pageCurrent = jsoup.select("#page-container").attr("data-page")

            bean.pageCount = try
            {
                pageCount.toInt()
            } catch (e: Exception)
            {
                2
            }
            bean.pageCurrent = try
            {
                pageCurrent.toInt()
            } catch (e: Exception)
            {
                1
            }
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
    fun xxsy(html: String, netName: String): IndexBook
    {
        val bean = IndexBook()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".result-list ul li")
            lis.forEach { li ->
                val bookBean = Book()

                val iconUrl = li.select(".book img").first().attr("data-src")
                val url = "http://www.xxsy.net" + li.select(".book").first().attr("href")
                val infoE = li.select(".info")

                val bookName = infoE.select("h4 a").first().html()
                val subtitleE = infoE.select(".subtitle")
                val desc = infoE.select(".detail").first().html()

                val name_IconE = subtitleE.select("a").first()
                val icon = name_IconE.select("i")
                icon.remove()
                val author = name_IconE.html()
                subtitleE.remove(subtitleE.select("a").first())
                val alist = subtitleE.select("a")
                val tags = StringBuffer()
                alist.forEach { a ->
                    tags.append(a.html()).append(" ")
                }

                bookBean.icon = "http:$iconUrl"
                bookBean.bookName = bookName
                bookBean.desc = desc
                bookBean.tag = author + tags.toString()
                bookBean.url = url
                bookBean.netName = netName

                bean.list.add(bookBean)
            }


            val pageE = jsoup.select(".pages")
            val prerPageE = pageE.select(".page-prev")
            prerPageE.remove()
            val nextPageE = pageE.select(".page-next")
            nextPageE.remove()
            val goE = pageE.select(".go")
            goE.remove()

            val pageCurrent = pageE.select(".active").first().html()
            val pageCount = pageE.select("a").last().html()


            bean.pageCurrent = pageCurrent.toInt()
            bean.pageCount = pageCount.toInt()

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
    fun hxtx(html: String, netName: String): IndexBook
    {
        val bean = IndexBook()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".right-book-list ul li")
            lis.forEach { li ->
                val bookBean = Book()
                val img = li.select(".book-img a img").first().attr("src")
                val url = "https://www.hongxiu.com" + li.select(".book-img a").first().attr("href")
                val bookInfo = li.select(".book-info")
                val name = bookInfo.select("h3 a").first().html()
                val author = bookInfo.select("h4 a").first().html()
                val tag = StringBuilder()
                val tags = bookInfo.select(".tag span")
                tags.forEach { tagE ->
                    tag.append(tagE.html()).append(" ")
                }
                val desc = bookInfo.select(".intro").html().trim()

                bookBean.icon = "https:$img"
                bookBean.bookName = name
                bookBean.tag = author + tag.toString()
                bookBean.url = url
                bookBean.desc = desc
                bookBean.netName = netName

                bean.list.add(bookBean)
            }
            val pageCount = jsoup.select("#page-container").attr("data-total")
            val pageCurrent = jsoup.select("#page-container").attr("data-page")

            bean.pageCount = pageCount.toInt()
            bean.pageCurrent = pageCurrent.toInt()
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
    fun yqxsb(html: String, netName: String): IndexBook
    {
        val bean = IndexBook()
        val jsoup = Jsoup.parse(html)
        try
        {
            val lis = jsoup.select(".right-book-list ul li")
            lis.forEach { li ->
                val bookBean = Book()
                val img = li.select(".book-img a img").first().attr("src")
                val url = "https://www.xs8.cn" + li.select(".book-img a").first().attr("href")
                val bookInfo = li.select(".book-info")

                val name = bookInfo.select("h3 a").first().html()
                val author = bookInfo.select("h4 a").first().html()
                val tag = StringBuilder()
                val tags = bookInfo.select(".tag span")
                tags.forEach { tagE ->
                    tag.append(tagE.html()).append(" ")
                }
                val desc = bookInfo.select(".intro").html().trim()

                bookBean.icon = "https:$img"
                bookBean.bookName = name
                bookBean.tag = author + tag.toString()
                bookBean.desc = desc
                bookBean.url = url
                bookBean.netName = netName

                bean.list.add(bookBean)
            }
            val pageCount = jsoup.select("#page-container").attr("data-total")
            val pageCurrent = jsoup.select("#page-container").attr("data-page")

            bean.pageCount = try
            {
                pageCount.toInt()
            } catch (e: Exception)
            {
                2
            }

            bean.pageCurrent = try
            {
                pageCurrent.toInt()
            } catch (e: Exception)
            {
                1
            }
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
    fun qdzww(html: String, netName: String): IndexBook
    {
        val result = IndexBook()
        val jsoup = Jsoup.parse(html)
        try
        {
            val elements = jsoup.select(".book-img-text ul li")
            if (elements.size == 0)
            {
                return result
            }
            val pageElement = jsoup.select("#page-container")
            val currentPage = pageElement.attr("data-page") // 当前页
            val maxPages = pageElement.attr("data-pagemax") // 最大页

            elements.forEach { element ->

                val imgurl = element.select(".book-img-box a img").first().attr("src") // 图片
                val url = "https:" + element.select(".book-img-box a").first().attr("href")
                val bookId = element.select(".book-img-box a").first().attr("data-bid")
                val book_mid_infoElement = element.select(".book-mid-info")
                val title = book_mid_infoElement.select("a").first().html() // 标题
                val authorElement = element.select(".author")
                val authorAs = authorElement.select("a")
                val author = authorElement.select(".name").first().html() // 作者
                val state = authorElement.select("span").first().html() // 连载中还是，已完结
                val intro = book_mid_infoElement.select(".intro").first().html() //  简介
                val typeStr = StringBuilder()

                authorAs.forEach { e ->
                    if ("name" != e.attr("class"))
                    {
                        typeStr.append(e.html()).append(" | ")
                    }
                }
                if (typeStr.endsWith(" | "))
                {
                    typeStr.delete(typeStr.length - 3, typeStr.length)
                }

                val bean = Book()


                bean.icon = "https:$imgurl"
                bean.bookName = title
                bean.tag = typeStr.toString()
                bean.desc = author + intro
                bean.url = url
                bean.netName = netName

                result.list.add(bean)
            }


            result.pageCount = try
            {
                maxPages.toInt()
            } catch (e: Exception)
            {
                1
            }

            result.pageCurrent = try
            {
                currentPage.toInt()
            } catch (e: Exception)
            {
                1
            }
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            jsoup.empty()
            return result
        }
    }
}