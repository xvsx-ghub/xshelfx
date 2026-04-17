package com.xvsx.shelf.data.local.dataBase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xvsx.shelf.data.local.dataBase.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("DELETE FROM ContactEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemList: List<ContactEntity>)

    @Query("SELECT COUNT(*) FROM ContactEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM ContactEntity")
    fun getListAsFlow(): Flow<List<ContactEntity>?>

    @Query("SELECT * FROM ContactEntity")
    suspend fun getList(): List<ContactEntity>?

    @Query("SELECT * FROM ContactEntity WHERE nickname = :nickname")
    suspend fun getByNickname(nickname: String): List<ContactEntity>?

    @Query("SELECT * FROM ContactEntity WHERE nickname LIKE '%' || :nickname || '%'")
    suspend fun getSameByNickname(nickname: String): List<ContactEntity>?

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}