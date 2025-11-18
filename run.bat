@echo off
setlocal
cd /d "%~dp0"

:: determine compose command (docker-compose or docker compose)
set COMPOSE_CMD=
where docker-compose >nul 2>nul
if %errorlevel%==0 set COMPOSE_CMD=docker-compose
if not defined COMPOSE_CMD (
  where docker >nul 2>nul
  if %errorlevel%==0 (
    docker compose version >nul 2>nul
    if %errorlevel%==0 set COMPOSE_CMD=docker compose
  )
)

if not defined COMPOSE_CMD (
  echo Docker Compose not found. Please install Docker Desktop/Compose.
  exit /b 1
)

%COMPOSE_CMD% -f deploy/docker-compose.yml up -d --build
if %errorlevel% neq 0 (
  echo Failed to start services.
  exit /b 1
)
echo Services started: frontend http://localhost:3000 , backend http://localhost:8080/api
endlocal
