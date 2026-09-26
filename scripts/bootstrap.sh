#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
API_KEY="$(grep '^RLAAS_API_KEY=' "$ROOT/.env" | cut -d= -f2-)"
RLAAS_URL="${RLAAS_URL:-http://localhost:8085}"

if [[ -z "$API_KEY" ]]; then
  echo "RLAAS_API_KEY not found in .env" >&2
  exit 1
fi

for i in $(seq 1 20); do
  if curl -sf -o /dev/null -X POST "$RLAAS_URL/check"; then break; fi
  sleep 2
done

echo "registering client with key $API_KEY"
curl -sf -X POST "$RLAAS_URL/createClient" \
  -H 'Content-Type: application/json' \
  -d "{\"apiKey\":\"$API_KEY\",\"clientName\":\"API Gateway\"}" >/dev/null || true

for spec in '/order 5' '/product 10000'; do
  endpoint=${spec% *}
  limit=${spec#* }
  echo "setting $endpoint -> $limit/min"
  curl -sf -X POST "$RLAAS_URL/addEndpoint" \
    -H 'Content-Type: application/json' \
    -H "x-api-key: $API_KEY" \
    -d "{\"endpoint\":\"$endpoint\",\"rateLimit\":$limit}" >/dev/null || true
done

echo "done. hit /order 5x fast -> 6th should be 429, /profile stays unlimited (fail-open)."