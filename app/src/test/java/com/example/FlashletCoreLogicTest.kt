package com.example

import com.example.data.export.DeckExporter
import com.example.data.model.Deck
import com.example.data.model.DeckWithStats
import com.example.data.model.Flashcard
import com.example.data.sample.SampleDecks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlashletCoreLogicTest {

    @Test
    fun testDeckWithStatsMasteryPercentage() {
        val deck = Deck(title = "Test Deck")
        
        // Zero cards edge case
        val zeroStats = DeckWithStats(deck = deck, totalCards = 0, rememberedCards = 0)
        assertEquals(0, zeroStats.masteryPercentage)

        // 5 of 10 remembered
        val halfStats = DeckWithStats(deck = deck, totalCards = 10, rememberedCards = 5)
        assertEquals(50, halfStats.masteryPercentage)

        // 10 of 10 remembered
        val perfectStats = DeckWithStats(deck = deck, totalCards = 10, rememberedCards = 10)
        assertEquals(100, perfectStats.masteryPercentage)

        // 1 of 3 remembered (rounding)
        val thirdStats = DeckWithStats(deck = deck, totalCards = 3, rememberedCards = 1)
        assertEquals(33, thirdStats.masteryPercentage)
    }

    @Test
    fun testMalformedJsonImportFailsGracefully() {
        assertNull("Empty string should return null", DeckExporter.importFromJson(""))
        assertNull("Whitespace should return null", DeckExporter.importFromJson("   "))
        assertNull("Invalid JSON syntax should return null", DeckExporter.importFromJson("{ broken json: 123"))
        assertNull("Missing title should return null", DeckExporter.importFromJson("""{"cards": []}"""))
        assertNull("Blank title should return null", DeckExporter.importFromJson("""{"title": "   ", "cards": []}"""))
    }

    @Test
    fun testBanglaUnicodeJsonExportImportRoundtrip() {
        val deck = Deck(
            title = "বাংলা শব্দকোষ",
            description = "প্রমিত বাংলা ব্যাকরণ ও প্রবচন",
            category = "Languages",
            colorHex = "#0D9488"
        )
        val cards = listOf(
            Flashcard(deckId = 1, front = "অধ্যবসায়", back = "কঠোর সাধনা ও একাগ্রতা"),
            Flashcard(deckId = 1, front = "সহমর্মিতা", back = "অন্যের ব্যথায় ব্যথিত হওয়া")
        )

        val json = DeckExporter.exportToJson(deck, cards)
        assertNotNull(json)
        assertTrue(json.contains("বাংলা শব্দকোষ"))
        assertTrue(json.contains("অধ্যবসায়"))

        val imported = DeckExporter.importFromJson(json)
        assertNotNull(imported)
        val (importedDeck, importedCards) = imported!!

        assertEquals("বাংলা শব্দকোষ", importedDeck.title)
        assertEquals("প্রমিত বাংলা ব্যাকরণ ও প্রবচন", importedDeck.description)
        assertEquals(2, importedCards.size)
        assertEquals("অধ্যবসায়", importedCards[0].front)
        assertEquals("কঠোর সাধনা ও একাগ্রতা", importedCards[0].back)
    }

    @Test
    fun testSampleDecksComprehensiveIntegrity() {
        val samples = SampleDecks.getSampleDecks()
        assertEquals(4, samples.size)

        val englishDeck = samples.first { it.deck.title.contains("English") }
        assertTrue(englishDeck.cards.size >= 5)

        val banglaDeck = samples.first { it.deck.title.contains("Bangla") || it.deck.title.contains("বাংলা") }
        assertTrue(banglaDeck.cards.size >= 5)

        val geographyDeck = samples.first { it.deck.title.contains("Geography") || it.deck.title.contains("Capitals") }
        assertTrue(geographyDeck.cards.size >= 5)

        val techDeck = samples.first { it.deck.title.contains("Computer") || it.deck.title.contains("Tech") }
        assertTrue(techDeck.cards.size >= 5)
    }
}
