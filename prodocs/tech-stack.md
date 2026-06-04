# Tech Stack — ClipScore

## Frontend (Android)
- Kotlin + Jetpack Compose + Material 3
- MVVM + StateFlow mimari paterni
- Hilt dependency injection
- Retrofit + OkHttp + Gson (HTTP istemcisi)
- Navigation Compose

## Backend
- Python 3.11 + Flask
- Gemini 2.5 Flash API (google/gemini-2.5-flash)
- flask-cors, gunicorn
- Deploy: Render.com

## AI Entegrasyonu
- Gemini 2.5 Flash modeli kullanıldı (hız/maliyet dengesi)
- Backend proxy pattern: Android → Flask → Gemini API
- API key asla istemcide tutulmaz, sadece backend .env'de

## Geliştirme Sürecinde AI Kullanımı
- UI component'leri (ScoreCard, MetadataCard, ClipScoreButton) Claude ile tasarlandı
- Prompt mühendisliği iterasyonları Claude ile yapıldı
- NavGraph ve ViewModel boilerplate Cursor + Claude ile oluşturuldu
