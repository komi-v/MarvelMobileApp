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
import com.example.mobileapp.presentation.HeroDetail
import com.example.mobileapp.presentation.HeroList


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
            route = "hero_detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0
            HeroDetail(navController = navController, characterId = characterId)
        }
    }
}
