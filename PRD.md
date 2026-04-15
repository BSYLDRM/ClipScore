# 📋 ClipScore — Ürün Gereksinimleri Dökümanı (PRD)

> **Versiyon:** 1.0  
> **Tarih:** Nisan 2026  
> **Hazırlayan:** ClipScore Ürün Ekibi  
> **Platform:** Android (Kotlin)

---

## 1. Yönetici Özeti

ClipScore, kısa video içerik üreticilerine yönelik geliştirilmiş, yapay zeka destekli bir ön-yayın analiz uygulamasıdır. Uygulama, kullanıcıların YouTube Shorts, TikTok ve Instagram Reels videolarını yayınlamadan önce viral potansiyellerini ölçmelerine, zayıf noktalarını görmelerine ve AI yardımıyla güçlü hook cümleleri ile açıklamalar üretmelerine olanak tanır.

Play Store'da mobil öncelikli, Shorts odaklı bir analiz aracı bulunmamaktadır. ClipScore bu boşluğu doldurmayı ve içerik üreticilerinin karar alma süreçlerini veriye dayalı hale getirmeyi hedeflemektedir.

---

## 2. Problem Tanımı

### 2.1 Temel Problem
İçerik üreticileri her gün video üretiyor ancak bu videoların tutup tutmayacağını önceden ölçmenin pratik bir yolu yok. Analiz araçlarının büyük çoğunluğu:
- Web tabanlı ve masaüstü ağırlıklı
- Yayın sonrası analitik odaklı (geç geri bildirim)
- Teknik kullanıcılara yönelik ve karmaşık arayüzlü

### 2.2 Sonuç
- Düşük performanslı videolar yayınlanıyor
- İçerik üreticisi zaman ve motivasyon kaybediyor
- Başlık/açıklama optimizasyonu sezgisel değil, deneme yanılmaya dayalı

### 2.3 Fırsat
Türkiye'de 2 milyondan fazla aktif kısa video içerik üreticisi var. Mobil öncelikli, Türkçe destekli ve AI destekli bir analiz aracına ciddi bir talep mevcut.

---

## 3. Hedefler ve Amaçlar

### 3.1 İş Hedefleri
- İlk 3 ayda Play Store'da 1.000+ indirme
- Aylık %5 premium dönüşüm oranı
- 6. ayda aylık yinelenen gelir (MRR): ₺5.000+

### 3.2 Kullanıcı Hedefleri
- Kullanıcı videoyu yayınlamadan önce somut bir skor alabilmeli
- Kullanıcı 1 dakika içinde 3 alternatif hook cümlesi görebilmeli
- Kullanıcı SEO uyumlu bir açıklama kopyalayabilmeli

### 3.3 Kapsam Dışı (Non-Goals)
- YouTube Analytics entegrasyonu (OAuth gerektirir, MVP sonrası)
- Web versiyonu (önce mobil)
- iOS versiyonu (MVP sonrası)
- Video düzenleme özellikleri

---

## 4. Hedef Kullanıcılar ve Personalar

### 👤 Persona 1 — "Hırslı Haluk"
| Alan | Detay |
|---|---|
| **Yaş** | 19 |
| **Meslek** | Üniversite öğrencisi |
| **Platform** | YouTube Shorts |
| **Takipçi** | 500 |
| **Hedef** | Viral bir video yapıp 10.000 aboneye ulaşmak |
| **Acı Noktası** | Videolarının neden tutmadığını anlamıyor, başlık yazmakta zorlanıyor |
| **Teknoloji** | Orta düzey, her gün telefon kullanıyor |
| **ClipScore'dan Beklentisi** | Basit bir skor + hızlı hook önerileri |

### 👤 Persona 2 — "Creator Cemre"
| Alan | Detay |
|---|---|
| **Yaş** | 26 |
| **Meslek** | Serbest içerik üreticisi |
| **Platform** | TikTok + Instagram Reels |
| **Takipçi** | 15.000 |
| **Hedef** | Markalardan sponsorluk almak, tutarlı büyüme |
| **Acı Noktası** | Her video için açıklama yazmak zaman alıyor, trend takibi yapamıyor |
| **Teknoloji** | İleri düzey, birden fazla tool kullanıyor |
| **ClipScore'dan Beklentisi** | Detaylı skor breakdown + SEO uyumlu açıklamalar |

