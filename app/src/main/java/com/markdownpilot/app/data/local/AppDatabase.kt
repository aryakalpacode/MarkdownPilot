package com.markdownpilot.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.markdownpilot.app.data.local.dao.*
import com.markdownpilot.app.data.local.entity.*

@Database(
    entities = [ConversationEntity::class, MessageEntity::class, GeneratedFileEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun fileDao(): FileDao
}
