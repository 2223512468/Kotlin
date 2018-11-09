package com.xiaolei.okbook.Daos

import android.arch.persistence.room.*
import com.xiaolei.okbook.Entitys.BookShelfBean

/**
 * Created by xiaolei on 2018/4/26.
 */
@Dao
interface BookShelfDao
{
    @Query("select * from bookshelfbean where url = :url")
    fun getBookShelfBy(url: String): BookShelfBean?
    
    @Insert
    fun insert(bean: BookShelfBean)
    
    @Update
    fun update(bean: BookShelfBean)
    
    @Delete
    fun delete(bean: BookShelfBean)
    
    @Query("select * from bookshelfbean")
    fun getAll(): List<BookShelfBean>
}