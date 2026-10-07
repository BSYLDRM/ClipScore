# ClipScore: Değerlendirme ve Google Play Yol Haritası

_Hazırlanma: 7 Ekim 2026_

## 1. Proje ne yapıyor?

Kısa video üreticileri (TikTok, Reels, Shorts, YouTube, X) için yayın öncesi analiz uygulaması.
Kullanıcı videoyu seçer, başlık/açıklama yazar, platformu seçer; uygulama videonun ilk karesini
base64 olarak backend'e yollar. Backend Gemini 2.5 Flash ile önce kareyi tarif ettiriyor, sonra
0-100 arası skorlar (hook, anahtar kelime, duygu, CTA, içerik uyumu, genel "vibe"), 3 hook cümlesi,
SEO açıklaması ve hashtag üretiyor. Son 10 analiz cihazda (Room) saklanıyor.

**Mimari**
- Android: Kotlin, Jetpack Compose, Hilt, Retrofit/Gson, Room, Firebase Auth (e-posta + Google). Yaklaşık 4.400 satır, 10 ekran.
- Backend: tek dosyalık Flask (`backend/main.py`), Render.com'da gunicorn ile çalışıyor, Gemini API anahtarı sadece sunucuda.
- Test: yalnızca şablon `ExampleUnitTest` / `ExampleInstrumentedTest` var. Backend testi yok. CI yok.

**Build durumu:** Bu makinede Android SDK kurulu olmadığı için (local.properties başka bir kullanıcı
yolunu gösteriyor) derleme yapılamadı. İki backend adresi de (`clipscore-dmmb`, `clipscore-ph79`)
`/api/health` için 200 dönüyor.

## 2. Fikir hakkında dürüst değerlendirme

**Güçlü yanlar**
- Hedef kitle net ve gerçek bir ihtiyaç var: üreticiler yayın öncesi başlık/hook/hashtag konusunda yardım arıyor.
- Hook + açıklama + hashtag üretimi somut, hemen kullanılabilir çıktı. Asıl değer burada.
- MVP akışı uçtan uca çalışıyor; UI düzenli, mimari (Hilt, ViewModel, Room) doğru tercihler.

**Zayıf yanlar / riskler**
- "Viral skor" iddiası doğrulanamaz. Skorlar gerçek performans verisine değil, LLM tahminine dayanıyor; aynı girdiye farklı skor verebilir. Kullanıcı bunu fark edince güven kaybı olur. Mağaza metninde "tahmin/öneri" dili kullanılmalı.
- Videonun yalnızca ilk karesi analiz ediliyor. "Video içeriğini analiz eder" demek abartılı; hook çoğunlukla ilk 1-3 saniyede ses ve hareketle kurulur.
- Rekabet yoğun: CapCut, TikTok'un kendi araçları, VidIQ/TubeBuddy ve ücretsiz ChatGPT/Gemini aynı işi kısmen yapıyor. Fark yaratacak şey: platforma özel, hızlı, Türkçe odaklı ve tek dokunuşla kopyalanabilir çıktı.
- Maliyet: her analiz 2 Gemini çağrısı. Backend kimlik doğrulaması olmadığı için herkes API'yi kullanıp kotanı tüketebilir.

**Öneri:** "Viral skor" yerine "yayın öncesi asistan" konumlandırması daha savunulabilir. Skorları
ikincil göster, hook/açıklama/hashtag üretimini ana değer yap. İleride birkaç kare + ses transkripti
analizi gerçek fark yaratır.

## 3. Bu dalda yapılan düzeltmeler (`claude/project-thread-i45lzc`)

- `RetrofitClient` artık sabit adres yerine `BuildConfig.BACKEND_URL` kullanıyor (varsayılan, şu an kullanılan `clipscore-dmmb`).
- `usesCleartextTraffic` sadece debug manifest'inde; release sürümü yalnızca HTTPS.
- Backend 500 hatalarında ham exception metnini istemciye döndürmüyor.

