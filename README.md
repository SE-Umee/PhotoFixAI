# PhotoFix AI

A production-ready Android photo toolkit built with **Kotlin + Jetpack Compose + Material 3**.
Remove backgrounds, make passport/ID photos, resize, compress, create marketplace product
shots, and clean up signatures — all with a premium, modern UI.

## Tech stack & architecture

- **UI:** Jetpack Compose, Material 3, Navigation Compose
- **Architecture:** MVVM + Clean Architecture (core / data / domain / presentation)
- **Async:** Coroutines + StateFlow
- **Local storage:** Room (history), DataStore (settings + onboarding)
- **Images:** Coil (loading), custom Bitmap pipeline (decode/resize/compress/export)
- **Networking (future API mode):** Retrofit + OkHttp
- **On-device segmentation:** ML Kit Selfie Segmentation
- **DI:** lightweight manual container (`di/AppContainer`) — swap for Hilt later if desired

```
app/src/main/java/com/umeetech/photofixai/
├── core/         common, constants, utils, image, permissions, result, ads, export, billing
├── data/         local (database/dao/entity/datastore), remote (api/dto), repository, service
├── domain/       model, repository (interfaces), usecase
└── presentation/ navigation, theme, components, screens/*
```

## Background removal strategies

Selected in `di/AppContainer.backgroundRemovalStrategy`:

| Strategy | Class | Use |
|----------|-------|-----|
| `MOCK` (default) | `MockBackgroundRemovalService` | MVP / testing, no deps |
| `LOCAL` | `LocalSegmentationBackgroundRemovalService` | Free, on-device (people) |
| `API` | `ApiBackgroundRemovalService` | Production, via **your** backend |

### Security (production API)

- **No API keys live in the app.** The app calls **your** backend / Firebase Function, which
  holds the paid provider's secret (remove.bg, PhotoRoom, Replicate, OpenAI, ...).
- Configure the backend URL at build time (never hardcode a secret):
  ```
  ./gradlew assembleRelease -Pbackend.url=https://your-backend.example.com/
  ```
- If auth is needed, pass a short-lived token (Firebase App Check / Auth), not a provider key.

## Monetization (kept disabled, code preserved)

- **AdMob:** all logic in `core/ads/AdsManager` (commented). Add the dependency + App ID and
  uncomment to enable Banner / Interstitial (after export) / Rewarded (free HD export) ads.
- **Google Play Billing:** placeholder in `core/billing/BillingManager`. The Premium screen UI
  is complete and ready to wire to real products (monthly / yearly / lifetime).

## Play Store readiness

- Uses the modern **Android Photo Picker** (no storage permission needed).
- Scoped storage via MediaStore for saving; legacy permissions capped with `maxSdkVersion`.
- Works fully offline, no login/signup required.
- Heavy image work runs on background dispatchers; large images are downscaled to avoid OOM.
- Replace privacy policy / terms / support placeholders in `res/values/strings.xml` before release.

## Build

```
./gradlew assembleDebug
./gradlew test          # unit tests (pure logic + use cases)
```
# PhotoFixAI
