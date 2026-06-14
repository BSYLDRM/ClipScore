import json
import os
import time
import threading
import traceback
import base64
import io

import requests
import google.generativeai as genai
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS
from PIL import Image

load_dotenv()

app = Flask(__name__)
CORS(app, origins=["*"])

# Gemini Client Initialization
genai.configure(api_key=os.environ.get("GEMINI_API_KEY"))
model = genai.GenerativeModel("gemini-2.5-flash")

request_counts = {}


def check_rate_limit(ip):
    now = time.time()
    cutoff = now - 60
    timestamps = request_counts.get(ip, [])
    timestamps = [t for t in timestamps if t > cutoff]
    if len(timestamps) >= 10:
        request_counts[ip] = timestamps
        return False
    timestamps.append(now)
    request_counts[ip] = timestamps
    return True


@app.get("/api/health")
def health():
    return jsonify({"status": "ok", "service": "clipscore-backend"})


@app.get("/api/message")
def get_message():
    return jsonify({"message": "iyiki varsınız"})


def _strip_markdown_fences(text):
    text = text.strip()
    if text.startswith("```json"):
        text = text[7:]
    elif text.startswith("```"):
        text = text[3:]
    text = text.strip()
    if text.endswith("```"):
        text = text[:-3]
    return text.strip()


@app.post("/api/analyze")
def analyze():
    ip = request.remote_addr
    if not check_rate_limit(ip):
        return (
            jsonify({"error": "Çok fazla istek. Lütfen bir dakika bekleyin."}),
            429,
        )

    try:
        data = request.get_json(silent=True)
        if data is None:
            data = {}

        title = data.get("title", "")
        description = data.get("description", "")
        language = data.get("language", "tr")
        platform = data.get("platform", "TikTok")
        video_frame = data.get("videoFrame", None)

        title_str = str(title).strip() if title is not None else ""
        desc_str = str(description).strip() if description is not None else ""

        if not title_str or not desc_str:
            return jsonify({"error": "Başlık ve açıklama zorunludur"}), 400

        # Eğer frame varsa görsel analiz yap
        video_content_description_ai = ""
        if video_frame:
            try:
                # Base64'ten image'a çevir
                image_data = base64.b64decode(video_frame)
                image = Image.open(io.BytesIO(image_data))

                # Vision modeli ile içeriği tanımla
                vision_response = model.generate_content([
                    "Bu video karesinde ne görüyorsun? Maksimum 2 cümleyle kısaca özetle. Sadece en önemli nesne ve ortamı belirt.",
                    image
                ])
                video_content_description_ai = vision_response.text
            except Exception as ve:
                print(f"Vision analiz hatası: {ve}")
                video_content_description_ai = ""

        # Ana prompt'a video içeriğini ekle:
        video_context = ""
        if video_content_description_ai:
            video_context = f"""
Görsel Analiz Verisi (AI tarafından sağlanan ham veri):
{video_content_description_ai}
"""

        prompt = f"""
Sen bir viral sosyal medya içerik uzmanısın.
Aşağıdaki {platform} içeriğini analiz et ve SADECE geçerli bir JSON döndür. Başka hiçbir şey yazma, markdown kullanma.

GİRİŞ VERİLERİ:
Platform: {platform}
Kullanıcı Başlığı: {title_str}
Kullanıcı Açıklaması: {desc_str}
Hedef Dil: {language}
{video_context}

ANALİZ KRİTERLERİ ({platform} özelinde):
- TikTok: trend müzik uyumu, hızlı dikkat çekme, GenZ dili.
- Instagram Reels: görsel estetik, hikaye anlatımı, topluluk etkileşimi.
- YouTube Shorts: thumbnail etkisi, arama optimizasyonu, izlenme süresi.
- YouTube: SEO anahtar kelimeler, izlenme süresi, abonelik çağrısı.
- X (Twitter): özlü mesaj, tartışma yaratma, retweet potansiyeli.

YANIT FORMATI (JSON):
{{
  "videoContentDescription": "Sahneyi 1-2 cümleyle kısaca özetle.",
  "hookScore": <0-100 tam sayı>,
  "keywordScore": <0-100 tam sayı>,
  "emotionScore": <0-100 tam sayı>,
  "ctaScore": <0-100 tam sayı>,
  "contentMatchScore": <0-100 tam sayı (başlık ile video uyumu)>,
  "vibeScore": <0-100 tam sayı (genel viral skor)>,
  "hooks": ["Tam olarak 3 adet etkileyici hook yaz"],
  "description": "SEO optimizeli, platforma uygun açıklama metni",
  "hashtags": ["En az 10, en fazla 20 adet popüler hashtag"]
}}

KURALLAR:
1. SADECE ham JSON döndür.
2. videoContentDescription her zaman tam ve bütün cümlelerle bitmelidir.
3. Tüm skorlar sayısal olmalıdır.
4. hooks dizisi tam olarak 3 elemanlı olmalıdır.
5. hashtags dizisi 10-20 elemanlı olmalıdır.
"""

        # Gemini API call
        response = model.generate_content(prompt)
        raw_text = response.text

        cleaned = _strip_markdown_fences(raw_text)
        result = json.loads(cleaned)

        return jsonify(result), 200

    except Exception as e:
        print(f"HATA: {str(e)}")
        print(traceback.format_exc())
        return jsonify({"error": str(e)}), 500


def keep_alive():
    while True:
        try:
            url = os.environ.get(
                "RENDER_EXTERNAL_URL",
                "https://clipscore-dmmb.onrender.com"
            )
            requests.get(f"{url}/api/health", timeout=10)
        except:
            pass
        time.sleep(840)


if __name__ == "__main__":
    # App başlarken thread'i başlat
    thread = threading.Thread(target=keep_alive, daemon=True)
    thread.start()

    port = int(os.getenv("PORT", "5000"))
    print("ClipScore Backend çalışıyor...")
    app.run(host="0.0.0.0", port=port)
