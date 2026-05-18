# Backend (Spring + Docker)

Standalone Git repository: Maven multi-module at **repo root** (`pom.xml`).

## Quick start

```bash
cp .env.example .env
# edit .env: MYSQL_ROOT_PASSWORD, APP_JWT_SECRET
docker compose up -d --build
curl -sS http://127.0.0.1:8080/actuator/health
```

## Docs

- Deploy VPS: `deploy/be/BE-DEPLOY.md`
- CI/CD: `.gitlab-ci.yml` (build/push four service images + gateway to GitLab Container Registry)

## Related

Pair with the **frontend** repository (Vite SPA + Nginx image). Point the SPA `VITE_API_BASE_URL` at this gateway’s public URL and add CORS origins on `api-gateway`.
