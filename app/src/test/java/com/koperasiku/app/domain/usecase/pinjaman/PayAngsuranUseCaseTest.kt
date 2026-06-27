package com.koperasiku.app.domain.usecase.pinjaman

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.repository.PinjamanRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PayAngsuranUseCaseTest {

    private val repository: PinjamanRepository = mockk()
    private val useCase = PayAngsuranUseCase(repository)

    @Test
    fun `when invoke should call payAngsuran on repository`() = runTest {
        // Arrange
        val angsuranId = "angsuran-123"
        val denda = 10000L
        val expectedResource = Resource.Success(Unit)
        coEvery { repository.payAngsuran(angsuranId, denda) } returns flowOf(expectedResource)

        // Act
        val flow = useCase(angsuranId, denda)

        // Assert
        flow.collect { resource ->
            assertEquals(expectedResource, resource)
        }
        coVerify(exactly = 1) { repository.payAngsuran(angsuranId, denda) }
    }
}
