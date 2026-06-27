package com.koperasiku.app.data.remote.source

import com.koperasiku.app.data.remote.dto.AngsuranDto
import com.koperasiku.app.data.remote.dto.PinjamanDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinjamanRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getPinjamanList(): List<PinjamanDto> {
        return supabase.postgrest.from("tbl_pinjaman")
            .select()
            .decodeList<PinjamanDto>()
    }

    suspend fun getPinjamanDetail(pinjamanId: String): PinjamanDto? {
        return supabase.postgrest.from("tbl_pinjaman")
            .select {
                filter { eq("id", pinjamanId) }
            }
            .decodeSingleOrNull<PinjamanDto>()
    }

    suspend fun getAngsuranList(pinjamanId: String): List<AngsuranDto> {
        return supabase.postgrest.from("tbl_angsuran")
            .select {
                filter { eq("pinjaman_id", pinjamanId) }
            }
            .decodeList<AngsuranDto>()
    }

    suspend fun insertPinjaman(dto: PinjamanDto) {
        supabase.postgrest.from("tbl_pinjaman").insert(dto)
    }

    suspend fun upsertPinjaman(dto: PinjamanDto) {
        supabase.postgrest.from("tbl_pinjaman").upsert(dto)
    }

    suspend fun upsertAngsuran(dto: AngsuranDto) {
        supabase.postgrest.from("tbl_angsuran").upsert(dto)
    }

    suspend fun insertAngsuranBatch(dtoList: List<AngsuranDto>) {
        supabase.postgrest.from("tbl_angsuran").insert(dtoList)
    }
}
