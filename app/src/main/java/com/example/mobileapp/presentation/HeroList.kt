package com.example.mobileapp.presentation

import android.util.Log
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobileapp.ui.theme.Typography
import androidx.compose.ui.res.painterResource
import com.example.mobileapp.R
import com.example.mobileapp.MarvelApiService
import com.example.mobileapp.Hero
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.example.mobileapp.data.HeroDao
import com.example.mobileapp.data.toEntity
import com.example.mobileapp.data.toUI
import kotlinx.coroutines.launch

@Composable
fun HeroList(navController: NavController, heroDao: HeroDao) {
    val heroes = remember { mutableStateListOf<Heroy>() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var offset by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun loadHeroesFromApi() {
        if (isLoading) return
        isLoading = true
        coroutineScope.launch {
            try {
                val response = MarvelApiService.getCharacterList(offset)
                val newHeroes = response.data.results.map {
                    Hero(
                        id = it.id,
                        name = it.name,
                        description = it.description ?: "",
                        thumbnail = it.thumbnail
                    )
                }

                Log.d("HeroList", "load ${newHeroes.size} heroes from API")

                val existingHeroesIds = heroDao.getAllHeroes().map { it.id }.toSet()
                val uniqueHeroes = newHeroes.filter { it.id !in existingHeroesIds }

                if (uniqueHeroes.isNotEmpty()) {
                    val heroyList = uniqueHeroes.map { it.toUI() }

                    heroes.addAll(heroyList)

                    heroDao.insertHeroes(uniqueHeroes.map { it.toEntity() })
                    Log.d("HeroList", "load ${uniqueHeroes.size} heroes to db")
                } else {
                    Log.d("HeroList", "all heroes already in db")
                }


                if (newHeroes.isNotEmpty()) {
                    offset += 100
                }
            } catch (e: Exception) {
                error = "load error: ${e.message}"
                Log.e("HeroList", "error loading hero", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val cachedHeroes = heroDao.getAllHeroes().map { it.toUI() }
            heroes.addAll(cachedHeroes)
            Log.d("HeroList", "load ${cachedHeroes.size} heroes from db")
            loadHeroesFromApi()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == heroes.size - 1 }
            .collect { isAtEnd ->
                if (isAtEnd && !isLoading) loadHeroesFromApi()
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
fun HeroCard(hero: Heroy, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(30.dp))
            .width(270.dp)
            .height(500.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(hero.img),
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

data class Heroy(val id: Int, val name: String, val img: String, val description: String)