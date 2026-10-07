import json
import os
import sys
from types import SimpleNamespace

import pytest

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import main  # noqa: E402

VALID_RESULT = {
    "videoContentDescription": "Bir kedi masada oturuyor.",
    "hookScore": 80,
    "keywordScore": 70,
    "emotionScore": 60,
    "ctaScore": 50,
    "contentMatchScore": 90,
    "vibeScore": 75,
    "hooks": ["a", "b", "c"],
    "description": "açıklama",
    "hashtags": ["#a"] * 10,
}

BODY = {"title": "Başlık", "description": "Açıklama", "platform": "TikTok"}


@pytest.fixture(autouse=True)
def reset(monkeypatch):
    main.request_counts.clear()
    monkeypatch.setattr(main, "REQUIRE_AUTH", False)

    def fake_generate(prompt):
        return SimpleNamespace(text="```json\n" + json.dumps(VALID_RESULT) + "\n```")

    monkeypatch.setattr(main.model, "generate_content", fake_generate)

    def fake_verify(token):
        if token == "good-token":
            return {"uid": "user-1"}
        raise ValueError("invalid token")

    monkeypatch.setattr(main.firebase_auth, "verify_id_token", fake_verify)


@pytest.fixture
def client():
    return main.app.test_client()


def test_health(client):
    assert client.get("/api/health").status_code == 200


def test_analyze_success_strips_markdown_fences(client):
    res = client.post("/api/analyze", json=BODY)
    assert res.status_code == 200
    assert res.get_json()["vibeScore"] == 75


def test_analyze_requires_title_and_description(client):
    res = client.post("/api/analyze", json={"title": "", "description": "x"})
    assert res.status_code == 400


def test_analyze_bad_ai_json_returns_generic_500(client, monkeypatch):
    monkeypatch.setattr(
        main.model, "generate_content", lambda prompt: SimpleNamespace(text="not json")
    )
    res = client.post("/api/analyze", json=BODY)
    assert res.status_code == 500
    assert "Expecting value" not in res.get_json()["error"]


def test_require_auth_rejects_missing_token(client, monkeypatch):
    monkeypatch.setattr(main, "REQUIRE_AUTH", True)
    assert client.post("/api/analyze", json=BODY).status_code == 401


def test_require_auth_rejects_invalid_token(client, monkeypatch):
    monkeypatch.setattr(main, "REQUIRE_AUTH", True)
    res = client.post("/api/analyze", json=BODY, headers={"Authorization": "Bearer bad"})
    assert res.status_code == 401


def test_require_auth_accepts_valid_token(client, monkeypatch):
    monkeypatch.setattr(main, "REQUIRE_AUTH", True)
    res = client.post(
        "/api/analyze", json=BODY, headers={"Authorization": "Bearer good-token"}
    )
    assert res.status_code == 200


def test_rate_limit_per_user(client):
    headers = {"Authorization": "Bearer good-token"}
    for _ in range(10):
        assert client.post("/api/analyze", json=BODY, headers=headers).status_code == 200
    assert client.post("/api/analyze", json=BODY, headers=headers).status_code == 429
    # Aynı IP'den token'sız istek ayrı limite sahip
    assert client.post("/api/analyze", json=BODY).status_code == 200
