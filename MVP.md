# 📱 ClipScore — MVP Dökümanı

> **"Know your score before you post"**  
> Sürüm: 1.0 | Platform: Android (Kotlin) | Tarih: Nisan 2026

---

## 1. Uygulama Genel Bakış

| Alan | Detay |
|---|---|
| **Uygulama Adı** | ClipScore |
| **Tagline** | "Paylaşmadan önce skorunu öğren" |
| **Platform** | Android (min. API 26 / Android 8.0) |
| **Geliştirme Dili** | Kotlin |
| **IDE** | Android Studio |
| **Hedef Kitle** | YouTube Shorts, TikTok ve Instagram Reels içerik üreticileri |
| **Temel Değer Önerisi** | Videoyu yayınlamadan önce viral potansiyelini AI ile ölçen, hook ve açıklama üretebilen mobil analiz aracı |

---

## 2. Problem

İçerik üreticileri her gün video yayınlıyor ancak videonun tutup tutmayacağını önceden bilmenin bir yolu yok. Başlık zayıf mı? Hook eksik mi? Açıklama yetersiz mi? Bu sorular ancak video yayınlandıktan sonra, düşük izlenme rakamlarıyla anlaşılıyor. ClipScore bu sorunu **yayın öncesinde** çözüyor.

---

## 3. MVP Kapsam — Olmazsa Olmaz Özellikler

### 3.1 Video Yükleme & Metadata Analizi
- Kullanıcı cihazından video seçer
- Uygulama otomatik olarak şunları çıkarır:
  - Video süresi
  - Çözünürlük (720p / 1080p / 4K)
  - Dosya boyutu
  - FPS (kare hızı)
  - Format (mp4, mov vb.)
- Veriler temiz bir dashboard kartında gösterilir

### 3.2 Trend Skoru Motoru
- Kullanıcı video başlığı ve açıklamasını girer
- Claude AI (Anthropic API) analiz eder ve 0–100 arası skor üretir
- Skor 4 alt kategoriye ayrılır:
  - 🎣 Hook Gücü
  - 🔍 Anahtar Kelime Uyumu
  - ❤️ Duygusal Tetikleyici
  - 📢 Harekete Geçirme (CTA) Kalitesi

### 3.3 AI Hook Üretici
- Video konusuna göre **3 alternatif hook cümlesi** üretilir
- Kopyala butonu ile panoya alınır
- Claude AI tarafından üretilir

### 3.4 AI Açıklama Üretici
- Video başlığı/konusuna göre:
  - SEO uyumlu 1 video açıklaması
  - İlgili hashtagler
- Kopyala butonu ile panoya alınır

### 3.5 Sonuç Dashboard'u
- Genel VibeScore animasyonlu gösterge (gauge) ile gösterilir
- Renk kodlu skor kartları (Yeşil: iyi / Sarı: orta / Kırmızı: zayıf)
- Üretilen hook ve açıklamalar listelenir
- Sonuçları paylaş butonu

---

## 4. MVP Dışı Kalan Özellikler (Sonraki Sürüm)

| Özellik | Neden MVP Dışı |
|---|---|
| On-device vision (MediaPipe) | Teknik karmaşıklık yüksek |
| Sesli önizleme (ElevenLabs) | Ek API maliyeti |
| Rakip karşılaştırma | Veri kaynağı gerektirir |
| Thumbnail skorer | Görüntü işleme altyapısı gerektirir |
| En iyi yayın saati | Analytics entegrasyonu gerektirir |
| Geçmiş / Kaydedilen skorlar | MVP sonrası P1 özellik |

---

## 5. Teknik Yığın (Tech Stack)

| Katman | Teknoloji |
|---|---|
| **Dil** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **State Management** | ViewModel + StateFlow |
| **AI API** | Anthropic Claude API (claude-sonnet-4-20250514) |
| **HTTP İstemcisi** | Retrofit + OkHttp |
| **Yerel Depolama** | SharedPreferences |
| **Video İşleme** | Android MediaMetadataRetriever |
| **Bağımlılık Enjeksiyonu** | Hilt |
| **Build** | Gradle (Kotlin DSL) |

---

## 6. MVP Kullanıcı Akışı

```
Uygulama Açılır
      ↓
Ana Ekran — "Videoyu Analiz Et" butonu
      ↓
Galeriden Video Seç
      ↓
Metadata Otomatik Çıkarılır (süre, çözünürlük, fps)
      ↓
Başlık & Açıklama Giriş Ekranı
      ↓
"Skoru Hesapla" butonuna basılır
      ↓
Claude API'ye istek gönderilir
      ↓
Yükleniyor animasyonu gösterilir
      ↓
Sonuç Dashboard'u açılır
      ↓
VibeScore + Alt Kategoriler + Hook Önerileri gösterilir
      ↓
Kopyala / Paylaş
```

---

## 7. Ekran Listesi (MVP)

| # | Ekran Adı | Açıklama |
|---|---|---|
| 1 | Splash Screen | Logo animasyonu, 2 saniyelik giriş |
| 2 | Ana Ekran | Video seç butonu, son analiz özeti |
| 3 | Video Önizleme | Seçilen video + metadata kartı |
| 4 | Başlık Giriş | Başlık + açıklama input alanları |
| 5 | Yükleniyor | AI analiz animasyonu |
| 6 | Sonuç Dashboard | Skor, breakdown, hook önerileri |

---

## 8. API Entegrasyonu — Claude AI

```kotlin
// Örnek istek yapısı
POST https://api.anthropic.com/v1/messages

{
  "model": "claude-sonnet-4-20250514",
  "max_tokens": 1000,
  "messages": [
    {
      "role": "user",
      "content": "Aşağıdaki YouTube Shorts başlığını ve açıklamasını analiz et. 
                  0-100 arası bir viral skor ver ve 3 alternatif hook cümlesi üret.
                  Başlık: [BAŞLIK]
                  Açıklama: [AÇIKLAMA]"
    }
  ]
}
```

---

## 9. MVP Zaman Çizelgesi

| Hafta | Görevler |
|---|---|
| **Hafta 1** | Android Studio proje kurulumu, UI ekranları (Compose), renk teması |
| **Hafta 2** | Video seçme, MediaMetadataRetriever ile metadata çıkarma |
| **Hafta 3** | Claude API entegrasyonu — skor + hook + açıklama üretimi |
| **Hafta 4** | Sonuç dashboard'u, animasyonlar, hata yönetimi, test |
| **Hafta 5** | Play Store hazırlığı — ikon, ekran görüntüleri, açıklama, yayın |

---

## 10. Başarı Metrikleri

| Metrik | Hedef |
|---|---|
| İlk ay indirme | 500+ |
| Play Store puanı | 4.0 ve üzeri |
| Analiz tamamlama oranı | %60+ |
| Günlük aktif kullanıcı (DAU) | 50+ (1. ay sonu) |
| Ortalama oturum süresi | 3 dakika+ |

---

## 11. Riskler (MVP Kapsamında)

| Risk | Etki | Çözüm |
|---|---|---|
| Claude API limiti | Yüksek | İstek önbellekleme, hata mesajı |
| API anahtarı güvenliği | Yüksek | BuildConfig + .gitignore |
| Büyük video dosyaları | Orta | Max 500MB dosya sınırı |
| İnternet bağlantısı yok | Orta | Offline hata ekranı |
