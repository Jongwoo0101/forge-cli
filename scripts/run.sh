#!/usr/bin/env bash
# ForgeCLI 실행 스크립트
set -e
cd "$(dirname "$0")/.."

JAR="build/libs/forgecli-1.0-all.jar"
if [ ! -f "$JAR" ]; then
  echo "실행 jar가 없습니다. 먼저 ./scripts/build.sh 를 실행하세요."
  exit 1
fi

exec java -Dfile.encoding=UTF-8 -jar "$JAR" "$@"
