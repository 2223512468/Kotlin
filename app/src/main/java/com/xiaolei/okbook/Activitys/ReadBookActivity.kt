package com.xiaolei.okbook.Activitys

import com.xiaolei.okbook.Base.BaseReadBookActivity
import com.xiaolei.okbook.Bean.ChapterBean
import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.Exts.enqueue
import com.xiaolei.okbook.Nets.APPNet
import com.xiaolei.okbook.Nets.BaseRetrofit
import com.xiaolei.okbook.Parsers.BookDetailParser
import com.xiaolei.okbook.Parsers.ChapterParser
import com.xiaolei.okbook.Utils.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*

/**
 * 小说阅读界面
 * Created by xiaolei on 2018/4/25.
 */
class ReadBookActivity : BaseReadBookActivity()
{
    private val pageNet by lazy {
        BaseRetrofit.create(APPNet::class.java)
    }
    private var chapterList: LinkedList<ChapterBean>? = null
    private val data by lazy { intent.getSerializableExtra("data") as? Book? }
    
    /**
     * 获取书本目录列表
     */
    override fun getChapterList(success: (List<ChapterBean>) -> Unit)
    {
        chapterList?.let {
            success(it)
        }
        chapterList ?: let {
            data?.let { data ->
                
                // 加载缓存
                val cacheCall = pageNet.getHtmlCacheFirst(data.url)
                cacheCall.enqueue(this, onSuccess = {
                    if (chapterList != null) return@enqueue
                    launch(UI) {
                        // 解析网络
                        val async = async { ChapterParser.parser(it, data.netName) }
                        chapterList = async.await()
                        // 再回调  
                        success(chapterList!!)
                        Log.e("XIAOLEI", "取到缓存")
                    }
                })
                
                // 进行网络获取
                val call = pageNet.getHtmlBy(data.url)
                call.enqueue(this, onSuccess = {
                    launch(UI) {
                        // 解析网络
                        val async = async { ChapterParser.parser(it, data.netName) }
                        val tmpList = async.await()
                        if (chapterList != null) //已经取到缓存了
                        {
                            if (chapterList!!.size != tmpList.size) // 章节数改变了
                            {
                                Log.e("XIAOLEI", "章节数已经改变了")
                                chapterList = tmpList
                                success(chapterList!!)
                            }
                        } else
                        {
                            Log.e("XIAOLEI", "没有取到缓存,加载最新的")
                            chapterList = tmpList
                            success(chapterList!!)
                        }
                    }
                })
            }
        }
    }
    
    /**
     * 获取书本详情的界面链接
     */
    override fun getInfoUrl() = data?.url ?: ""
    
    /**
     * @param url URL的链接
     * @param onsuccess 成功的回调
     */
    override fun getTextBy(url: String, onsuccess: (html: String, url: String) -> Unit)
    {
        Log.e("加载章节", url)
        val call = pageNet.getHtmlCacheFirst(url)
        call.enqueue(this, onSuccess = { html ->
            data?.let { data ->
                launch(UI) {
                    val async = async { BookDetailParser.parser(html, data.netName) }
                    val content = async.await()
                    onsuccess(content, url)
                }
            }
        })
    }
}