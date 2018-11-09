package com.xiaolei.okbook.Fragments.ReadSetting

import android.content.Context
import android.provider.Settings
import com.xiaolei.okbook.Base.BaseV4Fragment
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Exts.edit
import com.xiaolei.okbook.Exts.onSeek
import com.xiaolei.okbook.R
import kotlinx.android.synthetic.main.fragment_light.*


/**
 * 亮度
 */
class LightFragment : BaseV4Fragment()
{
    private val sp by lazy { requireContext().getSharedPreferences(Globals.config, Context.MODE_PRIVATE) }
    
    override fun contentViewId() = R.layout.fragment_light
    
    override fun initView()
    {
        val light_value = sp?.getFloat("light_value", getScreenBrightness())
                ?: getScreenBrightness() // 获取亮度
        light_seekbar.progress = (light_value * 100).toInt()
    }
    
    override fun setListeners()
    {
        light_seekbar.onSeek { progress ->
            val light_value = progress / 100f
            sp?.edit {
                putFloat("light_value", light_value)
            }
            val lp = activity?.window?.attributes
            lp?.screenBrightness = light_value
            activity?.window?.attributes = lp
        }
        
    }
    
    override fun initData()
    {
        
    }
    
    override fun loadData()
    {
        
    }
    
    /**
     * 获取屏幕亮度
     */
    private fun getScreenBrightness(): Float
    {
        var value = 0.5f
        val cr = activity?.contentResolver
        try
        {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (e: Settings.SettingNotFoundException)
        {
        }
        return value
    }
}