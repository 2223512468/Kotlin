package com.xiaolei.okbook.Fragments.ReadSetting

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import com.xiaolei.okbook.Adapters.BookChapterAdapter
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Bean.ChapterBean
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.xiaolei.okbook.Base.BaseReadBookActivity.ReadViewModel
import com.xiaolei.okbook.Exts.observeNotNull
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.Log
import kotlinx.android.synthetic.main.fragment_charpter.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 章节
 */
class CharpterFragment : BaseV4Fragment()
{
    private val chapterList = LinkedList<ChapterBean>()
    private val chapterAdapter = BookChapterAdapter(chapterList)

    private val readViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(ReadViewModel::class.java)
    }
    private val viewModel by lazy {
        getViewModel(CViewModel::class.java)
    }

    override fun contentViewId() = R.layout.fragment_charpter

    override fun initView()
    {
        EventBus.getDefault().register(this)
    }

    override fun onDestroy()
    {
        EventBus.getDefault().unregister(this)

        // 界面结束的时候，清除粘性事件
        val lastMsg = EventBus.getDefault().getStickyEvent(Message::class.java)
        lastMsg?.let {
            EventBus.getDefault().removeStickyEvent(lastMsg)
        }
        
        super.onDestroy()
    }

    override fun setListeners()
    {
        charpter_listview.adapter = chapterAdapter
        /**
         * 定位章节位置
         */
        charpter_listview.setOnItemClickListener { parent, view, position, id ->
            readViewModel.charpterPosition.value = position
        }
        // 监听书籍改变事件
        viewModel.chapterList.observeNotNull(this) { list ->
            // Log.e("XIAOLEI", "收到章节${list?.size}")
            chapterList.clear()
            chapterList.addAll(list)
            chapterAdapter.notifyDataSetChanged()
        }
    }

    //  接收粘性事件消息
    @Subscribe(sticky = true)
    fun setPosition(message: Message)
    {
        launch(UI) {
            val position = message.position
            if (chapterAdapter.position != position)
            {
                chapterAdapter.position = position
                charpter_listview.smoothScrollToPosition(position)
                chapterAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun initData()
    {

    }

    override fun loadData()
    {
        // Log.e("XIAOLEI", "要求获取章节")
        readViewModel.getCharpterAction.value = true
    }

    class CViewModel : ViewModel()
    {
        val chapterList = MutableLiveData<LinkedList<ChapterBean>>()
    }

    class Message(var position: Int = 0)
}