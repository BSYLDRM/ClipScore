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
└── frontend/            ← React/Vite web arayüzü (test & preview)
    ├── src/
    ├── package.json
    └── .env             ← VITE_BACKEND_URL (git'e commit edilmez)
```

> NOT: Android kaynak kodu ayrı dizinde tutulur (bu repoda yok).
> Backend ve Frontend bu repodaki iki bağımsız servistir.

---

## TEKNOLOJI_YIGINI

### Backend
```
dil: Python 3.11+
cerceve: Flask
bagimliliklar: flask, flask-cors, requests, python-dotenv
port: 5000
```

### Frontend
```
dil: JavaScript/TypeScript
cerceve: React 18 + Vite
ui_kutuphanesi: Tailwind CSS
port: 5173
```

---

## ORTAM_DEGISKENLERI

### backend/.env
```
CLAUDE_API_KEY=sk-ant-...   # Zorunlu — Anthropic API anahtarı
PORT=5000                    # Opsiyonel, varsayılan 5000
```

### frontend/.env
```
VITE_BACKEND_URL=http://localhost:5000   # Backend adresi
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

### GOREV_04 — Frontend Proje Kurulumu

```yaml
id: GOREV_04
baslik: Vite + React + Tailwind frontend projesini oluştur
on_kosul: []
dosyalar:
  - frontend/package.json
  - frontend/vite.config.js
  - frontend/tailwind.config.js
  - frontend/index.html
  - frontend/src/main.jsx
  - frontend/src/App.jsx
  - frontend/.env.example
  - frontend/.gitignore
adimlar:
  - frontend/ dizini oluştur
  - package.json oluştur (react, react-dom, vite, tailwindcss, autoprefixer, postcss bağımlılıkları)
  - vite.config.js oluştur (port 5173, proxy /api → localhost:5000)
  - tailwind.config.js oluştur (content: src/**/*.{jsx,js})
  - src/index.css oluştur (Tailwind direktifleri: base, components, utilities)
  - index.html oluştur (root div, main.jsx bağlantısı)
  - src/main.jsx oluştur (React render)
  - src/App.jsx oluştur (temel iskelet — "ClipScore" başlığı, siyah arka plan)
  - .env.example oluştur (VITE_BACKEND_URL=http://localhost:5000)
  - .gitignore oluştur (node_modules, .env)
kabul_kriteri:
  - npm install hatasız çalışır
  - npm run dev port 5173'te çalışır
  - Tarayıcıda "ClipScore" metni görünür
  - Tailwind sınıfları çalışır (arka plan rengi uygulanır)
```

---

### GOREV_05 — Frontend Bileşen: VideoAnalyzerForm

```yaml
id: GOREV_05
baslik: Video analiz formu bileşenini oluştur
on_kosul: [GOREV_04]
dosyalar:
  - frontend/src/components/VideoAnalyzerForm.jsx
adimlar:
  - src/components/ dizini oluştur
  - VideoAnalyzerForm.jsx bileşeni oluştur
  - title input alanı ekle (max 200 karakter, zorunlu)
  - description textarea ekle (max 1000 karakter, opsiyonel)
  - language seçimi ekle (tr/en, varsayılan tr)
  - "Skoru Hesapla" submit butonu ekle
  - Form state'ini useState ile yönet
  - onSubmit prop'u ile üst bileşene veri ilet
  - Boş title ile gönderide hata mesajı göster
  - Yükleme durumunda buton disabled ve "Analiz ediliyor..." göster
  - Tailwind ile stil ver: koyu arka plan (#0A0A0A), mor aksan (#7C3AED)
kabul_kriteri:
  - Form render olur
  - Boş title ile submit yapılırsa hata gösterilir
  - Dolu form submit edildiğinde onSubmit çağrılır
  - Yükleme sırasında buton tıklanamaz
```

---

### GOREV_06 — Frontend Bileşen: ScoreDisplay

```yaml
id: GOREV_06
baslik: Analiz sonuçlarını gösteren skor ekranı bileşeni
on_kosul: [GOREV_04]
dosyalar:
  - frontend/src/components/ScoreDisplay.jsx
adimlar:
  - ScoreDisplay.jsx bileşeni oluştur
  - prop: analysisResult (vibeScore, hookScore, keywordScore, emotionScore, ctaScore, hooks, description, hashtags)
  - Ana vibeScore'u büyük ve belirgin göster
  - Renk kodu uygula: 0-40 kırmızı, 41-70 sarı, 71-100 yeşil
  - 4 alt kategori skorunu kart olarak listele (Hook, Anahtar Kelime, Duygu, CTA)
  - hooks listesini göster; her hook için "Kopyala" butonu ekle
  - Kopyala butonuna tıklanınca clipboard'a kopyala, "Kopyalandı!" göster
  - description alanını göster, kopyala butonu ekle
  - hashtags'ı göster (mor renk, tıklanabilir görünüm)
kabul_kriteri:
  - vibeScore'a göre renk değişir
  - 4 kategori skoru görünür
  - Hook kopyala butonu çalışır (clipboard'a yazar)
  - Description kopyala butonu çalışır
  - Hashtag'ler görünür
```

---

### GOREV_07 — Frontend API Entegrasyonu

```yaml
id: GOREV_07
baslik: App.jsx'te backend'e API çağrısını entegre et
on_kosul: [GOREV_05, GOREV_06]
dosyalar:
  - frontend/src/App.jsx
  - frontend/src/api/analyzeApi.js
adimlar:
  - src/api/analyzeApi.js oluştur
  - analyzeVideo(title, description, language) async fonksiyonu yaz
  - VITE_BACKEND_URL env değişkenini kullan (/api/analyze endpoint)
  - App.jsx'i güncelle: VideoAnalyzerForm + ScoreDisplay entegre et
  - useState ile state yönet: {loading, error, result}
  - Form submit'te analyzeVideo çağır
  - Yükleme sırasında spinner/mesaj göster
  - Hata durumunda hata mesajı göster
  - Başarıda ScoreDisplay render et
kabul_kriteri:
  - Form doldur + submit → API çağrısı yapılır
  - Yükleme sırasında loading göstergesi var
  - Başarılı yanıtta ScoreDisplay render olur
  - Hata durumunda Türkçe hata mesajı gösterilir
  - Network hatası (backend çalışmıyor) yakalanır
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
2. GOREV_04 → GOREV_05 → GOREV_06 → GOREV_07   (Frontend akışı)
3. GOREV_09                                      (Dokümantasyon)

Backend ve Frontend paralel geliştirilebilir (GOREV_01-03 ve GOREV_04-06).
GOREV_07 her iki taraf da hazır olunca yapılır.
```

---

## DOGRULAMA_ADIMLARI

Tüm görevler tamamlandıktan sonra şu manuel testler yapılır:

```bash
# 1. Backend sağlık kontrolü
curl http://localhost:5000/api/health
# Beklenen: {"status": "ok", "service": "clipscore-backend"}

# 2. Analiz endpoint testi
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"title": "Bir günde 10kg verdim!", "description": "Diyet sırlarımı paylaşıyorum", "language": "tr"}'
# Beklenen: vibeScore, hookScore vb. içeren JSON

# 3. Frontend erişim
# Tarayıcıda http://localhost:5173 aç
# Form görünmeli, doldur, submit et, skor gelmeli

# 4. Hata senaryoları
curl -X POST http://localhost:5000/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"title": "", "description": ""}'
# Beklenen: 400 hatası
```

---

## SINIRLAMALAR_VE_NOTLAR

```
- Bu PLANNING.md Android Kotlin kodunu kapsamaz (ayrı scope)
- Backend Python/Flask kullanır (PLANNING.md'deki Node.js planı güncellendi)
- Frontend web arayüzü test/preview amaçlıdır, production Android app ayrıdır
- Claude API key .env'de tutulur, asla kaynak koda yazılmaz
- Rate limiting in-memory'dir; production'da Redis önerilir
- MVP sonrası geçmiş kaydetme Room DB ile Android'de yapılacak
```

---
