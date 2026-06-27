package com.koperasiku.app.data.remote.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.koperasiku.app.data.remote.dto.ProdukDto
import com.koperasiku.app.data.remote.dto.MutasiStokDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdukRemoteSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getProdukList(): List<ProdukDto> {
        return supabase.postgrest.from("tbl_produk")
            .select {
                filter {
                    eq("is_aktif", true)
                }
            }
            .decodeList<ProdukDto>()
    }

    suspend fun getProdukDetail(id: String): ProdukDto {
        return supabase.postgrest.from("tbl_produk")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<ProdukDto>()
    }

    suspend fun createProduk(produkDto: ProdukDto) {
        supabase.postgrest.from("tbl_produk")
            .insert(produkDto)
    }

    suspend fun updateProduk(produkDto: ProdukDto) {
        supabase.postgrest.from("tbl_produk")
            .update(produkDto) {
                filter {
                    eq("id", produkDto.id)
                }
            }
    }

    suspend fun updateStok(produkId: String, newStok: Int) {
        supabase.postgrest.from("tbl_produk")
            .update({
                set("stok_saat_ini", newStok)
            }) {
                filter {
                    eq("id", produkId)
                }
            }
    }

    suspend fun createMutasiStok(mutasiDto: MutasiStokDto) {
        supabase.postgrest.from("tbl_mutasi_stok")
            .insert(mutasiDto)
    }

    suspend fun getMutasiStok(produkId: String): List<MutasiStokDto> {
        return supabase.postgrest.from("tbl_mutasi_stok")
            .select {
                filter {
                    eq("produk_id", produkId)
                }
            }
            .decodeList<MutasiStokDto>()
    }
}
