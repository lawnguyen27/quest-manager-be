#!/usr/bin/env bash
# Ubuntu/Debian: install Docker Engine + Compose plugin (for BE deploy).
# Run on a fresh VPS (as root or with sudo):
#   curl -fsSL ... | bash   # or: bash be-vps-bootstrap.sh
set -euo pipefail

if [[ "${EUID}" -ne 0 ]]; then
  echo "Run as root or with: sudo bash $0"
  exit 1
fi

export DEBIAN_FRONTEND=noninteractive
apt-get update -qq
apt-get install -y -qq ca-certificates curl gnupg

install -m 0755 -d /etc/apt/keyrings
if [[ ! -f /etc/apt/keyrings/docker.gpg ]]; then
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
  chmod a+r /etc/apt/keyrings/docker.asc
fi

# shellcheck source=/dev/null
. /etc/os-release
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu ${VERSION_CODENAME:-jammy} stable" \
  > /etc/apt/sources.list.d/docker.list

apt-get update -qq
apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

systemctl enable --now docker

echo ""
echo "Docker installed. Next:"
echo "  1. (Optional) adduser ubuntu && usermod -aG docker ubuntu"
echo "  2. Clone backend repo root, cp .env.example .env"
echo "  3. docker compose up -d --build"
echo ""
