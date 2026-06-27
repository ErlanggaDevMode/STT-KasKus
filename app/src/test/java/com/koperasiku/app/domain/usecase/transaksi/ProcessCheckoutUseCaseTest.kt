package com.koperasiku.app.domain.usecase.transaksi

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.repository.TransaksiRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProcessCheckoutUseCaseTest {

    private val repository: TransaksiRepository = mockk()
    private val useCase = ProcessCheckoutUseCase(repository)

    @Test
    fun `when invoke should call processCheckout on repository`() = runTest {
        // Arrange
        val dummyTransaksi = Transaksi(
            id = "tx-123",
            nomorFaktur = "FAK-001",
            anggotaId = "anggota-1",
            totalBelanja = 100000L,
            nominalBayar = 100000L,
            kembalian = 0L,
            tanggal = "2026-06-27",
            createdAt = "2026-06-27T12:00:00Z",
            items = emptyList()
        )
        val expectedResource = Resource.Success(Unit)
        coEvery { repository.processCheckout(dummyTransaksi) } returns flowOf(expectedResource)

        // Act
        val flow = useCase(dummyTransaksi)

        // Assert
        flow.collect { resource ->
            assertEquals(expectedResource, resource)
        }
        coVerify(exactly = 1) { repository.processCheckout(dummyTransaksi) }
    }
}