---

## 5. Özellikler ve Gereksinimler

### 5.1 Video Yükleme & Metadata Analizi
**Öncelik:** P0 (Olmazsa Olmaz)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, videomun teknik detaylarını görmek istiyorum; böylece kalite sorunlarını yayın öncesinde fark edebileyim.

**Kabul Kriterleri:**
- [ ] Kullanıcı galeriden video seçebilmeli
- [ ] Uygulama 3 saniye içinde metadata çıkarabilmeli
- [ ] Süre, çözünürlük, dosya boyutu ve FPS gösterilmeli
- [ ] Desteklenmeyen format için anlamlı hata mesajı gösterilmeli
- [ ] Maksimum dosya boyutu 500MB ile sınırlandırılmalı

---

### 5.2 Trend Skoru Motoru
**Öncelik:** P0 (Olmazsa Olmaz)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, video başlığımın viral potansiyelini 0-100 arası bir skorla görmek istiyorum; böylece yayın öncesinde başlığımı optimize edebileyim.

**Kabul Kriterleri:**
- [ ] Kullanıcı başlık ve açıklama girebilmeli
- [ ] Claude API 10 saniye içinde yanıt vermeli
- [ ] Skor 4 alt kategoriyle birlikte gösterilmeli (Hook, Anahtar Kelime, Duygu, CTA)
- [ ] Her kategori için kısa açıklama metni bulunmalı
- [ ] API hatası durumunda kullanıcıya bilgi verilmeli

---

### 5.3 AI Hook Üretici
**Öncelik:** P0 (Olmazsa Olmaz)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, videom için 3 farklı hook cümlesi görmek istiyorum; böylece en dikkat çekici başlangıcı seçebileyim.

**Kabul Kriterleri:**
- [ ] 3 farklı ve birbirinden ayırt edilebilir hook üretilmeli
- [ ] Her hook için kopyala butonu olmalı
- [ ] Hook'lar Türkçe üretilebilmeli
- [ ] Yeniden üret butonu ile yeni hook'lar alınabilmeli

---

### 5.4 AI Açıklama Üretici
**Öncelik:** P0 (Olmazsa Olmaz)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, videoma uygun SEO açıklaması ve hashtagler almak istiyorum; böylece her video için sıfırdan yazmak zorunda kalmayayım.

**Kabul Kriterleri:**
- [ ] En az 100 kelimelik açıklama üretilmeli
- [ ] İlgili hashtagler açıklamaya dahil edilmeli
- [ ] Tek tıkla kopyalanabilmeli
- [ ] Açıklama Türkçe veya İngilizce seçeneğiyle üretilebilmeli

---

### 5.5 Sonuç Dashboard'u
**Öncelik:** P0 (Olmazsa Olmaz)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, analiz sonuçlarını tek ekranda görmek istiyorum; böylece hızlıca karar verebileyim.

**Kabul Kriterleri:**
- [ ] Genel skor animasyonlu gauge ile gösterilmeli
- [ ] Renk kodu: 0-40 kırmızı, 41-70 sarı, 71-100 yeşil
- [ ] Tüm sonuçlar (skor + hook + açıklama) tek ekranda erişilebilir olmalı
- [ ] Paylaş butonu ile sonuç ekran görüntüsü paylaşılabilmeli

---

### 5.6 Geçmiş / Kaydedilen Skorlar
**Öncelik:** P1 (Olması Gerekir)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, geçmiş analizlerimi görmek istiyorum; böylece ilerlemeimi takip edebileyim.

**Kabul Kriterleri:**
- [ ] Son 10 analiz yerel olarak kaydedilmeli
- [ ] Geçmiş listesi tarih ve başlıkla gösterilmeli
- [ ] Geçmiş analize tıklanınca detay açılmalı

---

### 5.7 Sonuçları Paylaş
**Öncelik:** P1 (Olması Gerekir)

**Kullanıcı Hikayesi:**
> Bir içerik üreticisi olarak, skor kartımı sosyal medyada paylaşmak istiyorum; böylece topluluğumla etkileşime geçebileyim.

