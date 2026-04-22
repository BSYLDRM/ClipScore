# 📱 ClipScore — 2 Aylık Gerçekçi Proje Planı

> **"Know your score before you post"**  
> Platform: Android (Kotlin) | Sürüm: 1.0 | Toplam Süre: 8 Hafta  
> Başlangıç: Nisan 2026 | Hedef Yayın: Haziran 2026

---

## 📌 İçindekiler

1. [Proje Özeti](#1-proje-özeti)
2. [Tech Stack — Tam Liste](#2-tech-stack--tam-liste)
3. [Mimari](#3-mimari)
4. [Klasör Yapısı](#4-klasör-yapısı)
5. [Haftalık Plan (8 Hafta)](#5-haftalık-plan-8-hafta)
6. [Backend Detayları](#6-backend-detayları)
7. [Frontend Detayları](#7-frontend-detayları)
8. [API Entegrasyonu](#8-api-entegrasyonu)
9. [Veri Modelleri](#9-veri-modelleri)
10. [Güvenlik](#10-güvenlik)
11. [Test Stratejisi](#11-test-stratejisi)
12. [CI/CD & Play Store](#12-cicd--play-store)
13. [Riskler](#13-riskler)
14. [Başarı Metrikleri](#14-başarı-metrikleri)
15. [Post-MVP Yol Haritası](#15-post-mvp-yol-haritası)

---

## 1. Proje Özeti

ClipScore, kısa video içerik üreticilerinin videolarını yayınlamadan önce viral potansiyellerini ölçmelerini sağlayan **AI destekli Android uygulamasıdır.**

| Alan | Detay |
|---|---|
| **Uygulama Adı** | ClipScore |
| **Platform** | Android (min. API 26 / Android 8.0) |
| **Dil** | Kotlin |
| **AI Sağlayıcı** | Anthropic Claude (`claude-sonnet-4-20250514`) |
| **Hedef Kitle** | YouTube Shorts, TikTok, Reels içerik üreticileri |
| **Geliştirici** | Solo geliştirici |
| **Toplam Süre** | 8 hafta (~280 saat) |

### Zaman Gerçekçiliği

| Durum | Haftalık Saat | 8 Haftada Toplam |
|---|---|---|
| Part-time (öğrenci/yan iş) | 15–20 saat | ~130 saat |
| Full-time | 35–40 saat | ~280 saat |

> ⚠️ Bu plan **full-time veya yoğun part-time** çalışma varsayımına göre hazırlanmıştır.  
> Part-time çalışıyorsan her haftayı 1.5x uzat (8 hafta → 12 hafta).

---

## 2. Tech Stack — Tam Liste

### Android (Frontend)

| Kategori | Teknoloji | Versiyon | Kullanım |
|---|---|---|---|
| **Dil** | Kotlin | 1.9+ | Tüm uygulama |
| **UI** | Jetpack Compose | BOM 2024.04 | Tüm ekranlar |
| **Design System** | Material 3 | Latest | Tema, bileşenler |
| **Navigation** | Navigation Compose | 2.7.7 | Ekranlar arası geçiş |
| **State** | StateFlow + ViewModel | Lifecycle 2.7 | UI state yönetimi |
| **DI** | Hilt | 2.51 | Bağımlılık enjeksiyonu |
| **Async** | Kotlin Coroutines | 1.8 | API çağrıları, arka plan işleri |
| **Image** | Coil | 2.6 | Thumbnail yükleme |
| **Animation** | Compose Animation | BOM | Gauge, geçişler |
| **Video** | MediaMetadataRetriever | Android SDK | Metadata çıkarma |
| **Storage** | SharedPreferences | Android SDK | API key, ayarlar |
| **Local DB** | Room | 2.6 | Geçmiş analizler (v1.1) |

### Networking (Backend iletişim)

| Kategori | Teknoloji | Versiyon | Kullanım |
|---|---|---|---|
| **HTTP Client** | Retrofit | 2.9.0 | REST API çağrıları |
| **HTTP Engine** | OkHttp | 4.12.0 | Connection pooling |
| **Logging** | OkHttp Logging Interceptor | 4.12.0 | Debug log |
| **JSON** | Gson | 2.10.1 | JSON serialize/deserialize |
| **SSL** | OkHttp TLS | Built-in | HTTPS zorunlu |

### Backend (API Proxy — Güvenlik için)

| Kategori | Teknoloji | Kullanım |
|---|---|---|
| **Runtime** | Node.js 20 LTS | Sunucu |
| **Framework** | Express.js | REST endpoint'ler |
| **Hosting** | Railway veya Render (ücretsiz tier) | Deploy |
| **API Proxy** | `/analyze` endpoint | Claude API key'i gizler |
| **Rate Limiting** | express-rate-limit | Kötüye kullanım engeli |
| **CORS** | cors paketi | Android istemci izni |
| **Env** | dotenv | API key yönetimi |

> 💡 **Neden backend proxy?**  
> API key'i Android APK içine gömmek güvensizdir. APK tersine mühendislikle açılabilir.  
> Backend bir ara katman görevi görür: Android → Senin sunucun → Claude API.

### Araçlar & DevOps

| Araç | Kullanım |
|---|---|
| **Android Studio Hedgehog+** | IDE |
| **Git + GitHub** | Versiyon kontrolü |
| **GitHub Actions** | CI/CD (build + lint) |
| **Postman** | API test |
| **Firebase Crashlytics** | Hata takibi (opsiyonel) |
| **Figma** | UI tasarım referansı |

---

## 3. Mimari

### Android — MVVM + Clean Architecture (Hafifletilmiş)

```
┌─────────────────────────────────────────────────┐
│                 UI Layer (Compose)               │
│         Screen ←→ ViewModel ←→ StateFlow        │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│              Domain Layer (UseCase)              │
│         AnalyzeVideoUseCase                      │
│         GetMetadataUseCase                       │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│              Data Layer (Repository)             │
│    ClipScoreRepository (interface + impl)        │
│         ↓                      ↓                 │
│   RemoteDataSource        LocalDataSource        │
│   (Retrofit/API)          (SharedPrefs)          │
└──────────────────────────────────────────────────┘
```

### Backend — Express.js Proxy

```
Android App
    │
    │  POST /api/analyze
    │  { title, description }
    ▼
Express.js Server (Railway)
    │
    │  x-api-key: CLAUDE_KEY (gizli)
    │  POST /v1/messages
    ▼
Anthropic Claude API
    │
    ▼
JSON Response → Android
```

### Veri Akışı

```
Kullanıcı video seçer
    → MediaMetadataRetriever → VideoMetadata
    → Kullanıcı başlık/açıklama girer
    → AnalyzeVideoUseCase çağrılır
    → Repository → RemoteDataSource
    → Retrofit → Express Backend
    → Claude API
    → JSON parse → AnalysisResult
    → ViewModel StateFlow güncellenir
    → ResultScreen render edilir
```

---

## 4. Klasör Yapısı

### Android Projesi

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/clipscore/
│   │   │   │
│   │   │   ├── ClipScoreApp.kt              # Hilt Application
│   │   │   ├── MainActivity.kt
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ClaudeApiService.kt  # Retrofit interface
│   │   │   │   │   ├── ApiClient.kt         # OkHttp + Retrofit builder
│   │   │   │   │   └── ApiInterceptor.kt    # Header ekler (auth vb.)
│   │   │   │   ├── model/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── AnalyzeRequest.kt
│   │   │   │   │   ├── response/
│   │   │   │   │   │   └── AnalyzeResponse.kt
│   │   │   │   │   └── local/
│   │   │   │   │       └── VideoMetadata.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── ClipScoreRepository.kt      # Interface
│   │   │   │   │   └── ClipScoreRepositoryImpl.kt  # Impl
│   │   │   │   └── local/
│   │   │   │       └── PreferencesManager.kt       # SharedPrefs wrapper
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   └── AnalysisResult.kt    # Domain modeli
│   │   │   │   └── usecase/
│   │   │   │       ├── AnalyzeVideoUseCase.kt
│   │   │   │       └── ExtractMetadataUseCase.kt
│   │   │   │
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt             # Genel sağlayıcılar
│   │   │   │   ├── NetworkModule.kt         # Retrofit, OkHttp
│   │   │   │   └── RepositoryModule.kt      # Repository binding
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   └── Screen.kt            # Sealed class route'lar
│   │   │   │   ├── components/              # Ortak Compose bileşenleri
│   │   │   │   │   ├── ScoreCard.kt
│   │   │   │   │   ├── VibeGauge.kt
│   │   │   │   │   ├── HookItem.kt
│   │   │   │   │   └── LoadingOverlay.kt
│   │   │   │   ├── splash/
│   │   │   │   │   └── SplashScreen.kt
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   ├── preview/
│   │   │   │   │   ├── VideoPreviewScreen.kt
│   │   │   │   │   └── VideoPreviewViewModel.kt
│   │   │   │   ├── input/
│   │   │   │   │   ├── InputScreen.kt
│   │   │   │   │   └── InputViewModel.kt
│   │   │   │   ├── loading/
│   │   │   │   │   └── LoadingScreen.kt
│   │   │   │   └── result/
│   │   │   │       ├── ResultScreen.kt
│   │   │   │       └── ResultViewModel.kt
│   │   │   │
│   │   │   └── util/
│   │   │       ├── Constants.kt
│   │   │       ├── Extensions.kt
│   │   │       └── UiState.kt               # sealed class: Loading/Success/Error
│   │   │
│   │   └── res/
│   │       ├── font/                        # Montserrat, Nunito ttf dosyaları
│   │       ├── drawable/                    # İkonlar, logo
│   │       └── values/
│   │           └── strings.xml
│   │
│   └── test/                                # Unit testler
│       └── androidTest/                     # Instrumented testler
│
├── build.gradle.kts
└── proguard-rules.pro
```

### Backend Projesi (Ayrı Repo veya `/backend` klasörü)

```
backend/
├── src/
│   ├── index.js                 # Express app entry point
│   ├── routes/
│   │   └── analyze.js           # POST /api/analyze
│   ├── middleware/
│   │   ├── rateLimiter.js       # Rate limiting
│   │   └── validator.js         # Input validation
│   └── services/
│       └── claudeService.js     # Claude API iletişimi
├── .env                         # CLAUDE_API_KEY (git'e commit etme!)
├── .env.example                 # Örnek env dosyası
├── .gitignore
├── package.json
└── README.md
```

---

## 5. Haftalık Plan (8 Hafta)

---

### 📅 Hafta 1 — Foundation & Setup
> **Hedef:** Proje kurulumu, tema, navigation iskeleti  
> **Tahmini Süre:** 35 saat

#### Backend (10 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Node.js + Express projesi kur | 1 saat | `npm init`, paketler |
| `.env` + `dotenv` yapılandır | 30 dk | API key güvenliği |
| `POST /api/analyze` endpoint skeleton | 2 saat | Boş yanıt döndürür |
| Railway/Render'a deploy et | 2 saat | URL al, test et |
| Rate limiter middleware | 1 saat | IP başına 10 istek/dk |
| Input validation middleware | 1 saat | Boş başlık kontrolü |
| Postman koleksiyonu oluştur | 30 dk | Test için |
| README.md backend | 30 dk | Kurulum talimatları |

#### Android Frontend (25 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Android Studio proje kontrolü | 30 dk | Package name, min SDK |
| `build.gradle.kts` — tüm bağımlılıklar | 1 saat | Versiyon uyumluluğu |
| `Color.kt` — PRD renk paleti | 30 dk | 8 renk token |
| `Type.kt` — Montserrat + Nunito | 1 saat | Font dosyaları indir, res/font |
| `Theme.kt` — sabit dark theme | 30 dk | Dynamic color kapat |
| `Screen.kt` — sealed class route'lar | 30 dk | 6 ekran |
| `NavGraph.kt` — navigation kurulumu | 2 saat | Tüm route'lar bağlı |
| `SplashScreen.kt` | 2 saat | Logo + fade animasyon |
| `HomeScreen.kt` — UI shell | 3 saat | CTA butonu, layout |
| `HomeViewModel.kt` | 1 saat | Temel state |
| `UiState.kt` sealed class | 30 dk | Loading/Success/Error |
| Hilt — `ClipScoreApp`, `AppModule` | 2 saat | Application setup |
| Emülatörde navigation testi | 1 saat | 6 ekran arası geçiş |

**Hafta 1 Sonu Hedefi:** Backend canlıda, Android'de navigation çalışıyor, tema uygulanmış.

---

### 📅 Hafta 2 — UI Ekranları (Shell)
> **Hedef:** Tüm ekranlar mock veriyle tamamlanmış  
> **Tahmini Süre:** 35 saat

#### Backend (5 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Claude API bağlantısını gerçekle | 3 saat | `claudeService.js` |
| Mock response yerine gerçek yanıt | 1 saat | Test et |
| Hata yönetimi (timeout, 429) | 1 saat | Anlamlı hata mesajları |

#### Android Frontend (30 saat)

| Görev | Süre | Notlar |
|---|---|---|
| `components/ScoreCard.kt` | 2 saat | Renk kodlu kart bileşeni |
| `components/VibeGauge.kt` | 4 saat | Canvas ile yarım daire gauge |
| `components/HookItem.kt` | 1 saat | Hook satırı + kopyala butonu |
| `components/LoadingOverlay.kt` | 1 saat | Shimmer veya spinner |
| `VideoPreviewScreen.kt` — UI | 3 saat | Mock metadata kartları |
| `InputScreen.kt` — UI | 3 saat | TextField'lar, karakter sayacı |
| `LoadingScreen.kt` | 2 saat | Animasyonlu AI ekranı |
| `ResultScreen.kt` — UI | 5 saat | Gauge + 4 kart + hook listesi |
| Mock veriyle ResultScreen testi | 2 saat | Renk kodları doğru mu? |
| Responsive tasarım kontrolü | 2 saat | Farklı ekran boyutları |
| Dark theme tutarlılık kontrolü | 1 saat | Tüm ekranlarda |

**Hafta 2 Sonu Hedefi:** Tüm UI ekranları tamamlanmış, mock veriyle görsel olarak doğrulanmış.

---

### 📅 Hafta 3 — Video & Metadata
> **Hedef:** Gerçek video seçme ve metadata çıkarma  
> **Tahmini Süre:** 30 saat

#### Backend (5 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Prompt engineering v1 | 3 saat | JSON çıktı formatı optimize et |
| Response parse ve validation | 1 saat | Eksik alan kontrolü |
| Postman ile tam uçtan uca test | 1 saat | Gerçek başlıkla test |

#### Android Frontend (25 saat)

| Görev | Süre | Notlar |
|---|---|---|
| `AndroidManifest.xml` izinleri | 30 dk | `READ_MEDIA_VIDEO` |
| `ActivityResultContracts.GetContent` | 2 saat | Galeri açma |
| `ExtractMetadataUseCase.kt` | 3 saat | `MediaMetadataRetriever` |
| `VideoMetadata` data class | 30 dk | Tüm alanlar |
| 500 MB dosya boyutu kontrolü | 1 saat | Snackbar hata |
| Thumbnail çıkarma (ilk frame) | 2 saat | Bitmap → Coil |
| `VideoPreviewViewModel.kt` | 2 saat | StateFlow, video URI yönetimi |
| `VideoPreviewScreen.kt` — gerçek veri | 2 saat | Mock'tan gerçeğe geç |
| Format kontrolü (mp4, mov vb.) | 1 saat | Desteklenmeyen format hatası |
| Edge case'ler | 2 saat | Silinen video, izin reddi |
| Entegrasyon testi | 2 saat | Farklı video formatları |

**Hafta 3 Sonu Hedefi:** Video seçilip metadata ekranda doğru gösteriliyor.

---

### 📅 Hafta 4 — API Entegrasyonu
> **Hedef:** Android ↔ Backend ↔ Claude bağlantısı tam çalışıyor  
> **Tahmini Süre:** 35 saat

#### Backend (10 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Prompt engineering v2 | 2 saat | Hook kalitesi iyileştirme |
| Türkçe/İngilizce dil desteği | 1 saat | Prompt'a parametre ekle |
| CORS yapılandırması | 1 saat | Sadece kendi Android app'i |
| Logging middleware | 1 saat | İstek kayıtları |
| `/health` endpoint | 30 dk | Uptime kontrolü |
| Yük testi (basit) | 1 saat | 10 eş zamanlı istek |
| Hata senaryoları dökümantasyonu | 30 dk | 400, 429, 500 |
| Backend README güncelle | 30 dk | Deploy talimatları |

#### Android Frontend (25 saat)

| Görev | Süre | Notlar |
|---|---|---|
| `NetworkModule.kt` — Hilt | 2 saat | Retrofit, OkHttp sağlayıcıları |
| `ApiInterceptor.kt` | 1 saat | Header yönetimi |
| `ClaudeApiService.kt` | 1 saat | Retrofit interface |
| `AnalyzeRequest.kt` | 30 dk | Request data class |
| `AnalyzeResponse.kt` | 30 dk | Response data class |
| `AnalysisResult.kt` — domain model | 30 dk | UI için temiz model |
| `ClipScoreRepositoryImpl.kt` | 3 saat | API çağrısı + parse |
| `AnalyzeVideoUseCase.kt` | 1 saat | İş mantığı |
| `RepositoryModule.kt` — Hilt | 1 saat | Binding |
| `InputViewModel.kt` — API bağlantısı | 2 saat | StateFlow + coroutine |
| `ResultViewModel.kt` | 2 saat | Sonuç state yönetimi |
| Network hata yönetimi | 3 saat | No internet, timeout, 429 |
| Uçtan uca test (gerçek API) | 3 saat | Tam akış testi |

**Hafta 4 Sonu Hedefi:** Gerçek başlık girilip gerçek AI skoru alınabiliyor.

---

### 📅 Hafta 5 — Result Dashboard & Animasyonlar
> **Hedef:** Sonuç ekranı canlı veriyle animasyonlu çalışıyor  
> **Tahmini Süre:** 30 saat

#### Backend (3 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Response format v3 — final | 1 saat | Tüm alanlar doğrulandı |
| Edge case: boş hook listesi | 30 dk | Fallback değerler |
| Production environment kontrolü | 1 saat | Railway/Render ayarları |
| Monitoring kurulumu | 30 dk | UptimeRobot (ücretsiz) |

#### Android Frontend (27 saat)

| Görev | Süre | Notlar |
|---|---|---|
| `VibeGauge.kt` — sweep animasyonu | 4 saat | 0'dan skora animasyon |
| Skor renk kodlaması (0-40/41-70/71-100) | 1 saat | Her kart dinamik renk |
| `ScoreCard.kt` — animasyonlu giriş | 2 saat | Staggered slide-in |
| Hook listesi + kopyala | 2 saat | `ClipboardManager` |
| Açıklama + hashtag kopyala | 1 saat | Tek tıkla |
| "Yeniden üret" butonu | 2 saat | Yeni API isteği |
| Android Share Sheet | 2 saat | `Intent.ACTION_SEND` |
| Skeleton loading states | 2 saat | Veri gelene kadar |
| Hata ekranı + Retry butonu | 2 saat | Network hatası UI |
| `ResultScreen.kt` — gerçek veri bağlama | 3 saat | Mock'tan gerçeğe |
| Tam akış kullanıcı testi | 4 saat | 5 farklı senaryo |

**Hafta 5 Sonu Hedefi:** Tüm ana akış uçtan uca çalışıyor, animasyonlar tamamlanmış.

---

### 📅 Hafta 6 — Hata Yönetimi, Edge Cases & Settings
> **Hedef:** Uygulama sağlam, çökmüyor, tüm senaryolar ele alınmış  
> **Tahmini Süre:** 30 saat

#### Backend (5 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Global error handler | 1 saat | Tek noktada hata yönetimi |
| Input sanitization | 1 saat | XSS, injection önlemi |
| API versiyonlama (`/api/v1/`) | 30 dk | İleriye uyumluluk |
| Backend unit testler | 2 saat | Jest ile |
| `.env.example` dosyası | 30 dk | Yeni deploy için |

#### Android Frontend (25 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Offline detection + UI | 2 saat | `ConnectivityManager` |
| Boş input validasyonu | 1 saat | Başlık zorunlu kontrol |
| Rate limit UI (429 hatası) | 1 saat | "Çok fazla istek" mesajı |
| Büyük dosya uyarısı | 30 dk | 100–500 MB arası uyarı |
| `SettingsScreen.kt` | 3 saat | Dil seçimi, hakkında |
| `PreferencesManager.kt` | 1 saat | SharedPrefs wrapper |
| Dil seçimi (TR/EN) | 2 saat | Prompt'a parametre geçilir |
| Back press yönetimi | 1 saat | Navigation stack |
| Memory leak kontrolü | 2 saat | LeakCanary ile |
| Farklı Android sürüm testleri | 3 saat | API 26, 30, 33, 34 |
| Farklı ekran boyutu testleri | 2 saat | 5", 6.5", tablet |
| `strings.xml` — tüm metinler | 1 saat | Hardcoded string'leri kaldır |
| Accessibility kontrol | 1 saat | Content description'lar |

**Hafta 6 Sonu Hedefi:** Uygulama stabil, edge case'ler ele alınmış.

---

### 📅 Hafta 7 — Test, Optimizasyon & CI/CD
> **Hedef:** Test kapsamı, performans optimizasyonu, otomatik build  
> **Tahmini Süre:** 30 saat

#### Backend (5 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Integration testler | 2 saat | Gerçek API ile e2e |
| Response süresi optimizasyonu | 1 saat | Ortalama < 8 saniye hedef |
| Concurrency testi | 1 saat | 20 eş zamanlı istek |
| Final production deploy | 1 saat | Environment kontrol |

#### Android (15 saat)

| Görev | Süre | Notlar |
|---|---|---|
| ViewModel unit testleri | 3 saat | JUnit + MockK |
| Repository unit testleri | 2 saat | Fake data source |
| UseCase testleri | 2 saat | İş mantığı doğrulama |
| Compose UI testleri | 3 saat | Kritik ekranlar |
| Performans profili | 2 saat | Android Studio Profiler |
| APK boyutu optimizasyonu | 1 saat | ProGuard kuralları |
| R8 / ProGuard yapılandırması | 2 saat | Release build |

#### CI/CD (10 saat)

| Görev | Süre | Notlar |
|---|---|---|
| GitHub Actions — Android lint | 2 saat | Her PR'da çalışır |
| GitHub Actions — unit test | 2 saat | Otomatik test |
| GitHub Actions — release build | 3 saat | APK üretimi |
| `.gitignore` kontrolü | 30 dk | `local.properties`, `.env` |
| Branch stratejisi | 30 dk | `main`, `develop`, `feature/*` |
| README.md — Android | 2 saat | Kurulum + katkı talimatları |

**Hafta 7 Sonu Hedefi:** Test kapsamı sağlam, CI pipeline çalışıyor.

---

### 📅 Hafta 8 — Play Store & Lansman
> **Hedef:** Uygulama yayında  
> **Tahmini Süre:** 30 saat

#### Son Kontroller (10 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Beta test (5–10 kişi) | 3 saat | Gerçek kullanıcı geri bildirimi |
| Kritik hata düzeltmeleri | 3 saat | Beta geri bildirimlerine göre |
| Release APK → AAB dönüşümü | 1 saat | Play Store AAB gerektirir |
| Signing keystore oluştur | 1 saat | Güvenli sakla! |
| Final uçtan uca test | 2 saat | Temiz cihazda test |

#### Play Store Hazırlık (15 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Uygulama ikonu (512x512) | 2 saat | Adaptive icon |
| Feature graphic (1024x500) | 1 saat | Store görseli |
| Ekran görüntüleri (8 adet) | 3 saat | Farklı ekran boyutları |
| Kısa açıklama (80 karakter) | 30 dk | ASO optimize |
| Uzun açıklama (4000 karakter) | 1 saat | Anahtar kelimeler |
| Gizlilik Politikası sayfası | 1 saat | GitHub Pages veya Notion |
| Play Store geliştirici hesabı | 30 dk | $25 tek seferlik ücret |
| Google Play Console — uygulama oluştur | 1 saat | |
| İçerik derecelendirmesi anketi | 30 dk | |
| Hedef kitle bildirimi | 30 dk | |
| AAB yükleme | 1 saat | |
| İnceleme için gönder | 30 dk | 1–3 gün inceleme süresi |

#### Post-Lansman (5 saat)

| Görev | Süre | Notlar |
|---|---|---|
| Lansman duyurusu | 2 saat | Reddit, Twitter/X, Discord |
| İlk yorumları takip et | 1 saat | |
| Crashlytics kurulumu | 1 saat | Hata takibi |
| v1.1 backlog oluştur | 1 saat | Geri bildirime göre |

**Hafta 8 Sonu Hedefi:** 🎉 Uygulama Play Store'da yayında.

---

## 6. Backend Detayları

### Express.js Kurulumu

```javascript
// package.json bağımlılıkları
{
  "dependencies": {
    "express": "^4.18.0",
    "cors": "^2.8.5",
    "dotenv": "^16.0.0",
    "express-rate-limit": "^7.0.0",
    "express-validator": "^7.0.0"
  },
  "devDependencies": {
    "jest": "^29.0.0",
    "nodemon": "^3.0.0"
  }
}
```

### Ana Endpoint

```javascript
// POST /api/v1/analyze
// Body: { title: string, description: string, language: "tr" | "en" }
// Response: { vibeScore, hookScore, keywordScore, emotionScore, ctaScore,
//             hooks: string[], description: string, hashtags: string[] }
```

### Rate Limiting

```javascript
// IP başına: 10 istek / dakika
// Aşılırsa: 429 Too Many Requests
// Header: Retry-After: 60
```

### Hata Kodları

| Kod | Durum | Açıklama |
|---|---|---|
| 200 | OK | Başarılı analiz |
| 400 | Bad Request | Eksik/geçersiz alan |
| 429 | Too Many Requests | Rate limit aşıldı |
| 503 | Service Unavailable | Claude API erişilemiyor |
| 500 | Internal Server Error | Beklenmedik hata |

### Deployment (Railway)

```bash
# 1. Railway hesabı aç: railway.app
# 2. GitHub repo bağla
# 3. Environment variable ekle: CLAUDE_API_KEY
# 4. Deploy — otomatik URL alırsın
# Örnek: https://clipscore-backend.railway.app
```

---

## 7. Frontend Detayları

### Renk Sistemi

```kotlin
// Color.kt
val Background = Color(0xFF0A0A0A)
val Surface = Color(0xFF1A1A1A)
val Primary = Color(0xFF7C3AED)
val OnBackground = Color(0xFFF8FAFC)
val OnSurface = Color(0xFFCBD5E1)
val Success = Color(0xFF22C55E)   // Skor 71–100
val Warning = Color(0xFFF59E0B)   // Skor 41–70
val Error = Color(0xFFEF4444)     // Skor 0–40
```

### Skor Renk Mantığı

```kotlin
fun scoreColor(score: Int): Color = when {
    score >= 71 -> Success
    score >= 41 -> Warning
    else -> Error
}
```

### Navigation Route'ları

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object VideoPreview : Screen("video_preview")
    object Input : Screen("input")
    object Loading : Screen("loading")
    object Result : Screen("result")
    object Settings : Screen("settings")
}
```

### UiState

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---

## 8. API Entegrasyonu

### Prompt Yapısı (Final)

```
Sen bir sosyal medya içerik uzmanısın.
Aşağıdaki kısa video içeriğini analiz et ve SADECE geçerli JSON döndür.

Başlık: {title}
Açıklama: {description}
Dil: {language}

JSON formatı (başka hiçbir şey yazma):
{
  "vibeScore": <0-100 genel viral potansiyel>,
  "hookScore": <0-100 ilk 3 saniye dikkat çekicilik>,
  "keywordScore": <0-100 SEO anahtar kelime uyumu>,
  "emotionScore": <0-100 duygusal etki gücü>,
  "ctaScore": <0-100 harekete geçirme kalitesi>,
  "hooks": ["<dikkat çekici açılış 1>", "<açılış 2>", "<açılış 3>"],
  "description": "<SEO uyumlu 150+ kelime video açıklaması>",
  "hashtags": ["#tag1", "#tag2", "#tag3", "#tag4", "#tag5"]
}
```

### Retrofit Service

```kotlin
interface ClaudeApiService {
    @POST("api/v1/analyze")
    suspend fun analyze(
        @Body request: AnalyzeRequest
    ): Response<AnalyzeResponse>
}
```

### OkHttp Timeout Yapılandırması

```kotlin
OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    .build()
```

---

## 9. Veri Modelleri

### Request

```kotlin
data class AnalyzeRequest(
    val title: String,
    val description: String,
    val language: String = "tr"
)
```

### Response (Backend'den gelen)

```kotlin
data class AnalyzeResponse(
    val vibeScore: Int,
    val hookScore: Int,
    val keywordScore: Int,
    val emotionScore: Int,
    val ctaScore: Int,
    val hooks: List<String>,
    val description: String,
    val hashtags: List<String>
)
```

### Domain Model (UI için)

```kotlin
data class AnalysisResult(
    val vibeScore: Int,
    val categories: List<ScoreCategory>,
    val hooks: List<String>,
    val generatedDescription: String,
    val hashtags: List<String>
)

data class ScoreCategory(
    val name: String,
    val score: Int,
    val emoji: String
)

// Video metadata
data class VideoMetadata(
    val uri: Uri,
    val duration: Long,
    val resolution: String,
    val fps: Int,
    val fileSizeBytes: Long,
    val format: String,
    val thumbnailBitmap: Bitmap?
)
```

---

## 10. Güvenlik

### Android Tarafı

```
local.properties             ← API key buraya (git'e gitmez)
    ↓
build.gradle.kts             ← BuildConfig'e aktarılır
    ↓
NetworkModule.kt (Hilt)      ← Runtime'da inject edilir
    ↓
ApiInterceptor.kt            ← Header olarak eklenir
```

### `.gitignore` Kontrol Listesi

```gitignore
# Android
local.properties
*.keystore
*.jks
/build
.gradle

# Backend
.env
node_modules/
```

### Güvenlik Kontrol Listesi

- [ ] API key kaynak koda yazılmamış
- [ ] `local.properties` `.gitignore`'da
- [ ] `.env` `.gitignore`'da
- [ ] Backend HTTPS zorunlu
- [ ] Rate limiting aktif
- [ ] Input validation hem Android hem backend'de
- [ ] ProGuard/R8 release build'de aktif
- [ ] Signing keystore güvenli saklanmış

---

## 11. Test Stratejisi

### Test Piramidi

```
        ╔══════════╗
        ║  E2E (5) ║          ← Emülatörde tam akış
       ╔╩══════════╩╗
       ║  UI  (15) ║          ← Compose testler
      ╔╩════════════╩╗
      ║  Unit  (30) ║         ← ViewModel, UseCase, Repository
     ╚══════════════╝
```

### Unit Test Kapsamı

| Sınıf | Test Edilecek |
|---|---|
| `AnalyzeVideoUseCase` | Başarılı, hatalı, boş input senaryoları |
| `ExtractMetadataUseCase` | Format kontrolü, boyut limiti |
| `ClipScoreRepositoryImpl` | API başarı/hata mapping |
| `InputViewModel` | Validasyon mantığı |
| `ResultViewModel` | State geçişleri |

### Manuel Test Senaryoları

| # | Senaryo | Beklenen Sonuç |
|---|---|---|
| 1 | Normal video + başlık → analiz | Skor ekranı açılır |
| 2 | 600 MB video seç | Hata mesajı gösterilir |
| 3 | İnternet yokken analiz et | "Bağlantı yok" ekranı |
| 4 | Başlık boş bırak | "Başlık zorunlu" uyarısı |
| 5 | Hook kopyala | Clipboard'a kopyalandı toast |
| 6 | Sonucu paylaş | Share sheet açılır |
| 7 | 10 hızlı istek gönder | Rate limit uyarısı |
| 8 | Uygulama arka plana al → geri dön | State korunuyor |

---

## 12. CI/CD & Play Store

### GitHub Actions Workflow

```yaml
# .github/workflows/android.yml
# Tetikleyiciler: push to main, PR to main
# Adımlar: lint → unit test → release build → artifact upload
```

### Play Store Kontrol Listesi

- [ ] Geliştirici hesabı açıldı ($25)
- [ ] Signing keystore oluşturuldu ve yedeklendi
- [ ] AAB (Android App Bundle) oluşturuldu
- [ ] Uygulama ikonu (512x512 PNG)
- [ ] Feature graphic (1024x500 PNG)
- [ ] En az 2 telefon ekran görüntüsü
- [ ] Gizlilik politikası URL'si hazır
- [ ] İçerik derecelendirmesi tamamlandı
- [ ] Hedef yaş grubu belirlendi

---

## 13. Riskler

| Risk | Olasılık | Etki | Azaltma |
|---|---|---|---|
| Claude API maliyeti beklenenden yüksek | Orta | Yüksek | Günlük limit + önbellekleme |
| Play Store reddi | Düşük | Yüksek | Politikaları önceden oku |
| Backend Railway ücretsiz limit | Orta | Orta | Render fallback hazır tut |
| API key sızıntısı | Düşük | Çok Yüksek | Backend proxy + rotasyon planı |
| Süre aşımı (2 ay yetmez) | Orta | Orta | MVP'yi küçült, post-MVP'ye al |
| Motivasyon kaybı | Orta | Yüksek | Haftalık küçük hedefler |

---

## 14. Başarı Metrikleri

### Teknik Hedefler (Yayın öncesi)

| Metrik | Hedef |
|---|---|
| API yanıt süresi | < 10 saniye |
| APK boyutu | < 20 MB |
| Uygulama başlangıç süresi | < 2 saniye |
| Çökme oranı | < %1 |
| Unit test kapsamı | > %60 |

### İş Hedefleri (1. ay)

| KPI | Hedef |
|---|---|
| Toplam indirme | 300 |
| Günlük aktif kullanıcı | 30 |
| Play Store puanı | 4.0+ |
| Analiz tamamlama oranı | %55 |

---

## 15. Post-MVP Yol Haritası

| Sürüm | Özellik | Öncelik | Tahmini Süre |
|---|---|---|---|
| **v1.1** | Geçmiş analiz arşivi (Room DB) | P1 | +2 hafta |
| **v1.2** | Günlük limit (ücretsiz: 3/gün) | P1 | +1 hafta |
| **v1.3** | Thumbnail skorer | P2 | +3 hafta |
| **v1.4** | Firebase Analytics entegrasyonu | P1 | +1 hafta |
| **v2.0** | In-app purchase (Pro plan) | P1 | +4 hafta |
| **v2.1** | On-device vision (MediaPipe) | P3 | +6 hafta |
| **v3.0** | iOS versiyonu | P2 | +3 ay |

---

## 🔧 Geliştirme Başlangıç Rehberi

```bash
# Android
git clone https://github.com/kullanici-adi/clipscore-android.git
# local.properties'e ekle:
# BACKEND_BASE_URL=https://your-backend.railway.app/
# Android Studio → Sync Project → Run

# Backend
git clone https://github.com/kullanici-adi/clipscore-backend.git
cd clipscore-backend
cp .env.example .env
# .env dosyasına CLAUDE_API_KEY ekle
npm install
npm run dev
```

---

*ClipScore v1.0 — 8 Haftalık Geliştirme Planı*  
*Son güncelleme: Nisan 2026*
