package com.xiaolei.okbook.Entitys

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.xiaolei.okbook.Gson.Gson

/**
 * Created by xiaolei on 2018/4/26.
 */
@Entity
class BookShelfBean
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var url:String? = "" // 链接
    var netName:String? = "" // 网站名字
    
    var bookName:String? = "" // 书本名字
    var bookIcon:String? = "" // 书本封面
    
}