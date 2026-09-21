#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
compose=(docker compose -p multitenancy-ci -f .github/compose.ci.yml)

cleanup() {
  local status=$?
  if [ "$status" -ne 0 ]; then
    "${compose[@]}" logs --no-color || true
  fi
  "${compose[@]}" down -v --remove-orphans
  return "$status"
}
trap cleanup EXIT

docker build -t multitenancy:ci .
"${compose[@]}" up -d
curl --fail --silent --show-error --retry 30 --retry-delay 2 \
  --retry-connrefused --retry-all-errors --max-time 5 \
  http://127.0.0.1:18080/actuator/health
python3 scripts/smoke-test.py http://127.0.0.1:18080
mkdir -p target/release
"${compose[@]}" cp app:/app/app.jar target/release/multitenancy.jar
(cd target/release && sha256sum multitenancy.jar > SHA256SUMS)
