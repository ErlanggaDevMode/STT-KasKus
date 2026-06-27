package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.AnggotaDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnggotaRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getAnggotaList(): List<AnggotaDto> {
        return supabase.postgrest.from("tbl_anggota")
            .select()
            .decodeList<AnggotaDto>()
    }

    suspend fun getAnggotaDetail(id: String): AnggotaDto {
        return supabase.postgrest.from("tbl_anggota")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<AnggotaDto>()
    }

    suspend fun createAnggota(anggotaDto: AnggotaDto) {
        supabase.postgrest.from("tbl_anggota")
            .insert(anggotaDto)
    }

    suspend fun updateAnggota(anggotaDto: AnggotaDto) {
        supabase.postgrest.from("tbl_anggota")
            .update(anggotaDto) {
                filter {
                    eq("id", anggotaDto.id)
                }
            }
    }

    suspend fun deactivateAnggota(id: String) {
        supabase.postgrest.from("tbl_anggota")
            .update({
                set("is_aktif", false)
            }) {
                filter {
                    eq("id", id)
                }
            }
    }
}
