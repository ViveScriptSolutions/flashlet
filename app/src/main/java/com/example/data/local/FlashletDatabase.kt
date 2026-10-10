package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Deck
import com.example.data.model.Flashcard

@Database(
    entities = [Deck::class, Flashcard::class],
    version = 1,
    exportSchema = false
)
abstract class FlashletDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: FlashletDatabase? = null

        fun getDatabase(context: Context): FlashletDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashletDatabase::class.java,
                    "flashlet_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
