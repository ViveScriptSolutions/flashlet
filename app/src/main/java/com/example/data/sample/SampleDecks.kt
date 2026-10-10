package com.example.data.sample

import com.example.data.model.Deck
import com.example.data.model.Flashcard

data class SampleDeckWithCards(
    val deck: Deck,
    val cards: List<Pair<String, String>> // front to back
)

object SampleDecks {
    fun getSampleDecks(): List<SampleDeckWithCards> = listOf(
        SampleDeckWithCards(
            deck = Deck(
                title = "Essential English Vocabulary",
                description = "High-impact vocabulary words with definitions and memory aids for exams and daily speaking.",
                category = "Languages",
                colorHex = "#4F46E5",
                isSample = true
            ),
            cards = listOf(
                "Ephemeral" to "Lasting for a very short time; fleeting or momentary.\n\nExample: 'The beauty of a sunset is ephemeral.'",
                "Ubiquitous" to "Present, appearing, or found everywhere at once.\n\nExample: 'Smartphones have become ubiquitous in modern life.'",
                "Resilient" to "Able to recover quickly from difficult conditions or hardship.\n\nExample: 'Her resilient spirit helped her overcome all challenges.'",
                "Serendipity" to "The occurrence of events by chance in a happy or beneficial way.\n\nExample: 'Finding this book was pure serendipity.'",
                "Pernicious" to "Having a harmful effect, especially in a gradual or subtle way.\n\nExample: 'The pernicious influence of disinformation.'",
                "Ineffable" to "Too great, extreme, or beautiful to be expressed in words.\n\nExample: 'An ineffable sense of peace washed over him.'",
                "Eloquent" to "Fluent or persuasive in speaking or writing.\n\nExample: 'She gave an eloquent speech advocating for education.'",
                "Cathartic" to "Providing psychological relief through open release of strong emotions.\n\nExample: 'Writing in a journal can be deeply cathartic.'"
            )
        ),
        SampleDeckWithCards(
            deck = Deck(
                title = "বাংলা মৌলিক শব্দ (Bangla Essentials)",
                description = "বাংলা গুরুত্বপূর্ণ শব্দ, শিষ্টাচার এবং প্রায়শ ব্যবহৃত প্রমিত পরিভাষা।",
                category = "Languages",
                colorHex = "#0D9488",
                isSample = true
            ),
            cards = listOf(
                "স্বাগতম (Shagotom)" to "Welcome\n\nকারো আগমন বা উপস্থিতিকে আন্তরিকভাবে গ্রহণ করার শিষ্টাচার।",
                "ধন্যবাদ (Dhonnobad)" to "Thank you / Gratitude\n\nকারো অনুগ্রহ বা অবদানের জন্য কৃতজ্ঞতা প্রকাশ।",
                "অধ্যবসায় (Oddhoboshay)" to "Perseverance / Diligence\n\nকোনো কাজে ক্রমাগত কঠোর পরিশ্রম ও নিষ্ঠা বজায় রাখা।",
                "শুভকামনা (Shubhokamona)" to "Best wishes\n\nকারো মঙ্গল বা শুভ পরিণতি প্রত্যাশা করা।",
                "কৃতজ্ঞতা (Kritoggota)" to "Gratitude / Appreciation\n\nঅন্যের উপকারের প্রতি শ্রদ্ধাশীল ও বিশ্বস্ত থাকা।",
                "সহমর্মিতা (Shohomormita)" to "Empathy\n\nঅন্যের অনুভূতি, কষ্ট বা আনন্দকে নিজের মতো উপলব্ধি করা।",
                "প্রত্যয় (Prottoy)" to "Conviction / Confidence\n\nদৃঢ় বিশ্বাস বা আত্মবিশ্বাস।"
            )
        ),
        SampleDeckWithCards(
            deck = Deck(
                title = "World Capitals & Geography",
                description = "Test and sharpen your global geography knowledge with quick country-capital cards.",
                category = "Geography",
                colorHex = "#D97706",
                isSample = true
            ),
            cards = listOf(
                "What is the capital of Australia?" to "Canberra\n(Sydney is the largest city, but Canberra is the capital!)",
                "What is the capital of Canada?" to "Ottawa\n(Located in the province of Ontario).",
                "What is the capital of Bangladesh?" to "Dhaka\n(Known historically as the City of Mosques).",
                "What is the capital of Japan?" to "Tokyo\n(One of the world's most populous metropolitan areas).",
                "What is the capital of Brazil?" to "Brasília\n(Planned city founded in 1960, not Rio de Janeiro).",
                "What is the capital of Egypt?" to "Cairo\n(Located near the historic Giza pyramids).",
                "What is the capital of Kenya?" to "Nairobi\n(Often referred to as the 'Green City in the Sun')."
            )
        ),
        SampleDeckWithCards(
            deck = Deck(
                title = "Computer Science Concepts",
                description = "Fundamental CS terms and system concepts essential for tech interviews and studies.",
                category = "Technology",
                colorHex = "#8B5CF6",
                isSample = true
            ),
            cards = listOf(
                "What is an Algorithm?" to "A step-by-step set of unambiguous instructions or rules designed to solve a specific problem.",
                "What is Big-O Notation?" to "A mathematical notation describing the limiting behavior of an algorithm's time or space complexity as input size grows.",
                "What is an API?" to "Application Programming Interface: A contract/set of definitions and protocols that lets software components communicate.",
                "What is Binary Search?" to "An efficient algorithm for finding an item in a sorted list by repeatedly dividing the search interval in half. Time complexity: O(log n).",
                "What is Cache?" to "A high-speed data storage layer that stores a subset of data so future requests can be served faster."
            )
        )
    )
}
