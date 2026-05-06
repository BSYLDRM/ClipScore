# ⚡ ClipScore

> **"Paylaşmadan önce skorunu öğren"**

AI destekli kısa video ön-yayın analiz uygulaması. YouTube Shorts, TikTok ve Instagram Reels içerik üreticileri için.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin)
![AI](https://img.shields.io/badge/AI-Claude%20API-7C3AED?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0%20MVP-orange?style=flat-square)

---

## 📱 Uygulama Ekranları

| # | Ekran | Açıklama |
|---|-------|----------|
| 1 | Splash Screen | Logo animasyonu, 2 saniyelik giriş |
| 2 | Auth Screen | Giriş Yap / Kayıt Ol — Email+Şifre, Google, Apple |
| 3 | Ana Ekran | Video analiz CTA + son analiz özet kartı |
| 4 | Video Önizleme | Thumbnail + metadata kartları (süre, çözünürlük, FPS, boyut, format) |
| 5 | Başlık & Açıklama | Input alanları, dil seçici (TR/EN), karakter sayacı |
| 6 | Loading | Animasyonlu AI analiz ekranı, döngüsel durum mesajları |
| 7 | Sonuç Dashboard | VibeScore gauge + 4 alt skor + hook önerileri + SEO açıklaması |

### Navigasyon Akışı

```
Splash → Auth → Home → Video Önizleme → Başlık Girişi → Loading → Sonuç
```

---

## 🛠️ Tech Stack

| Katman | Teknoloji |
|--------|-----------|
| Dil | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Mimari | MVVM + StateFlow |
| Navigasyon | Navigation Compose |
| HTTP İstemcisi | Retrofit + OkHttp + Gson |
| AI Sağlayıcı | Anthropic Claude API (`claude-sonnet-4-20250514`) |
| Video İşleme | Android MediaMetadataRetriever |
| Yerel Depolama | SharedPreferences |
| Bağımlılık Yönetimi | Hilt |
| Build Sistemi | Gradle (Kotlin DSL) |
| Min SDK | API 26 — Android 8.0+ |

---

## 📁 Proje Yapısı

```
clipscore/
├── backend/
│   ├── main.py                      # Flask proxy sunucusu
│   ├── requirements.txt
│   ├── .env                         # CLAUDE_API_KEY (git'e commit edilmez!)
│   └── .env.example
│
└── app/src/main/java/com/example/clipscore/
    ├── MainActivity.kt
    ├── NavGraph.kt
    ├── ui/
    │   ├── theme/
    │   │   ├── Color.kt             # ClipScore renk paleti
    │   │   ├── Type.kt              # Montserrat + Nunito fontları
    │   │   └── Theme.kt             # Dark theme
    │   ├── screens/
    │   │   ├── SplashScreen.kt
    │   │   ├── AuthScreen.kt
    │   │   ├── HomeScreen.kt
    │   │   ├── VideoPreviewScreen.kt
    │   │   ├── TitleInputScreen.kt
    │   │   ├── LoadingScreen.kt
    │   │   └── ResultScreen.kt
    │   └── components/
    │       ├── ScoreCard.kt
    │       ├── MetadataCard.kt
    │       └── ClipScoreButton.kt
```

---

## 🎨 Design System

### Renk Paleti

| Token | Hex | Kullanım |
|-------|-----|----------|
| `BrandBg` | `#0A0A0A` | Ana arka plan |
| `BrandSurface` | `#141414` | Kart arka planı |
| `BrandPrimary` | `#7C3AED` | Butonlar, vurgular |
| `BrandSuccess` | `#22C55E` | Yüksek skor (71–100) |
| `BrandWarning` | `#F59E0B` | Orta skor (41–70) |
| `BrandError` | `#EF4444` | Düşük skor (0–40) |
| `BrandText` | `#F8FAFC` | Ana metin |
| `BrandBorder` | `#2A2A2A` | Kart kenarlıkları |

### Tipografi

- **Başlıklar & Logo:** Montserrat ExtraBold / Bold
- **Skor Sayıları:** Montserrat ExtraBold
- **Gövde Metni:** Nunito Regular / SemiBold

### Skor Renk Mantığı

```
0  – 40  → BrandError   (#EF4444)  →  "ZAYIF"
41 – 70  → BrandWarning (#F59E0B)  →  "ORTA"
71 – 100 → BrandSuccess (#22C55E)  →  "İYİ"
```

---

## 🔌 API Entegrasyonu

### Mimari

Android uygulaması Claude API'ye **doğrudan bağlanmaz**. Yerel Flask proxy üzerinden iletişim kurulur:

```
Android App  →  Flask Backend (10.0.2.2:5000)  →  Claude API
```

### Endpoint'ler

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| `GET` | `/api/health` | Sunucu sağlık kontrolü |
| `GET` | `/api/message` | Bağlantı testi |
| `POST` | `/api/analyze` | Ana analiz endpoint'i |

### Örnek İstek

```bash
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Bir günde 10kg verdim!",
    "description": "Diyet sırlarımı paylaşıyorum",
    "language": "tr"
  }'
```

### Örnek Yanıt

```json
{
  "vibeScore": 73,
  "hookScore": 81,
  "keywordScore": 68,
  "emotionScore": 79,
  "ctaScore": 64,
  "hooks": [
    "Doktorum bile inanamadı — 1 günde 10 kilo nasıl verdim?",
    "Bu diyeti kimse size söylemez... ben söylüyorum",
    "Sabah 60kg, akşam 50kg — imkansız mı? İzle de gör"
  ],
  "description": "SEO uyumlu 150+ kelimelik açıklama...",
  "hashtags": ["#diyet", "#kilo", "#sağlık", "#fitness", "#shorts"]
}
```

---

## 🚀 Kurulum

### Ön Koşullar

- Android Studio Hedgehog veya üzeri
- Python 3.11+
- Android Emülatör (API 26+) veya fiziksel cihaz
- [Anthropic API anahtarı](https://console.anthropic.com)

### 1. Backend Kurulumu

```bash
cd backend

# Bağımlılıkları yükle
pip install -r requirements.txt

# .env dosyasını oluştur
cp .env.example .env

# API anahtarını ekle
echo "CLAUDE_API_KEY=sk-ant-..." >> .env

# Sunucuyu başlat
python main.py
```

Sunucu `http://localhost:5000` adresinde çalışmaya başlar.

### 2. Android Uygulaması

1. Android Studio'da projeyi aç
2. Emülatörü başlat (API 26+)
3. **Run ▶** ile uygulamayı çalıştır

> **Not:** Emülatör backend'e `10.0.2.2:5000` üzerinden bağlanır.
> Fiziksel cihaz kullanıyorsan bilgisayarının yerel IP'sini kullan (örn. `192.168.1.x:5000`).

### 3. Kurulum Doğrulama

```bash
# Backend sağlık kontrolü
curl http://localhost:5000/api/health
# → {"status": "ok", "service": "clipscore-backend"}

# Analiz testi
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"title": "Test", "description": "Test açıklama", "language": "tr"}'
# → vibeScore, hooks vb. içeren JSON
```

---

## 🔒 Güvenlik

- `CLAUDE_API_KEY` hiçbir zaman kaynak koda yazılmaz
- API anahtarı `backend/.env` dosyasında tutulur
- `.env` dosyası `.gitignore` ile versiyon kontrolü dışında bırakılır
- Android uygulaması API anahtarını görmez; sadece backend URL'ini bilir

> ⚠️ `.env` dosyasını **asla** GitHub'a push etme!

---

## 🗺️ Yol Haritası (Post-MVP)

| Sürüm | Özellik | Durum |
|-------|---------|-------|
| v1.0 | Frontend UI (7 ekran) | ✅ Tamamlandı |
| v1.0 | Backend Flask proxy | ✅ Tamamlandı |
| v1.1 | Geçmiş analiz arşivi (Room DB) | 🔜 Planlandı |
| v1.2 | Thumbnail skorer | 🔜 Planlandı |
| v1.3 | On-device vision (MediaPipe) | 🔜 Planlandı |
| v2.0 | iOS versiyonu | 🔜 Planlandı |
| v2.1 | Sesli önizleme (ElevenLabs) | 🔜 Planlandı |

---

## 📊 Başarı Metrikleri (Hedef)

| Metrik | 30. Gün | 60. Gün | 90. Gün |
|--------|---------|---------|---------|
| İndirme | 300 | 700 | 1.500 |
| DAU | 30 | 80 | 150 |
| Premium Dönüşüm | — | %3 | %5 |
| MRR | — | — | ₺5.000 |

---

## 📝 Notlar (Bu Teslim İçin)

Bu teslimatta tüm ekranlar **mock veri** ile çalışmaktadır. Backend entegrasyonu tamamlandığında `ResultScreen.kt` içindeki mock değerler canlı API yanıtlarıyla değiştirilecektir.

**Kullanılan mock veri:**
```kotlin
val mockVibeScore = 73
val mockHooks = listOf("Hook 1...", "Hook 2...", "Hook 3...")
// Detaylar ResultScreen.kt içinde
```

---

<div align="center">
  <strong>⚡ ClipScore</strong> — v1.0 MVP &nbsp;•&nbsp; Nisan 2026
</div>
