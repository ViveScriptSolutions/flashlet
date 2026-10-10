package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY orderIndex ASC, id ASC")
    fun getCardsForDeckFlow(deckId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY orderIndex ASC, id ASC")
    suspend fun getCardsForDeck(deckId: Long): List<Flashcard>

    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllCardsFlow(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Long): Flashcard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Flashcard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Flashcard>): List<Long>

    @Update
    suspend fun updateCard(card: Flashcard)

    @Delete
    suspend fun deleteCard(card: Flashcard)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteCardsForDeck(deckId: Long)

    @Query("UPDATE flashcards SET difficulty = :difficulty, reviewCount = reviewCount + 1, lastReviewedAt = :timestamp WHERE id = :cardId")
    suspend fun markCardReview(cardId: Long, difficulty: Int, timestamp: Long)

    @Query("UPDATE flashcards SET difficulty = 0, reviewCount = 0, lastReviewedAt = 0 WHERE deckId = :deckId")
    suspend fun resetDeckProgress(deckId: Long)
}
