package com.xiaolei.okbook.Fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import com.xiaolei.okbook.Adapters.BookshelfAdapter
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Entitys.User
import com.xiaolei.okbook.R
import kotlinx.android.synthetic.main.fragment_private.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Message
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import com.xiaolei.okbook.Activitys.MainActivity
import com.xiaolei.okbook.Activitys.ReadBookActivity
import com.xiaolei.okbook.Entitys.BookShelfBean
import com.xiaolei.okbook.Bean.Book
import com.xiaolei.okbook.Exts.Database
import com.xiaolei.okbook.PopupWindows.ShowDeletePop
import com.xiaolei.okbook.PopupWindows.ShowSettingPoup
import com.xiaolei.okbook.Utils.Log
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * 我的书架
 */
class BookShelfFragment : BaseV4Fragment()
{
    private val adapter = BookshelfAdapter(LinkedList())
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(PrivateViewModel::class.java)
    }
    private val showDeletePop by lazy {
        ShowDeletePop(requireActivity())
    }
    private val mainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainActivity.MainViewModel::class.java)
    }
    private val poup by lazy {
        ShowSettingPoup(requireActivity())
    }

    override fun contentViewId() = R.layout.fragment_private

    override fun initView()
    {
        EventBus.getDefault().register(this)
    }

    override fun setListeners()
    {
        bookshelf.adapter = adapter
        // 接收到UI界面更新指令
        viewModel.list.observe(this, Observer { list ->
            list?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
        // 点击继续看书
        bookshelf.setOnItemClickListener { parent, view, position, id ->
            val bookShelfBean = adapter.list[position]
            if (bookShelfBean.id == -1)
            {
                // 如果点击的是添加按钮，跳转到第二个界面
                mainViewModel.current.value = 1
            } else
            {
                val intent = Intent(activity, ReadBookActivity::class.java).apply {
                    val data = Book()
                    data.netName = "${bookShelfBean.netName}"
                    data.url = "${bookShelfBean.url}"
                    putExtra("data", data)
                }
                val bundle = ActivityOptionsCompat.makeScaleUpAnimation(view
                        , view.x.toInt()
                        , view.y.toInt()
                        , view.measuredWidth
                        , view.measuredHeight).toBundle()
                startActivity(intent, bundle)
            }
        }
        // 长按弹出删除框
        bookshelf.setOnItemLongClickListener { parent, view, position, id ->
            val bookShelfMapBean = adapter.list[position]
            if (bookShelfMapBean.id == -1)
            {

            } else
            {
                showDeletePop.showOnBottom {
                    launch {
                        val db = Database(bookshelf.context)
                        db.getBookShelfDao().delete(bookShelfMapBean)
                        db.close()
                        loadDBData(null)
                    }
                }
            }
            true
        }

        app_title.setOnLeftImageClick {
            poup.showAsDropDown(it)
        }
    }

    override fun initData()
    {

    }


    override fun loadData()
    {
        launch {
            getContext()?.let { context ->
                Database(context).getUserDao().insertAll(User().apply {
                    name = "肖蕾${System.currentTimeMillis()}"
                })
            }
        }
        loadDBData(null)
    }

    @Subscribe
    public fun loadDBData(msg: Message?)
    {
        Log.e("XIAOLEI", "接到指令，刷新一次")
        launch {
            activity?.let { activity ->
                val db = Database(activity)
                val bookShelfDao = db.getBookShelfDao()
                val list = bookShelfDao.getAll()
                viewModel.list.postValue(arrayListOf<BookShelfBean>().apply {
                    addAll(list)
                    if (list.isEmpty()) // 如果没有拿到列表
                    {
                        add(BookShelfBean().apply { id = -1 }) // 手动添加一个，显示去添加
                    }
                })
                db.close()
            }
        }
    }


    class PrivateViewModel : ViewModel()
    {
        val list = MutableLiveData<List<BookShelfBean>>()
    }
}