package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.local.entity.KasEntity
import com.koperasiku.app.data.mapper.toDto
import com.koperasiku.app.data.mapper.toEntity
import com.koperasiku.app.data.remote.source.KasRemoteSource
import com.koperasiku.app.domain.model.Kas
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class KasRepositoryTest {

    private val kasDao: KasDao = mockk(relaxed = true)
    private val remoteSource: KasRemoteSource = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk(relaxed = true)
    
    private val repository = KasRepositoryImpl(kasDao, remoteSource, networkMonitor)

    @Test
    fun `getKasSummary should combine total masuk and total keluar to compute dynamic balance`() = runTest {
        // Arrange
        every { kasDao.getTotalMasuk() } returns flowOf(250000L)
        every { kasDao.getTotalKeluar() } returns flowOf(100000L)

        // Act
        val summary = repository.getKasSummary().first()

        // Assert
        assertEquals(250000L, summary.totalMasuk)
        assertEquals(100000L, summary.totalKeluar)
        assertEquals(150000L, summary.saldoTotal) // 250000L - 100000L = 150000L
    }

    @Test
    fun `createKasTransaction should save locally and sync to remote if online`() = runTest {
        // Arrange
        val dummyKas = Kas(
            id = "kas-1",
            jenis = "MASUK",
            kategoriId = "PENDAPATAN",
            jumlah = 50000L,
            keterangan = "Setoran",
            referensiId = null,
            referensiTipe = null,
            userId = "admin-1",
            tanggal = "2026-06-27",
            createdAt = "2026-06-27T12:00:00Z"
        )
        every { networkMonitor.isOnline() } returns true
        coEvery { kasDao.upsert(any()) } returns Unit
        coEvery { remoteSource.createKasTransaction(any()) } returns Unit

        // Act
        val result = repository.createKasTransaction(dummyKas).first { it is Resource.Success }

        // Assert
        assert(result is Resource.Success)
        coVerify(exactly = 1) { kasDao.upsert(dummyKas.toEntity(isSynced = true)) }
        coVerify(exactly = 1) { remoteSource.createKasTransaction(dummyKas.toDto()) }
    }
}
