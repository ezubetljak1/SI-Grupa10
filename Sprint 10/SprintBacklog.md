# Sprint Backlog

## Product Backlog stavke za Sprint 10

| ID   | Naziv stavke                               | Opis                                                                                                    | Tip | Prioritet | Složenost | Status |
|------|--------------------------------------------|---------------------------------------------------------------------------------------------------------|-----|-----------|-----------|--------|
| PB1  | Historija statusa dokumenta                | Prikaz historije statusa dokumenta, uključujući odobrenja, odbijanja i vraćanja na doradu               | F   | P2        | M         | TODO   |
| PB2  | Obavještenja o dokumentima                 | Obavještenja za odobravanje, odbijanje i vraćanje na doradu                                             | F   | P2        | M         | TODO   |
| PB3  | Ručno dodavanje ekstraktovanog polja       | Operator može ručno dodati polje koje OCR nije ekstraktovao, sa canonical ključem i prikaznim imenom    | F   | P2        | M         | TODO   |
| PB4  | Notification centar                        | Korisnik ima pregled svih in-app obavještenja sa unread badge-om, označavanjem pročitanog i navigacijom | F   | P2        | M         | TODO   |
| PB5  | Email reminder za nepročitana obavještenja | Sistem automatski šalje email digest za stara nepročitana obavještenja putem scheduled job-a            | F   | P3        | S         | TODO   |
| PB6  | XML mapping                                | Mapiranje validiranih podataka u XML strukturu                                                          | T   | P1        | M         | TODO   | 
| PB7  | XML generator                              | Generisanje XML fajla iz podataka                                                                       | F   | P1        | M         | TODO   | 
| PB8  | Pregled XML                                | Prikaz XML sadržaja korisniku                                                                           | F   | P3        | S         | TODO   | 
| PB9  | Download XML                               | Omogućavanje preuzimanja XML fajla                                                                      | F   | P1        | S         | TODO   | 
| PB10 | Spremanje XML                              | Čuvanje XML fajla uz dokument                                                                           | T   | P1        | M         | TODO   | 
| PB11 | Final status                               | Završetak obrade dokumenta                                                                              | F   | P2        | S         | TODO   | 

---

## User Stories za Sprint 9

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

### US-10.3 - Ručno dodavanje ekstraktovanog polja

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

### US-10.4 - Notification centar i unread badge

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

### US-10.5 - Email reminder za nepročitana obavještenja

**Opis** - Kao korisnik sistema, želim dobiti email podsjetnik kada imam dugo nepročitana obavještenja, kako bih bio
obaviješten čak i kada nisam aktivan u sistemu.

**Acceptance Criteria**

- Kada postoje obavještenja koja su starija od konfigurabilnog praga (npr. 24 sata) i nisu pročitana te email još
  nije poslan, tada sistem mora automatski poslati email digest korisniku.
- Sistem mora grupirati sva nepročitana obavještenja u jedan email po korisniku kako bi se izbjegao spam.
- SMTP kredencijali moraju biti konfigurisani isključivo putem environment varijabli i ne smiju biti vidljivi u
  kodu, logovima niti frontend dijelu aplikacije.

---

### US-10.6 - Generisanje XML izlaza

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

### US-10.7 - Pregled i preuzimanje XML-a

**Opis** - Kao menadžer, želim pregledati i preuzeti generisani XML, kako bih provjerio njegov sadržaj i koristio ga
izvan sistema.

**Acceptance Criteria**

- Kada je XML uspješno generisan, tada sistem mora omogućiti njegov pregled u UI-u.
- Kada korisnik odabere preuzimanje, sistem mora omogućiti download ispravnog XML fajla.
- Korisnik treba dobiti fajl u očekivanom XML formatu.

---

### US-10.8 — Pohrana XML-a i završetak obrade dokumenta

**Opis** - Kao menadžer, želim da generisani XML bude sačuvan uz dokument i da dokument dobije završni status, kako bi
bio spreman za knjiženje ili dalju poslovnu upotrebu.

**Acceptance Criteria**

- Kada sistem generiše XML, ako je generisanje uspješno, tada mora sačuvati XML uz odgovarajući dokument.
- Kada korisnik potvrdi završetak obrade, sistem mora promijeniti status dokumenta u finalno stanje.
- Sistem ne smije dozvoliti finalizaciju dokumenta bez uspješnog XML izlaza.

### US-10.9 — Pretraga i filtriranje dokumenata

**Opis** - Kao menadžer, želim pretražiti i filtrirati dokumente po osnovnim kriterijima, kako bih brže pronašao
traženi dokument i lakše radio sa većim brojem zapisa.

**Acceptance Criteria**

- Kada korisnik unese kriterij pretrage, tada sistem mora vratiti listu dokumenata koji odgovaraju kriteriju.
- Kada korisnik primijeni filtere (npr. status, datum, iznos), tada sistem mora prikazati samo dokumente koji
  zadovoljavaju sve odabrane filtere.
- Kada nema rezultata pretrage, tada korisnik treba dobiti odgovarajuću poruku.
- Sistem ne smije vraćati dokumente iz drugih firmi.
- Sistem mora ograničiti rezultate po stranici radi performansi.

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
