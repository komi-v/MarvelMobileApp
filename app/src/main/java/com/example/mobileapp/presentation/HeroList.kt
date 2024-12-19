package com.example.mobileapp.presentation

import android.net.Uri
import android.util.Log
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobileapp.ui.theme.Typography
import androidx.compose.ui.res.painterResource
import com.example.mobileapp.R
import com.example.mobileapp.ui.theme.*
import com.example.mobileapp.MarvelApiService
import com.example.mobileapp.Thumbnail
import com.example.mobileapp.Hero
import com.example.mobileapp.CharacterData
import com.example.mobileapp.CharacterResponse
import kotlin.math.absoluteValue


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HeroList(navController: NavController) {
    val heroes = remember { mutableStateListOf<Hero>() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var offset by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Функция для загрузки героев с использованием offset
    fun loadHeroes() {
        if (isLoading) return // Избегаем одновременной загрузки
        isLoading = true
        coroutineScope.launch {
            try {
                val response = MarvelApiService.getCharacterList(offset)
                if (response.data.results.isNotEmpty()) {
                    heroes.addAll(response.data.results)
                    offset += 10 // Увеличиваем offset для следующей подгрузки
                } else {
                    error = "Больше героев нет"
                }
            } catch (e: Exception) {
                error = "Не удалось загрузить данные: ${e.message}"
                Log.e("HeroList", "Ошибка загрузки данных", e)
            } finally {
                isLoading = false
            }
        }
    }

    // Первоначальная загрузка
    LaunchedEffect(Unit) {
        loadHeroes()
    }

    // Автоматическая подгрузка при достижении конца списка
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == heroes.size - 1 }
            .collect { isAtEnd ->
                if (isAtEnd && !isLoading) loadHeroes()
            }
    }

    if (error != null) {
        Text(
            text = error ?: "",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "логотип марвел",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp)
            )
            Text(
                text = "Choose your hero",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = Typography.bodyLarge
            )
            val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(state = listState, flingBehavior = snapBehavior) {
                    items(heroes) { hero ->
                        HeroCard(hero = hero, onClick = { navController.navigate("hero_detail/${hero.id}") })
                    }
                }
            }
        }
    }
}

@Composable
fun HeroCard(hero: Hero, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(30.dp))
            .width(270.dp)
            .height(500.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(hero.thumbnail.url),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = hero.name,
            style = Typography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        )
    }
}

data class Hero(val name: String, val image: String, val description: String)