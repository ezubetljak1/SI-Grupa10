# Sprint Backlog

## Product Backlog stavke za Sprint 10

| ID   | Naziv stavke                                    | Opis                                                                                                                                       | Tip | Prioritet | Složenost | Status |
|------|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|-----|-----------|-----------|--------|
| PB2  | Obavještenja o dokumentima                      | Obavještenja za odobravanje, odbijanje i vraćanje na doradu                                                                                | F   | P2        | M         | Done   |
| PB3  | Ručno dodavanje i brisanje ekstraktovanog polja | Operator može ručno dodati ili polje koje OCR nije ekstraktovao, sa canonical ključem i prikaznim imenom ili obrisati polje ako je suvišno | F   | P2        | M         | Done   |
| PB4  | Notification centar                             | Korisnik ima pregled svih in-app obavještenja sa unread badge-om, označavanjem pročitanog i navigacijom                                    | F   | P2        | M         | Done   |
| PB5  | Email reminder za nepročitana obavještenja      | Sistem automatski šalje email digest za stara nepročitana obavještenja putem scheduled job-a                                               | F   | P3        | S         | Done   |
| PB6  | XML mapping                                     | Mapiranje validiranih podataka u XML strukturu                                                                                             | T   | P1        | M         | Done   | 
| PB7  | XML generator                                   | Generisanje XML fajla iz podataka                                                                                                          | F   | P1        | M         | Done   | 
| PB8  | Pregled XML                                     | Prikaz XML sadržaja korisniku                                                                                                              | F   | P3        | S         | Done   | 
| PB9  | Download XML                                    | Omogućavanje preuzimanja XML fajla                                                                                                         | F   | P1        | S         | Done   | 
| PB10 | Spremanje XML                                   | Čuvanje XML fajla uz dokument                                                                                                              | T   | P1        | M         | Done   | 
| PB11 | Final status                                    | Završetak obrade dokumenta                                                                                                                 | F   | P2        | S         | Done   | 

---

## User Stories za Sprint 10

### US-10.1 - Obavještenje o dokumentu koji čeka odobravanje

**Opis** - Kao osoba za odobravanje, želim dobiti obavještenje kada dokument čeka moju akciju, kako bih mogao
pravovremeno reagovati.

**Acceptance Criteria**

- Kada dokument pređe u status `READY FOR APPROVAL`, tada sistem mora generisati obavještenje za korisnika koji ima
  ulogu za odobravanje dokumenata.
- Sistem mora omogućiti da obavještenje bude povezano sa konkretnim dokumentom.
- Korisnik treba dobiti signal da postoji nova stavka koja traži akciju.

---

### US-10.2 - Obavještenje o odbijenom ili vraćenom dokumentu

**Opis** - Kao operater, želim dobiti obavještenje kada je dokument odbijen ili vraćen, kako bih znao da je potrebna
ponovna ili dodatna obrada.

**Acceptance Criteria**

- Kada dokument promijeni status u `REJECTED` ili `NEEDS CORRECTION`, tada sistem mora generisati obavještenje za
  odgovornog operatera.
- Kada postoji komentar pri odbijanju, tada sistem mora povezati obavještenje sa tim komentarom.
- Korisnik treba dobiti jasnu informaciju da je potrebna dodatna obrada dokumenta.

---

### US-10.3 - Notification centar i unread badge

**Opis** - Kao korisnik sistema, želim imati pregled svih mojih obavještenja na jednom mjestu, kako bih mogao lako
pratiti šta zahtijeva moju akciju.

**Acceptance Criteria**

- Kada korisnik ima nepročitana obavještenja, tada sistem mora prikazati unread badge u navigacijskoj traci sa brojem
  nepročitanih obavještenja.
- Kada korisnik otvori notification centar, tada sistem mora prikazati listu obavještenja.
- Korisnik mora moći označiti pojedinačno obavještenje kao pročitano ili sve odjednom.
- Klik na obavještenje mora označiti obavještenje kao pročitano i navigirati korisnika na relevantnu stranicu.
- Unread badge mora biti ažuriran nakon svakog označavanja pročitanog.

---

### US-10.4 - Email reminder za nepročitana obavještenja

**Opis** - Kao korisnik sistema, želim dobiti email podsjetnik kada imam dugo nepročitana obavještenja, kako bih bio
obaviješten čak i kada nisam aktivan u sistemu.

**Acceptance Criteria**

- Kada postoje obavještenja koja su starija od konfigurabilnog praga (npr. 24 sata) i nisu pročitana te email još
  nije poslan, tada sistem mora automatski poslati email digest korisniku.
- Sistem mora grupirati sva nepročitana obavještenja u jedan email po korisniku kako bi se izbjegao spam.
- SMTP kredencijali moraju biti konfigurisani isključivo putem environment varijabli i ne smiju biti vidljivi u
  kodu, logovima niti frontend dijelu aplikacije.

---

### US-10.5 - Ručno dodavanje ekstraktovanog polja

**Opis** - Kao operator, želim ručno dodati polje koje OCR nije ekstraktovao, kako bi dokument imao kompletne podatke
za odobravanje.

**Acceptance Criteria**

- Kada operator otvori modal za dodavanje polja, tada sistem mora ponuditi listu poznatih canonical ključeva za tip
  dokumenta, kao i opciju za unos prilagođenog polja.
- Sistem mora zahtijevati non-empty vrijednost polja.
- Sistem mora odbiti fieldName koji nije ni poznati canonical ključ ni odgovarajući format.
- Dodano polje mora biti označeno kao `manual=true`, `corrected=true`, a `confidence` mora biti `null`.
- Sistem mora prikazati ručno dodano polje sa čitljivom labelom i oznakom "Manual" u UI-u.
- Sistem mora zabilježiti dodavanje polja u audit logu (`FIELD_ADDED`).
- Ručno dodavanje polja je dozvoljeno samo u statusima `EXTRACTED` i `NEEDS_CORRECTION`.

