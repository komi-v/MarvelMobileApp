package com.example.mobileapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobileapp.ui.theme.Typography
import coil.compose.rememberAsyncImagePainter
import com.example.mobileapp.R
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.runtime.rememberCoroutineScope
import com.example.mobileapp.HeroRepository
import com.example.mobileapp.data.toUI
import okhttp3.HttpUrl.Companion.toHttpUrl

@Composable
fun HeroDetail(
    navController: NavController,
    characterId: Int,
    heroRepository: HeroRepository
) {
    val hero = remember { mutableStateOf<Heroy?>(null) }
    val error = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(characterId) {
        coroutineScope.launch {
            try {
                val localHero = heroRepository.getHeroByIdFromDb(characterId)?.toUI()
                hero.value = localHero

                if (localHero == null) {
                    val remoteHero = heroRepository.getHeroByIdFromApi(characterId)?.toUI()
                    hero.value = remoteHero
                }
            } catch (e: Exception) {
                error.value = "Не удалось загрузить данные: ${e.message}"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (error.value != null) {
            ErrorScreen(
                errorMessage = error.value ?: "Неизвестная ошибка",
                onRetry = {
                    error.value = null
                    coroutineScope.launch {
                        try {
                            val remoteHero = heroRepository.getHeroByIdFromApi(characterId)?.toUI()
                            hero.value = remoteHero
                        } catch (e: Exception) {
                            error.value = "Не удалось загрузить данные: ${e.message}"
                        }
                    }
                }
            )
        } else {
            hero.value?.let { loadedHero ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(loadedHero.img.toHttpUrl()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Назад",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(15.dp)
                    ) {
                        Text(
                            text = loadedHero.name,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 10.dp),
                            style = Typography.titleLarge
                        )
                        Text(
                            text = loadedHero.description,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 25.dp),
                            style = Typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}


