package com.markdownpilot.app.di

import com.markdownpilot.app.data.local.AppDatabase
import com.markdownpilot.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides fun convDao(db: AppDatabase): ConversationDao = db.conversationDao()
    @Provides fun msgDao(db: AppDatabase): MessageDao = db.messageDao()
    @Provides fun fileDao(db: AppDatabase): FileDao = db.fileDao()
}
