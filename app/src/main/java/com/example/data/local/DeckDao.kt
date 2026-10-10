package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun getAllDecksFlow(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id LIMIT 1")
    suspend fun getDeckById(id: Long): Deck?

    @Query("SELECT * FROM decks WHERE id = :id LIMIT 1")
    fun getDeckByIdFlow(id: Long): Flow<Deck?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck): Long

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteDeckById(id: Long)

    @Query("SELECT COUNT(*) FROM decks")
    suspend fun getDeckCount(): Int
}
