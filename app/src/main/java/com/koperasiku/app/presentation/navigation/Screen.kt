package com.koperasiku.app.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")

    // Main (setelah login)
    object Dashboard : Screen("dashboard")
    object Anggota : Screen("anggota")
    object AnggotaDetail : Screen("anggota/{anggotaId}") {
        fun createRoute(id: String) = "anggota/$id"
    }
    object AnggotaForm : Screen("anggota/form?id={id}") // null id = create
    object Stok : Screen("stok")
    object StokDetail : Screen("stok/{produkId}") {
        fun createRoute(id: String) = "stok/$id"
    }
    object ProdukForm : Screen("produk/form?id={id}")
    object StokMasuk : Screen("stok/masuk/{produkId}") {
        fun createRoute(id: String) = "stok/masuk/$id"
    }
    object StokOpname : Screen("stok/opname")
    object Pos : Screen("pos")
    object Struk : Screen("pos/struk/{transaksiId}") {
        fun createRoute(id: String) = "pos/struk/$id"
    }
    object RiwayatTransaksi : Screen("pos/riwayat")
    object Keuangan : Screen("keuangan")
    object CatatKas : Screen("keuangan/catat")
    object SimpanPinjam : Screen("simpanpinjam")
    object Setoran : Screen("simpanpinjam/setoran/{anggotaId}") {
        fun createRoute(id: String) = "simpanpinjam/setoran/$id"
    }
    object PinjamanDetail : Screen("pinjaman/{pinjamanId}") {
        fun createRoute(id: String) = "pinjaman/$id"
    }
    object AjukanPinjaman : Screen("pinjaman/ajukan")
    object ApprovalPinjaman : Screen("pinjaman/approval/{pinjamanId}") {
        fun createRoute(id: String) = "pinjaman/approval/$id"
    }
    object BayarAngsuran : Screen("pinjaman/bayar/{pinjamanId}") {
        fun createRoute(id: String) = "pinjaman/bayar/$id"
    }
    object Laporan : Screen("laporan")
    object Profil : Screen("profil")
}
