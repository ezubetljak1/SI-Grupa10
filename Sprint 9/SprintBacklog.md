# Sprint Backlog

## Product Backlog stavke za Sprint 9

| ID   | Naziv stavke                                 | Opis                                                                                                         | Tip | Prioritet | Složenost | Status |
|------|----------------------------------------------|--------------------------------------------------------------------------------------------------------------|-----|-----------|-----------|--------|
| PB1  | Ograničenja osobe za odobravanje             | Osoba za odobravanje treba da ima ograničene akcije u sistemu kao i ograničen pregled detalja o dokumentima  | F   | P1        | M         | TODO   |
| PB2  | Lista dokumenata na čekanju                  | Osoba za odobravanje treba samo da vidi listu dokumenata koji čekaju na odobrenje, a ne sve dokumente        | F   | P2        | S         | TODO   |
| PB3  | Odobravanje/odbijanje ili vraćanje dokumenta | Donošenje odluke o dokumentu uz komentar                                                                     | F   | P1        | M         | TODO   |
| PB4  | Historija statusa dokumenta                  | Prikaz historije statusa dokumenta, uključujući odobrenja, odbijanja i vraćanja na doradu                    | F   | P2        | M         | TODO   |
| PB5  | Obavještenja o dokumentima                   | Obavještenja za odobravanje, odbijanje i vraćanje na doradu                                                  | F   | P2        | M         | TODO   |
| PB6  | Slanje linka za ažuriranje lozinke na email  | Slanje emaila za ažuriranje lozinke u slučaju kreiranja korisnika ili resetovanja lozinke                    | F   | P1        | S         | TODO   |
| PB7  | Dodjela zadataka korisnicima                 | Admin/Manager može dodijeliti dokument konkretnom operateru ili odobravatelju putem Task entiteta            | F   | P2        | M         | TODO   |
| PB8  | Moji zadaci                                  | Korisnik može pregledati listu zadataka koji su mu dodijeljeni, filtrirati ih po statusu i otvoriti dokument | F   | P2        | S         | TODO   |
| PB9  | Ručno dodavanje ekstraktovanog polja         | Operator može ručno dodati polje koje OCR nije ekstraktovao, sa canonical ključem i prikaznim imenom         | F   | P2        | M         | TODO   |
| PB10 | Notification centar                          | Korisnik ima pregled svih in-app obavještenja sa unread badge-om, označavanjem pročitanog i navigacijom      | F   | P2        | M         | TODO   |
| PB11 | Email reminder za nepročitana obavještenja   | Sistem automatski šalje email digest za stara nepročitana obavještenja putem scheduled job-a                 | F   | P3        | S         | TODO   |
| PB12 | Audit log za ključne akcije                  | Sistem bilježi audit trail za assignment, approval, odbijanje, vraćanje, dodavanje polja i email remind.     | T   | P2        | M         | TODO   |

---

## User Stories za Sprint 9

### US-9.1 - Ograničenje akcija za odobravatelja

**Opis** - Kao osoba za odobravanje, želim da sistem ograniči moje akcije u skladu sa ulogom, kako bi se mogao
fokusirati na svoj dio posla bez rizika od nenamjernih izmjena i grešaka.

**Acceptance Criteria**

- Kada osoba za odobravanje pristupi sistemu, tada sistem mora ograničiti dostupne akcije na one koje su relevantne za
  odobravanje dokumenata.
- Sistem ne smije dozvoliti odobravatelju da mijenja validirana polja ili vrši akcije koje nisu vezane za odobravanje.
- Sistem mora jasno prikazati koje su akcije dostupne odobravatelju.

---

### US-9.2 - Pregled dokumenata koji čekaju odobravanje

**Opis** - Kao osoba za odobravanje, želim vidjeti dokumente koji čekaju moju akciju, kako bih znao šta trebam
pregledati.

**Acceptance Criteria**

- Kada osoba za odobravanje otvori listu dokumenata, tada sistem mora prikazati samo dokumente u statusu "na
  odobrenju".
- Kada nema dokumenata za odobravanje, tada treba ispisati poruku.
- Sistem mora omogućiti otvaranje detalja dokumenta sa liste čekanja.

---

### US-9.3 - Ograničen pregled detalja dokumenta koji čeka odobravanje

