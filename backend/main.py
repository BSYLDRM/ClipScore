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
        video_content_description = ""
        if video_frame:
            try:
                # Base64'ten image'a çevir
                image_data = base64.b64decode(video_frame)
                image = Image.open(io.BytesIO(image_data))

                # Vision modeli ile içeriği tanımla
                vision_response = model.generate_content([
                    "Bu video karesinde ne görüyorsun? "
                    "İçeriği 2-3 cümleyle kısaca Türkçe açıkla. "
                    "Sadece gördüklerini yaz, yorum yapma.",
                    image
                ])
                video_content_description = vision_response.text
                print(f"Video içeriği: {video_content_description}")
            except Exception as ve:
                print(f"Vision analiz hatası: {ve}")
                video_content_description = ""

        # Ana prompt'a video içeriğini ekle:
        video_context = ""
        if video_content_description:
            video_context = f"""
Video İçeriği (AI tarafından otomatik analiz edildi):
{video_content_description}

Başlığın video içeriğiyle uyumunu da değerlendir ve
contentMatchScore alanına 0-100 arası puan ver.
"""

        prompt = f"""
Sen bir viral sosyal medya içerik uzmanısın.
Aşağıdaki {platform} içeriğini analiz et ve SADECE JSON döndür.

Platform: {platform}
Başlık: {title_str}
Açıklama: {desc_str}
Dil: {language}
{video_context}

{platform} platformuna özel kriterler:
- TikTok: trend müzik uyumu, hızlı dikkat çekme, GenZ dili
- Instagram Reels: görsel estetik, hikaye anlatımı, topluluk etkileşimi
- YouTube Shorts: thumbnail etkisi, arama optimizasyonu, izlenme süresi
- YouTube: SEO anahtar kelimeler, izlenme süresi, abonelik çağrısı
- X (Twitter): özlü mesaj, tartışma yaratma, retweet potansiyeli

SADECE şu JSON formatında yanıt ver:
{{
  "vibeScore": <0-100 {platform} için viral potansiyel>,
  "hookScore": <0-100 ilk 3 saniye dikkat çekicilik>,
  "keywordScore": <0-100 {platform} SEO uyumu>,
  "emotionScore": <0-100 duygusal etki>,
  "ctaScore": <0-100 harekete geçirme kalitesi>,
  "contentMatchScore": <0-100 başlık ile video içeriği uyumu, frame yoksa 0>,
  "videoContentDescription": "{video_content_description[:100] if video_content_description else ''}",
  "hooks": ["<{platform} için hook 1>", "<hook 2>", "<hook 3>"],
  "description": "<{platform} için optimize edilmiş açıklama>",
  "hashtags": ["#{platform.lower().replace(' ', '')}tag1", "#tag2", "#tag3", "#tag4", "#tag5"]
}}
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