Not: SDK olmadığı için bu değişiklikler burada derlenmedi; Android Studio'da bir kez build alınmalı.

## 4. Sağlamlaştırma (yayından önce yapılmalı)

### Güvenlik ve backend
1. **Backend'e kimlik doğrulama:** Uygulama Firebase ID token'ını `Authorization: Bearer` ile göndersin, backend `firebase-admin` ile doğrulasın. Rate limit'i IP yerine kullanıcı (uid) bazında yap. En kritik madde budur.
2. `google-generativeai` paketi kullanımdan kaldırıldı; `google-genai` SDK'sına geç. Gemini'den JSON için `response_mime_type="application/json"` ve şema kullan, fence temizlemeye gerek kalmaz.
3. Görsel ve metin analizini tek Gemini çağrısında birleştir (maliyet ve süre yarıya iner).
4. Gemini çıktısını doğrula (skorlar 0-100 int, 3 hook, 10-20 hashtag); eksikse varsayılanla doldur.
5. İstek boyutu sınırı (`MAX_CONTENT_LENGTH`) koy; CORS `*` gereksiz, kaldır.
6. `keep_alive` thread'i gunicorn altında hiç çalışmıyor (sadece `__main__`'de). Ücretsiz Render uykusu için ya ücretli plan ya da harici cron/UptimeRobot kullan. Kökte ve `backend/` altında iki farklı `render.yaml` var; birini sil.
7. `requirements.txt` içinde sürümleri sabitle.

### Android
1. **applicationId `com.example.clipscore` Play'de kabul edilmez.** Kalıcı bir ad seç (ör. `com.buseyildirim.clipscore`). Bu yayından sonra değiştirilemez; Firebase'de yeni Android uygulaması ekleyip yeni `google-services.json` ve SHA-1/SHA-256 parmak izlerini (debug + upload + Play App Signing) girmen gerekir, yoksa Google ile giriş çalışmaz.
2. Room için `fallbackToDestructiveMigration` yerine gerçek migration (veya en azından yayından sonra şemayı dondur).
3. Hata mesajları `e.message` içeriğine string araması yapıyor; `HttpException`/`IOException` türüne göre ayır, backend'in `error` alanını göster.
4. `AnalyzeViewModel` içindeki `FirebaseAuth.getInstance()` ve kullanılmayan `buildEnrichedDescription`'ı temizle; Firebase'i Hilt ile enjekte et.
5. Firebase BoM 32.7.0 ve `play-services-auth` eski; güncelle, Google girişini Credential Manager'a taşı (eski GoogleSignIn API'si kullanımdan kalkıyor).
6. Room `2.7.0-alpha11` yerine kararlı sürüm kullan.