**Kabul Kriterleri:**
- [ ] Skor kartı görsel olarak dışa aktarılabilmeli
- [ ] Android Share Sheet ile paylaşım yapılabilmeli
- [ ] ClipScore markası görsel üzerinde görünmeli (organik büyüme)

---

### 5.8 Thumbnail Skorer
**Öncelik:** P2 (Güzel Olur)

Thumbnail görselinin tıklanabilirlik potansiyelini renk, yüz ifadesi ve metin oranı analizi ile puanlar.

---

### 5.9 Viral An Dedektörü
**Öncelik:** P2 (Güzel Olur)

Videoda en çok paylaşılabilecek 3-5 saniyelik kısımları otomatik işaretler.

---

## 6. Teknik Mimari

### 6.1 Sistem Mimarisi

```
┌─────────────────────────────────────┐
│         Android Uygulaması          │
│  (Kotlin + Jetpack Compose)         │
│                                     │
│  ┌──────────┐    ┌───────────────┐  │
│  │  UI      │    │  ViewModel    │  │
│  │  Layer   │◄──►│  StateFlow    │  │
│  └──────────┘    └───────┬───────┘  │
│                          │          │
│                  ┌───────▼───────┐  │
│                  │  Repository   │  │
│                  └───────┬───────┘  │
│                          │          │
│          ┌───────────────┼───────┐  │
│          │               │       │  │
│   ┌──────▼──┐    ┌───────▼────┐  │  │
│   │  Local  │    │  Remote    │  │  │
│   │  (Prefs)│    │  (Retrofit)│  │  │
│   └─────────┘    └─────┬──────┘  │  │
└─────────────────────────┼─────────┘  │
                          │
              ┌───────────▼───────────┐
              │   Anthropic Claude    │
              │       API             │
              │  claude-sonnet-4      │
              └───────────────────────┘
```

### 6.2 API Entegrasyonu

| Alan | Detay |
|---|---|
| **Sağlayıcı** | Anthropic |
| **Model** | claude-sonnet-4-20250514 |
| **Endpoint** | https://api.anthropic.com/v1/messages |
| **Auth** | API Key (Header: x-api-key) |
| **Max Token** | 1000 |
| **Timeout** | 30 saniye |

### 6.3 Güvenlik

- API anahtarı `BuildConfig` içinde tutulur
- `.gitignore` ile `local.properties` korunur
- API anahtarı kaynak koduna doğrudan yazılmaz
- Üretim sürümünde backend proxy değerlendirilebilir

---

## 7. UI/UX Gereksinimleri

### 7.1 Ekran Listesi

| Ekran | Açıklama |
|---|---|
| Splash | Logo + kısa animasyon |
| Ana Ekran | Video seç CTA, son analiz kartı |
| Video Önizleme | Thumbnail + metadata kartları |
| Başlık Giriş | TextField'lar + analiz et butonu |
| Yükleniyor | Animasyonlu AI analiz ekranı |
| Sonuç Dashboard | Skor gauge + breakdown + hook listesi |
| Geçmiş | Önceki analizler listesi |
| Ayarlar | Dil seçimi, API bilgileri, hakkında |

### 7.2 Tasarım Prensipleri
- **Minimal & Hızlı** — Kullanıcı 3 adımda sonuca ulaşmalı
- **Enerjik** — Animasyonlar ve renk kullanımı aktif hissettirmeli
- **Güven veren** — Skor ve analizler net ve okunabilir olmalı

### 7.3 Renk Paleti

| Renk | Hex | Kullanım |
|---|---|---|
| Arka Plan | `#0A0A0A` | Ana arka plan |
| Birincil | `#7C3AED` | Butonlar, vurgular |
| Başarı | `#22C55E` | Yüksek skor |
| Uyarı | `#F59E0B` | Orta skor |
| Hata | `#EF4444` | Düşük skor |
| Metin | `#F8FAFC` | Ana metin |

### 7.4 Tipografi
- **Başlıklar:** Montserrat Bold
- **Gövde:** Nunito Regular
- **Skor Sayıları:** Montserrat ExtraBold

---

