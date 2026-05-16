# Sprint Backlog

## Product Backlog stavke za Sprint 8

| ID  | Naziv stavke         | Opis                                                   | Tip | Prioritet | Složenost | Status | 
|-----|----------------------|--------------------------------------------------------|-----|-----------|-----------|--------|
| S42 | Model organizacije   | Definisanje modela organizacije koja koristi sistem    | T   | P1        | M         | TODO   |
| S43 | Model korisnika      | Definisanje korisničkog modela i veze sa firmom        | T   | P1        | M         | TODO   | 
| S44 | Registracija firme   | Omogućavanje registracije nove firme u sistemu         | F   | P1        | M         | TODO   | 
| S45 | Admin nalog          | Kreiranje prvog administratorskog korisnika firme      | F   | P1        | M         | TODO   | 
| S46 | Login                | Implementacija prijave korisnika u sistem              | F   | P1        | M         | TODO   | 
| S47 | Logout               | Omogućavanje odjave korisnika                          | F   | P1        | S         | TODO   | 
| S48 | Auth sigurnost       | Implementacija sigurnosti (hashiranje lozinki, tokeni) | T   | P1        | M         | TODO   | 
| S49 | Multi-tenant zaštita | Osiguranje da korisnici vide samo podatke svoje firme  | T   | P1        | M         | TODO   | 
| S50 | Test auth            | Testiranje autentifikacije i autorizacije              | T   | P2        | M         | TODO   | 
| S51 | Role model           | Definisanje korisničkih rola u sistemu                 | T   | P1        | M         | TODO   | 
| S52 | Kreiranje korisnika  | Omogućavanje adminu da dodaje nove korisnike           | F   | P1        | M         | TODO   | 
| S53 | Dodjela rola         | Dodjela rola korisnicima                               | F   | P1        | M         | TODO   |
| S54 | Test workflow        | Testiranje workflow logike i rola                      | T   | P2        | M         | TODO   | 
| S55 | Reset password       | Omogućavanje resetovanja lozinke                       | F   | P2        | M         | TODO   | 
| S56 | Dashboard            | Prikaz osnovnih statistika sistema                     | F   | P3        | M         | TODO   | 
| S57 | User management      | Upravljanje korisničkim podacima                       | F   | P2        | M         | TODO   | 

---

## User Stories za Sprint 8

### US-8.1 - Registracija firme / organizacije u sistem

**Opis** - Kao predstavnik firme, želim registrovati svoju firmu u sistem, kako bih mogao uspostaviti radno okruženje za
svoju organizaciju.

**Acceptance Criteria**

- Kada predstavnik firme unese validne podatke o firmi (npr. naziv, email), i potvrdi registraciju, tada sistem mora
  kreirati novu organizaciju u bazi.
- Kada se pokuša registracija sa već postojećim emailom firme, tada sistem ne smije dozvoliti duplikat i mora prikazati
  poruku greške.
- Sistem mora omogućiti jedinstvenu identifikaciju registrovane firme.
- Korisnik treba dobiti potvrdu da je registracija firme uspješna.

### US-8.2 — Kreiranje prvog administratorskog naloga firme

**Opis** - Kao predstavnik firme, želim kreirati prvi administratorski nalog firme prilikom registracije, kako bih mogao
upravljati pristupom sistemu.

**Acceptance Criteria**

- Kada se firma registruje, tada sistem mora omogućiti kreiranje prvog korisnika sa administratorskom ulogom.
- Kada korisnik unese validne podatke, tada sistem mora kreirati administratorski nalog povezan sa firmom.
- Sistem mora povezati administratorski nalog sa odgovarajućom firmom.
- Sistem mora sigurno pohraniti lozinku koristeći heširanje.

### US-8.3 — Prijava i odjava korisnika

**Opis** - Kao korisnik, želim se prijaviti i odjaviti iz sistema, kako bih mogao sigurno pristupati
dostupnim funkcionalnostima i zaštititi svoj nalog.

