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
import com.example.mobileapp.data.AppDatabase
import com.example.mobileapp.data.HeroDao


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val heroDao = database.heroDao()

        setContent {
            val navController = rememberNavController()
            HeroApp(navController = navController, heroDao = heroDao)
        }
    }
}

@Composable
fun HeroApp(navController: NavHostController, heroDao: HeroDao) {
    NavHost(navController = navController, startDestination = "hero_list") {
        composable("hero_list") {
            HeroList(navController = navController, heroDao = heroDao)
        }
        composable(
            route = "hero_detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0
            HeroDetail(navController = navController, characterId = characterId, heroRepository = HeroRepository(heroDao = heroDao))
        }
    }
}
