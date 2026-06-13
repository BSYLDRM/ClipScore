import json
import os
import time
import threading
import traceback

import requests
import anthropic
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

load_dotenv()

app = Flask(__name__)
CORS(app, origins=["*"])

# Claude Client Initialization
client = anthropic.Anthropic(
    api_key=os.environ.get("ANTHROPIC_API_KEY"),
    timeout=100.0  # 100 saniye timeout
)

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

        title_str = str(title).strip() if title is not None else ""
        desc_str = str(description).strip() if description is not None else ""

        if not title_str or not desc_str:
            return jsonify({"error": "Başlık ve açıklama zorunludur"}), 400

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

        # Claude API call
        message = client.messages.create(
            model="claude-sonnet-4-6",
            max_tokens=1000,
            timeout=100.0,
            messages=[
                {"role": "user", "content": prompt}
            ]
        )

        raw_text = message.content[0].text
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
