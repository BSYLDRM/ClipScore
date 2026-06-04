# ⚡ ClipScore

> "Paylaşmadan önce skorunu öğren"

YouTube Shorts, TikTok ve Instagram Reels içerik üreticileri için AI destekli ön-yayın analiz uygulaması.

## Ne Yapar?

Video başlığı ve açıklamanı gir → AI viral potansiyelini 0-100 arası puanlar → 3 hook cümlesi + SEO açıklaması üretir.

## Ekranlar

Splash → Auth → Home → Video Önizleme → Başlık Girişi → AI Analiz → Sonuç Dashboard

## Klasör Yapısı

```
clipscore/
├── app/             # Kotlin + Jetpack Compose uygulaması (Android modülü)
├── backend/         # Python/Flask API proxy
├── prodocs/         # PRD, planning, design system dokümanları
├── README.md
└── .env.example
```

## Kurulum

### Backend
```bash
cd backend
pip install -r requirements.txt
cp .env.example .env
# .env dosyasına GEMINI_API_KEY ekle
python main.py
```

### Android
1. Android Studio'da proje kökünü aç (`app/` modülü)
2. `local.properties`'e backend URL ekle:
   ```
   BACKEND_URL=http://10.0.2.2:5000/   # emülatör için
   ```
   Production release build için:
   ```
   BACKEND_URL=https://clipscore-backend.onrender.com/
   ```
3. Run → Run 'app'

## Deploy

Backend Render.com'a deploy edilmiştir: `https://clipscore-backend.onrender.com`

## .env.example
```
GEMINI_API_KEY=your_gemini_api_key_here
PORT=5000
```
