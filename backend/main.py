import json
import os
import time

import requests
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

load_dotenv()

app = Flask(__name__)
CORS(app)

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

    body = request.get_json(silent=True)
    if body is None:
        body = {}
    title = body.get("title", "")
    description = body.get("description", "")
    language = body.get("language", "tr")

    title_str = str(title).strip() if title is not None else ""
    desc_str = str(description).strip() if description is not None else ""

    if not title_str or not desc_str:
        return jsonify({"error": "Başlık ve açıklama zorunludur"}), 400

    if len(title_str) > 200 or len(desc_str) > 1000:
        return (
            jsonify(
                {
                    "error": "Başlık en fazla 200, açıklama en fazla 1000 karakter olabilir"
                }
            ),
            400,
        )

    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        return jsonify({"error": "API anahtarı yapılandırılmamış"}), 500

    prompt = (
        "Sen bir sosyal medya içerik uzmanısın. Aşağıdaki kısa video içeriğini analiz et ve SADECE geçerli JSON döndür. Başka hiçbir şey yazma, markdown kullanma, sadece ham JSON.\n\n"
        f"Başlık: {title_str}\nAçıklama: {desc_str}\nDil: {language}\n\n"
        'JSON formatı:\n{"vibeScore": <0-100>, "hookScore": <0-100>, "keywordScore": <0-100>, "emotionScore": <0-100>, "ctaScore": <0-100>, "hooks": ["hook1", "hook2", "hook3"], "description": "<SEO açıklama>", "hashtags": ["#tag1", "#tag2", "#tag3", "#tag4", "#tag5"]}'
    )

    url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"temperature": 0.7, "maxOutputTokens": 1000},
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
        print("Gemini status:", resp.status_code)
        print("Gemini response:", resp.text[:500])
        if resp.status_code != 200:
            return jsonify({"error": "Gemini API hatası", "detail": resp.text}), 502

        data = resp.json()
        raw_text = data["candidates"][0]["content"]["parts"][0]["text"]
        cleaned = _strip_markdown_fences(raw_text)
        result = json.loads(cleaned)
        return jsonify(result), 200
    except Exception as e:
        print("HATA:", str(e))
        print("RESPONSE:", resp.text if 'resp' in locals() else "istek gitmedi")
        return jsonify({"error": "Gemini API hatası", "detail": str(e)}), 502


if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    print("ClipScore Backend çalışıyor...")
    app.run(host="0.0.0.0", port=port)