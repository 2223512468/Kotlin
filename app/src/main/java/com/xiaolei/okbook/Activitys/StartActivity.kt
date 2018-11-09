package com.xiaolei.okbook.Activitys

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import com.xiaolei.okbook.Base.BaseActivity
import com.xiaolei.okbook.R
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class StartActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun initObj()
    {

    }

    override fun initData()
    {

    }

    override fun initView()
    {

    }

    override fun setListener()
    {
        
    }

    override fun loadData()
    {
        launch(UI) {
            delay(1500, TimeUnit.MILLISECONDS)
            val options = ActivityOptions.makeSceneTransitionAnimation(this@StartActivity).toBundle()
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent,options)
            delay(1500, TimeUnit.MILLISECONDS)
            finish()
        }
    }
}
