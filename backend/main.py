import os

from flask import Flask, jsonify
from flask_cors import CORS


app = Flask(__name__)
CORS(app)


@app.get("/api/message")
def get_message():
    return jsonify({"message": "iyiki varsınız"})


if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    print("ClipScore Backend çalışıyor...")
    app.run(host="0.0.0.0", port=port)
