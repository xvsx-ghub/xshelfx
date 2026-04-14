package com.xvsx.shelf.data.local.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xvsx.shelf.data.remote.http.HttpClientCore

@Entity(tableName = "RequestEntity")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val paramHashMap: HashMap<String, String>,
    val imageFileHashMap: HashMap<String, String>?,
    val typeValue: String,
    val methodValue: String
){
    fun mapToRequest() =
        HttpClientCore.Request(
            url = url,
            paramHashMap = paramHashMap,
            imageFileHashMap = imageFileHashMap,
            typeValue = typeValue,
            methodValue = methodValue
        )
}