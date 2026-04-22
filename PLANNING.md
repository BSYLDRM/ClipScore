# PLANNING.md — ClipScore

> Bu dosya LLM (yapay zeka) ajanları tarafından tüketilmek üzere tasarlanmıştır.
> Her görev atomik, bağımsız ve doğrulanabilir birimler halinde tanımlanmıştır.
> Görevler sırayla uygulanabilir. Her görevin ön koşulları ve kabul kriterleri belirtilmiştir.

---

## PROJE_TANIMI

```
ad: ClipScore
açıklama: Kısa video içerik üreticileri için AI destekli ön-yayın analiz uygulaması.
          Kullanıcı video başlığı ve açıklaması girer; Claude AI 0-100 arası viral skor,
          3 hook cümlesi ve SEO uyumlu açıklama üretir.
platform: Android (Kotlin + Jetpack Compose)
ai_saglayici: Anthropic Claude (claude-sonnet-4-20250514)
mimari: Android → Backend Proxy (Flask) → Claude API
hedef_kitle: YouTube Shorts, TikTok, Instagram Reels içerik üreticileri
dil: Türkçe öncelikli, İngilizce destekli
```

---

## DIZIN_YAPISI

```
clipscore/
├── PLANNING.md          ← Bu dosya
├── backend/             ← Python/Flask API proxy servisi
│   ├── main.py
│   ├── requirements.txt
│   └── .env             ← CLAUDE_API_KEY (git'e commit edilmez)
└── app/                 ← Android uygulaması (Kotlin + Jetpack Compose)
    ├── src/main/java/com/example/clipscore/
    │   ├── MainActivity.kt
    │   └── ui/theme/
    ├── build.gradle.kts
    └── src/main/AndroidManifest.xml
```

> Backend ve Android (frontend) iki bağımsız servis/modül olarak geliştirilir.
> Backend bilgisayarda çalışır, Android emülatör 10.0.2.2:5000 üzerinden bağlanır.

---

## TEKNOLOJI_YIGINI

### Backend
```
dil: Python 3.11+
cerceve: Flask
bagimliliklar: flask, flask-cors, requests, python-dotenv
port: 5000
```

### Frontend (Android)
```
dil: Kotlin
cerceve: Jetpack Compose + Material 3
mimari: MVVM + StateFlow
http_istemcisi: Retrofit + OkHttp
json: Gson
min_sdk: API 26 (Android 8.0)
baglanti: http://10.0.2.2:5000 (emülatörden host makineye)
```

---

## ORTAM_DEGISKENLERI

### backend/.env
```
CLAUDE_API_KEY=sk-ant-...   # Zorunlu — Anthropic API anahtarı
PORT=5000                    # Opsiyonel, varsayılan 5000
```

### Android (local.properties)
```
# Android Studio otomatik oluşturur, git'e commit edilmez
# Backend URL emülatör için sabit: http://10.0.2.2:5000
# Gerçek cihaz için bilgisayarın local IP'si kullanılır (örn. http://192.168.1.x:5000)
```

---

## GOREVLER

Her görev şu yapıdadır:
- ID: Benzersiz görev kimliği
- BASLIK: Kısa tanım
- ON_KOSUL: Tamamlanmış olması gereken görev ID'leri
- DOSYALAR: Etkilenen dosyalar
- ADIMLAR: Yapılacak işlemler (atomik)
- KABUL_KRITERI: Başarı koşulları

---

### GOREV_01 — Backend Klasör Yapısı Kurulumu

```yaml
id: GOREV_01
baslik: Backend dizin yapısını ve temel dosyaları oluştur
on_kosul: []
dosyalar:
  - backend/main.py
  - backend/requirements.txt
  - backend/.env.example
  - backend/.gitignore
adimlar:
  - backend/ dizini oluştur
  - requirements.txt dosyasını oluştur (flask, flask-cors, requests, python-dotenv)
  - .env.example dosyasını oluştur (CLAUDE_API_KEY=your_key_here, PORT=5000)
  - .gitignore dosyasını oluştur (.env satırını içermeli)
kabul_kriteri:
  - backend/ dizini mevcut
  - requirements.txt flask, flask-cors, requests, python-dotenv içeriyor
  - .env.example mevcut, .env yok
  - .gitignore .env'i kapsıyor
```

---

### GOREV_02 — Backend Ana Uygulama (main.py)

```yaml
id: GOREV_02
baslik: Flask uygulamasını kur, sağlık ve mesaj endpoint'lerini ekle
on_kosul: [GOREV_01]
dosyalar:
  - backend/main.py
adimlar:
  - Flask uygulaması başlat
  - flask-cors ile CORS etkinleştir (tüm originlere izin ver)
  - python-dotenv ile .env yükle
  - GET /api/health → {"status": "ok", "service": "clipscore-backend"} döndür
  - GET /api/message → {"message": "iyiki varsınız"} döndür (mevcut endpoint koru)
  - PORT env değişkeninden oku, varsayılan 5000
kabul_kriteri:
  - GET /api/health 200 döndürür
  - GET /api/message 200 ve {"message": "iyiki varsınız"} döndürür
  - Uygulama PORT=5000 ile başlar
  - CORS hataları alınmaz
```

