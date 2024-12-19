package com.example.mobileapp

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.MessageDigest

object MarvelApiService {
    private const val BASE_URL = "https://gateway.marvel.com/v1/public/"
    private const val PUBLIC_KEY = "6ec1b851dacd3274f7794e79fd851900"
    private const val PRIVATE_KEY = "380c8b97a3d2355c849dfc6150efd849866e0fe0"

    private fun createHash(timestamp: String): String {
        val value = "$timestamp$PRIVATE_KEY$PUBLIC_KEY"
        val md5 = MessageDigest.getInstance("MD5")
        return md5.digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: MarvelApi = retrofit.create(MarvelApi::class.java)

    interface MarvelApi {
        @GET("characters")
        suspend fun getCharacters(
            @Query("apikey") apiKey: String = PUBLIC_KEY,
            @Query("ts") timestamp: String,
            @Query("hash") hash: String,
            @Query("offset") offset: Int = 0, // Offset for pagination
            @Query("limit") limit: Int = 1   // Limit per request
        ): CharacterResponse

        @GET("characters/{characterId}")
        suspend fun getCharacterDetails(
            @Path("characterId") characterId: String,
            @Query("apikey") apiKey: String = PUBLIC_KEY,
            @Query("ts") timestamp: String,
            @Query("hash") hash: String
        ): CharacterResponse
    }

    suspend fun getCharacterList(offset: Int = 0, limit: Int = 1): CharacterResponse {
        val timestamp = System.currentTimeMillis().toString()
        return api.getCharacters(
            timestamp = timestamp,
            hash = createHash(timestamp),
            offset = offset,  // здесь мы передаем offset в API-запрос
            limit = limit)    // здесь мы передаем limit в API-запрос
    }

    suspend fun getCharacterDetails(characterId: String): CharacterResponse {
        val timestamp = System.currentTimeMillis().toString()
        return api.getCharacterDetails(
            characterId = characterId,
            timestamp = timestamp,
            hash = createHash(timestamp)
        )
    }
}