## 8. Monetizasyon Stratejisi

### 8.1 Ücretsiz Plan
- Günde 3 analiz hakkı
- Hook üretici (sınırlı)
- Temel skor breakdown

### 8.2 ClipScore Pro — ₺89/ay
- Sınırsız analiz
- Gelişmiş skor breakdown
- Açıklama üretici
- Geçmiş analiz arşivi
- Öncelikli destek

### 8.3 Ömür Boyu Lisans — ₺299 (tek seferlik)
- Pro'nun tüm özellikleri
- Gelecekteki güncellemeler dahil

### 8.4 Gelir Projeksiyonu (6. ay)

| Segment | Kullanıcı | Dönüşüm | Gelir |
|---|---|---|---|
| Ücretsiz | 2.000 | - | ₺0 |
| Pro (aylık) | 100 | %5 | ₺8.900 |
| Ömür Boyu | 20 | %1 | ₺5.980 |
| **Toplam** | | | **₺14.880/ay** |

---

## 9. Riskler ve Azaltma Stratejileri

| Risk | Olasılık | Etki | Azaltma Stratejisi |
|---|---|---|---|
| Claude API maliyet artışı | Orta | Yüksek | İstek önbellekleme, kullanım limiti |
| API anahtarı sızıntısı | Düşük | Yüksek | BuildConfig + backend proxy |
| Play Store reddi | Düşük | Yüksek | Politika uyumluluğu önceden kontrol |
| Düşük kullanıcı dönüşümü | Orta | Orta | Ücretsiz katman genişletme, onboarding iyileştirme |
| Rakip çıkması | Orta | Orta | Hızlı iterasyon, kullanıcı geri bildirimi |

---

## 10. Lansman Planı

### 10.1 Beta Süreci (Yayın öncesi 2 hafta)
- 20 içerik üreticisiyle kapalı beta
- Google Play İç Test Kanalı
- Geri bildirim formu ile haftalık iterasyon

### 10.2 Play Store ASO Optimizasyonu
- **Başlık:** ClipScore: AI Shorts Analiz
- **Kısa Açıklama:** Videounu yayınlamadan önce viral skorunu öğren
- **Anahtar Kelimeler:** shorts analiz, viral video, youtube skor, içerik üretici araç
- 8 ekran görüntüsü + 1 tanıtım videosu

### 10.3 Lansman Kanalları
- Reddit: r/NewTubers, r/TikTokCreators
- Twitter/X: İçerik üretici toplulukları
- YouTube: Shorts üreticilerine hedefli tanıtım
- Türkiye: İçerik üretici Discord sunucuları

---

## 11. Başarı Metrikleri ve KPI'lar

### 11.1 İlk 30 Gün
| KPI | Hedef |
|---|---|
| Toplam indirme | 300 |
| Günlük aktif kullanıcı (DAU) | 30 |
| Analiz tamamlama oranı | %55 |
| Ortalama oturum süresi | 2.5 dakika |
| Play Store puanı | 4.0+ |

### 11.2 İlk 60 Gün
| KPI | Hedef |
|---|---|
| Toplam indirme | 700 |
| Premium dönüşüm | %3 |
| Günlük aktif kullanıcı | 80 |
| Kullanıcı başına analiz | 5+ |

### 11.3 İlk 90 Gün
| KPI | Hedef |
|---|---|
| Toplam indirme | 1.500 |
| Aylık yinelenen gelir (MRR) | ₺5.000 |
| Günlük aktif kullanıcı | 150 |
| Organik büyüme oranı | %20/ay |

---

## 12. Sonraki Adımlar (Post-MVP Yol Haritası)

| Sürüm | Özellik | Tahmini Tarih |
|---|---|---|
| v1.1 | Geçmiş analiz arşivi | +6 hafta |
| v1.2 | Thumbnail skorer | +10 hafta |
| v1.3 | On-device vision (MediaPipe) | +16 hafta |
| v2.0 | iOS versiyonu | +6 ay |
| v2.1 | Sesli önizleme (ElevenLabs) | +7 ay |

---

*Bu döküman ClipScore v1.0 MVP kapsamını tanımlamaktadır. Ürün geliştikçe güncellenecektir.*
