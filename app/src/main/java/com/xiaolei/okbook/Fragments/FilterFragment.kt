package com.xiaolei.okbook.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.xiaolei.okbook.Adapters.FilterChouserAdapter
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Bean.SupportBean
import com.xiaolei.okbook.Bean.SupportBeanEntryParamEnum
import com.xiaolei.okbook.Exts.gone
import com.xiaolei.okbook.Exts.show
import com.xiaolei.okbook.R
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.fragment_filter.*
import java.util.*

class FilterFragment : BaseV4Fragment()
{
    private var viewModel: MarketFragment.MarketViewModel? = null
    private val contentList = LinkedList<LinearLayout>()

    override fun contentViewId() = R.layout.fragment_filter


    override fun initView()
    {
        parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(MarketFragment.MarketViewModel::class.java)
        }
    }

    override fun setListeners()
    {
        viewModel?.supportBean?.observe(this, Observer { supportBean ->
            // 监听父Fragment的JSON转换的对象
            supportBean?.let {
                loadSupportBean(it) // 根据对象进行解析加载到界面
            }
        })
    }

    override fun initData()
    {

    }

    override fun loadData()
    {

    }

    /**
     * 加载数据
     */
    private fun loadSupportBean(supportBean: SupportBean)
    {
        supportBean.entry.forEach { entry ->
            // 加载男，女模块

            // 实例化 按钮
            val radioButton = layoutInflater.inflate(R.layout.item_radiobutton, radio_group, false)
            (radioButton as RadioButton).text = entry.gender // 把文本设置为，男，女
            radio_group.addView(radioButton) // 添加到界面

            // 下面内容区的实例化
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                visibility = View.GONE
            }
            contentList.add(linearLayout)
            content_layout.addView(linearLayout)
            // 循环所有的 参数，这一步主要得到每一个Item的 key
            entry.params.forEach { param ->
                // 实例化每个Item的UI界面
                val view = layoutInflater.inflate(R.layout.layout_filter_item, linearLayout, false)
                val name = view.findViewById<TextView>(R.id.name) // 展示文字
                val chouser = view.findViewById<TagFlowLayout>(R.id.chouser) // 每个可供选择的值

                val adapter = FilterChouserAdapter(param.enum)
                chouser.adapter = adapter // 展示每个可选的值
                
                chouser.setTag(R.id.tag_tmp_data, param.key) //把key暂存起来，以后用得到
                name.text = param.name // 把名字显示
                // 这里设置每个可选值的选择事件
                chouser.setOnSelectListener {
                    // 当一个值被选，首先查看是哪个tab被点击了
                    val index = radio_group.indexOfChild(radio_group.findViewById(radio_group.checkedRadioButtonId))
                    // 按照那个被点击的tab的坐标，取出对应的显示的界面
                    val layout = contentList[index]
                    // 先组一个差不多的url临时放起来，判断是否有问号，
                    val url = if (entry.rootUrl.contains("?"))
                    {
                        StringBuilder(entry.rootUrl)
                    } else
                    {
                        StringBuilder("${entry.rootUrl}?")
                    }
                    // 循环界面里所有的子控件
                    for (childIndex in 0 until layout.childCount)
                    {
                        // 获取子控件
                        val childLinerLayout = layout.getChildAt(childIndex)
                        // 获取子控件里面的每个‘选择器’控件
                        val findChouser = childLinerLayout.findViewById<TagFlowLayout>(R.id.chouser)
                        // 获取刚才临时存起来的 key 
                        val key = findChouser.getTag(R.id.tag_tmp_data) as String
                        // 获取控件里被选择的总数
                        val selectedList = findChouser.selectedList
                        // 至少有一个被选择的话
                        if (selectedList.isNotEmpty())
                        {
                            // 取出第一个值
                            val em = findChouser.adapter.getItem(selectedList.first()) as SupportBeanEntryParamEnum
                            // 配合刚才的key，组合成一个 key=value 形式的 URL
                            url.append("&").append("$key").append("=").append(em.value)
                        }
                    }
                    // 所有循环完毕，把值发送给liveData，供触发监听刷新 UI 
                    viewModel?.url?.postValue(url.toString())
                }
                
                // val defaultParam = param.enum.firstOrNull { eum -> true == eum.default } // 默认需要被选中的
                // defaultParam?.let { enum ->
                //     adapter.setSelectedList(param.enum.indexOf(enum)) // 手动设置被选中
                // }
                
                linearLayout.addView(view)
            }
        }
        // 根据点的哪个项，显示对应的内容
        radio_group.setOnCheckedChangeListener { group, checkedId ->
            val index = radio_group.indexOfChild(radio_group.findViewById(checkedId))
            contentList.forEach { view ->
                view.gone()
            }
            contentList[index].show()
        }
        // 这里默认手动触发一次，自动选择第一个
        val child = radio_group.getChildAt(0)
        child?.let {
            (it as RadioButton).isChecked = true
        }
    }

}