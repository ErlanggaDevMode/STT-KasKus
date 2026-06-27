package com.koperasiku.app.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")

    // Main
    object Dashboard : Screen("dashboard")
    
    // Anggota
    object Anggota : Screen("anggota")
    object AnggotaDetail : Screen("anggota/{anggotaId}") {
        fun createRoute(id: String) = "anggota/$id"
    }
    object AnggotaForm : Screen("anggota/form?id={id}") {
        fun createRoute(id: String?) = "anggota/form?id=$id"
    }

    // Stok
    object Stok : Screen("stok")
    object StokDetail : Screen("stok/{produkId}") {
        fun createRoute(id: String) = "stok/$id"
    }
    object ProdukForm : Screen("produk/form?id={id}") {
        fun createRoute(id: String?) = "produk/form?id=$id"
    }
    object StokMasuk : Screen("stok/masuk/{produkId}") {
        fun createRoute(id: String) = "stok/masuk/$id"
    }
    object StokOpname : Screen("stok/opname")

    // POS
    object Pos : Screen("pos")
    object RiwayatTransaksi : Screen("pos/riwayat")

    // Kas / Keuangan
    object Keuangan : Screen("keuangan")
    object KasForm : Screen("keuangan/form")

    // Simpanan
    object Simpanan : Screen("simpanan")
    object SimpananDetail : Screen("simpanan/{anggotaId}") {
        fun createRoute(id: String) = "simpanan/$id"
    }
    object SimpananForm : Screen("simpanan/form/{anggotaId}") {
        fun createRoute(id: String) = "simpanan/form/$id"
    }

    // Pinjaman
    object Pinjaman : Screen("pinjaman")
    object PinjamanDetail : Screen("pinjaman/{pinjamanId}") {
        fun createRoute(id: String) = "pinjaman/$id"
    }
    object PinjamanForm : Screen("pinjaman/form")
    object BayarAngsuran : Screen("pinjaman/bayar/{angsuranId}") {
        fun createRoute(id: String) = "pinjaman/bayar/$id"
    }

    object Profil : Screen("profil")
}
