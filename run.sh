#!/bin/bash
set -euo pipefail

if [ ! -d ".venv" ]; then
  python3 -m venv .venv
fi
source .venv/bin/activate
pip install -r requirements.txt
export FLASK_APP=app
export FLASK_ENV=development
flask --app app run --host=0.0.0.0 --port=5000
