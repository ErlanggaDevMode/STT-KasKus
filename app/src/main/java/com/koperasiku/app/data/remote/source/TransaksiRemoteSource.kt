package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.TransaksiDto
import com.koperasiku.app.data.remote.dto.TransaksiItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransaksiRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getTransaksiHistory(): List<TransaksiDto> {
        return supabase.postgrest.from("tbl_transaksi")
            .select()
            .decodeList<TransaksiDto>()
    }

    suspend fun getTransaksiItems(transaksiId: String): List<TransaksiItemDto> {
        return supabase.postgrest.from("tbl_transaksi_item")
            .select {
                filter {
                    eq("transaksi_id", transaksiId)
                }
            }
            .decodeList<TransaksiItemDto>()
    }

    suspend fun createTransaksi(transaksi: TransaksiDto, items: List<TransaksiItemDto>) {
        supabase.postgrest.from("tbl_transaksi").insert(transaksi)
        supabase.postgrest.from("tbl_transaksi_item").insert(items)
    }
}