**Opis** - Kao osoba za odobravanje, želim da mi stranica za prikaz detalja dokumenta prikaže samo originalni dokument,
ekstraktovane podatke i historiju statusa, kako bih mogao donijeti odluku o odobrenju ili odbijanju.

**Acceptance Criteria**

- Kada osoba za odobravanje otvori detalje dokumenta, tada sistem mora prikazati originalni dokument, ekstraktovane
  podatke i historiju statusa dokumenta.
- Sistem mora prikazati trenutno stanje dokumenta.
- Sistem mora omogućiti pregled prethodnih komentara.
- Osoba za odobravanje ne smije moći mijenjati validirana polja.

---

### US-9.4 - Odobrenje dokumenta

**Opis** - Kao osoba za odobravanje, želim odobriti dokument, kako bi dokument prešao u završnu fazu.

**Acceptance Criteria**

- Kada osoba za odobravanje odobri dokument, tada sistem mora promijeniti status dokumenta u `APPROVED`.
- Sistem mora sačuvati approval akciju u historiji statusa.
- Sistem ne smije dozvoliti odobravanje dokumenta koji nije u odgovarajućem statusu.

---

### US-9.5 - Odbijanje dokumenta uz obavezan komentar

**Opis** - Kao osoba za odobravanje, želim odbiti dokument uz komentar, kako bi operater znao šta treba ispraviti.

**Acceptance Criteria**

- Kada osoba za odobravanje odbije dokument, tada sistem mora promijeniti status dokumenta u
  `REJECTED`.
- Sistem mora sačuvati odbijanje i komentar u historiji statusa.
- Kada je dokument odbijen, sistem ga automatski vraća u prethodnu fazu.
- Sistem ne smije dozvoliti odbijanje dokumenta bez navođenja komentara.

---

### US-9.6 - Vraćanje dokumenta na doradu

**Opis** - Kao osoba za odobravanje, želim vratiti dokument na doradu uz komentar bez finalnog odbijanja, kako bi
operater mogao izvršiti manje korekcije.

**Acceptance Criteria**

- Kada approver odabere "Return for correction", tada sistem mora promijeniti status dokumenta u `NEEDS CORRECTION`.
- Sistem mora zahtijevati komentar razloga vraćanja.
- Dokument mora ponovo biti dostupan operateru za izmjene.

---

### US-9.7 - Pregled historije statusa dokumenta

**Opis** - Kao menadžer ili osoba za odobravanje, želim vidjeti historiju statusa dokumenta, kako bih mogao pratiti tok
obrade.

**Acceptance Criteria**

- Kada menadžer ili osoba za odobravanje otvori detalje dokumenta, tada sistem mora prikazati historiju statusa
  dokumenta.
- Kada ne postoji historija (npr. novi dokument), tada sistem treba prikazati samo inicijalni status.
- Sistem mora čuvati historiju statusa trajno i ne smije dozvoliti njeno brisanje od strane korisnika.
- Sistem mora omogućiti pregled redoslijeda ključnih koraka obrade.

---

### US-9.8 - Obavještenje o dokumentu koji čeka odobravanje

**Opis** - Kao osoba za odobravanje, želim dobiti obavještenje kada dokument čeka moju akciju, kako bih mogao
pravovremeno reagovati.

**Acceptance Criteria**

- Kada dokument pređe u status `READY FOR APPROVAL`, tada sistem mora generisati obavještenje za korisnika koji ima
  ulogu za odobravanje dokumenata.
- Sistem mora omogućiti da obavještenje bude povezano sa konkretnim dokumentom.
- Korisnik treba dobiti signal da postoji nova stavka koja traži akciju.

---

### US-9.9 - Obavještenje o odbijenom ili vraćenom dokumentu

**Opis** - Kao operater, želim dobiti obavještenje kada je dokument odbijen ili vraćen, kako bih znao da je potrebna
ponovna ili dodatna obrada.

**Acceptance Criteria**

- Kada dokument promijeni status u `REJECTED` ili `NEEDS CORRECTION`, tada sistem mora generisati obavještenje za
  odgovornog operatera.
- Kada postoji komentar pri odbijanju, tada sistem mora povezati obavještenje sa tim komentarom.
- Korisnik treba dobiti jasnu informaciju da je potrebna dodatna obrada dokumenta.

