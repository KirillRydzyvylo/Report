#!/usr/bin/env python3
"""Mock-сервер авторизации для тестирования плагина.

POST /api/login
  200 + {"token": "..."} если login == password != пусто
  401 + {"error": "..."} иначе

Запуск:
    python3 mock/mock_auth_server.py

Остановка:
    Ctrl+C или: pkill -f mock_auth_server.py
"""
import json
from http.server import BaseHTTPRequestHandler, HTTPServer


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode("utf-8")
        try:
            data = json.loads(body)
        except json.JSONDecodeError:
            data = {}

        login = data.get("login", "")
        password = data.get("password", "")

        if login and login == password:
            resp = {"token": "mock-token-for-" + login}
            self._send(200, resp)
        else:
            self._send(401, {"error": "invalid credentials"})

    def _send(self, code, obj):
        payload = json.dumps(obj).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)

    def log_message(self, fmt, *args):
        print("[mock-auth]", fmt % args)


if __name__ == "__main__":
    print("Mock auth server on http://localhost:8080/api/login")
    HTTPServer(("127.0.0.1", 8080), Handler).serve_forever()
