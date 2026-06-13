import json
import os
import time
import threading

import requests
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

load_dotenv()

app = Flask(__name__)
CORS(app, origins=["*"])

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

    data = request.get_json(silent=True)
    if data is None:
        data = {}

    title = data.get("title", "")
    description = data.get("description", "")
    language = data.get("language", "tr")
    platform = data.get("platform", "TikTok")

    title_str = str(title).strip() if title is not None else ""
    desc_str = str(description).strip() if description is not None else ""

    if not title_str or not desc_str:
        return jsonify({"error": "Başlık ve açıklama zorunludur"}), 400

    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        return jsonify({"error": "API anahtarı yapılandırılmamış"}), 500

    prompt = f"""
Sen bir viral sosyal medya içerik uzmanısın.
Aşağıdaki {platform} içeriğini analiz et ve SADECE JSON döndür.

Platform: {platform}
Başlık: {title_str}
Açıklama: {desc_str}
Dil: {language}

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
  "hooks": ["<{platform} için hook 1>", "<hook 2>", "<hook 3>"],
  "description": "<{platform} için optimize edilmiş açıklama>",
  "hashtags": ["#{platform.lower().replace(' ', '')}tag1", "#tag2", "#tag3", "#tag4", "#tag5"]
}}
"""

    url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "temperature": 0.7,
            "maxOutputTokens": 2000,
        },
    }

    try:
        resp = requests.post(
            url,
            headers={
                "content-type": "application/json",
                "x-goog-api-key": api_key,
            },
            json=payload,
            timeout=60,
        )
        if resp.status_code != 200:
            return jsonify({"error": "Gemini API hatası", "detail": resp.text}), 502

        data_json = resp.json()
        raw_text = data_json["candidates"][0]["content"]["parts"][0]["text"]
        cleaned = _strip_markdown_fences(raw_text)
        result = json.loads(cleaned)
        return jsonify(result), 200
    except Exception as e:
        print("HATA:", str(e))
        return jsonify({"error": "Analiz sırasında bir hata oluştu", "detail": str(e)}), 500


def keep_alive():
    while True:
        try:
            url = os.environ.get("RENDER_EXTERNAL_URL", "https://clipscore-dmmb.onrender.com")
            requests.get(f"{url}/api/health")
        except:
            pass
        time.sleep(840)  # 14 dakikada bir ping at


if __name__ == "__main__":
    # App başlarken thread'i başlat
    thread = threading.Thread(target=keep_alive, daemon=True)
    thread.start()

    port = int(os.getenv("PORT", "5000"))
    print("ClipScore Backend çalışıyor...")
    app.run(host="0.0.0.0", port=port)