---

### GOREV_03 — Backend Claude API Entegrasyonu

```yaml
id: GOREV_03
baslik: POST /api/analyze endpoint'ini oluştur — Claude API proxy
on_kosul: [GOREV_02]
dosyalar:
  - backend/main.py
adimlar:
  - POST /api/analyze endpoint'i ekle
  - Request body: {"title": string, "description": string, "language": "tr"|"en"}
  - Input validasyonu: title ve description zorunlu, boş olamaz
  - title max 200 karakter, description max 1000 karakter
  - Claude API'ye istek at (model: claude-sonnet-4-20250514, max_tokens: 1000)
  - Prompt: title ve description ile viral skor analizi iste
  - Claude'dan JSON formatında yanıt iste (vibeScore, hookScore, keywordScore, emotionScore, ctaScore, hooks[], description, hashtags[])
  - Claude yanıtını parse et ve döndür
  - Hata durumunda anlamlı HTTP hata kodu ve mesaj döndür (400, 500)
  - CLAUDE_API_KEY env'den oku; yoksa 500 döndür
prompt_sablon: |
  Sen bir sosyal medya içerik uzmanısın.
  Aşağıdaki kısa video içeriğini analiz et ve SADECE geçerli JSON döndür.
  Başka hiçbir şey yazma, sadece JSON.

  Başlık: {title}
  Açıklama: {description}
  Dil: {language}

  JSON formatı:
  {
    "vibeScore": <0-100 genel viral potansiyel>,
    "hookScore": <0-100 ilk 3 saniye dikkat çekicilik>,
    "keywordScore": <0-100 SEO anahtar kelime uyumu>,
    "emotionScore": <0-100 duygusal etki gücü>,
    "ctaScore": <0-100 harekete geçirme kalitesi>,
    "hooks": ["<hook 1>", "<hook 2>", "<hook 3>"],
    "description": "<SEO uyumlu 150+ kelime video açıklaması>",
    "hashtags": ["#tag1", "#tag2", "#tag3", "#tag4", "#tag5"]
  }
kabul_kriteri:
  - POST /api/analyze geçerli title+description ile 200 ve JSON skor döndürür
  - Yanıtta vibeScore, hookScore, keywordScore, emotionScore, ctaScore alanları var
  - hooks listesi 3 eleman içeriyor
  - title eksikse 400 döndürür
  - CLAUDE_API_KEY yoksa 500 ve açıklayıcı mesaj döndürür
  - Claude API hatasında 502 döndürür
```

---

### GOREV_04 — Android Proje Yapılandırması

```yaml
id: GOREV_04
baslik: Android bağımlılıklarını ve izinlerini yapılandır
on_kosul: []
dosyalar:
  - app/build.gradle.kts
  - app/src/main/AndroidManifest.xml
adimlar:
  - build.gradle.kts'e Retrofit, OkHttp, Gson bağımlılıklarını ekle
  - build.gradle.kts'e Coroutines bağımlılığını ekle
  - AndroidManifest.xml'e INTERNET iznini ekle
  - AndroidManifest.xml'e usesCleartextTraffic=true ekle (emülatör HTTP için)
kabul_kriteri:
  - Proje hatasız build alır
  - Emülatörde internet iznine sahip
  - HTTP bağlantısına izin verilmiş
```

---

### GOREV_05 — Android Backend Bağlantısı (Retrofit)

```yaml
id: GOREV_05
baslik: Retrofit ile backend API bağlantısını kur
on_kosul: [GOREV_04]
dosyalar:
  - app/src/main/java/com/example/clipscore/MainActivity.kt
adimlar:
  - MessageResponse data class tanımla (message: String)
  - BackendApi interface tanımla (GET api/message → MessageResponse)
  - Retrofit instance oluştur (baseUrl: http://10.0.2.2:5000/, GsonConverterFactory)
  - LaunchScreen composable içinde LaunchedEffect ile getMessage() çağır
  - Başarıda mesajı ekranda göster, hata durumunda "Bağlantı hatası" göster
kabul_kriteri:
  - Emülatörde uygulama açılınca backend'den "iyiki varsınız" gelir
  - Backend kapalıysa "Bağlantı hatası" gösterilir
  - Coroutine ile async çağrı yapılır (UI donmaz)
```

---

### GOREV_06 — Android Tema Güncellemesi

