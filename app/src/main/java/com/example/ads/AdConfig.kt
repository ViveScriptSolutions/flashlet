package com.example.ads

/**
 * Centralized Google AdMob Configuration for Flashlet.
 *
 * Configured with official Google Mobile Ads SDK application and ad unit identifiers
 * for Flashlet by ViveScript Solutions LLC.
 */
object AdConfig {
    /**
     * Master toggle for ads in the application.
     * When false, no ad requests are made and ad containers collapse cleanly.
     */
    const val IS_ADS_ENABLED = true

    /**
     * Google AdMob Application ID for Flashlet.
     * Configured in AndroidManifest.xml meta-data.
     */
    const val APP_ID = "ca-app-pub-5222053984568989~4561947485"

    /**
     * Google AdMob Banner Ad Unit ID (Banner 1).
     */
    const val BANNER_AD_UNIT_ID = "ca-app-pub-5222053984568989/6223705483"

    /**
     * Google AdMob Interstitial Ad Unit ID.
     */
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5222053984568989/8690920870"

    /**
     * Google Mobile Ads SDK Version documented for release and Play Store Data Safety.
     */
    const val AD_SDK_VERSION = "23.6.0"
}
