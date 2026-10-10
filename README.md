# Flashlet

A lightweight, fast, and offline-first Android flashcard application designed for students, language learners, and exam candidates to master and memorize information efficiently through digital question-and-answer cards.

Developed by **ViveScript Solutions LLC**  
Website: [https://www.vivescriptsolutions.com/](https://www.vivescriptsolutions.com/)  
Copyright © 2026 ViveScript Solutions LLC.

---

## Overview

Flashlet allows users to create custom flashcards with questions or terms on the front and answers or explanations on the back, organize them into customized decks, flip cards through a tactile 3D interface, review cards repeatedly using active recall, and export/import decks as portable JSON without needing an account or an internet connection.

### Key Features
- **Zero Registration / 100% Offline:** Works completely offline out of the box. No login, credentials, or cloud synchronization required.
- **3D Card Flip Animation:** Realistic tap-to-flip motion for testing recall before revealing the answer.
- **Smart Active Recall:** Mark cards as "Got It" or "Need Practice", track session accuracy, and drill difficult cards.
- **Deck & Card Organization:** Create, edit, duplicate, search, and delete decks with customizable categories and color accents.
- **Deck Sharing (JSON):** Export complete decks as structured JSON to share with classmates via Android's native share sheet.
- **Milestone & Score Sharing:** Share study achievements and scores directly to WhatsApp, Instagram Stories, TikTok, or messaging apps.
- **Built-in Sample Decks:** Preloaded with high-value decks (English Vocabulary, Bangla Essentials, World Capitals, Tech Concepts).
- **Google AdMob Monetization:** Integrated Google Mobile Ads SDK with Banner (ca-app-pub-5222053984568989/6223705483) and natural transition Interstitial (ca-app-pub-5222053984568989/8690920870) adhering to AdMob user experience policies.

---

## Technology Stack

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Local Persistence:** AndroidX Room 2.7.0 with Kotlin Symbol Processing (KSP)
- **JSON Serialization:** Moshi 1.15.2 (Kotlin reflection)
- **Architecture:** Clean MVVM (Model-View-ViewModel) + Repository Pattern + Kotlin StateFlow
- **Advertising SDK:** Google Mobile Ads SDK (`com.google.android.gms:play-services-ads:23.6.0`)
- **Testing:** JUnit 4, Robolectric 4.16.1, AndroidX Test Core

---

## Requirements

- **Minimum SDK:** Android 7.0 (API level 24)
- **Target SDK:** Android 15 (API level 36)
- **JDK:** Java 11 / 17 compatible toolchain
- **Build System:** Gradle (Kotlin DSL)

---

## Installation & Development

### 1. Clone the repository
```bash
git clone https://github.com/vivescriptsolutions/flashlet.git
cd flashlet
```

### 2. Build the Project
```bash
gradle assembleDebug
```

### 3. Run Unit and Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

---

## Project Structure

```text
flashlet/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── ads/                # AdConfig & AdBanner integration
│   │   │   │   ├── data/
│   │   │   │   │   ├── export/         # JSON exporter & system share sheet
│   │   │   │   │   ├── local/          # Room Database, DAOs & Entities
│   │   │   │   │   ├── model/          # Deck, Flashcard & DeckWithStats
│   │   │   │   │   ├── repository/     # Data abstraction layer
│   │   │   │   │   └── sample/         # Preloaded sample study decks
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/     # CardFlipView 3D animation
│   │   │   │   │   ├── dialogs/        # Create/Edit Deck, Card & About dialogs
│   │   │   │   │   ├── screens/        # DeckList, DeckDetail, Study, Summary
│   │   │   │   │   ├── theme/          # Material 3 Color, Theme, Type
│   │   │   │   │   └── viewmodel/      # FlashletViewModel & UI state
│   │   │   │   └── MainActivity.kt     # App entry point
│   │   │   └── res/                    # Drawables, mipmaps, strings (en & bn)
│   │   └── test/                       # Local JVM & Robolectric tests
├── play_store_assets/                  # Google Play store publishing graphics
├── production_build/                   # Final production App Bundle (.aab)
├── LICENSE                             # MIT License with proprietary branding notice
├── PRIVACY_POLICY.md                   # Privacy Policy for Google Play
├── TERMS_OF_SERVICE.md                 # Terms of Service
├── PLAY_STORE_METADATA.md              # Google Play Store listing & SEO
├── PLAY_STORE_DATA_SAFETY.md           # Google Play Data Safety declaration
└── DESIGN_ASSETS.md                    # Brand & visual asset documentation
```

---

## Privacy & Data Safety

Flashlet does not harvest or track personal user data. All created flashcards, decks, and study logs are stored purely locally in the application's private SQLite sandbox. For full details, see [PRIVACY_POLICY.md](PRIVACY_POLICY.md) and [PLAY_STORE_DATA_SAFETY.md](PLAY_STORE_DATA_SAFETY.md).

---

## Licensing & Proprietary Branding

- **Source Code:** Licensed under the [MIT License](LICENSE).
- **Proprietary Branding Notice:** The app name **Flashlet**, the app icon, logos, screenshots, trademarks, and associated visual branding are proprietary property of **ViveScript Solutions LLC** and are not licensed under the MIT License.

---

**© 2026 ViveScript Solutions LLC. All rights reserved.**
