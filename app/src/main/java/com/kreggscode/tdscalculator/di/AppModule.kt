package com.kreggscode.tdscalculator.di

import android.content.Context
import com.kreggscode.tdscalculator.data.preferences.PreferencesManager
import com.kreggscode.tdscalculator.data.repository.TDSRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
    
    @Provides
    @Singleton
    fun provideTDSRepository(): TDSRepository {
        return TDSRepository()
    }
}
