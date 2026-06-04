# Progress Log — ClipScore

## Nisan 2026 — MVP Başlangıç

### Hafta 1
- [x] Android proje kurulumu (Kotlin + Compose)
- [x] Renk teması ve tipografi (Color.kt, Theme.kt, Type.kt)
- [x] Tüm ekranlar UI olarak implement edildi (Splash, Auth, Home, Preview, Input, Loading, Result)
- [x] NavGraph kuruldu (Splash→Auth→Home→Preview→Input→Loading→Result)
- [x] ClipScoreButton, MetadataCard, ScoreCard component'leri

### Hafta 2
- [x] Flask backend kurulumu
- [x] GET /api/health, GET /api/message endpoint'leri
- [x] POST /api/analyze — Gemini 2.5 Flash entegrasyonu
- [x] Rate limiting (IP başına dakikada 10 istek)
- [x] JSON parse ve hata yönetimi

### Hafta 3 (Bu sprint)
- [x] ViewModel + StateFlow entegrasyonu
- [x] TitleInputScreen → ViewModel bağlantısı
- [x] ResultScreen tam implementasyonu
- [x] Hilt DI kurulumu
- [x] Backend deploy (Render.com)
- [x] Zorunlu GitHub dokümanları
- [ ] Backend Render.com'a deploy edildi (render.yaml + Procfile hazır; GitHub push gerekli)
- [ ] /api/health endpoint'i production'da 200 döndürüyor

## Alınan Kararlar
- Gemini 2.5 Flash seçildi: Claude API key yönetimi backend'de tutuldu, Android'e açılmadı
- Rate limiting in-memory: MVP için yeterli, production'da Redis önerilir
- NavGraph tek dosyada: ekran sayısı az olduğu için ayrı modül gereksiz
- SharedPreferences ile son sonuç saklandı: Room DB MVP sonrası

## Karşılaşılan Sorunlar
- Emülatörde HTTP bağlantısı: usesCleartextTraffic=true AndroidManifest'e eklendi
- Gemini JSON parse: markdown fence'leri (_strip_markdown_fences) temizleme fonksiyonu yazıldı
