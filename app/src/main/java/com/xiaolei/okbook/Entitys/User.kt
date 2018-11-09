package com.xiaolei.okbook.Entitys

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class User
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo
    var name: String = ""
    
}