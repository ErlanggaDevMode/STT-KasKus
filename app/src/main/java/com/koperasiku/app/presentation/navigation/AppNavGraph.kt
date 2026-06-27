package com.koperasiku.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.koperasiku.app.presentation.anggota.AnggotaDetailScreen
import com.koperasiku.app.presentation.anggota.AnggotaFormScreen
import com.koperasiku.app.presentation.anggota.AnggotaListScreen
import com.koperasiku.app.presentation.auth.ForgotPasswordScreen
import com.koperasiku.app.presentation.auth.LoginScreen
import com.koperasiku.app.presentation.dashboard.DashboardScreen
import com.koperasiku.app.presentation.kas.KasFormScreen
import com.koperasiku.app.presentation.kas.KasScreen
import com.koperasiku.app.presentation.pinjaman.BayarAngsuranScreen
import com.koperasiku.app.presentation.pinjaman.PinjamanDetailScreen
import com.koperasiku.app.presentation.pinjaman.PinjamanFormScreen
import com.koperasiku.app.presentation.pinjaman.PinjamanScreen
import com.koperasiku.app.presentation.pos.PosScreen
import com.koperasiku.app.presentation.pos.TransaksiHistoryScreen
import com.koperasiku.app.presentation.profil.ProfilScreen
import com.koperasiku.app.presentation.simpanan.SimpananDetailScreen
import com.koperasiku.app.presentation.simpanan.SimpananFormScreen
import com.koperasiku.app.presentation.simpanan.SimpananScreen
import com.koperasiku.app.presentation.stok.ProdukFormScreen
import com.koperasiku.app.presentation.stok.StokDetailScreen
import com.koperasiku.app.presentation.stok.StokListScreen
import com.koperasiku.app.presentation.stok.StokMasukScreen
import com.koperasiku.app.presentation.stok.StokOpnameScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        // Main Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // Anggota Flow
        composable(Screen.Anggota.route) {
            AnggotaListScreen(navController = navController)
        }
        composable(
            route = Screen.AnggotaDetail.route,
            arguments = listOf(navArgument("anggotaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val anggotaId = backStackEntry.arguments?.getString("anggotaId").orEmpty()
            AnggotaDetailScreen(anggotaId = anggotaId, navController = navController)
        }
        composable(
            route = Screen.AnggotaForm.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            AnggotaFormScreen(anggotaId = id, navController = navController)
        }

        // Stok Flow
        composable(Screen.Stok.route) {
            StokListScreen(navController = navController)
        }
        composable(
            route = Screen.StokDetail.route,
            arguments = listOf(navArgument("produkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()
            StokDetailScreen(produkId = produkId, navController = navController)
        }
        composable(
            route = Screen.ProdukForm.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProdukFormScreen(produkId = id, navController = navController)
        }
        composable(
            route = Screen.StokMasuk.route,
            arguments = listOf(navArgument("produkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()
            StokMasukScreen(produkId = produkId, navController = navController)
        }
        composable(Screen.StokOpname.route) {
            StokOpnameScreen(navController = navController)
        }

        // POS Flow
        composable(Screen.Pos.route) {
            PosScreen(navController = navController)
        }
        composable(Screen.RiwayatTransaksi.route) {
            TransaksiHistoryScreen(navController = navController)
        }

        // Kas Flow
        composable(Screen.Keuangan.route) {
            KasScreen(navController = navController)
        }
        composable(Screen.KasForm.route) {
            KasFormScreen(navController = navController)
        }

        // Simpanan Flow
        composable(Screen.Simpanan.route) {
            SimpananScreen(navController = navController)
        }
        composable(
            route = Screen.SimpananDetail.route,
            arguments = listOf(navArgument("anggotaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val anggotaId = backStackEntry.arguments?.getString("anggotaId").orEmpty()
            SimpananDetailScreen(anggotaId = anggotaId, navController = navController)
        }
        composable(
            route = Screen.SimpananForm.route,
            arguments = listOf(navArgument("anggotaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val anggotaId = backStackEntry.arguments?.getString("anggotaId").orEmpty()
            SimpananFormScreen(anggotaId = anggotaId, navController = navController)
        }

        // Pinjaman Flow
        composable(Screen.Pinjaman.route) {
            PinjamanScreen(navController = navController)
        }
        composable(
            route = Screen.PinjamanDetail.route,
            arguments = listOf(navArgument("pinjamanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pinjamanId = backStackEntry.arguments?.getString("pinjamanId").orEmpty()
            PinjamanDetailScreen(pinjamanId = pinjamanId, navController = navController)
        }
        composable(Screen.PinjamanForm.route) {
            PinjamanFormScreen(navController = navController)
        }
        composable(
            route = Screen.BayarAngsuran.route,
            arguments = listOf(navArgument("angsuranId") { type = NavType.StringType })
        ) { backStackEntry ->
            val angsuranId = backStackEntry.arguments?.getString("angsuranId").orEmpty()
            BayarAngsuranScreen(angsuranId = angsuranId, navController = navController)
        }

        // Profil
        composable(Screen.Profil.route) {
            ProfilScreen(navController = navController)
        }
    }
}
