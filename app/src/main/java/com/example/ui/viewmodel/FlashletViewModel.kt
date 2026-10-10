package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.DeckExporter
import com.example.data.local.FlashletDatabase
import com.example.data.model.Deck
import com.example.data.model.DeckWithStats
import com.example.data.model.Flashcard
import com.example.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface Screen {
    object DeckList : Screen
    data class DeckDetail(val deckId: Long) : Screen
    data class Study(val deckId: Long, val difficultOnly: Boolean = false) : Screen
    data class StudySummary(
        val deckId: Long,
        val deckTitle: String,
        val total: Int,
        val remembered: Int,
        val difficultCards: List<Flashcard>
    ) : Screen
}

data class StudySessionState(
    val deck: Deck? = null,
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val rememberedCount: Int = 0,
    val needPracticeCount: Int = 0,
    val difficultCards: List<Flashcard> = emptyList(),
    val isShuffled: Boolean = false
) {
    val currentCard: Flashcard?
        get() = cards.getOrNull(currentIndex)
    val isFinished: Boolean
        get() = cards.isNotEmpty() && currentIndex >= cards.size
    val progress: Float
        get() = if (cards.isNotEmpty()) currentIndex.toFloat() / cards.size.toFloat() else 0f
}

class FlashletViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FlashletDatabase.getDatabase(application)
    val repository = FlashcardRepository(database.deckDao(), database.flashcardDao())

    private val _currentScreen = MutableStateFlow<Screen>(Screen.DeckList)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val decksWithStats: StateFlow<List<DeckWithStats>> = combine(
        repository.decksWithStatsFlow,
        _searchQuery,
        _selectedCategory
    ) { allDecks, query, category ->
        allDecks.filter { deckWithStats ->
            val matchesCategory = category == "All" || deckWithStats.deck.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    deckWithStats.deck.title.contains(query, ignoreCase = true) ||
                    deckWithStats.deck.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Study session state
    private val _studyState = MutableStateFlow(StudySessionState())
    val studyState: StateFlow<StudySessionState> = _studyState.asStateFlow()

    // Dialog & sheet states
    val deckToEdit = MutableStateFlow<Deck?>(null)
    val showCreateDeckDialog = MutableStateFlow(false)
    val cardToEdit = MutableStateFlow<Flashcard?>(null)
    val activeDeckForNewCard = MutableStateFlow<Long?>(null)
    val showImportDialog = MutableStateFlow(false)
    val showAboutDialog = MutableStateFlow(false)
    val deckToDelete = MutableStateFlow<Deck?>(null)
    val cardToDelete = MutableStateFlow<Flashcard?>(null)

    init {
        viewModelScope.launch {
            repository.initializeSampleDecksIfEmpty()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        return when (val screen = _currentScreen.value) {
            is Screen.Study -> {
                navigateTo(Screen.DeckDetail(screen.deckId))
                true
            }
            is Screen.StudySummary -> {
                navigateTo(Screen.DeckDetail(screen.deckId))
                true
            }
            is Screen.DeckDetail -> {
                navigateTo(Screen.DeckList)
                true
            }
            Screen.DeckList -> false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun saveDeck(title: String, description: String, category: String, colorHex: String, idToEdit: Long? = null) {
        viewModelScope.launch {
            if (idToEdit != null && idToEdit > 0) {
                val existing = repository.getDeckById(idToEdit)
                if (existing != null) {
                    repository.updateDeck(
                        existing.copy(
                            title = title.trim(),
                            description = description.trim(),
                            category = category.trim(),
                            colorHex = colorHex
                        )
                    )
                }
            } else {
                repository.insertDeck(
                    Deck(
                        title = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        colorHex = colorHex
                    )
                )
            }
            showCreateDeckDialog.value = false
            deckToEdit.value = null
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
            deckToDelete.value = null
            if (_currentScreen.value is Screen.DeckDetail) {
                navigateTo(Screen.DeckList)
            }
        }
    }

    fun duplicateDeck(deckId: Long) {
        viewModelScope.launch {
            repository.duplicateDeck(deckId)
        }
    }

    fun saveCard(deckId: Long, front: String, back: String, idToEdit: Long? = null) {
        viewModelScope.launch {
            if (idToEdit != null && idToEdit > 0) {
                repository.updateCard(
                    Flashcard(
                        id = idToEdit,
                        deckId = deckId,
                        front = front.trim(),
                        back = back.trim()
                    )
                )
            } else {
                repository.insertCard(
                    Flashcard(
                        deckId = deckId,
                        front = front.trim(),
                        back = back.trim()
                    )
                )
            }
            cardToEdit.value = null
            activeDeckForNewCard.value = null
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
            cardToDelete.value = null
        }
    }

    fun resetDeckProgress(deckId: Long) {
        viewModelScope.launch {
            repository.resetDeckProgress(deckId)
        }
    }

    fun restoreSampleDecks() {
        viewModelScope.launch {
            repository.populateSampleDecks()
        }
    }

    fun importDeckFromJson(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = DeckExporter.importFromJson(jsonString)
            if (result != null) {
                val (deck, cards) = result
                repository.importDeckWithCards(deck, cards)
                showImportDialog.value = false
                onResult(true, "Successfully imported \"${deck.title}\" with ${cards.size} cards!")
            } else {
                onResult(false, "Invalid Flashlet JSON format. Please check the text and try again.")
            }
        }
    }

    fun startStudySession(deckId: Long, difficultOnly: Boolean = false) {
        viewModelScope.launch {
            val deck = repository.getDeckById(deckId) ?: return@launch
            val allCards = repository.getCardsForDeck(deckId)
            val filteredCards = if (difficultOnly) {
                allCards.filter { it.difficulty == 2 }
            } else {
                allCards
            }

            if (filteredCards.isEmpty()) {
                // If no difficult cards exist and difficultOnly was requested, use all cards
                val fallbackCards = if (allCards.isNotEmpty()) allCards else emptyList()
                _studyState.value = StudySessionState(
                    deck = deck,
                    cards = fallbackCards,
                    currentIndex = 0,
                    isFlipped = false
                )
            } else {
                _studyState.value = StudySessionState(
                    deck = deck,
                    cards = filteredCards,
                    currentIndex = 0,
                    isFlipped = false
                )
            }
            navigateTo(Screen.Study(deckId, difficultOnly))
        }
    }

    fun flipCard() {
        _studyState.value = _studyState.value.copy(
            isFlipped = !_studyState.value.isFlipped
        )
    }

    fun markCard(difficulty: Int) {
        val state = _studyState.value
        val card = state.currentCard ?: return

        viewModelScope.launch {
            repository.markCardReview(card.id, difficulty)
        }

        val newRemembered = if (difficulty == 1) state.rememberedCount + 1 else state.rememberedCount
        val newPractice = if (difficulty == 2) state.needPracticeCount + 1 else state.needPracticeCount
        val newDifficult = if (difficulty == 2) state.difficultCards + card else state.difficultCards
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= state.cards.size) {
            // Session complete
            val deck = state.deck
            val deckTitle = deck?.title ?: "Deck"
            _studyState.value = state.copy(
                currentIndex = nextIndex,
                rememberedCount = newRemembered,
                needPracticeCount = newPractice,
                difficultCards = newDifficult,
                isFlipped = false
            )
            navigateTo(
                Screen.StudySummary(
                    deckId = deck?.id ?: 0L,
                    deckTitle = deckTitle,
                    total = state.cards.size,
                    remembered = newRemembered,
                    difficultCards = newDifficult
                )
            )
        } else {
            _studyState.value = state.copy(
                currentIndex = nextIndex,
                rememberedCount = newRemembered,
                needPracticeCount = newPractice,
                difficultCards = newDifficult,
                isFlipped = false
            )
        }
    }

    fun toggleShuffle() {
        val state = _studyState.value
        if (state.cards.isEmpty()) return

        val shuffled = if (!state.isShuffled) {
            state.cards.shuffled()
        } else {
            state.cards.sortedBy { it.orderIndex }
        }

        _studyState.value = state.copy(
            cards = shuffled,
            currentIndex = 0,
            isFlipped = false,
            isShuffled = !state.isShuffled
        )
    }
}
