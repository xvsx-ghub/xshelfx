package com.xvsx.shelf.data.local.dataBase

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xvsx.shelf.data.local.dataBase.dao.AuthorizationDao
import com.xvsx.shelf.data.local.dataBase.dao.ChatMessageDao
import com.xvsx.shelf.data.local.dataBase.dao.CustomerDao
import com.xvsx.shelf.data.local.dataBase.dao.CustomerTaskDao
import com.xvsx.shelf.data.local.dataBase.dao.DestinationDao
import com.xvsx.shelf.data.local.dataBase.dao.JobDao
import com.xvsx.shelf.data.local.dataBase.dao.NotServicingReasonDao
import com.xvsx.shelf.data.local.dataBase.dao.RequestDao
import com.xvsx.shelf.data.local.dataBase.dao.StompDao
import com.xvsx.shelf.data.local.dataBase.dao.TaskDao
import com.xvsx.shelf.data.local.dataBase.dao.TruckReportDao
import com.xvsx.shelf.data.local.dataBase.dao.UserDao
import com.xvsx.shelf.data.local.dataBase.dao.WasteTypeDao
import com.xvsx.shelf.data.local.dataBase.entity.AuthorizationEntity
import com.xvsx.shelf.data.local.dataBase.entity.ChatMessageEntity
import com.xvsx.shelf.data.local.dataBase.entity.CustomerEntity
import com.xvsx.shelf.data.local.dataBase.entity.CustomerTaskEntity
import com.xvsx.shelf.data.local.dataBase.entity.DestinationEntity
import com.xvsx.shelf.data.local.dataBase.entity.JobEntity
import com.xvsx.shelf.data.local.dataBase.entity.NotServicingReasonEntity
import com.xvsx.shelf.data.local.dataBase.entity.RequestEntity
import com.xvsx.shelf.data.local.dataBase.entity.StompEntity
import com.xvsx.shelf.data.local.dataBase.entity.TaskEntity
import com.xvsx.shelf.data.local.dataBase.entity.TruckReportEntity
import com.xvsx.shelf.data.local.dataBase.entity.UserEntity
import com.xvsx.shelf.data.local.dataBase.entity.WasteTypeEntity

@Database(
    entities = [
        AuthorizationEntity::class,
        StompEntity::class,
        TaskEntity::class,
        RequestEntity::class,
        CustomerEntity::class,
        NotServicingReasonEntity::class,
        CustomerTaskEntity::class,
        TruckReportEntity::class,
        WasteTypeEntity::class,
        DestinationEntity::class,
        JobEntity::class,
        ChatMessageEntity::class,
        UserEntity::class
    ],
    version = 38
)

@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthorizationDao(): AuthorizationDao
    abstract fun getStompDao(): StompDao
    abstract fun getTaskDao(): TaskDao
    abstract fun getRequestDao(): RequestDao
    abstract fun getCustomerDao(): CustomerDao
    abstract fun getNotServicingReasonDao(): NotServicingReasonDao
    abstract fun getCustomerTaskDao(): CustomerTaskDao
    abstract fun getTruckReportDao(): TruckReportDao
    abstract fun getWasteTypeDao(): WasteTypeDao
    abstract fun getDestinationDao(): DestinationDao
    abstract fun getJobDao(): JobDao
    abstract fun getChatMessageDao(): ChatMessageDao
    abstract fun getUserDao(): UserDao
}