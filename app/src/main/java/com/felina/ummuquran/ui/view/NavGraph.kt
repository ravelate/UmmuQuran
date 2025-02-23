package com.felina.ummuquran.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felina.ummuquran.R
import com.felina.ummuquran.ui.view.dashboard.DashboardView
import com.felina.ummuquran.ui.view.quran.QuranView
import com.felina.ummuquran.ui.view.read.ReadView

object Routes {
    const val Dashboard = "dashboard"
    const val Quran = "quran"
    const val Read = "read/{id}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(startDestination: String = Routes.Dashboard) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Dashboard) {
            DashboardView(navController)
        }
        composable(Routes.Quran) {
            QuranView(navController)
        }
        composable(Routes.Read) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "1"
            ReadView(id)
        }
    }
}

sealed class NavDestination(val title: String, val route: String, val icon: Int) {
    object dashboard: NavDestination(
        title = "Dashboard",
        route = "dashboard",
        icon = R.drawable.dashboard
    )
    object quran: NavDestination(
        title = "Al-Quran",
        route = "quran",
        icon = R.drawable.quran
    )
}