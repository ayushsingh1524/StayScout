# StayScout QA Report

Date: 2026-11-22

## Scope

Reviewed the public discovery flow, hotel detail and booking flow, authentication, trips, admin inventory, Docker configuration, backend validation, and secret handling.

## Fixed in this update

| Finding | Resolution |
| --- | --- |
| Bengaluru searches did not give guests clear feedback when no matching results were displayed. | The search now reports the number of results or gives an actionable empty-state message with supported destinations. `Bangalore` is normalized to `Bengaluru` by the API. |
| Search was restricted to an exact city match. | Search now accepts partial city or country names from the StayScout inventory; for example `Beng`, `Bangalore`, `Bengaluru`, and `India` are valid searches. |
| Hotel cards and detail pages used abstract colour blocks rather than property photography. | Added a hotel image field, migration-backed image data for seeded hotels, responsive images on search cards and hotel detail pages, and a visual fallback if a remote image cannot load. |
| New hotel inventory could be published without rooms. | Admins can now add rooms, nightly prices, and capacity after creating a hotel. |
| Codespaces build was unreliable. | The current development container uses the Codespaces universal image. Frontend package versions and the Docker installation step were made deterministic. |

## Verified

- Backend Maven tests pass.
- Frontend dependency installation and production build pass.
- Docker was available in the recovered Codespace.
- No real database password, JWT secret, token, or API key is tracked in the repository.
- API authorization remains server-enforced for bookings and administrator writes.

## Production follow-up

- Replace development image URLs with licensed, optimized images stored in your own CDN/object storage before public launch.
- Add a payment provider only after selecting one; the current booking flow intentionally collects no payment data.
- Add monitoring, backups, transactional email, rate limiting, and a privacy policy before accepting public traffic.
- Seed content is intended for demonstration; replace it with verified property inventory before launch.
- A worldwide, live hotel marketplace requires a contracted supplier API (for example Amadeus, Expedia Rapid, or Booking.com Connectivity) and its credentials. The current app intentionally searches its own managed inventory and never invents availability for locations that have no stored properties.
