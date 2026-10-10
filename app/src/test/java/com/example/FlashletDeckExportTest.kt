package com.example

import com.example.data.export.DeckExporter
import com.example.data.model.Deck
import com.example.data.model.Flashcard
import com.example.data.sample.SampleDecks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlashletDeckExportTest {

    @Test
    fun testDeckExportAndImportRoundTrip() {
        val originalDeck = Deck(
            id = 1,
            title = "Bengali Basics",
            description = "Common everyday phrases",
            category = "Languages",
            colorHex = "#0D9488"
        )
        val originalCards = listOf(
            Flashcard(id = 10, deckId = 1, front = "স্বাগতম", back = "Welcome"),
            Flashcard(id = 11, deckId = 1, front = "ধন্যবাদ", back = "Thank you")
        )

        val json = DeckExporter.exportToJson(originalDeck, originalCards)
        assertNotNull(json)
        assertTrue(json.contains("Bengali Basics"))
        assertTrue(json.contains("স্বাগতম"))

        val imported = DeckExporter.importFromJson(json)
        assertNotNull(imported)

        val (importedDeck, importedCards) = imported!!
        assertEquals("Bengali Basics", importedDeck.title)
        assertEquals("Common everyday phrases", importedDeck.description)
        assertEquals("Languages", importedDeck.category)
        assertEquals(2, importedCards.size)
        assertEquals("স্বাগতম", importedCards[0].front)
        assertEquals("Welcome", importedCards[0].back)
    }

    @Test
    fun testSampleDecksIntegrity() {
        val samples = SampleDecks.getSampleDecks()
        assertTrue("Sample decks should not be empty", samples.isNotEmpty())
        for (sample in samples) {
            assertTrue("Sample deck title should not be blank", sample.deck.title.isNotBlank())
            assertTrue("Sample deck should contain cards", sample.cards.isNotEmpty())
            for ((front, back) in sample.cards) {
                assertTrue("Card front should not be blank", front.isNotBlank())
                assertTrue("Card back should not be blank", back.isNotBlank())
            }
        }
    }
}