---

### US-9.10 - Slanje linka za ažuriranje lozinke na email

**Opis** - Kao korisnik sistema, želim da prilikom kreiranja korisnika ili resetovanja lozinke dobijem email sa linkom
za ažuriranje lozinke, kako bih na siguran način mogao postaviti novu lozinku i pristupiti sistemu.

**Acceptance Criteria**

- Kada se kreira novi korisnik ili resetuje lozinka, sistem mora poslati email korisniku sa jedinstvenim linkom za
  ažuriranje lozinke.
- Link za ažuriranje lozinke mora biti vremenski ograničen i isteći nakon definisanog perioda (npr. 12 sati)
- Sistem ne smije slati privremene šifre putem emaila.
- Email mora sadržavati jasne instrukcije za postavljanje nove lozinke putem dostavljenog linka.

---

### US-9.11 - Dodjela zadatka korisniku

**Opis** - Kao Admin ili Manager, želim dodijeliti dokument konkretnom operateru ili odobravatelju putem zadatka, kako
bi bilo jasno ko je odgovoran za obradu dokumenta.

**Acceptance Criteria**

- Kada Admin ili Manager kreira zadatak, tada sistem mora kreirati Task zapis sa odgovarajućim tipom, dodijeljenim
  korisnikom i opcionim rokom.
- Sistem mora validirati da je dodijeljeni korisnik iz iste firme i da ima odgovarajuću ulogu za tip zadatka.
- Sistem ne smije dozvoliti kreiranje duplog aktivnog zadatka istog tipa za isti dokument.
- Sistem mora kreirati in-app obavještenje za korisnika kome je zadatak dodijeljen.
- Sistem mora zabilježiti dodjelu u audit logu.

---

### US-9.12 - Pregled mojih zadataka

**Opis** - Kao operator ili odobravatelj, želim imati pregled zadataka koji su mi dodijeljeni, kako bih znao šta trebam
raditi.

**Acceptance Criteria**

- Kada korisnik otvori stranicu "Moji zadaci", tada sistem mora prikazati samo zadatke dodijeljene trenutnom korisniku.
- Klik na zadatak mora otvoriti detalje relevantnog dokumenta.
- Kada nema aktivnih zadataka, sistem treba prikazati odgovarajuću poruku praznog stanja.

---

### US-9.13 - Ručno dodavanje ekstraktovanog polja

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

### US-9.14 - Notification centar i unread badge

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

### US-9.15 - Email reminder za nepročitana obavještenja

**Opis** - Kao korisnik sistema, želim dobiti email podsjetnik kada imam dugo nepročitana obavještenja, kako bih bio
obaviješten čak i kada nisam aktivan u sistemu.

**Acceptance Criteria**

- Kada postoje obavještenja koja su starija od konfigurabilnog praga (npr. 24 sata) i nisu pročitana te email još
  nije poslan, tada sistem mora automatski poslati email digest korisniku.
- Sistem mora grupirati sva nepročitana obavještenja u jedan email po korisniku kako bi se izbjegao spam.
- SMTP kredencijali moraju biti konfigurisani isključivo putem environment varijabli i ne smiju biti vidljivi u
  kodu, logovima niti frontend dijelu aplikacije.

---

### US-9.16 - Audit log za ključne akcije

**Opis** - Kao Admin ili Manager, želim imati uvid u audit log ključnih akcija na dokumentu, kako bih mogao pratiti ko
je šta radio i kada.

**Acceptance Criteria**

- Sistem mora bilježiti sljedeće akcije u audit log: dodjela zadatka, odobravanje, odbijanje, vraćanje na doradu,
  ručno dodavanje polja, otkazivanje zadatka i slanje email reminders.
- Kada Admin ili Manager otvori audit log za dokument, tada sistem mora prikazati hronološki sortiran popis akcija sa
  akterom, akcijom, timestampom i kratkim detaljima.
- Operator i odobravatelj ne smiju imati pristup audit logu.
- Audit log mora biti append-only; nije dozvoljen update ni delete.
- Audit log ne smije sadržavati osjetljive podatke kao što su lozinke, tokeni ili SMTP kredencijali.

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
