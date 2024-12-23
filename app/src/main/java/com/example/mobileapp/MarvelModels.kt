package com.example.mobileapp

data class CharacterResponse(
    val data: CharacterData
)

data class CharacterData(
    val results: List<Hero>
)

data class Hero(
    val id: Int,
    val name: String,
    val description: String,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val path: String,
    val extension: String
) {
    val url: String get() = "$path.$extension"
}
