package com.example.mobileapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
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
import coil.compose.rememberAsyncImagePainter
import com.example.mobileapp.R
import com.example.mobileapp.MarvelApiService
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.runtime.rememberCoroutineScope
import com.example.mobileapp.Hero

@Composable
fun HeroDetail(navController: NavController, characterId: Int) {
    val hero = remember { mutableStateOf<Hero?>(null) } // состояние для героя
    val error = remember { mutableStateOf<String?>(null) } // состояние для ошибки
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(characterId) {
        coroutineScope.launch {
            try {
                val response = MarvelApiService.getCharacterDetails(characterId.toString())
                hero.value = response.data.results.firstOrNull()
            } catch (e: Exception) {
                error.value = "Не удалось загрузить данные: ${e.message}" // изменяем значение через .value
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Показываем экран ошибки, если ошибка присутствует
        if (error.value != null) {
            ErrorScreen(
                errorMessage = error.value ?: "Неизвестная ошибка",
                onRetry = {
                    error.value = null
                    coroutineScope.launch {
                        try {
                            val response = MarvelApiService.getCharacterDetails(characterId.toString())
                            hero.value = response.data.results.firstOrNull()
                        } catch (e: Exception) {
                            error.value = "Не удалось загрузить данные: ${e.message}"
                        }
                    }
                }
            )
        } else {
            // Показываем информацию о герое, если данные успешно загружены
            hero.value?.let { loadedHero ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(loadedHero.thumbnail.url),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "кнопка назад",
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
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = loadedHero.description,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 25.dp)
                        )
                    }
                }
            }
        }
    }
}

