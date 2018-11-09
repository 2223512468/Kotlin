package com.xiaolei.okbook.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.xiaolei.okbook.Entitys.User


@Dao
interface UserDao
{
    @Query("SELECT * FROM user")
    fun getAll(): List<User>
    
    
    @Insert
    fun insertAll(vararg users: User)
}