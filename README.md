# StayScout

StayScout is a full-stack hotel discovery and booking application. Its original visual system combines travel-led discovery, calm premium hierarchy, and practical decision support; it does not copy another company’s interface or branding.

## Project layout

- `frontend/` — React + Vite pages, design tokens, and the static web container.
- `backend/` — Spring Boot API, Flyway migrations, PostgreSQL booking rules, Redis cache, and JWT security.
- `.github/` — Continuous integration that verifies the backend.

## Run the complete application with Docker

The Codespace must be created or rebuilt from the current `.devcontainer` configuration before using Docker. It uses the standard Codespaces universal image, which includes Java, Node.js, and Docker. In an existing Codespace, run **Codespaces: Rebuild Container** once after pulling the latest `main` branch.

Set runtime-only credentials in your shell (do not commit them), then start the stack from the repository root. The fastest secure option in Codespaces is:

```bash
export DB_PASSWORD="$(openssl rand -hex 24)"
export JWT_SECRET="$(openssl rand -hex 48)"
docker compose up --build
```

Alternatively, copy `.env.example` to `.env`, replace all placeholder values, and run `docker compose up --build`. The `.env` file is ignored by Git.

To create an administrator account during the first start, use:

```bash
export DB_PASSWORD='choose-a-long-local-password'
export JWT_SECRET='use-a-random-secret-at-least-32-characters-long'
export BOOTSTRAP_ADMIN_EMAIL='admin@example.com'       # optional
export BOOTSTRAP_ADMIN_PASSWORD='use-a-strong-password' # optional
docker compose up --build
```

Open `http://localhost:8080` for the website. The frontend reverse-proxies API calls internally, so browser requests never need a public database or JWT credential.

## Local development

Run the API from `backend/` and the user interface from `frontend/` in separate terminals:

```bash
cd backend && mvn verify
cd frontend && npm install && npm run dev
```

The Vite development server proxies `/api` to `http://localhost:8080`.

## Security notes

- Environment variables supply database passwords, JWT signing secrets, and the optional bootstrap-admin credentials.
- Passwords are BCrypt-hashed; browser sessions keep the access token only in `sessionStorage`.
- PostgreSQL enforces non-overlapping bookings at the database level.
- Administrator write routes are protected server-side with the `ADMIN` role; the UI does not grant access by itself.
