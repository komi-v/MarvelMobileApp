package com.example.mobileapp

import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import com.example.mobileapp.presentation.HeroDetail
import com.example.mobileapp.presentation.HeroList
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            HeroApp(navController)
        }
    }
}

@Composable
fun HeroApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "hero_list") {
        composable("hero_list") {
            HeroList(navController = navController)
        }
        composable(
            route = "hero_detail/{name}/{image}/{description}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val image = backStackEntry.arguments?.getString("image") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            HeroDetail(
                navController = navController,
                name = name,
                image = image,
                description = description
            )
        }
    }
}
