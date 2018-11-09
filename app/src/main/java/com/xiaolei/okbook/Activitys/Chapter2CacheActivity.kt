package com.xiaolei.okbook.Activitys

import android.os.Bundle
import android.widget.Toast
import com.xiaolei.okbook.Adapters.Chapter2CacheAdapter
import com.xiaolei.okbook.Base.BaseActivity
import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.Bean.ChapterBean
import com.xiaolei.okbook.Exts.enqueue
import com.xiaolei.okbook.Exts.show
import com.xiaolei.okbook.Nets.APPNet
import com.xiaolei.okbook.Nets.BaseRetrofit
import com.xiaolei.okbook.Parsers.ChapterParser
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.Log
import com.xiaolei.okhttputil.Catch.CacheImpl.SqliteCacheImpl
import com.xiaolei.smartpull2layout.SmartLoadingLayout
import kotlinx.android.synthetic.main.activity_chapter_cache.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * 选择缓存章节
 * Created by xiaolei on 2018/5/4.
 */
class Chapter2CacheActivity : BaseActivity()
{
    private val cache by lazy {
        SqliteCacheImpl.getInstance(File(cacheDir, "ResponseCache"), this)
    }
    private val data by lazy {
        intent.getSerializableExtra("data") as? Book
    }
    private val appNet by lazy { BaseRetrofit.create(APPNet::class.java) }

    private val loadingLayout by lazy {
        SmartLoadingLayout.createDefaultLayout(this, listview)
    }
    private val list = LinkedList<ChapterWrap>()
    private val adapter = Chapter2CacheAdapter(list)

    private val checkAllText = "全选"
    private val uncheckAllText = "反选"
    private var isCaching = false // 是否正在缓存

    override fun onCreate(savedInstanceState: Bundle?)
    {
        setContentView(R.layout.activity_chapter_cache)
        super.onCreate(savedInstanceState)
    }

    override fun initObj()
    {

    }

    override fun initData()
    {
        Log.e("XIAOLEI", "infoUrl:${data?.url}")
    }

    override fun initView()
    {
        app_title.setTitleText(data?.bookName ?: "")
        loadingLayout.onLoading()
    }

    override fun setListener()
    {
        app_title.setOnLeftImageClick { finish() }
        app_title.setOnRightTextClick {
            if (isCaching)
            {
                return@setOnRightTextClick
            }
            when (app_title.getRightText())
            {
                checkAllText ->
                {
                    // 执行全选
                    list.forEach { wrap ->
                        wrap.isChecked = true
                    }
                    app_title.setRightText(uncheckAllText)
                }
                uncheckAllText ->
                {
                    // 执行全选
                    list.forEach { wrap ->
                        wrap.isChecked = false
                    }
                    app_title.setRightText(checkAllText)
                }
            }
            adapter.notifyDataSetChanged()
        }
        listview.adapter = adapter
        listview.setOnItemClickListener { parent, view, position, id ->
            if (isCaching)
            {
                return@setOnItemClickListener
            }
            val wrap = adapter.getItem(position)
            wrap.isChecked = !wrap.isChecked
            adapter.notifyDataSetChanged()
            start_cache_btn.text = "开始缓存"
        }

        start_cache_btn.setOnClickListener {
            // 过滤出任务列表，没有被缓存，并且，被勾选了
            val downloadList = list.filter { bean -> !bean.isCached && bean.isChecked }
            start_cache_btn.isClickable = false // 下载期间不许点击
            launch(UI) {
                try
                {
                    var downloadCount = 0
                    isCaching = true
                    start_cache_btn.text = "$downloadCount/${downloadList.size} 正在缓存"
                    downloadList.forEach { bean ->
                        val call = appNet.getHtmlCacheFirst(bean.chapter.url)
                        val response = async { call.execute() }.await()
                        if (response.isSuccessful)
                        {
                            val str = response.body()

                            downloadCount++
                            if (downloadCount >= downloadList.size)
                            {
                                start_cache_btn.text = "缓存完成"
                            } else
                            {
                                start_cache_btn.text = "$downloadCount/${downloadList.size} 正在缓存"
                            }
                            bean.isCached = true
                            adapter.notifyDataSetChanged()
                        }
                    }

                } finally
                {
                    start_cache_btn.isClickable = true
                    isCaching = false
                }
            }
        }
    }

    override fun loadData()
    {
        val infoUrl = data?.url

        infoUrl?.let {
            app_title.setRightText(checkAllText)
            val call = appNet.getHtmlBy(infoUrl)
            call.enqueue(this, ::onSuccess, {
                loadingLayout.onDone()
            })
        }
        infoUrl ?: let {
            loadingLayout.onDone()
        }
    }


    private fun onSuccess(html: String)
    {
        launch(UI) {
            val async = async { ChapterParser.parser(html, data!!.netName) }
            val chapterList = async.await()
            list.addAll(chapterList.map {
                ChapterWrap(it, isCached = cache.containsKey("${it.url}?"))
            })
            adapter.notifyDataSetChanged()
            start_cache_btn.show()
        }
    }

    /**
     * 是否被勾选
     */
    class ChapterWrap(val chapter: ChapterBean, var isChecked: Boolean = false, var isCached: Boolean = false)

}