**Acceptance Criteria**

- Kada korisnik unese validne pristupne podatke, tada sistem mora omogućiti pristup sistemu.
- Sistem mora kreirati validnu sesiju ili token nakon uspješne prijave.
- Sistem ne smije dozvoliti pristup za nevalidne pristupne podatke.
- Sistem mora ograničiti broj uzastopnih neuspješnih pokušaja prijave
- Kada je korisnik prijavljen i odabere opciju odjave, sistem mora završiti aktivnu sesiju.
- Sistem mora onemogućiti pristup zaštićenim dijelovima nakon odjave.
- Korisnik treba dobiti povratnu informaciju da je uspješno odjavljen.

### US-8.4 — Prikaz poruke pri neuspješnoj prijavi

**Opis** - Kao uposlenik firme, želim vidjeti jasnu poruku pri neuspješnoj prijavi, kako bih znao šta je problem.

**Acceptance Criteria**

- Kada korisnik unese neispravne pristupne podatke, tada sistem mora prikazati poruku o neuspješnoj prijavi.
- Sistem ne smije prikazivati osjetljive sigurnosne detalje u poruci greške.
- Korisnik treba dobiti mogućnost da ponovo pokuša prijavu, ako nije prekoračio dozvoljeni broj pokušaja prijave.
- Kada korisnik prekorači dozvoljen broj pokušaja prijave, tada sistem mora privremeno blokirati pristup i prikazati
  odgovarajuću poruku.

### US-8.5 - Kreiranje korisničkih naloga unutar firme

**Opis** - Kao administrator firme, želim kreirati korisničke naloge unutar svoje firme, kako bih omogućio pristup
drugim članovima organizacije.

**Acceptance Criteria**

- Kada administrator unese validne podatke za novog korisnika (ime, email), tada sistem mora omogućiti kreiranje
  korisničkog naloga unutar iste firme.
- Kada administrator pokuša kreirati korisnika sa već postojećim emailom unutar iste firme, tada sistem ne smije
  dozvoliti duplikat i mora prikazati poruku greške.
- Sistem mora automatski povezati novog korisnika sa firmom administratora.
- Sistem mora omogućiti pregled kreiranih korisnika firme.
- Sistem ne smije dozvoliti kreiranje korisnika bez obaveznih podataka.

### US-8.6 - Dodjela korisničkih rola unutar firme

**Opis** - Kao administrator firme, želim dodijeliti korisniku odgovarajuću ulogu, kako bi imao ispravne odgovornosti u
sistemu.

**Acceptance Criteria**

- Kada administrator dodijeli korisniku rolu (operater, računovođa, odobravatelj), tada sistem mora sačuvati rolu
  povezanu sa korisnikom.
- Kada korisnik nema dodijeljenu rolu, tada sistem ne smije dozvoliti pristup zaštićenim funkcionalnostima.
- Kada administrator promijeni rolu korisnika, tada sistem mora odmah primijeniti nova prava pristupa.

### US-8.7 — Ograničenje pristupa na podatke vlastite organizacije

**Opis** - Kao uposlenik firme, želim da pristupam samo podacima svoje organizacije, kako bi podaci moje firme ostali
odvojeni i sigurni.

**Acceptance Criteria**

- Kada je korisnik prijavljen, ako pristupa dokumentima ili korisnicima, tada sistem mora prikazati samo podatke njegove
  organizacije.
- Sistem ne smije dozvoliti pristup podacima druge organizacije.
- Sistem mora provoditi provjeru organizacijske pripadnosti na nivou poslovne logike i pristupa podacima.

### US-8.8 - Ograničenje akcija prema ulozi

**Opis** - Kao administrator firme, želim da korisnici imaju pristup samo akcijama koje odgovaraju njihovoj ulozi, kako
bi proces rada bio ispravan i siguran.

**Acceptance Criteria**

