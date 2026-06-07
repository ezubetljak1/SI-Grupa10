# Deployment procedura

## 1. Svrha dokumenta

Ovaj dokument opisuje postupak lokalnog pokretanja, testiranja i produkcijskog deploymenta sistema **Docflow**.

Cilj dokumenta je omogućiti članu tima ili drugoj tehnički osposobljenoj osobi da, bez dodatnih pitanja autorima sistema:

* pripremi lokalno razvojno okruženje;
* pokrene baze podataka i autentifikacijski servis;
* pokrene backend i frontend aplikaciju;
* izvrši automatizovane testove;
* razumije način upravljanja konfiguracijom i osjetljivim vrijednostima;
* ponovi produkcijski deployment;
* provjeri dostupnost sistema nakon deploymenta;
* identifikuje i riješi najčešće probleme;
* izvrši rollback u slučaju neuspješnog deploymenta.

Produkcijska verzija sistema dostupna je na adresi:

```text
https://docflow.page
```

Autentifikacijski servis dostupan je na adresi:

```text
https://auth.docflow.page
```

### Brzi pregled sadržaja

Za brži pregled dokumenta mogu se koristiti sljedeći linkovi:

| Potrebna informacija                                       | Sekcija                                                                                                |
| ---------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| Kratak opis aplikacije                                     | [2. Kratak opis sistema](#2-kratak-opis-sistema)                                                       |
| Pregled arhitekture i Docker servisa                       | [3. Arhitektura sistema](#3-arhitektura-sistema)                                                       |
| Korištene tehnologije                                      | [4. Tehnologije](#4-tehnologije)                                                                       |
| Potrebni alati i preduvjeti                                | [5. Potrebni alati](#5-potrebni-alati)                                                                 |
| Deployment fajlovi i server-side konfiguracija             | [6. Deployment fajlovi i server-side konfiguracija](#6-deployment-fajlovi-i-server-side-konfiguracija) |
| Sve potrebne environment varijable                         | [7. Environment varijable](#7-environment-varijable)                                                   |
| Lokalno pokretanje baze, Keycloaka, backend-a i frontend-a | [8. Lokalno pokretanje sistema](#8-lokalno-pokretanje-sistema)                                         |
| Migracije i seed podaci                                    | [9. Migracije i seed podaci](#9-migracije-i-seed-podaci)                                               |
| Pokretanje automatizovanih testova                         | [10. Pokretanje automatizovanih testova](#10-pokretanje-automatizovanih-testova)                       |
| Produkcijsko okruženje i javni deployment linkovi          | [11. Produkcijsko okruženje](#11-produkcijsko-okruženje)                                               |
| Automatski Continuous Deployment pipeline                  | [12. Continuous Deployment pipeline](#12-continuous-deployment-pipeline)                               |
| Način rada serverske `deploy.sh` skripte                   | [13. Serverska deployment skripta](#13-serverska-deployment-skripta)                                   |
| Javna provjera dostupnosti sistema                         | [14. Javna provjera nakon deploymenta](#14-javna-provjera-nakon-deploymenta)                           |
| Ručno pokretanje produkcijskog deploymenta                 | [15. Ručno pokretanje produkcijskog deploymenta](#15-ručno-pokretanje-produkcijskog-deploymenta)       |
| Ručna smoke provjera sistema nakon deploymenta             | [16. Ručna provjera nakon deploymenta](#16-ručna-provjera-nakon-deploymenta)                           |
| Rollback procedura                                         | [17. Rollback procedura](#17-rollback-procedura)                                                       |
| Poznata ograničenja                                        | [18. Poznata ograničenja deploymenta](#18-poznata-ograničenja-deploymenta)                             |
| Najčešći problemi i rješenja                               | [19. Najčešći problemi i rješenja](#19-najčešći-problemi-i-rješenja)                                   |
| Završna kontrolna lista                                    | [20. Završna kontrolna lista](#20-završna-kontrolna-lista)                                             |


---

# Dio I — Tehnička osnova i konfiguracija

U ovom dijelu opisana je tehnička struktura sistema, korištene tehnologije,
potrebni alati, deployment fajlovi i environment konfiguracija.

---

## 2. Kratak opis sistema

**Docflow** je web aplikacija za upravljanje dokumentima i njihovim poslovnim workflowom.

Sistem omogućava:

* upload i arhiviranje dokumenata;
* automatsku klasifikaciju dokumenata;
* OCR i AI ekstrakciju podataka iz dokumenata;
* pregled i korekciju ekstrahovanih polja;
* dodjelu workflow zadataka korisnicima;
* odobravanje, odbijanje i vraćanje dokumenata na korekciju;
* generisanje XML izlaza;
* pregled statusne historije i audit loga;
* in-app i email notifikacije;
* upravljanje korisnicima, ulogama i kompanijama.

Za OCR obradu i klasifikaciju koristi se vanjski servis **Google Document AI**. Za autentifikaciju i autorizaciju koristi se **Keycloak**.

---

## 3. Arhitektura sistema

### 3.1. Pregled arhitekture

Docflow koristi višeslojnu web arhitekturu.

```text
Korisnički browser
        │
        ▼
Javni HTTPS reverse proxy
        │
        ├──────────────► docflow.page
        │                    │
        │                    ▼
        │              Frontend Nginx
        │                    │
        │                    │ /api/
        │                    ▼
        │              Spring Boot backend
        │                    │
        │                    ├────────► PostgreSQL baza
        │                    ├────────► Google Document AI
        │                    ├────────► SMTP servis
        │                    └────────► Keycloak Admin API
        │
        └──────────────► auth.docflow.page
                             │
                             ▼
                         Keycloak
                             │
                             ▼
                      Keycloak PostgreSQL baza
```

Frontend i backend aplikacija komuniciraju preko REST API-ja.

Frontend Nginx prosljeđuje zahtjeve koji počinju sa:

```text
/api/
```

internom backend servisu:

```text
http://docflow-backend:8080/api/
```

Frontend, backend, baze podataka i Keycloak nalaze se u zajedničkoj Docker mreži:

```text
docflow-network
```

Komunikacija između kontejnera koristi interne Docker DNS nazive servisa. Backend kontejner se ne mora direktno izlagati javnoj mreži.

### 3.2. Docker Compose servisi

Kompletan sistem sastoji se od pet Docker Compose servisa.

| Servis                | Uloga                                                |
| --------------------- | ---------------------------------------------------- |
| `docflow-db`          | PostgreSQL baza za poslovne podatke aplikacije       |
| `docflow-backend`     | Spring Boot backend aplikacija i REST API            |
| `docflow-frontend`    | Angular frontend aplikacija servirana kroz Nginx     |
| `docflow-keycloak-db` | Odvojena PostgreSQL baza za autentifikacijski servis |
| `docflow-keycloak`    | Keycloak servis za autentifikaciju i autorizaciju    |

### 3.3. Trajno čuvanje podataka

Za trajno čuvanje podataka koriste se Docker named volume-i.

| Volume                           | Namjena                                      |
| -------------------------------- | -------------------------------------------- |
| `docflow_postgres_data`          | Trajno čuvanje poslovnih podataka aplikacije |
| `docflow_keycloak_postgres_data` | Trajno čuvanje Keycloak podataka             |
| `docflow_uploads`                | Trajno čuvanje uploadovanih dokumenata       |

Ponovno kreiranje aplikacijskih kontejnera ne briše baze podataka, korisničke račune niti prethodno uploadovane dokumente.

---

## 4. Tehnologije

| Sloj ili namjena               | Tehnologija                              | Verzija ili napomena                           |
| ------------------------------ | ---------------------------------------- | ---------------------------------------------- |
| Backend programski jezik       | Java                                     | `17`                                           |
| Backend framework              | Spring Boot                              | `3.5.13`                                       |
| Backend build alat             | Maven Wrapper                            | Uključen u repozitorij                         |
| ORM i pristup bazi             | Spring Data JPA i Hibernate              | Konfigurisani kroz Spring Boot                 |
| Backend sigurnost              | Spring Security i OAuth2 Resource Server | JWT validacija                                 |
| Frontend framework             | Angular                                  | `21.2.x`                                       |
| Frontend build runtime         | Node.js                                  | `22`                                           |
| Frontend package manager       | npm                                      | `11.10.1`                                      |
| Frontend unit testovi          | Vitest                                   | `4.x`                                          |
| Automatizovani UI testovi      | Playwright                               | `1.60.x`                                       |
| Poslovna baza podataka         | PostgreSQL                               | `16`                                           |
| Keycloak baza podataka         | PostgreSQL                               | `16`                                           |
| Autentifikacija i autorizacija | Keycloak                                 | `26.6.1`                                       |
| Serviranje frontend aplikacije | Nginx                                    | Alpine Docker image                            |
| Kontejnerizacija               | Docker                                   | Potreban na lokalnom i produkcijskom okruženju |
| Orkestracija kontejnera        | Docker Compose                           | Compose plugin                                 |
| CI/CD                          | GitHub Actions                           | Workflow fajlovi u `.github/workflows/`        |
| Cloud infrastruktura           | DigitalOcean Droplet                     | Ubuntu serversko okruženje                     |
| OCR i klasifikacija dokumenata | Google Document AI                       | Vanjski servis                                 |
| Slanje email obavijesti        | SMTP servis                              | Kredencijali se čuvaju izvan repozitorija      |

---

## 5. Potrebni alati

### 5.1. Preduvjeti za lokalno pokretanje

| Alat                             | Namjena                                  | Provjera instalacije     |
| -------------------------------- | ---------------------------------------- | ------------------------ |
| Git                              | Kloniranje i ažuriranje repozitorija     | `git --version`          |
| Docker Engine ili Docker Desktop | Pokretanje kontejnera                    | `docker --version`       |
| Docker Compose plugin            | Pokretanje servisa                       | `docker compose version` |
| Node.js `22`                     | Lokalno pokretanje frontend aplikacije   | `node --version`         |
| npm                              | Instalacija frontend zavisnosti          | `npm --version`          |
| Java JDK `17`                    | Lokalno pokretanje i testiranje backenda | `java --version`         |

Za standardno pokretanje kompletnog sistema dovoljan je Docker Compose.

Java i Node.js potrebni su za razvojni režim rada i lokalno izvršavanje testova.

### 5.2. Preduvjeti za produkcijsko okruženje

Produkcijski server mora imati:

| Preduvjet                         | Namjena                                    |
| --------------------------------- | ------------------------------------------ |
| Ubuntu serversko okruženje        | Host operativni sistem                     |
| Git                               | Povlačenje najnovije verzije aplikacije    |
| Docker Engine                     | Pokretanje Docker kontejnera               |
| Docker Compose plugin             | Pokretanje kompletnog sistema              |
| `curl`                            | Provjera dostupnosti servisa               |
| SSH pristup                       | Automatizovani i ručni deployment          |
| Javni DNS zapisi                  | Usmjeravanje domena prema serveru          |
| Reverse proxy i TLS konfiguracija | Javni HTTPS pristup aplikaciji i Keycloaku |

---

## 6. Deployment fajlovi i server-side konfiguracija

| Fajl                         | Lokacija                                  | Namjena                                                                                                |
| ---------------------------- | ----------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| CI workflow                  | `.github/workflows/ci.yml`                | Pokretanje backend testova, frontend unit testova, frontend builda i Playwright UI smoke testova       |
| CD workflow                  | `.github/workflows/cd.yml`                | Automatizovani deployment na DigitalOcean nakon uspješnog CI izvršavanja                               |
| Serverska deployment skripta | `Project/scripts/deploy.sh`               | Povlačenje koda, validacija konfiguracije, build image-a, pokretanje kontejnera i provjera dostupnosti |
| Docker Compose konfiguracija | `Project/docker-compose.yml`              | Definicija produkcijskih servisa, mreže i volume-a                                                     |
| Backend Dockerfile           | `Project/backend/Dockerfile`              | Build Spring Boot backend image-a                                                                      |
| Frontend Dockerfile          | `Project/frontend/Dockerfile`             | Build Angular aplikacije i Nginx image-a                                                               |
| Frontend Nginx konfiguracija | `Project/frontend/nginx.conf`             | Serviranje Angular aplikacije i proxy prema backend API-ju                                             |
| Primjer konfiguracije        | `Project/.env.example`                    | Predložak environment varijabli bez stvarnih osjetljivih vrijednosti                                   |
| Produkcijski `.env` fajl     | `Project/.env`                            | Stvarne produkcijske environment varijable; postoji samo na serveru                                    |
| Google Document AI JSON      | `Project/secrets/google-document-ai.json` | Service-account credentials; postoje samo na serveru                                                   |

Produkcijski `.env` fajl i Google Document AI JSON fajl nisu dio Git repozitorija.

---

## 7. Environment varijable

### 7.1. Pravila dokumentovanja osjetljivih vrijednosti

Produkcijski `.env` fajl sadrži javne konfiguracijske vrijednosti, interne identifikatore i osjetljive podatke.

| Kategorija                                  | Pravilo                                                |
| ------------------------------------------- | ------------------------------------------------------ |
| Javne URL vrijednosti                       | Mogu se navesti                                        |
| Nazivi baza i tehničkih korisnika           | Mogu se navesti                                        |
| Interne tehničke vrijednosti                | Navodi se namjena i informacija da su konfigurisane    |
| Lozinke                                     | Nikada se ne objavljuju                                |
| Privatni SSH ključevi                       | Nikada se ne objavljuju                                |
| SMTP kredencijali                           | Nikada se ne objavljuju                                |
| Google service-account JSON                 | Nikada se ne objavljuje niti prilaže                   |
| Google Document AI processor ID vrijednosti | Navodi se da postoje, bez objavljivanja identifikatora |
| Keycloak admin kredencijali                 | Nikada se ne objavljuju                                |
| Keycloak backend admin client secret        | Nikada se ne objavljuje                                |

### 7.2. Glavna baza podataka i JPA konfiguracija

| Varijabla                       | Dokumentovana vrijednost ili status        | Namjena                                                   |
| ------------------------------- | ------------------------------------------ | --------------------------------------------------------- |
| `POSTGRES_DB`                   | `docflow`                                  | Naziv glavne PostgreSQL baze                              |
| `POSTGRES_USER`                 | `docflow_user`                             | Tehnički korisnik glavne baze                             |
| `POSTGRES_PASSWORD`             | Konfigurisana; vrijednost se ne objavljuje | Lozinka tehničkog korisnika baze                          |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update`                                   | Automatsko usklađivanje šeme baze pri pokretanju backenda |
| `SPRING_JPA_SHOW_SQL`           | `false`                                    | Isključivanje ispisa SQL upita u produkcijskim logovima   |
| `DOCFLOW_MAX_FILE_SIZE`         | `10485760` bajtova                         | Maksimalna veličina uploadovanog dokumenta                |

### 7.3. Frontend i Nginx konfiguracija

| Varijabla                   | Dokumentovana vrijednost ili status | Namjena                                                                        |
| --------------------------- | ----------------------------------- | ------------------------------------------------------------------------------ |
| `CLIENT_MAX_BODY_SIZE`      | `20M`                               | Maksimalna veličina request body-ja koju prihvata frontend Nginx               |
| `DOCFLOW_FRONTEND_URL`      | `https://docflow.page`              | Javna frontend adresa korištena u integraciji sistema                          |
| `DOCFLOW_FRONTEND_BASE_URL` | `https://docflow.page`              | Bazna frontend adresa za generisanje linkova u aplikaciji i email obavijestima |
| `FRONTEND_PORT`             | `80`                                | Informativna frontend port konfiguracija                                       |

Napomena: frontend Nginx kontejner sluša port `80`, dok je na produkcijskom hostu mapiran na lokalni port `8082`.

### 7.4. Keycloak baza i autentifikacijski servis

| Varijabla                 | Dokumentovana vrijednost ili status        | Namjena                                |
| ------------------------- | ------------------------------------------ | -------------------------------------- |
| `KEYCLOAK_DB`             | `keycloak`                                 | Naziv Keycloak PostgreSQL baze         |
| `KEYCLOAK_DB_USER`        | `keycloak`                                 | Tehnički korisnik Keycloak baze        |
| `KEYCLOAK_DB_PASSWORD`    | Konfigurisana; vrijednost se ne objavljuje | Lozinka Keycloak baze                  |
| `KEYCLOAK_ADMIN_USERNAME` | Konfigurisan; vrijednost se ne objavljuje  | Bootstrap administratorski korisnik    |
| `KEYCLOAK_ADMIN_PASSWORD` | Konfigurisana; vrijednost se ne objavljuje | Bootstrap administratorska lozinka     |
| `KEYCLOAK_PORT`           | `8081`                                     | Lokalni host port Keycloak servisa     |
| `KEYCLOAK_HOSTNAME`       | `https://auth.docflow.page`                | Javna Keycloak adresa                  |
| `KEYCLOAK_HOSTNAME_ADMIN` | `https://auth.docflow.page`                | Javna administratorska Keycloak adresa |

### 7.5. Backend i Keycloak integracija

| Varijabla                                      | Dokumentovana vrijednost ili status              | Namjena                                                        |
| ---------------------------------------------- | ------------------------------------------------ | -------------------------------------------------------------- |
| `DOCFLOW_KEYCLOAK_ISSUER_URI`                  | Konfigurisana interna URL vrijednost             | Issuer URI za validaciju tokena                                |
| `DOCFLOW_KEYCLOAK_JWK_SET_URI`                 | Konfigurisana interna URL vrijednost             | URI za preuzimanje javnih ključeva potrebnih za JWT validaciju |
| `DOCFLOW_KEYCLOAK_REALM`                       | `docflow`                                        | Keycloak realm aplikacije                                      |
| `DOCFLOW_KEYCLOAK_SERVER_URL`                  | Konfigurisana interna URL vrijednost             | Keycloak server URL korišten u backend integraciji             |
| `DOCFLOW_KEYCLOAK_FRONTEND_CLIENT_ID`          | Konfigurisan                                     | Identifikator javnog frontend Keycloak klijenta                |
| `DOCFLOW_KEYCLOAK_FRONTEND_REDIRECT_URI`       | Konfigurisana frontend URL vrijednost            | Redirect URI nakon autentifikacije                             |
| `DOCFLOW_KEYCLOAK_BACKEND_ADMIN_CLIENT_ID`     | Konfigurisan; vrijednost se ne objavljuje        | Identifikator povjerljivog backend admin klijenta              |
| `DOCFLOW_KEYCLOAK_BACKEND_ADMIN_CLIENT_SECRET` | Konfigurisan; vrijednost se nikada ne objavljuje | Secret povjerljivog backend admin klijenta                     |

### 7.6. Google Document AI konfiguracija

| Varijabla                                        | Dokumentovana vrijednost ili status         | Namjena                                                 |
| ------------------------------------------------ | ------------------------------------------- | ------------------------------------------------------- |
| `GOOGLE_CLOUD_PROJECT_ID`                        | Konfigurisan interni identifikator projekta | Google Cloud projekat u kojem se nalaze procesori       |
| `GOOGLE_DOCUMENT_AI_LOCATION`                    | Konfigurisana regionalna vrijednost         | Regija Google Document AI procesora                     |
| `GOOGLE_DOCUMENT_AI_ENDPOINT`                    | Konfigurisan regionalni endpoint            | Endpoint za komunikaciju sa Google Document AI servisom |
| `GOOGLE_DOCUMENT_AI_CLASSIFIER_PROCESSOR_ID`     | Konfigurisan; vrijednost se ne objavljuje   | Procesor za automatsku klasifikaciju                    |
| `GOOGLE_DOCUMENT_AI_INVOICE_PROCESSOR_ID`        | Konfigurisan; vrijednost se ne objavljuje   | Procesor za fakture                                     |
| `GOOGLE_DOCUMENT_AI_RECEIPT_PROCESSOR_ID`        | Konfigurisan; vrijednost se ne objavljuje   | Procesor za račune i troškove                           |
| `GOOGLE_DOCUMENT_AI_BANK_STATEMENT_PROCESSOR_ID` | Konfigurisan; vrijednost se ne objavljuje   | Procesor za bankovne izvode                             |
| `GOOGLE_DOCUMENT_AI_FORM_PROCESSOR_ID`           | Konfigurisan; vrijednost se ne objavljuje   | Procesor za obrasce                                     |

Google service-account JSON fajl montira se u backend kontejner kao read-only fajl.

Interna putanja unutar backend kontejnera je:

```text
/app/secrets/google-document-ai.json
```

### 7.7. SMTP i email reminder konfiguracija

| Varijabla                            | Dokumentovana vrijednost ili status               | Namjena                                                                 |
| ------------------------------------ | ------------------------------------------------- | ----------------------------------------------------------------------- |
| `DOCFLOW_SMTP_HOST`                  | Konfigurisan; vrijednost se ne objavljuje         | SMTP server                                                             |
| `DOCFLOW_SMTP_PORT`                  | Konfigurisan                                      | SMTP port                                                               |
| `DOCFLOW_SMTP_USERNAME`              | Konfigurisano; vrijednost se ne objavljuje        | SMTP korisničko ime                                                     |
| `DOCFLOW_SMTP_PASSWORD`              | Konfigurisano; vrijednost se nikada ne objavljuje | SMTP lozinka                                                            |
| `DOCFLOW_MAIL_FROM_NAME`             | Konfigurisano                                     | Prikazano ime pošiljaoca                                                |
| `DOCFLOW_MAIL_FROM`                  | Konfigurisano                                     | Email adresa pošiljaoca                                                 |
| `DOCFLOW_EMAIL_REMINDER_ENABLED`     | `true`                                            | Uključivanje email reminder funkcionalnosti                             |
| `DOCFLOW_EMAIL_REMINDER_AFTER_HOURS` | `4`                                               | Minimalna starost nepročitane notifikacije prije slanja reminder emaila |
| `DOCFLOW_EMAIL_REMINDER_CRON`        | `0 0 * * * *`                                     | Provjera nepročitanih notifikacija na početku svakog sata               |

### 7.8. Varijable koje Docker Compose postavlja interno

Sljedeće vrijednosti Docker Compose prosljeđuje kontejnerima automatski. Nije ih potrebno ručno unositi u produkcijski `.env` fajl.

| Varijabla                        | Namjena                                                        |
| -------------------------------- | -------------------------------------------------------------- |
| `SERVER_PORT`                    | Backend port unutar kontejnera                                 |
| `SPRING_DATASOURCE_URL`          | JDBC URL glavne baze                                           |
| `SPRING_DATASOURCE_USERNAME`     | Korisnik glavne baze                                           |
| `SPRING_DATASOURCE_PASSWORD`     | Lozinka glavne baze                                            |
| `DOCFLOW_UPLOAD_DIR`             | Putanja za uploadovane dokumente unutar backend kontejnera     |
| `GOOGLE_APPLICATION_CREDENTIALS` | Putanja do Google service-account JSON fajla unutar kontejnera |

---

# Dio II — Lokalno pokretanje i testiranje

U ovom dijelu navedeni su koraci za lokalno pokretanje sistema, rad sa bazama
podataka, inicijalnu konfiguraciju i izvršavanje automatizovanih testova.

---

## 8. Lokalno pokretanje sistema

### 8.1. Kloniranje repozitorija

```bash
git clone https://github.com/ezubetljak1/SI-Grupa10.git
cd SI-Grupa10/Project
```

### 8.2. Priprema lokalnog `.env` fajla

Kopirati primjer konfiguracije:

```bash
cp .env.example .env
```

Na Windows sistemu može se koristiti:

```powershell
Copy-Item .env.example .env
```

Prije pokretanja sistema potrebno je unijeti lokalne vrijednosti.

Za lokalno Docker Compose pokretanje preporučene su sljedeće URL vrijednosti:

```env
KEYCLOAK_HOSTNAME=http://localhost:8081
KEYCLOAK_HOSTNAME_ADMIN=http://localhost:8081

DOCFLOW_FRONTEND_URL=http://localhost:8082
DOCFLOW_FRONTEND_BASE_URL=http://localhost:8082

DOCFLOW_KEYCLOAK_ISSUER_URI=http://localhost:8081/realms/docflow
DOCFLOW_KEYCLOAK_JWK_SET_URI=http://docflow-keycloak:8080/realms/docflow/protocol/openid-connect/certs
DOCFLOW_KEYCLOAK_REALM=docflow
DOCFLOW_KEYCLOAK_SERVER_URL=http://docflow-keycloak:8080
DOCFLOW_KEYCLOAK_FRONTEND_CLIENT_ID=docflow-frontend
DOCFLOW_KEYCLOAK_FRONTEND_REDIRECT_URI=http://localhost:8082/documents
```

Za backend administrativni Keycloak klijent potrebno je unijeti lokalno konfigurisane vrijednosti:

```env
DOCFLOW_KEYCLOAK_BACKEND_ADMIN_CLIENT_ID=docflow-backend-admin
DOCFLOW_KEYCLOAK_BACKEND_ADMIN_CLIENT_SECRET=<lokalna-vrijednost>
```

Stvarni `.env` fajl ne smije se commitovati u Git repozitorij.

### 8.3. Google Document AI credentials

Za funkcionalnosti koje koriste OCR i klasifikaciju dokumenata potrebno je kreirati fajl:

```text
Project/secrets/google-document-ai.json
```

Sadržaj Google service-account JSON fajla ne smije se commitovati niti javno dijeliti.

Ako se lokalno provjeravaju samo funkcionalnosti koje ne koriste OCR, moguće je pokrenuti sistem bez stvarnih Google Document AI zahtjeva. Za kompletno funkcionalno testiranje OCR toka kredencijali i processor ID vrijednosti moraju biti ispravno konfigurisani.

### 8.4. Pokretanje baza podataka

Iz direktorija `Project` pokrenuti:

```bash
docker compose up -d docflow-db docflow-keycloak-db
```

Provjeriti stanje:

```bash
docker compose ps
```

### 8.5. Pokretanje Keycloak servisa

```bash
docker compose up -d docflow-keycloak
```

Keycloak je lokalno dostupan na adresi:

```text
http://localhost:8081
```

### 8.6. Pokretanje backend aplikacije

Pokrenuti backend kontejner:

```bash
docker compose up -d --build docflow-backend
```

Provjeriti stanje:

```bash
docker compose ps
```

Pregled posljednjih backend logova:

```bash
docker compose logs --tail=120 docflow-backend
```

### 8.7. Pokretanje frontend aplikacije kroz Docker Compose

Pokrenuti frontend kontejner:

```bash
docker compose up -d --build docflow-frontend
```

Frontend je dostupan na adresi:

```text
http://localhost:8082
```

### 8.8. Pokretanje kompletnog sistema jednom komandom

Nakon pripreme `.env` fajla kompletan sistem može se pokrenuti komandom:

```bash
docker compose up -d --build
```

Za zaustavljanje sistema bez brisanja trajnih podataka koristi se:

```bash
docker compose down
```

Ne koristiti opciju:

```text
-v
```

osim kada je namjerno potrebno obrisati lokalne baze i volume-e.

### 8.9. Lokalno pokretanje frontend aplikacije u razvojnom režimu

Frontend se može pokrenuti i izvan Docker kontejnera.

Iz direktorija:

```text
Project/frontend
```

pokrenuti:

```bash
npm ci
npm start
```

Frontend je tada dostupan na adresi:

```text
http://localhost:4200
```

Angular razvojni proxy prosljeđuje `/api` zahtjeve na:

```text
http://localhost:8080
```

Ako se frontend pokreće na portu `4200`, lokalne Keycloak redirect vrijednosti trebaju koristiti:

```env
DOCFLOW_FRONTEND_URL=http://localhost:4200
DOCFLOW_FRONTEND_BASE_URL=http://localhost:4200
DOCFLOW_KEYCLOAK_FRONTEND_REDIRECT_URI=http://localhost:4200/documents
```

---

## 9. Migracije i seed podaci

Za isporučenu verziju sistema nije potrebno ručno izvršavati dodatne SQL migracije niti seed skripte.

Sve korekcije šeme baze koje su bile potrebne tokom razvoja već su primijenjene i uzete u obzir u trenutnom stanju sistema.

Backend koristi:

```env
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Hibernate pri pokretanju backend aplikacije kreira ili usklađuje kompatibilne dijelove šeme baze.

Za čisto lokalno okruženje tabele glavne aplikacijske baze kreiraju se pri prvom pokretanju backend aplikacije.

Keycloak servis koristi opciju:

```text
--import-realm
```

i pri inicijalnom pokretanju učitava početnu realm konfiguraciju iz direktorija:

```text
Project/keycloak/import
```

Za glavnu aplikacijsku bazu nije uvedeno automatsko kreiranje demo poslovnih podataka. Početni poslovni podaci unose se kroz aplikacijske funkcionalnosti.

---

## 10. Pokretanje automatizovanih testova

### 10.1. Backend testovi

Na Linux ili macOS sistemu:

```bash
cd Project/backend
./mvnw clean verify
```

Na Windows sistemu:

```powershell
cd Project/backend
.\mvnw.cmd clean verify
```

Backend testovi obuhvataju unit i integration provjere poslovne logike.

### 10.2. Frontend unit testovi

Iz direktorija:

```text
Project/frontend
```

pokrenuti:

```bash
npm ci
npm run test:ci
```

### 10.3. Frontend production build

```bash
npm run build
```

### 10.4. Playwright UI smoke testovi

Pri prvom lokalnom pokretanju instalirati Chromium:

```bash
npx playwright install chromium
```

Zatim pokrenuti:

```bash
npm run e2e
```

Playwright HTML report može se otvoriti komandom:

```bash
npm run e2e:report
```

### 10.5. GitHub Actions CI

Isti testovi automatski se izvršavaju kroz:

```text
.github/workflows/ci.yml
```

CI pipeline pokreće:

1. backend build i backend testove;
2. generisanje backend test summaryja;
3. generisanje Surefire HTML izvještaja;
4. frontend unit testove;
5. frontend production build;
6. Playwright UI smoke testove;
7. čuvanje backend i Playwright reporta kao GitHub Actions artifacts.

Pored automatskog pokretanja prilikom odgovarajućih `pull_request` i `push` događaja, CI workflow se može pokrenuti i ručno korištenjem opcije `workflow_dispatch`.

Koraci za ručno pokretanje:

```text
GitHub repozitorij
→ Actions
→ Docflow CI
→ Run workflow
→ Odabrati željenu granu
→ Run workflow
```

Ručno pokretanje je korisno kada je potrebno ponoviti provjeru builda i testova bez kreiranja novog commita ili pull requesta.

---

# Dio III — Produkcijski deployment

U ovom dijelu opisani su produkcijsko okruženje, Continuous Deployment pipeline,
serverska deployment skripta i način provjere uspješnosti deploymenta.

---

## 11. Produkcijsko okruženje

| Stavka                             | Vrijednost                                             |
| ---------------------------------- | ------------------------------------------------------ |
| Cloud platforma                    | DigitalOcean                                           |
| Tip okruženja                      | Produkcijski Ubuntu server                             |
| Deployment direktorij              | `/opt/docflow`                                         |
| Deployment grana                   | `main`                                                 |
| Docker Compose direktorij          | `/opt/docflow/Project`                                 |
| Javno dostupna frontend aplikacija | `https://docflow.page`                                 |
| Javno dostupan Keycloak servis     | `https://auth.docflow.page`                            |
| GitHub Actions environment         | `production`                                           |
| Produkcijski `.env` fajl           | `/opt/docflow/Project/.env`                            |
| Google credentials fajl            | `/opt/docflow/Project/secrets/google-document-ai.json` |

### 11.1. Reverse proxy i HTTPS

Na produkcijskom serveru javni HTTPS saobraćaj za domene:

```text
docflow.page
auth.docflow.page
```

prosljeđuje se na lokalno izložene portove Docker servisa.

| Javni servis | Lokalni host port | Port unutar kontejnera |
| ------------ | ----------------: | ---------------------: |
| Frontend     |            `8082` |                   `80` |
| Keycloak     |            `8081` |                 `8080` |

Reverse proxy i TLS konfiguracija predstavljaju jednokratni preduvjet produkcijskog servera i ne kreiraju se ponovo pri svakom deploymentu.

---

## 12. Continuous Deployment pipeline

### 12.1. Način pokretanja

CD pipeline implementiran je kroz GitHub Actions workflow:

```text
.github/workflows/cd.yml
```

Pipeline se pokreće:

1. automatski nakon uspješnog izvršavanja workflowa `Docflow CI` za `push` događaj na grani `main`;
2. ručno korištenjem opcije `workflow_dispatch`.

Automatski tok deploymenta:

```text
Merge pull requesta u main
        ↓
Push događaj na main grani
        ↓
Pokretanje Docflow CI workflowa
        ↓
Backend testovi
        ↓
Frontend unit testovi i production build
        ↓
Playwright UI smoke testovi
        ↓
Uspješan CI rezultat
        ↓
Pokretanje Docflow CD workflowa
        ↓
SSH povezivanje sa DigitalOcean serverom
        ↓
Ažuriranje server-side repozitorija
        ↓
Pokretanje deploy.sh skripte
        ↓
Build i pokretanje Docker kontejnera
        ↓
Lokalne i javne provjere dostupnosti
```

Ako CI provjera ne prođe uspješno, automatski deployment se ne pokreće.

### 12.2. GitHub Actions deployment secrets

Stvarne vrijednosti secrets varijabli ne navode se u dokumentaciji.

| Secret                   | Namjena                                                             |
| ------------------------ | ------------------------------------------------------------------- |
| `DEPLOY_HOST`            | Adresa produkcijskog servera                                        |
| `DEPLOY_USER`            | SSH korisnik koji se koristi za deployment                          |
| `DEPLOY_PORT`            | SSH port produkcijskog servera                                      |
| `DEPLOY_SSH_PRIVATE_KEY` | Privatni SSH ključ namijenjen isključivo GitHub Actions deploymentu |
| `DEPLOY_KNOWN_HOSTS`     | Provjereni SSH host key produkcijskog servera                       |

Pored secrets vrijednosti koristi se sljedeća GitHub Actions environment varijabla:

| Varijabla         | Namjena                                                     |
| ----------------- | ----------------------------------------------------------- |
| `DEPLOY_REPO_DIR` | Putanja do kloniranog repozitorija na produkcijskom serveru |

Secrets i environment varijabla nalaze se unutar GitHub Actions environmenta:

```text
production
```

### 12.3. SSH pristup

GitHub Actions runner:

1. kreira lokalni `.ssh` direktorij;
2. upisuje deployment privatni ključ;
3. upisuje provjereni `known_hosts` zapis;
4. postavlja dozvole pristupa;
5. povezuje se sa produkcijskim serverom.

Za automatizovani deployment koristi se poseban SSH ključ odvojen od ključa za ručni administratorski pristup.

### 12.4. Ažuriranje repozitorija na serveru

Na serveru se izvršavaju naredbe:

```bash
git fetch --prune origin
git checkout main
git reset --hard origin/main
```

Time se osigurava da deployment koristi trenutno stanje grane:

```text
main
```

---

## 13. Serverska deployment skripta

Serverska Bash skripta nalazi se na lokaciji:

```text
Project/scripts/deploy.sh
```

### 13.1. Validacija server-side fajlova

Skripta provjerava da postoje:

```text
Project/.env
Project/secrets/google-document-ai.json
```

Ako obavezni fajl nedostaje, deployment se prekida.

### 13.2. Validacija Docker Compose konfiguracije

Skripta izvršava:

```bash
docker compose config -q
```

Time se provjerava sintaksna ispravnost Docker Compose konfiguracije i dostupnost obaveznih environment varijabli.

### 13.3. Build backend i frontend image-a

Skripta izvršava:

```bash
docker compose build --pull docflow-backend docflow-frontend
```

Opcija:

```text
--pull
```

omogućava preuzimanje novijih verzija baznih image-a prije builda.

Backend Dockerfile:

1. koristi Java JDK `17` tokom build faze;
2. pokreće Maven build;
3. kreira izvršni `.jar` fajl;
4. koristi Java JRE `17` u runtime image-u.

Frontend Dockerfile:

1. koristi Node.js `22` tokom build faze;
2. instalira zavisnosti kroz `npm ci`;
3. izvršava Angular production build;
4. kopira buildane fajlove u Nginx image.

### 13.4. Pokretanje kontejnera

Nakon uspješnog builda skripta izvršava:

```bash
docker compose up -d --remove-orphans
```

Komanda:

* kreira nove kontejnere kada je potrebno;
* pokreće ažurirane backend i frontend image-e;
* zadržava postojeće Docker volume-e;
* uklanja kontejnere koji više nisu definisani u Compose konfiguraciji;
* izvršava deployment u pozadini.

### 13.5. Lokalna provjera dostupnosti

Nakon podizanja kontejnera skripta periodično provjerava:

```text
http://127.0.0.1:8082/
http://127.0.0.1:8081/realms/docflow/.well-known/openid-configuration
```

Prva adresa provjerava frontend aplikaciju.

Druga adresa provjerava Keycloak realm konfiguraciju.

Ako servis ne postane dostupan u predviđenom vremenu, deployment se označava kao neuspješan.

### 13.6. Dijagnostika greške

U slučaju greške skripta automatski prikazuje:

```bash
docker compose ps --all
docker compose logs --tail=120
```

GitHub Actions log zato sadrži status kontejnera i posljednje log poruke potrebne za osnovnu analizu problema.

---

## 14. Javna provjera nakon deploymenta

Pored lokalnih provjera unutar serverske skripte, GitHub Actions CD workflow provjerava javne HTTPS adrese.

| Provjera                     | URL                                                                         | Očekivani rezultat            |
| ---------------------------- | --------------------------------------------------------------------------- | ----------------------------- |
| Frontend aplikacija          | `https://docflow.page/`                                                     | HTTP zahtjev uspješno prolazi |
| Keycloak realm konfiguracija | `https://auth.docflow.page/realms/docflow/.well-known/openid-configuration` | HTTP zahtjev uspješno prolazi |

Za provjeru se koristi `curl` sa ponovljenim pokušajima.

Nakon uspješnog deploymenta GitHub Actions prikazuje deployment summary sa:

* statusom deploymenta;
* deployment granom;
* javnom adresom frontenda;
* javnom adresom autentifikacijskog servisa.

---

## 15. Ručno pokretanje produkcijskog deploymenta

Pored automatskog pokretanja nakon uspješnog CI izvršavanja na grani `main`, deployment se može pokrenuti i ručno.

Koraci:

```text
GitHub repozitorij
→ Actions
→ Docflow CD
→ Run workflow
→ Branch: main
→ Run workflow
```

Ručno pokretanje je korisno:

* kada se promijeni server-side `.env` konfiguracija bez izmjene izvornog koda;
* kada je potrebno ponoviti deployment;
* kada je potrebno provjeriti CD proceduru;
* nakon infrastrukturne intervencije na serveru.

---

# Dio IV — Provjera, održavanje i rješavanje problema

U ovom dijelu navedeni su koraci za ručnu smoke provjeru, rollback procedura,
poznata ograničenja i rješenja najčešćih problema.

---

## 16. Ručna provjera nakon deploymenta

Nakon uspješnog izvršavanja CD pipelinea potrebno je ručno provjeriti najmanje:

1. otvaranje frontend aplikacije na `https://docflow.page`;
2. otvaranje Keycloak login stranice;
3. prijavu postojećeg korisnika;
4. prikaz Documents stranice;
5. prikaz Profile stranice;
6. prikaz My Tasks stranice;
7. dostupnost notification centra;
8. logout korisnika.

Na serveru se stanje kontejnera može provjeriti komandom:

```bash
cd /opt/docflow/Project
docker compose ps
```

Posljednji backend logovi dostupni su kroz:

```bash
docker compose logs --tail=120 docflow-backend
```

Posljednji Keycloak logovi dostupni su kroz:

```bash
docker compose logs --tail=120 docflow-keycloak
```

---

## 17. Rollback procedura

Preporučeni rollback obavlja se kroz Git historiju.

Koraci:

1. identifikovati problematični commit na grani `main`;
2. kreirati revert commit;
3. otvoriti pull request prema grani `main`;
4. sačekati uspješan CI rezultat;
5. mergeati revert pull request;
6. omogućiti CD pipelineu da automatski deploya prethodno stabilno stanje;
7. izvršiti ručnu smoke provjeru aplikacije.

Na ovaj način rollback ostaje evidentiran u Git historiji i ponovljiv kroz isti automatizovani proces.

U hitnim situacijama moguće je ručno vratiti prethodno stabilno stanje na serveru. Takva intervencija mora naknadno biti evidentirana i u Git repozitoriju.

---

## 18. Poznata ograničenja deploymenta

| Ograničenje                                                                                               | Posljedica                                                               | Moguće buduće unapređenje                                          |
| --------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------ | ------------------------------------------------------------------ |
| Sistem ne koristi verzionisane migracije kroz Flyway ili Liquibase                                        | Složenije buduće promjene šeme mogu zahtijevati kontrolisane SQL skripte | Uvesti verzionisane migracije                                      |
| Deployment ne garantuje potpuno zero-downtime ažuriranje                                                  | Pri rekreiranju kontejnera moguć je kratak prekid dostupnosti            | Uvesti rolling deployment ili orkestraciju                         |
| Automatski backup baze nije dio `deploy.sh` skripte                                                       | Backup se mora organizovati odvojeno prije rizičnih izmjena              | Dodati automatski backup korak i retention politiku                |
| Produkcijski `.env` i Google JSON postavljaju se ručno pri početnoj pripremi servera                      | Prvi deployment zahtijeva administratorsku konfiguraciju                 | Uvesti centralizovani secrets manager                              |
| Health provjere potvrđuju dostupnost frontenda i Keycloaka, ali ne izvršavaju kompletan poslovni workflow | Uspješan deployment ne dokazuje ispravnost svakog korisničkog toka       | Dodati post-deployment autentifikovane UI smoke testove            |
| Google Document AI i SMTP su vanjski servisi                                                              | OCR i email obavijesti zavise od dostupnosti vanjskih servisa            | Uvesti dodatni monitoring i retry strategije                       |
| Lokalno okruženje zahtijeva ručno podešavanje URL vrijednosti u `.env` fajlu                              | Pogrešna lokalna URL konfiguracija može onemogućiti autentifikaciju      | Dodati zaseban lokalni Compose override ili lokalni `.env.example` |

---

## 19. Najčešći problemi i rješenja

| Problem                                                 | Mogući uzrok                                                                 | Rješenje                                                                                         |
| ------------------------------------------------------- | ---------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `docker compose config -q` prijavljuje grešku           | Nedostaje obavezna `.env` vrijednost ili je Compose sintaksa neispravna      | Provjeriti `.env` i ponovo izvršiti `docker compose config -q`                                   |
| Backend kontejner se ne pokreće                         | Baza nije spremna ili su pogrešni PostgreSQL kredencijali                    | Izvršiti `docker compose ps` i `docker compose logs --tail=120 docflow-backend`                  |
| Keycloak nije dostupan                                  | Keycloak baza nije spremna ili nedostaju Keycloak vrijednosti                | Izvršiti `docker compose logs --tail=120 docflow-keycloak`                                       |
| Login ne radi u lokalnom okruženju                      | Pogrešni Keycloak hostname, issuer ili redirect URL                          | Provjeriti lokalne URL vrijednosti u `.env` fajlu                                                |
| OCR obrada ne radi                                      | Nedostaje Google JSON fajl ili processor ID vrijednosti                      | Provjeriti `Project/secrets/google-document-ai.json` i Google Document AI varijable              |
| Email reminder ne radi                                  | SMTP vrijednosti nisu postavljene ili backend nije učitao novu konfiguraciju | Provjeriti SMTP varijable i rekreirati backend kontejner                                         |
| Izmijenjene `.env` vrijednosti nisu aktivne             | Postojeći kontejner nije rekreiran                                           | Izvršiti `docker compose up -d --force-recreate docflow-backend` ili ručno pokrenuti CD workflow |
| CD workflow prijavljuje `Permission denied (publickey)` | SSH ključ nije pravilno konfigurisan                                         | Provjeriti `DEPLOY_SSH_PRIVATE_KEY`, `DEPLOY_KNOWN_HOSTS` i serverski `authorized_keys`          |
| Frontend prikazuje staru verziju                        | Browser koristi cache                                                        | Izvršiti hard refresh; Nginx konfiguracija onemogućava cache Angular entry pointa                |
| Servis nije dostupan nakon deploymenta                  | Kontejner nije pokrenut ili reverse proxy nije dostupan                      | Provjeriti `docker compose ps`, logove kontejnera i reverse proxy konfiguraciju                  |
| Baza sadrži neželjene lokalne podatke                   | Lokalni volume-i nisu obrisani                                               | Izvršiti `docker compose down -v` samo kada je namjerno potrebno obrisati lokalno stanje         |

---

## 20. Završna kontrolna lista

### Lokalno pokretanje

* [ ] Instalirani su Git, Docker i Docker Compose.
* [ ] Kreiran je lokalni `.env` fajl.
* [ ] Konfigurisane su lokalne URL vrijednosti.
* [ ] Po potrebi je dodat Google Document AI JSON fajl.
* [ ] Pokrenute su baze podataka.
* [ ] Pokrenut je Keycloak.
* [ ] Pokrenut je backend.
* [ ] Pokrenut je frontend.
* [ ] Provjerena je dostupnost lokalne aplikacije.

### Testiranje

* [ ] Backend testovi prolaze.
* [ ] Frontend unit testovi prolaze.
* [ ] Frontend production build prolazi.
* [ ] Playwright UI smoke testovi prolaze.

### Produkcijski deployment

* [ ] CI workflow je uspješno završen.
* [ ] CD workflow je uspješno završen.
* [ ] Frontend javna adresa je dostupna.
* [ ] Keycloak javna adresa je dostupna.
* [ ] Login postojećeg korisnika radi.
* [ ] Documents stranica je dostupna.
* [ ] Profile stranica je dostupna.
* [ ] My Tasks stranica je dostupna.
* [ ] Notification centar je dostupan.

---

## 21. Zaključak

Docflow deployment procedura omogućava ponovljivo, dokumentovano i provjerivo lokalno i produkcijsko pokretanje sistema.

Produkcijski deployment obuhvata:

* provjeru kvaliteta kroz CI pipeline;
* automatizovani SSH pristup DigitalOcean serveru;
* ažuriranje koda sa grane `main`;
* validaciju server-side konfiguracije;
* build backend i frontend Docker image-a;
* povezivanje sa PostgreSQL bazama;
* korištenje trajnih Docker volume-a;
* pokretanje ažuriranih kontejnera;
* interno povezivanje frontend aplikacije sa backend REST API-jem;
* lokalne provjere dostupnosti servisa;
* javne HTTPS provjere dostupnosti;
* prikaz deployment summaryja u GitHub Actions interfejsu.

Za isporučenu verziju sistema nije potrebno ručno izvršavati dodatne SQL migracije niti seed skripte.

Time su ispunjeni zahtjevi za lokalno pokretanje, produkcijski deployment, automatizaciju, provjerivost i dokumentovanje poznatih ograničenja sistema.
