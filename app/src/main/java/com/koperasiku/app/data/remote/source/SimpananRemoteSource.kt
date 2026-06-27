package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.SimpananDto
import com.koperasiku.app.data.remote.dto.MutasiSimpananDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpananRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getSimpananList(): List<SimpananDto> {
        return supabase.postgrest.from("tbl_simpanan")
            .select()
            .decodeList<SimpananDto>()
    }

    suspend fun getSimpananForAnggota(anggotaId: String): List<SimpananDto> {
        return supabase.postgrest.from("tbl_simpanan")
            .select {
                filter {
                    eq("anggota_id", anggotaId)
                }
            }
            .decodeList<SimpananDto>()
    }

    suspend fun upsertSimpanan(simpananDto: SimpananDto) {
        supabase.postgrest.from("tbl_simpanan")
            .upsert(simpananDto)
    }

    suspend fun createMutasiSimpanan(mutasiDto: MutasiSimpananDto) {
        supabase.postgrest.from("tbl_mutasi_simpanan")
            .insert(mutasiDto)
    }

    suspend fun getMutasiSimpanan(anggotaId: String): List<MutasiSimpananDto> {
        return supabase.postgrest.from("tbl_mutasi_simpanan")
            .select {
                filter {
                    eq("anggota_id", anggotaId)
                }
            }
            .decodeList<MutasiSimpananDto>()
    }
}