- Kada korisnik pokuša izvršiti akciju, tada sistem mora provjeriti da li njegova rola ima dozvolu za tu akciju.
- Kada korisnik nema odgovarajuću rolu, tada sistem ne smije dozvoliti izvršenje akcije i mora prikazati grešku.
- Kada korisnik ima odgovarajuću rolu, tada sistem mora omogućiti izvršenje akcije bez dodatnih prepreka.
- Sistem mora omogućiti pristup samo onim akcijama koje su dozvoljene toj ulozi.
- Korisnik treba dobiti konzistentno ponašanje sistema u skladu sa svojom rolom.

### US-8.9 - Pregled korisnika firme

**Opis** - Kao administrator firme, želim vidjeti listu korisnika svoje firme, kako bih mogao upravljati postojećim
nalozima.

**Acceptance Criteria**

- Kada administrator otvori listu korisnika, tada sistem mora prikazati sve korisnike povezane sa njegovom firmom.
- Kada nema korisnika osim administratora, tada sistem treba prikazati odgovarajuću poruku
- Sistem mora omogućiti pregled osnovnih podataka i role korisnika.
- Sistem ne smije prikazivati korisnike drugih organizacija.

### US-8.10 - Ažuriranje osnovnih podataka o korisnicima

**Opis** -Kao administrator firme, želim pregledati i ažurirati osnovne podatke o korisnicima, kako bih lakše održavao
aktivne naloge.

**Acceptance Criteria**

- Kada administrator otvori korisnički nalog, ako ima odgovarajuću dozvolu, tada sistem mora omogućiti izmjenu
  definisanih osnovnih podataka.
- Sistem mora sačuvati ažurirane podatke korisnika.
- Sistem ne smije dozvoliti izmjenu podataka koji su zaštićeni pravilima sigurnosti bez dodatnih uslova.

### US-8.11 - Reset lozinke

**Opis** - Kao korisnik sistema, želim resetovati lozinku, kako bih mogao povratiti pristup nalogu.

**Acceptance Criteria**

- Kada korisnik pokrene proceduru resetovanja lozinke, ako ispuni definisane uslove, tada sistem mora omogućiti
  postavljanje nove lozinke.
- Sistem mora osigurati da stara lozinka više ne važi nakon uspješnog resetovanja.
- Korisnik treba dobiti potvrdu da je lozinka uspješno promijenjena.

### US-8.12 - Dashboard za menadžment firme

**Opis** - Kao menadžment, želim vidjeti osnovni dashboard sa brojem dokumenata po statusu te odgovornim osobama, kako
bih imao pregled stanja
procesa.

**Acceptance Criteria**

- Kada korisnik sa odgovarajućom ulogom otvori dashboard, ako podaci postoje, tada sistem mora prikazati osnovne
  pokazatelje stanja procesa.
- Sistem mora omogućiti pregled barem broja dokumenata po statusu.
- Korisnik treba dobiti pregled koji je jasan i dovoljno sažet za operativno praćenje.

---

## Legenda za Product Backlog stavke

### Tip stavke

- **F (Feature)** – funkcionalnost koja donosi direktnu vrijednost korisniku
- **T (Technical Task)** – tehnička implementacija (backend, integracije, arhitektura)
- **D (Documentation)** – projektni i korisnički artefakti
- **R (Research)** – analiza i modeliranje sistema
- **B (Bug)** – ispravka greške ili problema u sistemu

### Prioritet

- **P1** – kritično za MVP i osnovni tok sistema
- **P2** – važno, ali ne blokira osnovnu funkcionalnost
- **P3** – dodatne ili napredne funkcionalnosti

### Procjena složenosti

- **S (Small)** – jednostavna implementacija (1–2 dana)
- **M (Medium)** – srednje složen zadatak (3–5 dana)
- **L (Large)** – kompleksna implementacija (više dana ili integracije)

### Status

- **DONE** – zadatak je završen
- **IN-PROGRESS** – zadatak je trenutno u realizaciji
- **TODO** – zadatak je planiran ali nije započet

---
