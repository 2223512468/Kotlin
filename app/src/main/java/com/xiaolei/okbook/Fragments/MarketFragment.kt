package com.xiaolei.okbook.Fragments

import android.app.ActivityOptions
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.widget.DrawerLayout
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.widget.AbsListView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.xiaolei.okbook.Activitys.BookInfoActivity
import com.xiaolei.okbook.Adapters.IndexAdapter
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Bean.SupportBean
import com.xiaolei.okbook.Bean.IndexBook
import com.xiaolei.okbook.Exts.*
import com.xiaolei.okbook.Gson.Gson
import com.xiaolei.okbook.Nets.APPNet
import com.xiaolei.okbook.Nets.BaseRetrofit
import com.xiaolei.okbook.Parsers.IndexParser
import com.xiaolei.okbook.PopupWindows.SearchPopup
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.Log
import com.xiaolei.smartpull2layout.SmartLoadingLayout
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder

/**
 * 小说列表首页
 */
class MarketFragment : BaseV4Fragment()
{
    private val target by lazy {
        object : SimpleTarget<Drawable>()
        {
            override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?)
            {
                app_title.titleTextview.draw(drawable, Position.RIGHT)
            }
        }
    }
    private val viewModel by lazy {
        getViewModel(MarketViewModel::class.java)
    }

    private val adater = IndexAdapter(arrayListOf())
    private var clearData = true // 是否清理数据

    private val searchPoup by lazy {
        SearchPopup(activity)
    }

    lateinit var supportBean: SupportBean
    private val loadingLayout by lazy {
        SmartLoadingLayout.createDefaultLayout(activity, refresh_layout)
    }
    // 是否正在加载
    private var isLoading = false
    
    override fun contentViewId(): Int = R.layout.fragment_market
    override fun initView()
    {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun setListeners()
    {
        listview.adapter = adater

        app_title.setOnRightTextClick {
            drawer_layout.openDrawer(Gravity.END)
        }
        // 调用搜索
        app_title.setOnLeftImageClick {
            viewModel.supportBean.value?.searchUrl?.let { searchUrl ->
                // 在菜单栏下面显示搜索框
                searchPoup.show(app_title) { word ->
                    searchByWord(word, searchUrl)
                }
                return@setOnLeftImageClick
            }
        }

        viewModel.supportBean.observe(this, Observer {
            app_title.setTitleText(supportBean.name)
            Glide.with(this)
                    .load(supportBean.icon)
                    .into(target)
            if (supportBean.searchUrl != null) // 有搜索服务，显示搜索图标，没有则显示浏览器图标
            {
                app_title.setLeftImage(R.drawable.icon_search)
            } else
            {
                app_title.setLeftImageVisible(View.GONE)
            }
        })

        viewModel.url.observe(this, Observer { url ->
            url?.let {
                isLoading = true
                viewModel.loadNet(url, supportBean.name)
            }
        })

        refresh_layout.onPull(onPullUp = null, onPullDown = {
            isLoading = true
            viewModel.loadNet(viewModel.url.value ?: supportBean.entry[0].rootUrl, supportBean.name)
        })

        listview.setOnItemClickListener { parent, view, position, id ->
            val indexBookBean = adater.list[position]

            val iconView = view.findViewById<View>(R.id.icon)
            val descView = view.findViewById<View>(R.id.desc)
            val bookNameView = view.findViewById<View>(R.id.title)

            val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(),
                    Pair.create(iconView, "icon"),
                    Pair.create(descView, "desc"),
                    Pair.create(bookNameView, "bookName")
            ).toBundle()
            Log.e("XIAOLEI", indexBookBean.netName + ":" + indexBookBean.url)
            val intent = Intent(context, BookInfoActivity::class.java).apply {
                putExtra("data", indexBookBean)
            }
            startActivity(intent, options)
        }

        viewModel.indexBook.observeNotNull(this) { indexBean ->
            isLoading = false
            if (clearData)
            {
                adater.list.clear()
            } else
            {
                clearData = true
            }
            refresh_layout.setPageCount(indexBean.pageCount)
            refresh_layout.pageIn = indexBean.pageCurrent
            adater.list.addAll(indexBean.list)
            adater.notifyDataSetChanged()

            refresh_layout.stopPullBehavior()
            loadingLayout.onDone()
        }

        // 监听滚动事件
        listview.setOnScrollListener(object : AbsListView.OnScrollListener
        {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) = Unit
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int)
            {
                // 只剩一页可以翻了，赶紧加载下一页
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= visibleItemCount)
                {
                    // 没有在加载更多
                    if(!isLoading)
                    {
                        Log.e("XIAOLEI", "差不多了，加载下一页")
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadMore()
    {
        // 加载更多
        viewModel.url.value?.let { url ->
            viewModel.supportBean.value?.let { bean ->
                clearData = false // 暂时申请不需要清空数据
                val urlP = url.split("?")
                if (urlP.size >= 2)
                {
                    val urlA = urlP[1].split("&")
                    val pageParam = urlA.find { it.startsWith(bean.pageKey) }
                    pageParam?.let {
                        val newUrl = url.replace(pageParam, "${bean.pageKey}=${refresh_layout.pageIn + 1}")
                        isLoading = true
                        viewModel.loadNet(newUrl, supportBean.name)
                        return
                    }
                }
                val newUrl = if (url.indexOf("?") > 0)
                {
                    "$url&${bean.pageKey}=${refresh_layout.pageIn + 1}"
                } else
                {
                    "$url?${bean.pageKey}=${refresh_layout.pageIn + 1}"
                }
                isLoading = true
                viewModel.loadNet(newUrl, supportBean.name)
            }
        }
    }

    /**
     * 通过关键字搜索
     */
    private fun searchByWord(word: String, searchUrl: String)
    {
        searchPoup.dismiss()
        if (word.isEmpty())
        {
            viewModel.url.value?.let {
                viewModel.loadNet(it, supportBean.name)
            }
            return
        }
        val url = if (searchUrl.contains("?", true))
        {
            "$searchUrl&${viewModel.supportBean.value?.searchKey}=${URLEncoder.encode(word)}"
        } else
        {
            "$searchUrl?${viewModel.supportBean.value?.searchKey}=${URLEncoder.encode(word)}"
        }
        viewModel.loadNet(url, supportBean.name)
        Log.e("XIAOLEI", url)
    }


    override fun initData() = Unit

    override fun loadData()
    {
        val support_name = arguments?.getString("support_name") // 首页分配过来的名字
        support_name?.let { support_name ->
            val inputStream = activity?.assets?.open("support/$support_name") //根据名字取得输入流
            inputStream?.let { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream)) // 流转缓冲读取流
                val json = reader.readText() // 读取JSON文本
                reader.close() // 关掉流
                supportBean = Gson.gson.fromJson<SupportBean>(json, SupportBean::class.java) // JSON 转对象
                viewModel.supportBean.value = supportBean // 把对象放到liveData里，供监听器触发
            }
        }
        val url = supportBean.entry[0].rootUrl
        viewModel.url.value = url
        loadingLayout.onLoading()
    }


    /**
     * 书城的ViewModel
     */
    class MarketViewModel : ViewModel()
    {
        private val pageNet by lazy { BaseRetrofit.create(APPNet::class.java) }

        /**
         * 网络正常获取
         */
        fun loadNet(url: String, supportName: String)
        {
            val call = pageNet.getHtmlBy(url)
            launch {
                val response = call.execute()
                if (response.isSuccessful)
                {
                    val html = response.body() ?: ""
                    indexBook.postValue(IndexParser.parser(html, supportName))
                }
            }
        }

        val indexBook = MutableLiveData<IndexBook>() // 获取到的HTML
        val supportBean = MutableLiveData<SupportBean>() // JSON转换后的文本
        val url = MutableLiveData<String>()
    }
}