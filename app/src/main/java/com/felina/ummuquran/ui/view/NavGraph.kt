package com.felina.ummuquran.ui.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felina.ummuquran.ui.view.dashboard.DashboadView
import com.felina.ummuquran.ui.view.read.ReadView

object Routes {
    const val Dashboard = "dashboard"
    const val Read = "read/{id}"
}

@Composable
fun NavGraph(startDestination: String = Routes.Dashboard) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Dashboard) {
            DashboadView(navController)
        }
        composable(Routes.Read) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "1"
            ReadView(id)
        }
    }
}