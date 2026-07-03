package com.umeetech.photofixai.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log

/**
 * Centralized ad orchestration. ALL AdMob logic lives here and nowhere else, so
 * monetization can be enabled/disabled from a single place.
 *
 * =============================================================================
 * ADMOB IS INTENTIONALLY DISABLED FOR NOW. Do NOT delete the commented code.
 *
 * To enable ads later:
 *   1. Add the dependency in app/build.gradle.kts:
 *        implementation("com.google.android.gms:play-services-ads:23.6.0")
 *   2. Add your AdMob App ID meta-data in AndroidManifest.xml (see the commented
 *      block there).
 *   3. Replace the TEST unit IDs below with your real ad unit IDs.
 *   4. Uncomment the implementation bodies.
 *   5. Gate every ad behind `!isPremium` (premium users never see ads).
 * =============================================================================
 */
class AdsManager(
    private val appContext: Context,
    /** When true, ads must never be shown (premium users). */
    var isPremiumProvider: () -> Boolean = { false }
) {

    companion object {
        private const val TAG = "AdsManager"

        // --- AdMob TEST unit IDs (safe placeholders) -------------------------
        const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    /** Initialize the Mobile Ads SDK once, at app startup. */
    fun initialize() {
        // MobileAds.initialize(appContext) {}
        Log.d(TAG, "AdMob disabled — initialize() is a no-op placeholder.")
    }

    /** BANNER AD — show at the bottom of Home / Tools screens for free users. */
    fun loadBanner() {
        if (isPremiumProvider()) return
        // val adView = AdView(appContext).apply {
        //     setAdSize(AdSize.BANNER)
        //     adUnitId = TEST_BANNER_UNIT_ID
        //     loadAd(AdRequest.Builder().build())
        // }
    }

    /** INTERSTITIAL AD — show after a successful export for free users. */
    fun showInterstitialAfterExport(activity: Activity, onDismiss: () -> Unit) {
        if (isPremiumProvider()) { onDismiss(); return }
        // InterstitialAd.load(activity, TEST_INTERSTITIAL_UNIT_ID, AdRequest.Builder().build(),
        //     object : InterstitialAdLoadCallback() {
        //         override fun onAdLoaded(ad: InterstitialAd) {
        //             ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        //                 override fun onAdDismissedFullScreenContent() = onDismiss()
        //             }
        //             ad.show(activity)
        //         }
        //         override fun onAdFailedToLoad(error: LoadAdError) = onDismiss()
        //     })
        onDismiss()
    }

    /** REWARDED AD — unlock a single free HD export in exchange for a rewarded view. */
    fun showRewardedForHdExport(activity: Activity, onReward: () -> Unit, onDismiss: () -> Unit) {
        if (isPremiumProvider()) { onReward(); return }
        // RewardedAd.load(activity, TEST_REWARDED_UNIT_ID, AdRequest.Builder().build(),
        //     object : RewardedAdLoadCallback() {
        //         override fun onAdLoaded(ad: RewardedAd) {
        //             ad.show(activity) { onReward() }
        //         }
        //         override fun onAdFailedToLoad(error: LoadAdError) = onDismiss()
        //     })
        onDismiss()
    }
}