### Testler
- Backend: `pytest` + Flask test client. Gemini'yi mock'la; 400 (eksik alan), 429 (rate limit), 500 (bozuk JSON) ve başarılı yanıt senaryoları.
- Android birim testleri: `AnalyzeViewModel` (başarı/hata state'leri, geçmişe kayıt), `VideoFormatUtils`, `AnalysisDao` (Room in-memory, son 10 kaydı tutma).
- Basit bir GitHub Actions: `./gradlew testDebugUnitTest lintDebug` + `pytest`.

### Kullanılabilirlik
- Yükleme ekranında Render uyanırken 30-60 sn bekleme olabilir; kullanıcıya "sunucu uyanıyor" bilgisi ve iptal butonu.
- Hook, açıklama ve hashtag'ler için tek tıkla kopyala/paylaş.
- Analiz geçmişinde silme.
- Karanlık mod ve küçük ekran kontrolü, TalkBack etiketleri.

## 5. Google Play yayın yol haritası

### Adım 1: Hesap ve hazırlık
- Google Play Console geliştirici hesabı (tek seferlik 25 USD). Kişisel hesaplar için kimlik doğrulama gerekir.
- **Yeni kişisel hesaplar için zorunlu kapalı test:** en az 12 test kullanıcısıyla 14 gün kesintisiz kapalı test yapmadan üretime çıkamazsın. Bunu erkenden planla.

### Adım 2: İmzalama ve release build
- Upload keystore oluştur (Android Studio > Build > Generate Signed Bundle). Keystore ve şifreleri repoya koyma (`*.keystore` zaten gitignore'da); şifreleri `keystore.properties` veya ortam değişkeninden oku.
- Play App Signing'i aç (varsayılan). Play'in verdiği uygulama imzalama SHA-1/SHA-256'yı Firebase'e ekle.
- `release` için `isMinifyEnabled = true` ve `isShrinkResources = true`; Gson model sınıfları için ProGuard `-keep` kuralları ekle ve release sürümünü cihazda mutlaka test et.
- `versionCode`'u her yüklemede artır. Çıktı AAB olmalı: `./gradlew bundleRelease`.
- targetSdk 36 uygun (Play'in güncel API seviyesi şartını karşılıyor).

### Adım 3: Gizlilik ve politika
- **Gizlilik politikası URL'si** (zorunlu): toplanan veriler (e-posta, ad, video karesi, başlık/açıklama), Gemini'ye (Google) gönderildiği, saklama süresi, silme yolu. GitHub Pages veya Firebase Hosting'de yayınlayabilirsin. Uygulama içinden de erişilebilir olmalı.
- **Hesap silme** (zorunlu, çünkü uygulamada hesap oluşturuluyor): uygulama içinde "Hesabımı sil" ve ayrıca web üzerinden silme talebi bağlantısı. Şu an kodda yok.
- **Data Safety formu:** kişisel bilgi (e-posta, ad), fotoğraf/video (kare), uygulama içeriği (başlık/açıklama); aktarımda şifreli; üçüncü tarafla paylaşım: Google Gemini (işleme amaçlı).
- **AI içerik politikası:** üretken AI uygulamalarında kullanıcının uygunsuz çıktıyı bildirebileceği bir "bildir" butonu beklenir.
- İçerik derecelendirme anketi, hedef kitle (13+ önerilir; çocuk hedefleme yok), reklam beyanı (yok).
- `READ_MEDIA_VIDEO` izni: Play, yalnızca dosya seçmek için geniş medya izinlerini kısıtlıyor. `VideoPickerScreen` zaten Photo Picker (`PickVisualMedia`) kullanıyor, bu yüzden `READ_MEDIA_VIDEO` ve `READ_EXTERNAL_STORAGE` izinleri ve izin isteme kodu kaldırılabilir; kalırsa izin beyan formu gerekir.

### Adım 4: Mağaza listesi
- Uygulama adı (30 karakter), kısa açıklama (80), tam açıklama (4000). "Viral olacağını garanti eder" gibi iddialardan kaçın.
- 512x512 ikon, 1024x500 öne çıkan görsel, en az 2 telefon ekran görüntüsü (README'deki ekranlar iyi başlangıç).
- İletişim e-postası, kategori (Video Oynatıcılar ve Editörler ya da Araçlar).
- Google ile giriş/kayıt gerektiği için inceleme ekibine **test hesabı** bilgisi ver.

### Adım 5: Test kanalları ve yayın
1. Dahili test (anında, 100 kişiye kadar) ile release AAB'yi doğrula.
2. Kapalı test: 12+ kişi, 14 gün. Geri bildirim topla, Firebase Crashlytics ekle.
3. Üretim başvurusu, ardından kademeli yayın (%10 → %50 → %100).

## 6. Önerilen sıra

1. applicationId + Firebase yeniden yapılandırma
2. Backend token doğrulaması ve kullanıcı bazlı limit
3. Gizlilik politikası + hesap silme + bildir butonu
4. Temel testler ve CI
5. İmzalı release AAB, dahili test
6. Kapalı test (14 gün) sırasında kullanılabilirlik iyileştirmeleri
7. Mağaza listesi ve üretim yayını
