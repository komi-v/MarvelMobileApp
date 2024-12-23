package com.example.mobileapp.data

import android.content.Context
import androidx.room.*

@Entity(tableName = "heroes")
data class HeroEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val img: String
)


@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes")
    suspend fun getAllHeroes(): List<HeroEntity>

    @Query("SELECT * FROM heroes WHERE id = :id")
    suspend fun getHeroById(id: Int): HeroEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroes(heroes: List<HeroEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHero(hero: HeroEntity)
}

@Database(entities = [HeroEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heroes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}




