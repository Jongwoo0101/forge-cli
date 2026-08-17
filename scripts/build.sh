#!/usr/bin/env bash
# ForgeCLI 빌드 스크립트
#
# 커널(io.github.jongwoo0101:forgeframework)이 로컬 Maven 저장소에 없으면
# 의존성 해석 단계에서 실패한다. 그럴 때는 forge-framework 저장소에서
# ./scripts/publish.sh 를 먼저 실행한다.
set -e
cd "$(dirname "$0")/.."

./gradlew build

echo
echo "산출물:"
find build/libs -name "*.jar" -print | sed 's|^|  |'
