package com.koperasiku.app.core.di

import android.content.Context
import androidx.room.Room
import com.koperasiku.app.data.local.AppDatabase
import com.koperasiku.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "koperasiku_db"
        ).build()
    }

    @Provides
    fun provideAnggotaDao(db: AppDatabase): AnggotaDao = db.anggotaDao()

    @Provides
    fun provideProdukDao(db: AppDatabase): ProdukDao = db.produkDao()

    @Provides
    fun provideTransaksiDao(db: AppDatabase): TransaksiDao = db.transaksiDao()

    @Provides
    fun provideKasDao(db: AppDatabase): KasDao = db.kasDao()

    @Provides
    fun provideSimpananDao(db: AppDatabase): SimpananDao = db.simpananDao()

    @Provides
    fun providePinjamanDao(db: AppDatabase): PinjamanDao = db.pinjamanDao()
}
