#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if ! command -v docker-compose >/dev/null 2>&1; then
  echo "需要 docker-compose 请先安装。" >&2
  exit 1
fi

docker-compose -f deploy/docker-compose.yml up -d --build
echo "服务已启动：前端 http://localhost:3000 ，后端 http://localhost:8080/api"
