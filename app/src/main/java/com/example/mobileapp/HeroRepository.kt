package com.example.mobileapp

import com.example.mobileapp.data.HeroDao
import com.example.mobileapp.data.HeroEntity

class HeroRepository(val heroDao: HeroDao) {
    suspend fun getHeroesFromDb(): List<HeroEntity> = heroDao.getAllHeroes()

    suspend fun updateHeroesFromApi(offset: Int) {
        val response = MarvelApiService.getCharacterList(offset)
        val heroEntities = response.data.results.map {
            HeroEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                img = it.thumbnail.url
            )
        }
        heroDao.insertHeroes(heroEntities)
    }

    suspend fun getHeroByIdFromDb(id: Int): HeroEntity? = heroDao.getHeroById(id)

    suspend fun getHeroByIdFromApi(id: Int): HeroEntity? {
        val response = MarvelApiService.getCharacterDetails(id.toString())
        val hero = response.data.results.firstOrNull()
        return hero?.let {
            HeroEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                img = it.thumbnail.url
            )
        }?.also { heroDao.insertHero(it) }
    }
}

