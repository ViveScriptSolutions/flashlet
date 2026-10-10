package com.example.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages Google AdMob Interstitial Ads in compliance with Google Play and AdMob policies.
 * Interstitial ads are shown only at natural transitions (e.g., after completing a study session).
 */
object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd(context: Context) {
        if (!AdConfig.IS_ADS_ENABLED || isLoading || interstitialAd != null) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        try {
            InterstitialAd.load(
                context.applicationContext,
                AdConfig.INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isLoading = false
                    }
                }
            )
        } catch (e: Exception) {
            isLoading = false
        }
    }

    /**
     * Shows the interstitial ad if loaded. Always executes [onDismissed] whether
     * the ad shows, fails, or was not yet loaded, ensuring zero UX blockers.
     */
    fun showAd(activity: Activity, onDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null && AdConfig.IS_ADS_ENABLED) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadAd(activity)
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            loadAd(activity)
            onDismissed()
        }
    }
}
