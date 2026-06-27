package com.koperasiku.app.core.di

import com.koperasiku.app.data.repository.AnggotaRepositoryImpl
import com.koperasiku.app.data.repository.AuthRepositoryImpl
import com.koperasiku.app.data.repository.KasRepositoryImpl
import com.koperasiku.app.data.repository.PinjamanRepositoryImpl
import com.koperasiku.app.data.repository.ProdukRepositoryImpl
import com.koperasiku.app.data.repository.SimpananRepositoryImpl
import com.koperasiku.app.data.repository.TransaksiRepositoryImpl
import com.koperasiku.app.domain.repository.AnggotaRepository
import com.koperasiku.app.domain.repository.AuthRepository
import com.koperasiku.app.domain.repository.KasRepository
import com.koperasiku.app.domain.repository.PinjamanRepository
import com.koperasiku.app.domain.repository.ProdukRepository
import com.koperasiku.app.domain.repository.SimpananRepository
import com.koperasiku.app.domain.repository.TransaksiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindAnggotaRepository(impl: AnggotaRepositoryImpl): AnggotaRepository

    @Binds @Singleton
    abstract fun bindProdukRepository(impl: ProdukRepositoryImpl): ProdukRepository

    @Binds @Singleton
    abstract fun bindTransaksiRepository(impl: TransaksiRepositoryImpl): TransaksiRepository

    @Binds @Singleton
    abstract fun bindKasRepository(impl: KasRepositoryImpl): KasRepository

    @Binds @Singleton
    abstract fun bindSimpananRepository(impl: SimpananRepositoryImpl): SimpananRepository

    @Binds @Singleton
    abstract fun bindPinjamanRepository(impl: PinjamanRepositoryImpl): PinjamanRepository
}
