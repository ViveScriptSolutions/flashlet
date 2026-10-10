package com.example.data.model

data class DeckWithStats(
    val deck: Deck,
    val totalCards: Int = 0,
    val rememberedCards: Int = 0,
    val needPracticeCards: Int = 0,
    val unreviewedCards: Int = 0
) {
    val masteryPercentage: Int
        get() = if (totalCards > 0) ((rememberedCards * 100) / totalCards) else 0
}
