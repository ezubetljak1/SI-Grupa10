# Architecture / Technical Overview

---

## 8.1 Deployment arhitektura

DocFlow je kontejnerizovana web aplikacija koja se pokreće kao Docker Compose stack na jednom DigitalOcean Dropletu. Korisnik pristupa aplikaciji isključivo putem HTTPS-a.

**Tok zahtjeva kroz sistem:**

1. Browser → HTTPS → host-level reverse proxy (Nginx / Caddy - dopuniti sa servera)
2. Reverse proxy prosljeđuje promet na dva interna endpointa:
   - `docflow.page` → frontend Nginx kontejner (port 80)
   - `auth.docflow.page` → Keycloak kontejner (port 8080)
3. Frontend Nginx kontejner servira Angular SPA i prosljeđuje `/api/*` zahtjeve backend kontejneru
4. Backend komunicira sa: aplikacijskom PostgreSQL bazom, Keycloakom (JWT validacija i admin operacije), Google Document AI (OCR / klasifikacija), SMTP providerom i lokalnim Docker volumenom za fajlove
5. Keycloak ima zasebnu PostgreSQL bazu i importuje realm pri pokretanju

### Docker Compose servisi

| Servis | Image / Build | Namjena | Trajni podaci |
|---|---|---|---|
| `docflow-db` | `postgres:16` | Aplikacijska PostgreSQL baza | `docflow_postgres_data` |
| `docflow-keycloak-db` | `postgres:16` | Posebna PostgreSQL baza za Keycloak | `docflow_keycloak_postgres_data` |
| `docflow-keycloak` | `quay.io/keycloak/keycloak:26.6.1` | Identity provider, realm import pri startu | Keycloak konfiguracija |
| `docflow-backend` | `build: ./backend` | Spring Boot REST API i poslovna logika | `docflow_uploads` |
| `docflow-frontend` | `build: ./frontend` | Angular SPA serviran kroz Nginx | - (bez trajnih podataka) |

### Tehnologije i verzije

| Tehnologija | Verzija | Namjena |
|---|---|---|
| Java | 17 | Backend build i runtime |
| Spring Boot | 3.5.x | Backend framework |
| Node.js | 22 | Frontend build i CI |
| Angular | 21.2.x | Frontend SPA |
| PostgreSQL | 16 | Obje baze (aplikacijska + Keycloak) |
| Keycloak | 26.6.1 | Autentifikacija i upravljanje identitetima |
| Nginx | Alpine | SPA serving + `/api` proxy (unutar frontend kontejnera) |
| Docker Compose | 29.3.0 | Orkestracija kontejnera |

---

## 8.2 Frontend arhitektura

Frontend je Angular SPA aplikacija poslužena kroz Nginx. Sva komunikacija sa backendom odvija se putem relativne `/api` putanje, koju Nginx prosljeđuje backend kontejneru - frontend nema direktan pristup backend URL-u.

### Ključne rute

| Ruta | Pristup / Uloge |
|---|---|
| `/register-company` | Javno |
| `/auth/callback` | Javno (Keycloak callback) |
| `/dashboard` | ADMIN, MANAGER |
| `/documents/upload` | ADMIN, OPERATOR |
| `/documents` | Svi prijavljeni korisnici |
| `/documents/:id` | Svi prijavljeni korisnici |
| `/tasks/my` | ADMIN, OPERATOR, APPROVER |
| `/review` | Prema finalnoj implementaciji |
| `/company/users` | ADMIN |
| `/profile` | Svi prijavljeni korisnici |

### Sigurnost na frontendu

- `authGuard` - sprječava pristup zaštićenim rutama bez aktivne Keycloak sesije
- `roleGuard` - ograničava navigaciju prema ulozi korisnika
- Guardovi poboljšavaju UX, ali **ne zamjenjuju** backend autorizaciju
- Backend ostaje jedini autoritativan izvor sigurnosnih provjera

---

## 8.3 Backend arhitektura

Backend je Spring Boot modularni monolit sa slojevitom organizacijom unutar jednog deployable artifakta. Nije mikroservisna arhitektura - sve domenske cjeline dijele isti Spring context i istu bazu.

### Slojevita organizacija

| Sloj / Paket | Namjena |
|---|---|
| `controller` | REST endpointi - prima HTTP zahtjeve, delegira servisnom sloju |
| `service` | Poslovna logika i domenske operacije |
| `dao` | Pristup bazi (repozitoriji) |
| `entity` | JPA modeli - mapiranje na bazu |
| `dto` | Request i response modeli za API |
| `mapper` | Mapiranje između entiteta i DTO objekata |
| `security` | Autentifikacija i autorizacija (JWT filteri, role provjere) |
| `config` | Konfiguracija aplikacije (CORS, security chain, beans) |
| `exception / response` | Standardizovana obrada grešaka i API odgovori |

---

