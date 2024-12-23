package com.example.mobileapp.data

import com.example.mobileapp.Hero
import com.example.mobileapp.presentation.Heroy

fun Hero.toEntity(): HeroEntity {
    return HeroEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        img = this.thumbnail.url
    )
}

fun HeroEntity.toUI(): Heroy {
    return Heroy(
        id = this.id,
        name = this.name,
        description = this.description,
        img = this.img
    )
}

fun Hero.toUI(): Heroy {
    return Heroy(
        id = this.id,
        name = this.name,
        description = this.description,
        img = this.thumbnail.url
    )
}
