package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.KasDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KasRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getKasList(): List<KasDto> {
        return supabase.postgrest.from("tbl_kas")
            .select()
            .decodeList<KasDto>()
    }

    suspend fun createKasTransaction(kasDto: KasDto) {
        supabase.postgrest.from("tbl_kas")
            .insert(kasDto)
    }
}
