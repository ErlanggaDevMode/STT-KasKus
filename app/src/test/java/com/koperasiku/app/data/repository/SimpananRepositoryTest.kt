package com.koperasiku.app.data.repository

import com.koperasiku.app.core.network.NetworkMonitor
import com.koperasiku.app.core.session.SessionManager
import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.data.local.dao.AnggotaDao
import com.koperasiku.app.data.local.dao.KasDao
import com.koperasiku.app.data.local.dao.MutasiSimpananDao
import com.koperasiku.app.data.local.dao.SimpananDao
import com.koperasiku.app.data.local.entity.AnggotaEntity
import com.koperasiku.app.data.local.entity.SimpananEntity
import com.koperasiku.app.data.remote.source.SimpananRemoteSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimpananRepositoryTest {

    private val simpananDao: SimpananDao = mockk(relaxed = true)
    private val mutasiDao: MutasiSimpananDao = mockk(relaxed = true)
    private val anggotaDao: AnggotaDao = mockk(relaxed = true)
    private val kasDao: KasDao = mockk(relaxed = true)
    private val remoteSource: SimpananRemoteSource = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)

    private val repository = SimpananRepositoryImpl(
        simpananDao,
        mutasiDao,
        anggotaDao,
        kasDao,
        remoteSource,
        networkMonitor,
        sessionManager
    )

    @Test
    fun `executeSimpananTransaction SETORAN should increase savings balance and create ledger and mutasi entries`() = runTest {
        // Arrange
        val anggotaId = "anggota-1"
        val jenisSimpanan = "SUKARELA"
        val jumlahSetor = 100000L
        val currentSaldo = 200000L

        val dummyAnggota = AnggotaEntity(
            id = anggotaId,
            nomorAnggota = "AGT-001",
            nama = "Budi",
            noHp = "0812",
            isAktif = true,
            createdAt = "2026-06-27"
        )
        val dummySimpanan = SimpananEntity(
            id = "sim-1",
            anggotaId = anggotaId,
            jenis = jenisSimpanan,
            saldo = currentSaldo
        )

        coEvery { anggotaDao.getById(anggotaId) } returns dummyAnggota
        coEvery { simpananDao.getSimpananByJenis(anggotaId, jenisSimpanan) } returns dummySimpanan
        every { networkMonitor.isOnline() } returns false
        every { sessionManager.currentUserId } returns "admin-1"

        // Act
        val flow = repository.executeSimpananTransaction(
            anggotaId = anggotaId,
            jenisTransaction = "SETORAN",
            jenisSimpanan = jenisSimpanan,
            jumlah = jumlahSetor,
            keterangan = "Setor Sukarela"
        )
        val results = flow.toList()

        // Assert
        assertTrue(results.any { it is Resource.Success })
        
        // Verify balance updated (current 200k + setor 100k = 300k)
        coVerify(exactly = 1) { 
            simpananDao.upsert(withArg { entity ->
                assertEquals(300000L, entity.saldo)
                assertEquals(anggotaId, entity.anggotaId)
                assertEquals(jenisSimpanan, entity.jenis)
            }) 
        }

        // Verify mutasi simpanan was saved
        coVerify(exactly = 1) {
            mutasiDao.upsert(withArg { mutasi ->
                assertEquals(anggotaId, mutasi.anggotaId)
                assertEquals("SETORAN", mutasi.jenis)
                assertEquals(jumlahSetor, mutasi.jumlah)
                assertEquals(currentSaldo, mutasi.saldoSebelum)
                assertEquals(300000L, mutasi.saldoSesudah)
            })
        }

        // Verify kas entry was created
        coVerify(exactly = 1) {
            kasDao.upsert(withArg { kas ->
                assertEquals("MASUK", kas.jenis)
                assertEquals("SIMPANAN", kas.kategoriId)
                assertEquals(jumlahSetor, kas.jumlah)
            })
        }
    }

    @Test
    fun `executeSimpananTransaction PENARIKAN with insufficient balance should fail with error resource`() = runTest {
        // Arrange
        val anggotaId = "anggota-1"
        val jenisSimpanan = "SUKARELA"
        val jumlahTarik = 250000L
        val currentSaldo = 200000L // current saldo is less than jumlahTarik

        val dummyAnggota = AnggotaEntity(
            id = anggotaId,
            nomorAnggota = "AGT-001",
            nama = "Budi",
            noHp = "0812",
            isAktif = true,
            createdAt = "2026-06-27"
        )
        val dummySimpanan = SimpananEntity(
            id = "sim-1",
            anggotaId = anggotaId,
            jenis = jenisSimpanan,
            saldo = currentSaldo
        )

        coEvery { anggotaDao.getById(anggotaId) } returns dummyAnggota
        coEvery { simpananDao.getSimpananByJenis(anggotaId, jenisSimpanan) } returns dummySimpanan

        // Act
        val flow = repository.executeSimpananTransaction(
            anggotaId = anggotaId,
            jenisTransaction = "PENARIKAN",
            jenisSimpanan = jenisSimpanan,
            jumlah = jumlahTarik,
            keterangan = "Tarik Sukarela"
        )
        val results = flow.toList()

        // Assert
        val errorResult = results.first { it is Resource.Error } as Resource.Error
        assertTrue(errorResult.message.contains("Saldo SUKARELA tidak mencukupi"))
        
        // Verify no updates or writes were made to Room
        coVerify(exactly = 0) { simpananDao.upsert(any()) }
        coVerify(exactly = 0) { mutasiDao.upsert(any()) }
        coVerify(exactly = 0) { kasDao.upsert(any()) }
    }
}