## 8.4 Funkcionalni backend moduli

| Modul | Odgovornost |
|---|---|
| Company | Registracija kompanije, kreiranje i povezivanje korisnika |
| User i Role | Upravljanje korisnicima, ulogama i pravima pristupa |
| Document | Upload, metapodaci, lifecycle statusi, lista, filteri, pretraga i brisanje |
| Storage | Čuvanje uploadovanih fajlova i XML izlaza na Docker volumenu |
| OCR | Integracija sa Google Document AI procesorima (classifier + parseri) |
| Extraction | OCR polja, placeholder polja, low-confidence pravila, validacija |
| Workflow i Task | Statusni prelazi, task assignment, rokovi, izvršavanje zadataka |
| Approval | Approve, reject i return-for-correction tok uz komentare |
| Audit i Comment | Revizijski trag aktivnosti i komentari uz dokument |
| Notification | In-app obavijesti i email podsjetnici (SMTP) |
| Dashboard | Agregirani pregled stanja sistema po ulozi |
| XML Output | Generisanje, čuvanje, preview i download XML-a za odobrene dokumente |

---

## 8.5 Model podataka

Aplikacija koristi jednu PostgreSQL bazu (`docflow-db`). `Hibernate ddl-auto=update` upravlja šemom; tokom razvoja bile su potrebne ručne SQL korekcije check constrainta za nove tipove i statuse.

### Ključni entiteti

| Entitet | Namjena |
|---|---|
| `Company` | Kompanija koja koristi sistem; osnovna jedinica izolacije podataka |
| `User` | Korisnik kompanije; vezan za Keycloak account |
| `Role` | Uloga korisnika: ADMIN, MANAGER, OPERATOR, APPROVER |
| `Document` | Uploadovani dokument, metapodaci, tip i trenutni status |
| `Extraction` | Rezultat OCR obrade za dokument |
| `ExtractionField` | Pojedinačno izdvojeno polje iz OCR rezultata |
| `Task` | Workflow zadatak (EXTRACTION, CORRECTION, APPROVAL) |
| `Comment` | Komentar uz dokument (pri rejection ili correction) |
| `StatusHistory` | Historija svih promjena statusa dokumenta |
| `AuditLog` | Revizijski trag značajnih akcija u sistemu |
| `Notification` | In-app obavijest korisniku |
| `XmlOutput` | Generisani XML izlaz za odobreni dokument |

### Statusni lifecycle dokumenta

| Status | Opis |
|---|---|
| `UPLOADED` | Dokument je uploadovan, čeka pokretanje OCR-a |
| `PROCESSING_FAILED` | OCR obrada nije uspjela |
| `EXTRACTED` | OCR polja su uspješno izvučena |
| `UNDER_REVIEW` | Operator pregledava i koriguje polja |
| `NEEDS_CLASSIFICATION_REVIEW` | Tip dokumenta nije potvrđen, čeka ručnu klasifikaciju |
| `READY_FOR_APPROVAL` | Ekstrakcija potvrđena, čeka approval |
| `NEEDS_CORRECTION` | Approver vratio na korekciju |
| `APPROVED` | Dokument odobren |
| `REJECTED` | Dokument odbijen |
| `COMPLETED` | XML generisan i finalizovan |

### Tipovi dokumenata

`INVOICE`, `RECEIPT`, `BANK_STATEMENT`, `FORM`, `OTHER`



---

## 8.6 Vanjski servisi i integracije

| Servis | Uloga | Napomene |
|---|---|---|
| Keycloak 26.6.1 | Autentifikacija i upravljanje identitetima | Realm, frontend client, backend admin client, JWT, role mapping, password setup flow |
| Google Document AI | OCR i automatska klasifikacija dokumenata | Classifier + invoice/receipt/bank statement/form parseri; fallback na ručni review |
| SMTP provider | Email poruke i podsjetnici | Konfigurabilno: host, port, korisnik, password, sender, reminder cron |
| Docker Volume (filesystem) | Trajni storage za uploadovane fajlove i XML izlaze | Lokalni filesystem - ograničava skaliranje na više instanci (poznato ograničenje) |

### Keycloak konfiguracija - pregled

- Realm: `docflow` (importovan pri startu iz JSON fajla)
- Frontend client - public client, redirect na `docflow.page`
- Backend admin client - service account za admin operacije
- JWT validacija na backendu putem issuer URI i JWK URI
- Role mapiranje: Keycloak role → Spring Security authorities
- Password setup: novi korisnici dobijaju email za postavljanje passworda

---

## 8.7 Ključni tokovi kroz arhitekturu

