# TalentBridge AI — Backend

This is the backend build from the 4-week solo development plan, covering the
P0 backend scope: auth, opportunities/platforms, public search, candidate
profile, consent engine, referral engine, admin CRM, finance ledger,
notifications, analytics, and security hardening. The frontend (React/TS) is
**not** included in this delivery — see "What's not done" below.

## Run it

```
cp .env.example .env
docker compose up --build
```

- API: http://localhost:8080 (Swagger UI at /swagger-ui.html)
- Mailpit (catches dev emails): http://localhost:8025
- MongoDB: localhost:27017

With `SEED_ENABLED=true` (the default in docker-compose), 30 synthetic
published listings and one platform are created on first boot.

## Smoke test (matches the Day 6 "done when" check)

1. `POST /api/auth/register` with an email + password.
2. Open Mailpit, find the verification email, copy the token from the link.
3. `POST /api/auth/verify-email` with that token.
4. `POST /api/auth/login`.
5. `GET /api/auth/me` (uses the session cookie from login).

## What's implemented

- **Common layer**: API envelope (`ApiResponse`/`ApiError`), global exception
  handler, correlation-ID filter, pagination helper, base entity, append-only
  audit log + service.
- **Auth**: register, hashed+expiring email verification, login/logout/me via
  server-side sessions (Spring Session/MongoDB), password reset, BCrypt-12,
  account lockout after 5 failed attempts, rate limiting on register/login/
  forgot-password.
- **Candidate**: profile CRUD (no DOB/address/ID numbers collected), saved
  opportunities, privacy-request filing, skill-overlap recommendations.
- **Opportunity/Platform**: admin CRUD, the full status machine (draft →
  pending review → published → paused/expired/archived), the four-eyes rule
  (a listing claiming a referral type other than PUBLIC_APPLICATION can't be
  published by its own author), public search with filters + text index,
  daily scheduler for expiry/staleness, listing reports + contact endpoints.
- **Consent**: append-only, versioned, per-purpose-and-recipient; withdrawing
  one purpose/recipient never touches another.
- **Referral**: full state machine, the validation service (every blueprint
  11.2 rule, each with its own candidate-safe error code), idempotent
  creation, append-only event log with actor/time/evidence, candidate +
  admin endpoints.
- **Admin**: read-only candidate CRM (audited), referral manager, privacy
  request queue, audit-log viewer (Super Admin only).
- **Finance**: reward ledger with EXPECTED→CONFIRMED→PAID enforcement,
  evidence + received-date requirements on PAID, per-currency summary
  (currencies are never summed together), optional second-approver gate.
- **Notifications/analytics**: mail service with duplicate-send protection,
  cookieless aggregate analytics beacon + funnel overview.
- **Security**: Spring Security session auth, role-based access per module,
  CSRF, CORS, CSP/HSTS/frame/referrer headers, rate limiting.
- A handful of unit tests for the two state machines and the consent
  independence rule.

## What's NOT done / simplified

- **Not build-verified.** This sandbox has no route to Maven Central (only
  npm/PyPI/GitHub are reachable), so `mvn verify` could not be run here.
  The code was written and reviewed carefully, but **run `mvn verify` /
  `docker compose up --build` yourself before trusting it** — some library
  API details (e.g. the exact Bucket4j 8.x builder calls) were written from
  memory and are the most likely spot for a small fix.
- **MFA (TOTP)** fields exist on `User` but enrollment/verification endpoints
  are not built — Day 22 item, flagged as a gap.
- **CSV export, k6 load test, Testcontainers integration tests, Playwright
  E2E, accessibility, and most of the "full branch/line coverage" testing
  from Weeks 3–4** are not included — this delivery is the backend's P0/P1
  functional code and a starter test suite, not the full 28-day test plan.
- **Company leads / companyReferrals (P1, Day 19)** — skipped per the plan's
  own cut-ladder guidance ("cut first when behind").
- **Frontend, CI workflows, deployment (Caddy/VPS), SEO meta injection** are
  out of scope for this delivery (it was scoped as "the backend part").
- Notification "versions" and preference-check gating (Day 20) are
  simplified to a generic mail service with duplicate-send protection only.

## Structure

```
backend/   Spring Boot 3 / Java 21 source (Maven)
docs/      (empty placeholder — PRD/ADRs/data model were not generated)
docker-compose.yml, .env.example
```
