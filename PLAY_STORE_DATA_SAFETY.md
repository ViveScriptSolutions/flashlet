# Google Play Data Safety Guidance — Flashlet

**Application ID:** `com.vivescriptsolutions.flashlet`  
**Publisher:** ViveScript Solutions LLC  
**SDKs Used:** Google Mobile Ads SDK (`com.google.android.gms:play-services-ads:23.6.0`)  
**AdMob App ID:** `ca-app-pub-5222053984568989~4561947485`  
**Banner Ad Unit ID:** `ca-app-pub-5222053984568989/6223705483`  
**Interstitial Ad Unit ID:** `ca-app-pub-5222053984568989/8690920870`  

---

## 1. Overview of Data Safety Responses

| Category | Response | Details |
|---|---|---|
| **Does your app collect or share user data?** | **Yes** *(Only via Google Mobile Ads SDK for ad serving/fraud prevention)* | Flashlet itself collects 0 personal data. Standard ad identifiers handled by Google Play Services. |
| **Is all data collected by your app encrypted in transit?** | **Yes** | HTTPS/TLS is enforced for any network request. |
| **Do you provide a way for users to request data deletion?** | **Yes** | Deleting the app or clearing local app data removes all local flashcard content. |
| **Is this app designed for children?** | **No** *(Target audience: General audience, age 13+ and above)* | Suitable for all ages, but not child-directed. |

---

## 2. Detailed Data Declarations (AdMob SDK)

Google Mobile Ads SDK declares the following data types for advertising, analytics, and fraud prevention:

### 2.1 Device or Other IDs
- **Data Type:** Device or other IDs (e.g. Advertising ID, Android ID)
- **Collected:** Yes (by Google Mobile Ads SDK)
- **Shared:** Yes (with Google for advertising purposes)
- **Purpose:** Advertising or marketing, Fraud prevention, security, and compliance
- **Ephemeral processing:** No

### 2.2 App Performance / Diagnostics
- **Data Type:** Crash logs, diagnostics, and other app performance data
- **Collected:** Yes (by Google Mobile Ads SDK)
- **Shared:** Yes (with Google)
- **Purpose:** Analytics, Advertising

### 2.3 User-Authored Flashcards & Decks
- **Collected by Developer:** **No** (Stored exclusively in local SQLite on device).
- **Shared with Third Parties:** **No**.

---

## 3. Play Console Content Rating Questionnaire Answers

- **Violence:** No
- **Sexuality / Nudity:** No
- **Profanity:** No
- **Controlled Substances:** No
- **Gambling:** No
- **User-Generated Content Shared with Others:** Only via external user-initiated system share sheet (`ACTION_SEND`); no built-in social network, public wall, or server hosting.
- **Location Sharing:** No
- **Digital Purchases:** No
- **Recommended Rating:** **Everyone / PEGI 3**
