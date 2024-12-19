package com.example.mobileapp.presentation

import android.net.Uri
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.absoluteValue

@Composable
fun HeroList(navController: NavController) {
    val heroes = listOf(
        Hero("Deadpool", "https://cdn.marvel.com/content/1x/036dpl_com_crd_01.jpg", "healing factor and big mouth"),
        Hero("Iron Man", "https://cdn.marvel.com/content/1x/002irm_com_crd_01.jpg", "has a heart"),
        Hero("Spider-Man", "https://cdn.marvel.com/content/1x/005smp_com_crd_01.jpg", "The great responsibility")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleGrey40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
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

        val listState = rememberLazyListState()
        val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp)
                .padding(horizontal = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                state = listState,
                flingBehavior = snapBehavior,
                content = {
                    itemsIndexed(heroes) { index, hero ->
                        val visibleItemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                        val offset = visibleItemInfo?.offset?.toFloat() ?: 0f

                        val translationY = (offset.absoluteValue * 0.1f).coerceIn(0f, 50f)
                        val alpha = (1f - (offset.absoluteValue / 400f).coerceIn(0f, 1f))

                        HeroCard(
                            hero = hero,
                            translationY = translationY,
                            alpha = alpha
                        ) {
                            navController.navigate("hero_detail/${Uri.encode(hero.name)}/${Uri.encode(hero.image)}/${Uri.encode(hero.description)}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun HeroCard(hero: Hero, translationY: Float, alpha: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(600.dp)
            .graphicsLayer(
                translationY = translationY,
                alpha = alpha
            )
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(hero.image),
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