---

### US-10.6 - Ručno brisanje suvišnog ekstraktovanog polja

**Opis** - Kao operator, želim ručno obrisati polje koje je OCR ekstraktovao, ali je suvišno za dalju obradu, kako bi
dokument bio čist i relevantan.

**Acceptance Criteria**

- Kada operator odabere opciju brisanja pored ekstraktovanog polja, tada sistem mora prikazati potvrdu prije brisanja.
- Nakon potvrde, sistem mora ukloniti polje iz prikaza
- Sistem ne smije dozvoliti brisanje obaveznih polja

---

### US-10.7 - Generisanje XML izlaza

**Opis** - Kao menadžer, želim da sistem generiše XML iz validiranih i odobrenih podataka, kako bi dokument bio spreman
za dalju upotrebu.

**Acceptance Criteria**

- Kada je dokument u statusu `APPROVED`, ako menadžer inicira generisanje XML-a, tada sistem mora generisati XML fajl
  na osnovu validiranih podataka.
- Kada dokument nije u statusu `APPROVED`, tada sistem ne smije dozvoliti generisanje XML-a.
- Kada generisanje ne uspije (npr. nedostaju podaci), tada sistem mora prikazati grešku i ne smije kreirati neispravan
  XML.
- Sistem mora koristiti definisana mapping pravila za popunjavanje XML strukture.

---

### US-10.8 - Pregled i preuzimanje XML-a

**Opis** - Kao menadžer, želim pregledati i preuzeti generisani XML, kako bih provjerio njegov sadržaj i koristio ga
izvan sistema.

**Acceptance Criteria**

- Kada je XML uspješno generisan, tada sistem mora omogućiti njegov pregled u UI-u.
- Kada korisnik odabere preuzimanje, sistem mora omogućiti download ispravnog XML fajla.
- Korisnik treba dobiti fajl u očekivanom XML formatu.

---

### US-10.9 — Pohrana XML-a i završetak obrade dokumenta

**Opis** - Kao menadžer, želim da generisani XML bude sačuvan uz dokument i da dokument dobije završni status, kako bi
bio spreman za knjiženje ili dalju poslovnu upotrebu.

**Acceptance Criteria**

- Kada sistem generiše XML, ako je generisanje uspješno, tada mora sačuvati XML uz odgovarajući dokument.
- Kada korisnik potvrdi završetak obrade, sistem mora promijeniti status dokumenta u finalno stanje.
- Sistem ne smije dozvoliti finalizaciju dokumenta bez uspješnog XML izlaza.

---

### US-10.10 — Pretraga i filtriranje dokumenata

**Opis** - Kao menadžer, želim pretražiti i filtrirati dokumente po osnovnim kriterijima, kako bih brže pronašao
traženi dokument i lakše radio sa većim brojem zapisa.

**Acceptance Criteria**

- Kada korisnik unese kriterij pretrage, tada sistem mora vratiti listu dokumenata koji odgovaraju kriteriju.
- Kada korisnik primijeni filtere (npr. status, datum, iznos), tada sistem mora prikazati samo dokumente koji
  zadovoljavaju sve odabrane filtere.
- Kada nema rezultata pretrage, tada korisnik treba dobiti odgovarajuću poruku.
- Sistem ne smije vraćati dokumente iz drugih firmi.
- Sistem mora ograničiti rezultate po stranici radi performansi.

---

### US-10.11 — Pregled dokumenata koji čekaju odobravanje

**Opis** - Kao approver, želim vidjeti samo listu dokumenata sa statusom `READY_FOR_APPROVAL`, kako bih se mogao
fokusirati na dokumente koji zahtijevaju moju odluku.

**Acceptance Criteria**

- Kada se approver prijavi u sistem i otvori listu dokumenata, tada sistem mora prikazati samo dokumente sa statusom
  `READY_FOR_APPROVAL`.
- Approver ne smije vidjeti dokumente u drugim statusima kroz listu dokumenata.
- Sistem ne smije vraćati dokumente iz drugih firmi.
- Kada nema dokumenata koji čekaju odobravanje, tada korisnik treba dobiti odgovarajuću poruku da trenutno nema
  dokumenata za pregled.
- Backend mora primijeniti ograničenje po statusu i roli, tako da se ponašanje ne oslanja isključivo na frontend
  filtriranje.

---

### US-10.12 — Ograničeni prikaz detalja finaliziranog dokumenta

**Opis** - Kao menadžer, želim pregledati detalje finaliziranog dokumenta bez operativnih sekcija koje više nisu
relevantne, kako bih imao jasan read-only prikaz završene obrade.

**Acceptance Criteria**

- Kada menadžer otvori detalje dokumenta sa statusom `COMPLETED`, tada sistem mora prikazati osnovne metapodatke
  dokumenta i informacije relevantne za završenu obradu.
- Za dokument sa statusom `COMPLETED` sistem ne smije prikazati preview originalnog fajla.
- Za dokument sa statusom `COMPLETED` sistem ne smije prikazati sekciju za task assignment.
- Za dokument sa statusom `COMPLETED` sistem ne smije prikazati sekciju sa extracted fields.
- Generisani XML izlaz mora ostati dostupan za pregled i preuzimanje.
- Prikaz finaliziranog dokumenta mora biti read-only, bez akcija za izmjenu extraction polja, ponovno pokretanje
  ekstrakcije, dodjelu taskova ili regenerisanje XML izlaza.


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
