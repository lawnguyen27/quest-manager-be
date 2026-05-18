# Deploy Backend lên VPS (Docker Compose)

Hướng dẫn triển khai stack trong `docker-compose.yml` (repo backend-only): MySQL, Redis, Zookeeper, Kafka, `user-service`, `mission-service`, `wallet-service`, `api-gateway`.

File deploy backend nằm gọn trong **`deploy/be/`** (`mysql/`, `docker/Dockerfile.spring`, `scripts/`).

## Yêu cầu tối thiểu

- **RAM**: nên **≥ 4 GB** (Kafka + MySQL + 4 JVM). 2 GB có thể chạy được nhưng dễ OOM khi khởi động.
- **OS**: Ubuntu 22.04/24.04 LTS (hoặc Debian tương tự).
- **Biến môi trường**: copy `.env` từ `.env.example` và đặt `MYSQL_ROOT_PASSWORD`, `APP_JWT_SECRET` đủ dài.

## Cách 1 — Build image ngay trên VPS (đơn giản, không cần GitLab Registry)

Phù hợp khi server **cùng kiến trúc** với máy dev (thường là `amd64`) hoặc bạn chấp nhận build chậm trên ARM.

Trên VPS, clone **repo backend** (root có `pom.xml`, `deploy/be/`, `docker-compose.yml`):

```bash
sudo apt update && sudo apt install -y git
git clone https://gitlab.com/<group>/<backend-project>.git
cd <backend-project>
cp .env.example .env
nano .env   # MYSQL_ROOT_PASSWORD, APP_JWT_SECRET, ...
docker compose up -d --build
```

Kiểm tra gateway:

```bash
curl -sS http://127.0.0.1:8080/actuator/health
```

Mở firewall (ví dụ `ufw`): cho phép **22** (SSH), **8080** (API gateway — có thể đóng sau khi có HTTPS reverse proxy). **Không** nên mở **3306** ra internet nếu không cần.

## Cách 2 — Kéo image từ GitLab Container Registry (sau khi CI đã push)

1. Trên GitLab: **Deploy → Deploy tokens** hoặc **Personal Access Token** với quyền **read_registry**.
2. Trên VPS: cài Docker (xem `deploy/be/scripts/be-vps-bootstrap.sh`).

Chỉ cần **file compose + `deploy/be/`** (MySQL init, Dockerfile Spring nếu cần rebuild). Tối thiểu:

- `docker-compose.yml`
- `deploy/be/mysql/`, `deploy/be/docker/Dockerfile.spring`

Tạo `.env`:

```env
MYSQL_ROOT_PASSWORD=...
APP_JWT_SECRET=...
DOCKER_REGISTRY=registry.gitlab.com/<group>/<project>
DOCKER_TAG=<short_sha từ job GitLab, ví dụ a1b2c3d4>
```

Đăng nhập registry và chạy:

```bash
echo "<token_hoặc_deploy_token_password>" | docker login registry.gitlab.com -u "<username_hoặc_deploy_token_username>" --password-stdin
docker compose pull user-service mission-service wallet-service api-gateway
docker compose up -d --no-build
```

**Lưu ý ARM (Oracle Ampere / Apple server):** image CI build trên `amd64` sẽ **không chạy** trên ARM trừ khi bạn bật QEMU (chậm) hoặc đổi CI sang **multi-arch**. Trường hợp đó dùng **Cách 1** (build trên server).

## HTTPS và tên miền (khuyến nghị)

Dùng **Caddy** hoặc **Nginx** trên host làm reverse proxy: `https://api.ban.com` → `127.0.0.1:8080`. Giữ MySQL/Redis/Kafka **chỉ listen nội bộ** Docker (mặc định compose vẫn publish 3306 — xem mục “Cứng hóa” bên dưới).

## CORS (FE trên Vercel / domain khác)

Gateway hiện chỉ cho `localhost:5173` trong `BE/api-gateway/src/main/resources/application.yml`. Khi FE ở Vercel, thêm pattern origin production (ví dụ `https://ten-ban.vercel.app` hoặc `https://*.vercel.app`) vào `allowedOriginPatterns`, build lại image `api-gateway`, hoặc tạm dùng Cách 1 và rebuild trên VPS sau khi sửa file.

## Cứng hóá nhanh (production)

- Đổi mật khẩu MySQL / JWT secret mạnh; không commit `.env`.
- Hạn chế public port: chỉ **80/443** (reverse proxy) và **8080** nếu chưa có proxy; cân nhắc bỏ `ports: "3306:3306"` khỏi service `mysql` trong bản override riêng — khi đó chỉ container trong cùng network mới kết nối DB được (đúng ý muốn thường).

## Xem log / gỡ lỗi

```bash
docker compose ps
docker compose logs -f api-gateway
docker compose logs -f kafka mysql
```

---

## English (short)

1. **Requirements**: ~4GB+ RAM recommended; Ubuntu LTS; strong `MYSQL_ROOT_PASSWORD` and `APP_JWT_SECRET`.
2. **Option A — build on VPS**: clone repo → `.env` → `docker compose up -d --build`.
3. **Option B — pull from GitLab Registry**: set `DOCKER_REGISTRY` + `DOCKER_TAG` in `.env`, `docker login registry.gitlab.com`, `pull` the four app services, `up -d --no-build`. Watch **amd64 vs arm64**.
4. **Smoke test**: `curl http://127.0.0.1:8080/actuator/health`.
5. **Put HTTPS** in front (Caddy/Nginx); **tighten** DB exposure; **extend** gateway CORS for your real FE origin.

Backend deploy assets live under **`deploy/be/`**.
