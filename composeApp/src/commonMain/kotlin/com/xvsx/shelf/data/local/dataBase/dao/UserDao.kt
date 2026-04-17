package com.xvsx.shelf.data.local.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xvsx.shelf.data.local.dataBase.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("DELETE FROM UserEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemList: List<UserEntity>)

    @Query("SELECT COUNT(*) FROM UserEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM UserEntity")
    fun getListAsFlow(): Flow<List<UserEntity>?>

    @Query("SELECT * FROM UserEntity")
    suspend fun getList(): List<UserEntity>?
}