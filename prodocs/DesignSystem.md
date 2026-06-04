# Design System — ClipScore

## Renk Paleti
| Token | Hex | Kullanım |
|---|---|---|
| BrandBg | #0A0A0A | Ana arka plan |
| BrandPrimary | #7C3AED | Butonlar, vurgular, ikonlar |
| BrandSuccess | #22C55E | Yüksek skor (71-100) |
| BrandWarning | #F59E0B | Orta skor (41-70), badge |
| BrandError | #EF4444 | Düşük skor (0-40) |
| BrandText | #F8FAFC | Ana metin |
| BrandSurface | #1A1A1A | Card arka planları |
| BrandBorder | #2A2A2A | Card borderleri |

## Tipografi
| Kullanım | Font | Weight |
|---|---|---|
| Başlıklar, logo | Montserrat | Bold / ExtraBold |
| Body, label | Nunito | Regular / SemiBold |
| Skor sayıları | Montserrat | ExtraBold |

## Component Kuralları
- Butonlar: ClipScoreButton (tam genişlik, 52dp yükseklik, BrandPrimary)
- Kartlar: BrandSurface arka plan + 1dp BrandBorder
- Shape: MaterialTheme.shapes.large (12dp radius)
- Spacing: 8/12/16/20/24dp grid
- Skor renklendirme: <41 Error, <71 Warning, >=71 Success

## Skor Gösterim Kuralları
- VibeScore: büyük animasyonlu sayaç, renk kodlu
- Alt skorlar: LinearProgressIndicator + sayı, ScoreCard component
- Progress hesabı: score / 100f
