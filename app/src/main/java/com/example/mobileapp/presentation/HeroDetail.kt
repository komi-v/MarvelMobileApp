package com.example.mobileapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mobileapp.R
import com.example.mobileapp.ui.theme.Typography
import com.example.mobileapp.MarvelApiService
import com.example.mobileapp.Hero
import kotlinx.coroutines.launch

@Composable
fun HeroDetail(navController: NavController, characterId: Int) {
    val hero = remember { mutableStateOf<Hero?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(characterId) {
        coroutineScope.launch {
            try {
                val response = MarvelApiService.getCharacterDetails(characterId.toString())
                hero.value = response.data.results.firstOrNull()
            } catch (e: Exception) {
                error = "Не удалось загрузить данные: ${e.message}"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (error != null) {
            Text(text = error ?: "", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            hero.value?.let { loadedHero ->
                // Отображение изображения и информации о герое
                Image(
                    painter = rememberAsyncImagePainter(loadedHero.thumbnail.path + "." + loadedHero.thumbnail.extension),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Кнопка назад
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "кнопка назад",
                        modifier = Modifier.size(50.dp)
                    )
                }

                // Текстовые данные
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(15.dp)
                ) {
                    Text(
                        text = loadedHero.name,
                        style = Typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = loadedHero.description,
                        style = Typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 25.dp)
                    )
                }
            }
        }
    }
}
