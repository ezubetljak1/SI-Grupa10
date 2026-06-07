#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
REPO_DIR="$(cd "${PROJECT_DIR}/.." && pwd)"
DEPLOY_BRANCH="${DEPLOY_BRANCH:-main}"

on_error() {
  exit_code=$?
  trap - ERR

  echo
  echo "Deployment failed with exit code ${exit_code}."
  echo "Current container status:"

  cd "${PROJECT_DIR}" || exit "${exit_code}"
  docker compose ps --all || true

  echo
  echo "Recent container logs:"
  docker compose logs --tail=120 || true

  exit "${exit_code}"
}

trap on_error ERR

wait_for_url() {
  url="$1"
  service_name="$2"
  max_attempts="${3:-30}"
  delay_seconds="${4:-5}"

  for ((attempt = 1; attempt <= max_attempts; attempt++)); do
    if curl --fail --silent --show-error "${url}" > /dev/null; then
      echo "${service_name} is available."
      return 0
    fi

    echo "Waiting for ${service_name} (${attempt}/${max_attempts})..."
    sleep "${delay_seconds}"
  done

  echo "${service_name} did not become available in time."
  return 1
}

echo "Fetching ${DEPLOY_BRANCH} from origin..."
cd "${REPO_DIR}"

git fetch --prune origin
git checkout "${DEPLOY_BRANCH}"
git reset --hard "origin/${DEPLOY_BRANCH}"

cd "${PROJECT_DIR}"

echo "Checking required server-side files..."
test -f .env
test -f secrets/google-document-ai.json

echo "Validating Docker Compose configuration..."
docker compose config -q

echo "Building application images..."
docker compose build --pull docflow-backend docflow-frontend

echo "Starting containers..."
docker compose up -d --remove-orphans

echo "Waiting for local services..."
wait_for_url "http://127.0.0.1:8082/" "Frontend"
wait_for_url \
  "http://127.0.0.1:8081/realms/docflow/.well-known/openid-configuration" \
  "Keycloak"

echo
echo "Final container status:"
docker compose ps

echo
echo "Deployment completed successfully."