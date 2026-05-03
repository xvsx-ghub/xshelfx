package com.xvsx.shelf.data.local.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("DELETE FROM ChatMessageEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemList: List<ChatMessageEntity>)

    @Query("SELECT COUNT(*) FROM ChatMessageEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM ChatMessageEntity")
    fun getListAsFlow(): Flow<List<ChatMessageEntity>?>

    @Query("SELECT * FROM ChatMessageEntity")
    suspend fun getList(): List<ChatMessageEntity>?

    @Query("SELECT * FROM ChatMessageEntity " +
            "WHERE nickname = :nickname " +
            "AND contactName = :contactName " +
            "ORDER BY id DESC LIMIT 1")
    suspend fun getLastMessage(
        nickname: String,
        contactName: String
    ): ChatMessageEntity?
}