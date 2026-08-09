#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/backend"
if ! command -v mvn >/dev/null 2>&1; then
  echo "ERROR: Maven 3.9.16+ is required but mvn was not found." >&2
  exit 127
fi
mvn -ntp clean compile
mvn -ntp test
mvn -ntp package
