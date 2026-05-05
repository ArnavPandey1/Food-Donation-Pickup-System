# Food Donation Pickup System — Hyper-Local Food Logistics System

Food Donation Pickup System is a Spring Boot application that connects donors, volunteers, and admins to coordinate food donation pickup and distribution in real time.

It supports donation posting, live nearby-drop discovery, OTP-based pickup verification, completion tracking, delivery history, and real-time alerts over WebSockets.

## What the project does

Food Donation Pickup System turns unused food into a managed delivery workflow:

1. A donor posts a food donation with location and expiry data.
2. The donation becomes available to nearby volunteers.
3. Volunteers see live nearby drops, accept a job, and travel to the pickup point.
4. The donor shares a generated OTP for pickup verification.
5. The volunteer verifies pickup, distributes the food, and the system records the delivery.
6. Admins can view impact stats and top donors.

## Complete workflow

### 1) Donor flow

- Open the donor dashboard.
- Enter donation details such as food type, quantity, expiry time, and pickup location.
- Submit the donation.
- The backend stores the donation and generates an OTP.
- The donation is published to the volunteer alert stream.

### 2) Volunteer / Hero flow

- Open the volunteer dashboard.
- Enter the volunteer name in the login screen.
- Allow browser location access.
- The dashboard shows nearby donations and sorts them by distance.
- The volunteer accepts a donation job.
- The volunteer reaches the donor and verifies pickup with the OTP.
- The volunteer marks the delivery as distributed.
- The job moves into delivery history.

### 3) Admin flow

- Open the admin dashboard.
- View total meals saved.
- Review top donors.
- Monitor active platform activity.

### 4) Real-time update flow

- A donation is posted.
- `DonationService` broadcasts the event to `/topic/nearby-donations`.
- `volunteer.html` listens through SockJS + STOMP.
- The volunteer feed refreshes automatically.
- Location updates recompute distances in the browser.

## Hero Logistics — delivery branch focus

This branch centers on the volunteer delivery experience:

- Nearby drops feed in `src/main/resources/static/volunteer.html`
- Live GPS distance updates using browser geolocation
- Haversine distance calculation in `LocationService.java`
- WebSocket configuration in `WebSocketConfig.java`
- Volunteer delivery history and job state transitions

## Features

- Donor donation posting with food details and location data
- Nearby donation discovery for volunteers
- Real-time alerts over WebSocket for active drops
- Volunteer delivery workflow:
  - Accept a nearby donation
  - Verify pickup using OTP
  - Mark the donation as distributed
- Delivery history for donors and volunteers
- Admin impact reporting and top-donor visibility
- Image upload stub for donation proofs
- H2 in-memory database for quick demo/setup

## Tech stack

- Java 17
- Spring Boot 3.2.4
- Spring Web
- Spring Data JPA
- Spring WebSocket
- H2 Database
- Lombok
- HTML, CSS, and vanilla JavaScript for the static UI

## Project structure

- `pom.xml` — Maven build file and dependencies
- `src/main/java/com/example/relieffeed/` — main application source code
  - `RelieffeedApplication.java` — Spring Boot entry point
  - `config/` — application configuration
  - `controller/` — REST API endpoints
  - `model/` — entities and enums
  - `repository/` — Spring Data repositories
  - `service/` — business logic, OTP generation, impact stats, and location handling
- `src/main/resources/` — runtime resources
  - `application.properties` — server, database, and app settings
  - `static/` — frontend pages served directly by Spring Boot
    - `index.html` — login and role selection page
    - `donor.html` — donor dashboard
    - `volunteer.html` — Hero delivery dashboard
    - `admin.html` — admin dashboard
- `target/` — generated build output after Maven compilation

## Core backend components

### `DonationService`

- Creates new donations
- Generates OTPs
- Marks donations as claimed, picked up, and distributed
- Expires outdated donations automatically on a schedule
- Broadcasts new donations to the WebSocket topic

### `LocationService`

- Finds nearby available donations
- Computes great-circle distance with the Haversine formula
- Supports location-aware feed sorting

### `OTPService`

- Generates secure 6-digit OTPs for pickup verification

### `ImpactService`

- Calculates meals saved from donated quantity
- Builds top donor summaries

### `WebSocketConfig`

- Enables STOMP over SockJS
- Exposes `/ws`
- Publishes to `/topic`
- Supports user destinations via `/user`

## Pages in the UI

- `index.html` — landing/login page with role selection
- `donor.html` — donor posting and donation history
- `volunteer.html` — Hero delivery dashboard with nearby drops and GPS distance
- `admin.html` — admin dashboard and platform insights

## API endpoints

Base path: `/api/donations`

### Donation management

- `POST /api/donations` — create a donation
- `GET /api/donations/all` — list all donations
- `GET /api/donations/active` — list active donations for volunteers

### Volunteer workflow

- `GET /api/donations/nearby?lat={lat}&lng={lng}` — find nearby donations
- `POST /api/donations/{id}/claim?volunteerName={name}` — claim a donation
- `POST /api/donations/{id}/pickup?otp={otp}` — verify pickup OTP
- `POST /api/donations/{id}/distribute` — mark donation distributed
- `GET /api/donations/volunteer/{volunteerName}` — volunteer delivery history

### Donor and admin views

- `GET /api/donations/donor/{donorName}` — donor history
- `GET /api/donations/impact` — total meals saved
- `GET /api/donations/impact/top-donors` — top donors

### Image upload

- `POST /api/donations/image` — upload proof image stub

## WebSocket channel

The app exposes a SockJS endpoint at `/ws` and publishes alerts on:

- `/topic/nearby-donations`

This is used to refresh the volunteer feed when new donation opportunities are available.

## Configuration

Key settings are in `src/main/resources/application.properties`:

- Server port: `8081`
- H2 in-memory database URL
- Search radius default: `5.0 km`
- Hibernate schema auto-update enabled
- H2 console enabled at `/h2-console`

## Prerequisites

- Java 17 or later
- Maven 3.8+
- A modern browser with geolocation support

## Run the project

```bash
mvn spring-boot:run
```

The app runs on:

- `http://localhost:8081`

## Useful URLs

- Landing page: `http://localhost:8081/index.html`
- Donor dashboard: `http://localhost:8081/donor.html`
- Volunteer dashboard: `http://localhost:8081/volunteer.html`
- Admin dashboard: `http://localhost:8081/admin.html`
- H2 console: `http://localhost:8081/h2-console`

## Data flow summary

Donation lifecycle states:

`AVAILABLE → CLAIMED → PICKED_UP → DISTRIBUTED`

If a donation expires before it is claimed, the scheduled cleanup marks it as `EXPIRED`.

## Notes

- The app is designed for demo and hackathon-style deployment.
- Browser geolocation may require permission to show accurate distances in the volunteer dashboard.
- The H2 console is enabled for development convenience.
- The `arnav` branch contains the Hero Logistics delivery flow updates and related real-time support improvements.
