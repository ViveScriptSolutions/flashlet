package com.example.data.repository

import com.example.data.local.DeckDao
import com.example.data.local.FlashcardDao
import com.example.data.model.Deck
import com.example.data.model.DeckWithStats
import com.example.data.model.Flashcard
import com.example.data.sample.SampleDecks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FlashcardRepository(
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {
    val decksWithStatsFlow: Flow<List<DeckWithStats>> = combine(
        deckDao.getAllDecksFlow(),
        flashcardDao.getAllCardsFlow()
    ) { decks, cards ->
        val cardsByDeck = cards.groupBy { it.deckId }
        decks.map { deck ->
            val deckCards = cardsByDeck[deck.id] ?: emptyList()
            val total = deckCards.size
            val remembered = deckCards.count { it.difficulty == 1 }
            val needPractice = deckCards.count { it.difficulty == 2 }
            val unreviewed = deckCards.count { it.difficulty == 0 }
            DeckWithStats(
                deck = deck,
                totalCards = total,
                rememberedCards = remembered,
                needPracticeCards = needPractice,
                unreviewedCards = unreviewed
            )
        }
    }

    fun getCardsForDeckFlow(deckId: Long): Flow<List<Flashcard>> =
        flashcardDao.getCardsForDeckFlow(deckId)

    suspend fun getCardsForDeck(deckId: Long): List<Flashcard> =
        flashcardDao.getCardsForDeck(deckId)

    suspend fun getDeckById(deckId: Long): Deck? =
        deckDao.getDeckById(deckId)

    suspend fun insertDeck(deck: Deck): Long =
        deckDao.insertDeck(deck)

    suspend fun updateDeck(deck: Deck) =
        deckDao.updateDeck(deck)

    suspend fun deleteDeck(deck: Deck) =
        deckDao.deleteDeck(deck)

    suspend fun duplicateDeck(deckId: Long): Long? {
        val originalDeck = deckDao.getDeckById(deckId) ?: return null
        val originalCards = flashcardDao.getCardsForDeck(deckId)

        val newDeck = originalDeck.copy(
            id = 0,
            title = "${originalDeck.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            isSample = false
        )
        val newDeckId = deckDao.insertDeck(newDeck)

        val duplicatedCards = originalCards.map { card ->
            card.copy(
                id = 0,
                deckId = newDeckId,
                difficulty = 0,
                reviewCount = 0,
                lastReviewedAt = 0L
            )
        }
        if (duplicatedCards.isNotEmpty()) {
            flashcardDao.insertCards(duplicatedCards)
        }
        return newDeckId
    }

    suspend fun insertCard(card: Flashcard): Long =
        flashcardDao.insertCard(card)

    suspend fun updateCard(card: Flashcard) =
        flashcardDao.updateCard(card)

    suspend fun deleteCard(card: Flashcard) =
        flashcardDao.deleteCard(card)

    suspend fun markCardReview(cardId: Long, difficulty: Int) =
        flashcardDao.markCardReview(cardId, difficulty, System.currentTimeMillis())

    suspend fun resetDeckProgress(deckId: Long) =
        flashcardDao.resetDeckProgress(deckId)

    suspend fun importDeckWithCards(deck: Deck, cards: List<Flashcard>): Long {
        val newDeckId = deckDao.insertDeck(deck)
        val readyCards = cards.map { it.copy(deckId = newDeckId) }
        if (readyCards.isNotEmpty()) {
            flashcardDao.insertCards(readyCards)
        }
        return newDeckId
    }

    suspend fun initializeSampleDecksIfEmpty() {
        if (deckDao.getDeckCount() == 0) {
            populateSampleDecks()
        }
    }

    suspend fun populateSampleDecks() {
        val samples = SampleDecks.getSampleDecks()
        for (sample in samples) {
            val deckId = deckDao.insertDeck(sample.deck)
            val cards = sample.cards.mapIndexed { index, (front, back) ->
                Flashcard(
                    deckId = deckId,
                    front = front,
                    back = back,
                    orderIndex = index
                )
            }
            flashcardDao.insertCards(cards)
        }
    }
}
