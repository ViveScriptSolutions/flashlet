package com.example.data.export

import android.content.Context
import android.content.Intent
import com.example.data.model.Deck
import com.example.data.model.Flashcard
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = false)
data class ExportedCard(
    val front: String,
    val back: String
)

@JsonClass(generateAdapter = false)
data class ExportedDeck(
    val format: String = "flashlet-deck-v1",
    val title: String,
    val description: String = "",
    val category: String = "General",
    val colorHex: String = "#4F46E5",
    val cards: List<ExportedCard>
)

object DeckExporter {
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val adapter by lazy {
        moshi.adapter(ExportedDeck::class.java)
    }

    fun exportToJson(deck: Deck, cards: List<Flashcard>): String {
        val exported = ExportedDeck(
            title = deck.title,
            description = deck.description,
            category = deck.category,
            colorHex = deck.colorHex,
            cards = cards.map { ExportedCard(front = it.front, back = it.back) }
        )
        return adapter.indent("  ").toJson(exported)
    }

    fun importFromJson(jsonString: String): Pair<Deck, List<Flashcard>>? {
        return try {
            val exported = adapter.fromJson(jsonString) ?: return null
            if (exported.title.isBlank()) return null
            val deck = Deck(
                title = exported.title.trim(),
                description = exported.description.trim(),
                category = if (exported.category.isNotBlank()) exported.category.trim() else "Imported",
                colorHex = if (exported.colorHex.isNotBlank()) exported.colorHex else "#4F46E5",
                isSample = false
            )
            val cards = exported.cards.mapIndexed { index, card ->
                Flashcard(
                    deckId = 0,
                    front = card.front.trim(),
                    back = card.back.trim(),
                    orderIndex = index
                )
            }.filter { it.front.isNotBlank() || it.back.isNotBlank() }
            deck to cards
        } catch (e: Exception) {
            null
        }
    }

    fun shareDeck(context: Context, deck: Deck, cards: List<Flashcard>) {
        val json = exportToJson(deck, cards)
        val shareText = buildString {
            appendLine("📚 Flashlet Deck: ${deck.title}")
            if (deck.description.isNotBlank()) {
                appendLine(deck.description)
            }
            appendLine("Total cards: ${cards.size}")
            appendLine()
            appendLine("--- Flashlet Deck JSON (Copy & Import into Flashlet) ---")
            appendLine(json)
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Flashlet Deck: ${deck.title}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Deck via")
        context.startActivity(shareIntent)
    }

    fun shareStudyResult(
        context: Context,
        deckTitle: String,
        totalCards: Int,
        rememberedCount: Int,
        percentage: Int
    ) {
        val message = buildString {
            appendLine("🎯 Flashlet Study Milestone!")
            appendLine("I just practiced \"$deckTitle\" on Flashlet:")
            appendLine("✅ Score: $rememberedCount/$totalCards ($percentage% remembered)")
            if (percentage >= 80) {
                appendLine("🌟 Feeling confident and ready!")
            } else {
                appendLine("💪 Practice makes progress!")
            }
            appendLine()
            appendLine("Flashlet — Fast & offline flashcards by ViveScript Solutions LLC")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "My Flashlet Study Progress")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share your milestone with friends")
        context.startActivity(shareIntent)
    }

    fun shareAppQuote(context: Context) {
        val quotes = listOf(
            "\"Repetition is the mother of learning, the father of action, and the architect of accomplishment.\" — Master concepts with Flashlet!",
            "\"An investment in knowledge pays the best interest.\" — Benjamin Franklin. Study smarter with Flashlet!",
            "\"Small daily habits lead to monumental achievements.\" — Keep memorizing with Flashlet!"
        )
        val selectedQuote = quotes.random()
        val text = "$selectedQuote\n\nStudy anytime offline with Flashlet."
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Study Motivation from Flashlet")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Study Quote")
        context.startActivity(shareIntent)
    }
}
