package com.xiaolei.okbook.Base

import android.animation.ObjectAnimator
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Exts.*
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.StateBarUtil
import kotlinx.android.synthetic.main.activity_read_book.*
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.widget.RadioButton
import android.widget.TextView
import com.xiaolei.okbook.Bean.ChapterBean
import com.xiaolei.okbook.Fragments.ReadSetting.*
import java.util.*
import com.xiaolei.okbook.Fragments.ReadSetting.CharpterFragment.CViewModel
import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import com.xiaolei.okbook.Adapters.ReadPageAdapter
import com.xiaolei.okbook.Utils.Log
import kotlinx.android.synthetic.main.layout_read_guide_.*
import org.greenrobot.eventbus.EventBus
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File


/**
 * 阅读小说的界面
 * Created by xiaolei on 2018/4/25.
 */
abstract class BaseReadBookActivity : BaseActivity()
{
    private val sp by lazy { getSharedPreferences(Globals.config, Context.MODE_PRIVATE) }
    private val history by lazy { getSharedPreferences(Globals.history, Context.MODE_PRIVATE) }
    private val chapterList = LinkedList<ChapterBean>()
    private val layoutManager by lazy {
        LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }
    // 屏幕宽度
    private val screnWidth by lazy {
        val manager = this.windowManager
        val outMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)
        outMetrics.widthPixels
    }
    private val lastOffset by lazy {
        history.getInt((getInfoUrl() + "lastOffset").md5(), 0)
    }
    private val lastPosition by lazy {
        history.getInt((getInfoUrl() + "lastPosition").md5(), 0)
    }
    private val isFirstRun = "isFirstRun_readBook"
    private val lightFragment = LightFragment()
    private val fontFragment = FontFragment()
    private val themeFragment = ThemeFragment()
    private val charpterFragment = CharpterFragment()
    private val modelFragment = ModelFragment()

    private val settingAdapter = ReadPageAdapter(supportFragmentManager, listOf(
            lightFragment, //亮度
            fontFragment, //字号
            themeFragment, //主题
            charpterFragment, //章节
            modelFragment //模式
    ))

    private val chapterViewModel by lazy {
        ViewModelProviders.of(charpterFragment).get(CViewModel::class.java)
    }

    private val viewModel by lazy {
        getViewModel(ReadViewModel::class.java)
    }

    // 标题的动画
    private val titleAnimator by lazy {
        ObjectAnimator.ofFloat(app_title
                , "translationY"
                , -app_title.measuredHeight.toFloat(), 0f).apply {
            duration = 300
        }
    }
    // 控制中心的动画
    private val menuAnimator by lazy {
        ObjectAnimator.ofFloat(controller_menu
                , "translationY",
                controller_menu.measuredHeight.toFloat(), 0f).apply {
            duration = 300
        }
    }

    private val bookAdapter by lazy {
        object : RecyclerAdapter<ChapterBean>(chapterList)
        {
            // 是否加载过历史记录
            var hasLoadHistory = false

            override fun onCreate(parent: ViewGroup, viewType: Int): BaseHolder
            {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_book_detial, parent, false)
                return BaseHolder(view)
            }

            override fun onBindView(holder: BaseHolder, position: Int)
            {
                val textcolor = recycler_view.getTag(R.id.tag_text_color) as? Int
                val textsize = recycler_view.getTag(R.id.tag_text_size) as? Float
                val chapter = list[position]
                val tv = holder.get<TextView>(R.id.textview)
                tv.setTag(R.id.url, chapter.url)
                getTextBy(chapter.url, onsuccess = { text, ourl ->
                    val tagUrl = tv.getTag(R.id.url)
                    if (tagUrl != ourl)
                    {
                        return@getTextBy
                    }

                    // 字体颜色
                    textcolor?.let {
                        tv.setTextColor(it)
                    }
                    // 字体大小
                    textsize?.let {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
                    }
                    // 字体
                    if (tv.typeface != Globals.typeFace)
                    {
                        Globals.typeFace?.let { typeFace ->
                            tv.typeface = typeFace
                        }
                    }

                    tv.text = Html.fromHtml(text)

                    if (!hasLoadHistory && position == lastPosition)
                    {
                        hasLoadHistory = true
                        layoutManager.scrollToPositionWithOffset(lastPosition, lastOffset)
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_book)
    }


    override fun initObj()
    {

    }

    override fun initData()
    {
        val fontPath = sp.getString("fontTypeFace_path", "")
        if (fontPath.isNotEmpty() && File(fontPath).exists())
        {
            Globals.typeFace ?: let {
                Globals.typeFace = Typeface.createFromFile(fontPath)
            }
        }

        lastOffset
        lastPosition
    }

    override fun initView()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            app_title.afterMeasured { width, height ->
                statebar_view.layoutParams.height = StateBarUtil.getStateBarHeigh(this@BaseReadBookActivity)
                statebar_view.requestLayout()
            }
        }
        setFullScreen() // 默认全屏
        recycler_view.layoutManager = layoutManager
        reSetting()
        // 首次运行，会有操作提示
        val firstRun = sp.getBoolean(isFirstRun, true)
        if (firstRun)
        {
            view_stub.inflate()
            val animator = ObjectAnimator.ofFloat(touch_point, "translationY", 0f, -50f, 0f)
            animator.duration = 1000
            animator.repeatCount = -1
            animator.start()
            touch_space.setOnClickListener {
                sp.edit {
                    putBoolean(isFirstRun, false)
                }
                guide_content.gone()
                animator.cancel()
            }
        }

    }

    /**
     * 加载上次设置
     */
    private fun reSetting()
    {
        val textColor = sp.getInt("text_color", Color.parseColor("#353535"))
        val bg_ground = sp.getString("bg_ground", "#ffffff")
        recycler_view.setTag(R.id.tag_text_color, textColor)
        if (bg_ground.startsWith("#"))
        {
            recycler_view.setBackgroundColor(Color.parseColor(bg_ground))
        } else
        {
            if (File(bg_ground).exists())
            {
                Luban.with(this)
                        .load(bg_ground)
                        .setCompressListener(object : OnCompressListener
                        {
                            override fun onSuccess(file: File)
                            {
                                recycler_view.background = BitmapDrawable.createFromPath(file.path)
                            }

                            override fun onError(e: Throwable?) = Unit

                            override fun onStart() = Unit
                        }).launch()
            }
        }

        val font_size = sp?.getFloat("font_size", 16f) ?: 16f
        recycler_view.setTag(R.id.tag_text_size, font_size)

        val light_value = sp?.getFloat("light_value", getScreenBrightness())
                ?: getScreenBrightness() // 获取亮度

        val lp = window?.attributes
        lp?.screenBrightness = light_value
        window?.attributes = lp

    }

    override fun setListener()
    {
        view_pager.adapter = settingAdapter

        back.setOnClickListener { finish() }

        recycler_view.adapter = bookAdapter.apply {
            this.onItemClick { view, position ->
                hidenMenu()
                touch_img.show()
            }
        }

        // 添加滚动监听
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int)
            {
                super.onScrollStateChanged(recyclerView, newState)
                //获取可视的第一个view
                val topView = layoutManager.getChildAt(0)
                if (topView != null)
                {
                    //获取与该view的顶部的偏移量
                    val lastOffset = topView.top
                    //得到该View的数组位置
                    val lastPosition = layoutManager.getPosition(topView)
                    val chapter = bookAdapter.list[lastPosition]
                    title_tv.text = chapter.name
                    // 发送粘性事件消息
                    EventBus.getDefault().postSticky(CharpterFragment.Message(lastPosition))
                    history.edit {
                        putInt((getInfoUrl() + "lastOffset").md5(), lastOffset)
                        putInt((getInfoUrl() + "lastPosition").md5(), lastPosition)
                    }
                }
            }
        })

        recycler_view.setOnTouchListener { v, event ->
            when (event.action)
            {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                {
                    Log.e("XIAOLEI", "取消")
                    val topView = layoutManager.getChildAt(0)
                    if (topView != null)
                    {
                        val lastPosition = layoutManager.getPosition(topView)
                        Log.e("XIAOLEI", "left:${topView.left} screnWidth:$screnWidth")

                    }
                }
            }

            false
        }

        touch_img.setOnClickListener {
            showMenu()
            touch_img.gone()
        }

        // 向fragment发送章节数据
        viewModel.getCharpterAction.observe(this, Observer {
            chapterViewModel.chapterList.value = chapterList
        })
        // 查看某个章节
        viewModel.charpterPosition.observe(this, Observer { position ->
            layoutManager.scrollToPositionWithOffset(position ?: 0, 0)
            hidenMenu()
        })
        // 改变主题的事件
        viewModel.themeBean.observe(this, Observer { themeBean ->
            themeBean?.let {
                recycler_view.setTag(R.id.tag_text_color, themeBean.textColor)
                if (themeBean.bg_ground.startsWith("#"))
                {
                    recycler_view.setBackgroundColor(Color.parseColor(themeBean.bg_ground))
                } else
                {
                    if (File(themeBean.bg_ground).exists())
                    {
                        Luban.with(this)
                                .load(themeBean.bg_ground)
                                .setCompressListener(object : OnCompressListener
                                {
                                    override fun onSuccess(file: File)
                                    {
                                        recycler_view.background = BitmapDrawable.createFromPath(file.path)
                                    }

                                    override fun onError(e: Throwable?) = Unit

                                    override fun onStart() = Unit
                                }).launch()
                    }
                }
                recycler_view.adapter.notifyDataSetChanged()
            }
        })
        // 改变文字大小的事件
        viewModel.fontSize.observeNotNull(this, { font_size ->
            recycler_view.setTag(R.id.tag_text_size, font_size)
            recycler_view.adapter.notifyDataSetChanged()
        })
        // 字体改变事件
        viewModel.fontTypeFace.observeNotNull(this, { typeface ->
            Globals.typeFace = typeface
            recycler_view.adapter.notifyDataSetChanged()
        })
    }

    override fun loadData()
    {
        getChapterList({ list ->
            chapterList.addAll(list)
            // 数据加载成功，隐藏菜单
            hidenMenu()
            recycler_view.adapter.notifyDataSetChanged()
            // 读取历史记录
            layoutManager.scrollToPosition(lastPosition)
            EventBus.getDefault().postSticky(CharpterFragment.Message(lastPosition))
        })

        settingControMenu()
    }

    /**
     * 获取书本目录列表
     */
    abstract fun getChapterList(success: ((List<ChapterBean>) -> Unit))

    /**
     * 获取书本详情的界面链接
     */
    abstract fun getInfoUrl(): String

    /**
     * 显示菜单
     */
    private fun showMenu()
    {
        cancelFullScreen()

        if (app_title.translationY < 0f)
        {
            titleAnimator.setFloatValues(-app_title.measuredHeight.toFloat(), 0f)
            menuAnimator.setFloatValues(controller_menu.measuredHeight.toFloat(), 0f)
            titleAnimator.start()
            menuAnimator.start()
        }

        setStatusBar(true)
    }

    /**
     * 隐藏菜单
     */
    private fun hidenMenu()
    {
        setFullScreen()
        app_title.hideKeyboard()

        if (app_title.translationY == 0f)
        {
            titleAnimator.setFloatValues(0f, -app_title.measuredHeight.toFloat())
            menuAnimator.setFloatValues(0f, controller_menu.measuredHeight.toFloat())

            titleAnimator.start()
            menuAnimator.start()
        }
    }

    /**
     * 设置面板
     */
    private fun settingControMenu()
    {
        radio_group.setOnCheckedChangeListener { group, checkedId ->
            val view = radio_group.findViewById<RadioButton>(checkedId)
            val position = radio_group.indexOfChild(view)
            view_pager.currentItem = position
        }
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener()
        {
            override fun onPageSelected(position: Int)
            {
                radio_group.get(position).isChecked = true
            }
        })
        radio_group.get(0).isChecked = true
    }

    /**
     * @param url URL的链接
     * @param onsuccess 成功的回调
     */
    abstract fun getTextBy(url: String, onsuccess: (html: String, url: String) -> Unit)


    /**
     * 设置全屏
     *
     */
    fun setFullScreen()
    {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 取消全屏
     *
     */
    fun cancelFullScreen()
    {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 获取屏幕亮度
     */
    private fun getScreenBrightness(): Float
    {
        var value = 0.5f
        val cr = contentResolver
        try
        {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (e: Settings.SettingNotFoundException)
        {
        }
        return value
    }


    class ReadViewModel : ViewModel()
    {
        // 获取章节信息的指令
        val getCharpterAction = MutableLiveData<Boolean>()

        val charpterPosition = MutableLiveData<Int>()
        val themeBean = MutableLiveData<ThemeBean>()
        val fontSize = MutableLiveData<Float>()
        val fontTypeFace = MutableLiveData<Typeface>()


        class ThemeBean(var textColor: Int, var bg_ground: String)
    }
}