| Tok | Sažetak |
|---|---|
| **Login** | Browser → Angular frontend → Keycloak redirect → JWT token → backend JWT validacija → autorizovani API pristup |
| **Upload i OCR** | Operator uploaduje fajl → backend validacija (tip, veličina) → Docker volume storage → Document zapis u bazi → ručno pokretanje OCR-a → Google Document AI → Extraction polja → pregled u UI |
| **Workflow i approval** | Potvrđena ekstrakcija → `READY_FOR_APPROVAL` → APPROVAL task → approve / reject / return-for-correction → komentar, StatusHistory, AuditLog i Notification |
| **XML generisanje** | `APPROVED` dokument → validacija → XML generisanje → Docker volume storage → preview / download u UI → `COMPLETED` status |
| **Deployment** | GitHub Actions (`cd.yml`) → SSH na DigitalOcean Droplet → `deploy.sh` → Docker Compose build i up → health check → smoke provjera javnih URL-ova |

---

## 8.8 Sigurnosne odluke

| Odluka / Mjera | Opis |
|---|---|
| Keycloak autentifikacija | Sva autentifikacija delegirana Keycloaku - aplikacija ne čuva passworde |
| JWT validacija na backendu | Svaki API zahtjev validira JWT token (issuer + potpis + expiry) |
| Role-based autorizacija | Backend provjerava ulogu za svaki endpoint; frontend guardovi su samo UX sloj |
| Company isolation | Korisnici mogu pristupati samo podacima svoje kompanije; backend enforcea na servisnom nivou |
| Secrets izvan repozitorija | Produkcijski `.env` i Google service account JSON nisu commitovani; repo sadrži samo `.env.example` |
| Read-only credential mount | Google JSON ključ mountovan kao read-only u backend kontejner |
| HTTPS i reverse proxy | Sav javni promet ide kroz TLS; kontejneri su vezani na loopback portove |
| Upload validacija | Backend provjerava tip fajla (PDF, JPG, JPEG, PNG) i veličinu (default max 10 MB) |
| AuditLog | Značajne akcije (upload, status promjena, approval, brisanje) bilježe se u AuditLog |
| Swagger u produkciji | Pristup Swagger UI-ju treba ograničiti ili isključiti u produkcijskom okruženju |

---

## Dodatak A - Kategorije environment varijabli

> Sve vrijednosti trebaju biti postavljene u `.env` fajlu koji se nikada ne commituje.

| Kategorija | Varijable / Sadržaj |
|---|---|
| Aplikacijska baza | `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, datasource URL, `Hibernate ddl-auto` |
| Keycloak baza | `KEYCLOAK_DB`, `KEYCLOAK_DB_USER`, `KEYCLOAK_DB_PASSWORD` |
| Keycloak | Admin korisnik/password, hostname, realm, issuer URI, JWK URI, frontend i backend client ID, redirect URI |
| Google Document AI | Project ID, lokacija (`eu`), endpoint, service account JSON putanja, classifier i parser processor ID-jevi |
| SMTP i podsjetnici | SMTP host, port, korisnik, password, sender, `reminder.enabled`, prag i cron izraz |
| Upload / Storage | Upload direktorij, `max-file-size`, Nginx `CLIENT_MAX_BODY_SIZE` |


---

## Dodatak B - CI/CD pregled

| Pipeline | Detalji |
|---|---|
| CI (`.github/workflows/ci.yml`) | Pokreće se na push i PR na `develop` granu + ručno (`workflow_dispatch`). Backend: Java 17, `./mvnw clean install`. Frontend: Node.js 22, `npm ci`, `npm run build`. |
| CD (`.github/workflows/cd.yml`) | Ručni trigger (`workflow_dispatch`) za produkcijski deploy. Koraci: SSH na Droplet → `deploy.sh` → Docker Compose build i up → health check → smoke provjera URL-ova. |
| GitHub Secrets | `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_PORT`, `DEPLOY_SSH_PRIVATE_KEY`, `DEPLOY_KNOWN_HOSTS` |
| Aplikacijski secrets | Ostaju na serveru u `.env` fajlu; ne unose se direktno u GitHub Secrets |

---

## Dodatak C - Arhitekturalna ograničenja

| Ograničenje | Opis i posljedica |
|---|---|
| Filesystem storage | Fajlovi se čuvaju na Docker volumenu jednog servera. Ograničava horizontalno skaliranje. |
| Single Droplet | Nema load balancera ni automatskog failovera. Single point of failure. |
| Keycloak `start-dev` | Compose koristi `start-dev --import-realm`. Za produkciju potrebno hardening podešavanje. |
| `Hibernate ddl-auto=update` | Šema baze se ažurira automatski. Flyway ili Liquibase preporučeni kao buduće unapređenje. |
| Vanjski OCR | Google Document AI može biti nedostupan ili imati cijenu. Sistem ima fallback na ručni review. |
| XML bez XSD-a | Generički format dovoljan za projekat; integracija sa vanjskim sistemima zahtijevala bi formalni XSD. |
| Swagger u produkciji | Treba ograničiti ili isključiti pristup Swagger UI-ju na produkciji. |

---
