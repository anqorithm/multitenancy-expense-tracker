#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE:-http://127.0.0.1:8080}"

bold()  { printf '\033[1m%s\033[0m\n' "$*"; }
dim()   { printf '\033[2m%s\033[0m\n' "$*"; }
step()  { echo; bold "== $1"; if [ -n "${2:-}" ]; then dim "   $2"; fi; }

pretty() {
  if command -v jq >/dev/null 2>&1; then jq .
  elif command -v python3 >/dev/null 2>&1; then python3 -m json.tool
  else cat; fi
}

call() {
  local host="$1" method="$2" path="$3" body="${4:-}"
  local cmd="curl -s -H 'Host: $host' -X $method $BASE$path"
  [ -n "$body" ] && cmd="$cmd -H 'Content-Type: application/json' -d '$body'"
  dim "   \$ $cmd"
  if [ -n "$body" ]; then
    curl -s -H "Host: $host" -X "$method" "$BASE$path" -H 'Content-Type: application/json' -d "$body" | pretty
  else
    curl -s -H "Host: $host" -X "$method" "$BASE$path" | pretty
  fi
}

raw() { curl -s -H "Host: $1" "$BASE$2"; }

bold "multitenancy demo: one app, one database, two companies"
dim  "Only the Host header changes between calls. Same URL, same process, same table."

step "1. Who am I?" "Same endpoint, different company depending on the subdomain."
call tenant1.localhost GET /api/me
call tenant2.localhost GET /api/me

step "2. Each company sees only its own expenses" "Hibernate adds 'where tenant_id = ?' to every query."
call tenant1.localhost GET /api/expenses
call tenant2.localhost GET /api/expenses

step "3. Totals are per company too"
call tenant1.localhost GET /api/expenses/total
call tenant2.localhost GET /api/expenses/total

step "4. tenant1 adds an expense" "The request body has no tenant field. Hibernate stamps tenant_id from the context."
call tenant1.localhost POST /api/expenses '{"description":"Printer","amount":400,"spentOn":"2026-09-21"}'
dim "   tenant2 still sees only its own two rows:"
call tenant2.localhost GET /api/expenses

step "5. Cross-tenant access by id is a 404" "tenant2 tries to open an expense that belongs to tenant1."
FIRST_ID=$(raw tenant1.localhost /api/expenses | { jq -r '.[0].id' 2>/dev/null || python3 -c 'import sys,json;print(json.load(sys.stdin)[0]["id"])'; })
dim "   tenant1 expense id = $FIRST_ID"
call tenant1.localhost GET "/api/expenses/$FIRST_ID"
call tenant2.localhost GET "/api/expenses/$FIRST_ID"

step "6. The database enforces isolation on its own" "Raw SQL with NO where clause. Postgres row-level security still filters."
call tenant1.localhost GET /api/debug/leak-test
call tenant2.localhost GET /api/debug/leak-test

step "7. Unknown or missing tenant is rejected before any business code runs"
call localhost GET /api/expenses
call nobody.localhost GET /api/expenses

echo
bold "Done. Watch 'make logs' to see the generated SQL and the bound tenant_id values."
