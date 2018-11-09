package com.xiaolei.okbook.Activitys

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.text.Html
import android.transition.Fade
import com.bumptech.glide.Glide
import com.xiaolei.okbook.Base.BaseActivity
import com.xiaolei.okbook.Bean.BookInfoBean
import com.xiaolei.okbook.Entitys.BookShelfBean
import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.Exts.*
import com.xiaolei.okbook.Nets.APPNet
import com.xiaolei.okbook.Nets.BaseRetrofit
import com.xiaolei.okbook.Parsers.BookInfoParser
import com.xiaolei.okbook.R
import com.xiaolei.okbook.RetrofitExt.common.SCallBack
import com.xiaolei.okbook.Utils.Log
import kotlinx.android.synthetic.main.activity_book_info.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus

/**
 * 图书详情
 * Created by xiaolei on 2018/4/24.
 */
class BookInfoActivity : BaseActivity()
{
    lateinit var data: Book
    private val pageNet by lazy { BaseRetrofit.create(APPNet::class.java) }
    private lateinit var bookInfoBean: BookInfoBean

    override fun onCreate(savedInstanceState: Bundle?)
    {
        window.enterTransition = Fade().setDuration(300)
        window.exitTransition = Fade().setDuration(300)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
    }

    override fun initObj()
    {

    }

    override fun initData()
    {
        data = intent.getSerializableExtra("data") as Book
    }

    override fun initView()
    {
        Glide.with(this)
                .load(data.icon)
                .into(imgUrl)
        bookName.text = data.bookName
        intro.text = data.desc
    }

    override fun setListener()
    {
        app_title.setOnLeftImageClick { finish() }
        start_read_btn.setOnClickListener {
            launch { insertBookShelf() }
            startActivity(Intent(this, ReadBookActivity::class.java).apply {
                putExtra("data", this@BookInfoActivity.data)
            })
        }
        cache_all_chapter.setOnClickListener {
            startActivity(Intent(this, Chapter2CacheActivity::class.java).apply {
                putExtra("data", this@BookInfoActivity.data)
            })
        }
    }

    /**
     * 检查，并且插入一条记录
     */
    private fun insertBookShelf()
    {
        val db = Database(this)
        val dao = db.getBookShelfDao()
        val bookShelfBean = dao.getBookShelfBy(data.url)
        Log.e("XIAOLEI", "有没有？：${bookShelfBean == null}")
        if (bookShelfBean == null) // 没有记录，则插入记录
        {
            // 插入记录
            val bean = BookShelfBean()
            bean.bookName = bookInfoBean.bookName
            bean.bookIcon = bookInfoBean.imgUrl
            bean.netName = bookInfoBean.netName
            bean.url = data.url
            Database(this).getBookShelfDao().insert(bean)
            EventBus.getDefault().post(Message.obtain())
            Log.e("XIAOLEI", "插入一个")
        }
    }

    override fun loadData()
    {
        var call = pageNet.getHtmlCacheFirst(data.url)
        call.enqueue(object : SCallBack<String>(this)
        {
            override fun onSuccess(html: String)
            {
                parserHtml(html, data)
            }
            override fun onCache(html: String)
            {
                parserHtml(html, data)
                call = pageNet.getHtmlBy(data.url)
                call.enqueue(this@BookInfoActivity, onSuccess = { html ->
                    parserHtml(html, data)
                })
            }
        })
    }

    /**
     * 解析数据，显示在界面上
     */
    private fun parserHtml(html: String, data: Book)
    {
        launch(UI) {
            val bookInfoBeanAsync = async { BookInfoParser.parser(html, data.netName) }
            bookInfoBean = bookInfoBeanAsync.await()
            
            // Glide.with(this@BookInfoActivity).load(bookInfoBean.imgUrl).into(imgUrl)
            
            bookName.text = bookInfoBean.bookName
            app_title.setTitleText(if (bookInfoBean.bookName.isEmpty())
            {
                "未获取到数据"
            } else
            {
                bookInfoBean.bookName
            })
            author.text = bookInfoBean.author
            tag_tv.text = bookInfoBean.tag
            if (bookInfoBean.wordCount.isNotEmpty())
            {
                wordCount.text = "总字数：${bookInfoBean.wordCount}"
            }
            if (bookInfoBean.catalogCount.isNotEmpty())
            {
                catalogCount.text = "章节数：${bookInfoBean.catalogCount}"
            }
            cache_all_chapter.show()
            intro.text = Html.fromHtml(bookInfoBean.desc)
            if (bookInfoBean.bookName.isNotEmpty())
            {
                launch(UI) {
                    val db = Database(this@BookInfoActivity)
                    val dao = db.getBookShelfDao()
                    val bookShelfBeanAsync = async { dao.getBookShelfBy(data.url) }
                    val bookShelfBean = bookShelfBeanAsync.await()
                    if (bookShelfBean != null) // 没有记录，则插入记录
                    {
                        start_read_btn.text = "继续阅读"
                    }
                    db.close()
                }
                start_read_btn.show()
            }
        }
    }
}
