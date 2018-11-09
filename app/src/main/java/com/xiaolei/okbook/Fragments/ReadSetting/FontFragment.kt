package com.xiaolei.okbook.Fragments.ReadSetting

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.xiaolei.okbook.Base.BaseReadBookActivity
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Exts.edit
import com.xiaolei.okbook.Exts.onSeek
import com.xiaolei.okbook.R
import com.xiaolei.okbook.Utils.FileUtils
import kotlinx.android.synthetic.main.fragment_font.*
import java.io.File

/**
 * 字号
 */
class FontFragment : BaseV4Fragment()
{
    private val sp by lazy { requireContext().getSharedPreferences(Globals.config, Context.MODE_PRIVATE) }

    // activity 的viewmodel
    private val readViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BaseReadBookActivity.ReadViewModel::class.java)
    }
    private val requestCode = 0x0002

    override fun contentViewId() = R.layout.fragment_font

    override fun initView()
    {
        val font_size = sp?.getFloat("font_size", 16f) ?: 16f
        fontsize_seekbar.progress = (font_size - 12).toInt()
        val fontPath = sp.getString("fontTypeFace_path", "")
        if (fontPath.isNotEmpty())
        {
            val fontFile = File(fontPath)
            font_type.text = fontFile.name
        }
    }

    override fun setListeners()
    {
        fontsize_seekbar.onSeek { progress ->
            val fontSize = (12 + progress).toFloat()
            sp?.edit {
                putFloat("font_size", fontSize)
            }
            readViewModel.fontSize.value = fontSize
        }
        // 选择字体文件
        font_type.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, requestCode)
        }
        reset_system_fonttype.setOnClickListener {

            sp.edit {
                remove("fontTypeFace_path")
            }
            font_type.text = "系统默认"
            readViewModel.fontTypeFace.value = font_type_label.typeface
        }
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
            val fontFile = FileUtils.getFile(requireActivity(), data.data)
            if (!fontFile.name.endsWith(".ttf", true))
            {
                Toast.makeText(requireActivity(), "必须选择 ttf 后缀的字体文件", Toast.LENGTH_SHORT).show()
            } else
            {
                sp.edit {
                    putString("fontTypeFace_path", fontFile.path)
                }
                font_type.text = fontFile.name
                readViewModel.fontTypeFace.value = Typeface.createFromFile(fontFile)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}