```yaml
id: GOREV_06
baslik: ClipScore renk temasını uygula
on_kosul: [GOREV_04]
dosyalar:
  - app/src/main/java/com/example/clipscore/ui/theme/Color.kt
  - app/src/main/java/com/example/clipscore/ui/theme/Theme.kt
adimlar:
  - Color.kt'ye ClipScore renklerini ekle
    (BrandBg=#0A0A0A, BrandPrimary=#7C3AED, BrandSuccess=#22C55E,
     BrandWarning=#F59E0B, BrandError=#EF4444, BrandText=#F8FAFC)
  - Theme.kt'de dinamik renk devre dışı bırak (dynamicColor=false)
  - DarkColorScheme'i ClipScore renkleriyle güncelle
kabul_kriteri:
  - Uygulama siyah arka plan ile açılır
  - Mor aksan rengi butonlarda görünür
  - Dinamik renk (Android 12 Material You) devre dışı
```

---

### GOREV_07 — Android Analyze Endpoint Entegrasyonu

```yaml
id: GOREV_07
baslik: POST /api/analyze endpoint'ini Android'e entegre et
on_kosul: [GOREV_05, GOREV_03]
dosyalar:
  - app/src/main/java/com/example/clipscore/MainActivity.kt
adimlar:
  - AnalyzeRequest data class ekle (title, description, language)
  - AnalyzeResponse data class ekle (vibeScore, hookScore, keywordScore, emotionScore, ctaScore, hooks, description, hashtags)
  - BackendApi interface'e POST api/analyze endpoint'i ekle
  - Basit bir input ekranı (TextField title + description) composable yaz
  - "Skoru Hesapla" butonu ile analyze çağrısı yap
  - Sonuçları ekranda göster (vibeScore + hooks listesi)
kabul_kriteri:
  - TextField'lara başlık ve açıklama girilebilir
  - Butona basınca backend'e POST atılır
  - vibeScore ve hooks sonuç ekranında görünür
  - Yükleme sırasında "Analiz ediliyor..." gösterilir
```

---

### GOREV_08 — Backend Rate Limiting

```yaml
id: GOREV_08
baslik: Backend'e basit rate limiting ekle
on_kosul: [GOREV_03]
dosyalar:
  - backend/main.py
adimlar:
  - IP başına dakikada max 10 istek sınırı ekle (in-memory dict ile)
  - Sınır aşılınca 429 Too Many Requests döndür
  - Response body: {"error": "Çok fazla istek. Lütfen bir dakika bekleyin."}
kabul_kriteri:
  - Aynı IP'den 11. istekte 429 döner
  - 1 dakika sonra limit sıfırlanır
  - Normal kullanımda etkilenmez
```

---

### GOREV_09 — Sağlık Kontrolü ve README

```yaml
id: GOREV_09
baslik: README.md dosyalarını oluştur
on_kosul: [GOREV_02, GOREV_04]
dosyalar:
  - README.md
  - backend/README.md
  - frontend/README.md
adimlar:
  - Ana README.md: proje açıklaması, mimari diyagramı, hızlı başlangıç
  - backend/README.md: kurulum, env değişkenleri, endpoint listesi, örnek istek
  - frontend/README.md: kurulum, env değişkenleri, npm komutları
kabul_kriteri:
  - Her README kurulum adımlarını içeriyor
  - Endpoint örnekleri var (curl veya fetch)
  - .env.example referansı var
```

---

## UYGULAMA_SIRASI

```
1. GOREV_01 → GOREV_02 → GOREV_03 → GOREV_08   (Backend akışı)
2. GOREV_04 → GOREV_05 → GOREV_06 → GOREV_07   (Android akışı)
3. GOREV_09                                      (Dokümantasyon)

Backend ve Android paralel geliştirilebilir.
GOREV_07 her iki taraf da hazır olunca yapılır.
```

---

## DOGRULAMA_ADIMLARI

Tüm görevler tamamlandıktan sonra şu manuel testler yapılır:

```bash
# 1. Backend sağlık kontrolü
curl http://localhost:5000/api/health
# Beklenen: {"status": "ok", "service": "clipscore-backend"}

# 2. Mesaj endpoint testi
curl http://localhost:5000/api/message
# Beklenen: {"message": "iyiki varsınız"}

# 3. Analiz endpoint testi
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"title": "Bir günde 10kg verdim!", "description": "Diyet sırlarımı paylaşıyorum", "language": "tr"}'
# Beklenen: vibeScore, hookScore vb. içeren JSON

# 4. Android emülatör testi
# Android Studio'da emülatörü başlat
# Uygulamayı çalıştır → backend'den "iyiki varsınız" gelmeli

# 5. Hata senaryosu
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"title": "", "description": ""}'
# Beklenen: 400 hatası
```

---

## SINIRLAMALAR_VE_NOTLAR

```
- Backend Python/Flask kullanır
- Frontend Android Kotlin + Jetpack Compose'dur 
- Emülatör backend'e 10.0.2.2:5000 üzerinden bağlanır 
- Gerçek fiziksel cihaz için bilgisayarın yerel IP'si kullanılmalıdır
- Claude API key .env'de tutulur, asla kaynak koda yazılmaz
- Rate limiting in-memory'dir; production'da Redis önerilir
- MVP sonrası geçmiş kaydetme Room DB ile Android'de yapılacak
```

---