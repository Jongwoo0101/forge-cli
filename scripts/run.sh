#!/usr/bin/env bash
# ForgeCLI 실행 스크립트
set -e
cd "$(dirname "$0")/.."

# 버전을 박아 두면 올릴 때마다 이 줄이 썩는다. 산출물에서 찾아 쓴다.
JAR="$(ls -t build/libs/forgecli-*-all.jar 2>/dev/null | head -1)"
if [ -z "$JAR" ] || [ ! -f "$JAR" ]; then
  echo "실행 jar가 없습니다. 먼저 ./scripts/build.sh 를 실행하세요."
  exit 1
fi

exec java -Dfile.encoding=UTF-8 -jar "$JAR" "$@"
