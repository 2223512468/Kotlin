package com.xiaolei.okbook.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.xiaolei.okbook.Daos.BookShelfDao
import com.xiaolei.okbook.Daos.UserDao
import com.xiaolei.okbook.Entitys.BookShelfBean
import com.xiaolei.okbook.Entitys.User

@Database(entities = [User::class
    , BookShelfBean::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun getUserDao(): UserDao
    abstract fun getBookShelfDao(): BookShelfDao
}