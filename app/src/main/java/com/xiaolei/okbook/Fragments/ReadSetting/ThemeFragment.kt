package com.xiaolei.okbook.Fragments.ReadSetting

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.MediaStore
import android.view.View
import com.xiaolei.okbook.Base.BaseReadBookActivity
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Exts.edit
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.Log
import com.xiaolei.okbook.Base.BaseReadBookActivity.ReadViewModel.ThemeBean
import kotlinx.android.synthetic.main.fragment_theme.*
import com.jrummyapps.android.colorpicker.ColorPickerDialog
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener
import com.xiaolei.okbook.Utils.ColorUtil
import com.xiaolei.okbook.Widgets.CircleView


/**
 * 主题
 */
class ThemeFragment : BaseV4Fragment()
{
    private val themeBean by lazy {
        ThemeBean(getTextColor(), getBgGround())
    }
    private val sp by lazy { context?.getSharedPreferences(Globals.config, Context.MODE_PRIVATE) }
    private val requestCode = 0x0001
    // activity 的viewmodel
    private val readViewModel by lazy {
        if (activity != null)
        {
            ViewModelProviders.of(activity!!).get(BaseReadBookActivity.ReadViewModel::class.java)

        } else
        {
            null
        }
    }


    override fun contentViewId() = R.layout.fragment_theme

    override fun initView()
    {
        chouse_txtcolor_tv.setColor(themeBean.textColor)
    }

    override fun setListeners()
    {
        setOnBgCLick(theme_white
                , theme_cyan
                , theme_light_black
                , theme_deep_black)

        setOnTextClick(textcolor_white
                , textcolor_cyan
                , textcolor_light_black
                , textcolor_deep_black
                , chouse_bg_color)

        // 点击选文字颜色
        chouse_txtcolor_tv.setOnClickListener {
            opeAdvancenDialog(getTextColor()) { color ->
                Log.e("XIAOLEI", "onColorSelected:$color")
                chouse_txtcolor_tv.setColor(color)
                sp?.edit {
                    putInt("text_color", color)
                }
                readViewModel?.themeBean?.value = themeBean.apply {
                    this.textColor = color
                    this.bg_ground = getBgGround()
                }
            }
        }
        // 自定义背景图片
        theme_own_pic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, requestCode)
        }
        // 选择自定义背景色
        chouse_bg_color.setOnClickListener {
            opeAdvancenDialog(chouse_bg_color.getColor()) { color ->
                chouse_bg_color.setColor(color)
                val bg_ground = ColorUtil.toHexEncoding(color)
                sp?.edit {
                    putString("bg_ground", bg_ground)
                }
                readViewModel?.themeBean?.value = themeBean.apply {
                    this.textColor = getTextColor()
                    this.bg_ground = bg_ground
                }
            }
        }
    }

    /**
     * 选择背景色
     */
    private fun setOnBgCLick(vararg views: View)
    {
        views.forEach { view ->
            view.setOnClickListener {
                val text_color = getTextColor()
                if (it is CircleView)
                {
                    val bg_ground = ColorUtil.toHexEncoding(it.getColor())
                    sp?.edit {
                        putString("bg_ground", bg_ground)
                    }
                    readViewModel?.themeBean?.value = themeBean.apply {
                        this.textColor = text_color
                        this.bg_ground = bg_ground
                    }
                }
            }
        }

    }

    /**
     * 选择文字颜色
     */
    private fun setOnTextClick(vararg views: View)
    {
        views.forEach { view ->
            view.setOnClickListener {
                if (it is CircleView)
                {
                    val color = it.getColor()
                    sp?.edit {
                        putInt("text_color", color)
                    }
                    readViewModel?.themeBean?.value = themeBean.apply {
                        this.textColor = color
                        this.bg_ground = getBgGround()
                    }
                }
            }
        }
    }

    private fun opeAdvancenDialog(color: Int, block: (color: Int) -> Unit)
    {
        //传入的默认color
        val colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color)
                .setDialogTitle(R.string.chouse_text_color)
                //设置dialog标题
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                //设置为自定义模式
                .setShowAlphaSlider(true)
                //设置有透明度模式，默认没有透明度
                .setDialogId(0)
                //设置Id,回调时传回用于判断
                .setAllowPresets(false)
                //不显示预知模式
                .create()
        //Buider创建
        colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener
        {
            override fun onDialogDismissed(dialogId: Int) = Unit

            override fun onColorSelected(dialogId: Int, color: Int)
            {
                block(color)
            }
        })
        //设置回调，用于获取选择的颜色
        colorPickerDialog.show(activity!!.fragmentManager, "color-picker-dialog")
    }

    private fun getTextColor(): Int
    {
        val mainTextcolor = Color.parseColor("#353535")
        val textColor = sp?.getInt("text_color", mainTextcolor) ?: mainTextcolor
        return textColor
    }

    private fun getBgGround(): String
    {
        return sp?.getString("bg_ground", "#ffffff") ?: "#ffffff"
    }

    override fun initData()
    {

    }

    override fun loadData()
    {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.e("XIAOLEI", data)
            val selectedImage = data.data
            val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity?.contentResolver?.query(selectedImage, filePathColumns, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumns[0])
            columnIndex?.let {
                val imagePath = cursor.getString(columnIndex)
                selectImg(imagePath)
            }
            cursor?.close()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 选择图片
     */
    private fun selectImg(imagePath: String)
    {
        val text_color = getTextColor()
        val bg_ground = imagePath
        sp?.edit {
            putInt("text_color", text_color)
            putString("bg_ground", bg_ground)
        }
        readViewModel?.themeBean?.value = themeBean.apply {
            this.textColor = text_color
            this.bg_ground = bg_ground
        }
    }

}