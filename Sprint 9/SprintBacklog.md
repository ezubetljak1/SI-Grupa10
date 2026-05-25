## Product Backlog stavke za Sprint 9

| ID   | Naziv stavke                                | Opis                                                                                                         | Tip | Prioritet | Složenost | Status |
|------|---------------------------------------------|--------------------------------------------------------------------------------------------------------------|-----|-----------|-----------|--------|
| PB1  | Ograničenja osobe za odobravanje            | Osoba za odobravanje ima ograničene akcije i read-only pregled relevantnih dijelova dokumenta                | F   | P1        | M         | DONE   |
| PB2  | Odobravanje, odbijanje i vraćanje dokumenta | Donošenje odluke o dokumentu uz komentar, uključujući approve, reject i return for correction                | F   | P1        | M         | DONE   |
| PB3  | Historija statusa dokumenta                 | Prikaz historije statusa dokumenta, uključujući odobrenja, odbijanja i vraćanja na doradu                    | F   | P2        | M         | DONE   |
| PB4  | Slanje linka za ažuriranje lozinke na email | Slanje sigurnog email linka za ažuriranje lozinke pri kreiranju korisnika ili resetovanju lozinke            | F   | P1        | S         | DONE   |
| PB5  | Dodjela zadataka korisnicima                | Admin/Manager može dodijeliti dokument konkretnom operateru ili odobravatelju putem Task entiteta            | F   | P2        | M         | DONE   |
| PB6  | Moji zadaci                                 | Korisnik može pregledati listu zadataka koji su mu dodijeljeni, filtrirati ih po statusu i otvoriti dokument | F   | P2        | S         | DONE   |
| PB7  | Audit log za ključne akcije                 | Sistem bilježi audit trail za implementirane workflow akcije i prikazuje ga samo Admin/Manager korisnicima   | T   | P2        | M         | DONE   |
| PB8  | Komentar approver-a uz svaku akciju         | Approver može ostaviti komentar uz svaku svoju odluku (approve, reject, return) kako bi akcije bile jasne    | F   | P2        | S         | DONE   |
| PB9  | Generalni komentari na dokumentu            | Korisnici mogu ostavljati slobodne komentare na dokumentu koji nisu vezani za statusnu promjenu              | F   | P3        | S         | DONE   |
| PB10 | Zaštita zadatka od neovlaštenog preuzimanja | Samo korisnik kome je zadatak dodijeljen može raditi na tom zadatku; drugi korisnici ne mogu ga preuzeti     | F   | P2        | S         | DONE   |

---

## User Stories za Sprint 9

### US-9.1 - Ograničenje akcija za approver-a

**Opis** - Kao osoba za odobravanje, želim da sistem ograniči moje akcije u skladu sa ulogom, kako bih mogao pregledati
dokument i donijeti odluku bez rizika od nenamjernih izmjena.

**Acceptance Criteria**

- Kada osoba za odobravanje pristupi detaljima dokumenta, tada sistem mora ograničiti dostupne akcije na one koje su
  relevantne za odobravanje.
- Sistem ne smije dozvoliti odobravatelju da mijenja ekstraktovana polja ili pokreće extraction akcije.
- Sistem mora omogućiti odobravatelju pregled originalnog dokumenta, ekstraktovanih podataka, komentara i historije
  statusa.
- Backend mora blokirati nedozvoljene akcije čak i ako korisnik pokuša zaobići frontend ograničenja.

---

### US-9.2 - Komentar approver-a uz svaku akciju

**Opis** - Kao osoba za odobravanje, želim uz svaku svoju akciju ostaviti komentar, kako bi moje odluke bile jasne i
dokumentovane za sve učesnike u procesu.

**Acceptance Criteria**

- Kada approver odobri dokument, sistem mora ponuditi polje za komentar koji se čuva uz approval akciju.
- Svaki komentar mora biti sačuvan u historiji statusa dokumenta uz odgovarajuću akciju i aktora.
- Komentari moraju biti vidljivi svim korisnicima koji imaju pristup detaljima dokumenta.

---

### US-9.3 - Odobrenje dokumenta

**Opis** - Kao osoba za odobravanje, želim odobriti dokument, kako bi dokument prešao u završnu fazu.

**Acceptance Criteria**

- Kada osoba za odobravanje odobri dokument, tada sistem mora promijeniti status dokumenta u `APPROVED`.
- Sistem mora sačuvati approval akciju u historiji statusa.
- Sistem mora zabilježiti approval akciju u audit logu.
- Sistem ne smije dozvoliti odobravanje dokumenta koji nije u odgovarajućem statusu.
- Ako postoji aktivan approval task, sistem ga mora automatski završiti nakon odobravanja dokumenta.

---

### US-9.4 - Odbijanje dokumenta

**Opis** - Kao osoba za odobravanje, želim odbiti dokument uz komentar, kako bi razlog odbijanja bio jasno evidentiran.

**Acceptance Criteria**

- Kada osoba za odobravanje odbije dokument, tada sistem mora promijeniti status dokumenta u `REJECTED`.
- Sistem mora sačuvati odbijanje i komentar u historiji statusa.
- Sistem mora zabilježiti odbijanje u audit logu.
- Sistem ne smije dozvoliti odbijanje dokumenta bez navođenja komentara.
- Kada je dokument odbijen, dokument se smatra finalno odbijenim i ne vraća se automatski operateru na doradu.
- Za vraćanje dokumenta operateru na doradu koristi se odvojena akcija "Return for correction".

---

### US-9.5 - Vraćanje dokumenta na doradu

**Opis** - Kao osoba za odobravanje, želim vratiti dokument na doradu uz komentar bez finalnog odbijanja, kako bi
operater mogao izvršiti potrebne korekcije.

**Acceptance Criteria**

- Kada approver odabere "Return for correction", tada sistem mora promijeniti status dokumenta u `NEEDS_CORRECTION`.
- Sistem mora zahtijevati komentar razloga vraćanja.
- Sistem mora sačuvati komentar i promjenu statusa u historiji statusa.
- Sistem mora zabilježiti vraćanje na doradu u audit logu.
- Dokument mora ponovo biti dostupan operateru za pregled i uređivanje ekstraktovanih polja.
- Nakon ponovne potvrde ekstrakcije, dokument se vraća u status `READY_FOR_APPROVAL`.

---

### US-9.6 - Pregled historije statusa dokumenta

**Opis** - Kao menadžer, operater ili osoba za odobravanje, želim vidjeti historiju statusa dokumenta, kako bih mogao
pratiti tok obrade.

**Acceptance Criteria**

- Kada korisnik otvori detalje dokumenta, tada sistem mora prikazati historiju statusa dokumenta.
- Kada ne postoji historija, tada sistem treba prikazati trenutno stanje dokumenta.
- Sistem mora čuvati historiju statusa trajno i ne smije dozvoliti njeno brisanje od strane korisnika.
- Sistem mora omogućiti pregled redoslijeda ključnih koraka obrade.
- Historija mora uključivati ključne workflow promjene kao što su extraction completed, extraction confirmed, approved,
  rejected i returned for correction.

---

### US-9.7 - Audit log za ključne akcije

**Opis** - Kao Admin ili Manager, želim imati uvid u audit log ključnih akcija na dokumentu, kako bih mogao pratiti ko
je šta radio i kada.

**Acceptance Criteria**

- Sistem mora bilježiti ključne implementirane workflow akcije: dodjela zadatka, pokretanje zadatka, završavanje
  zadatka, otkazivanje zadatka, uređivanje ekstraktovanog polja, odobravanje, odbijanje i vraćanje dokumenta na doradu.
- Kada Admin ili Manager otvori audit log za dokument, tada sistem mora prikazati hronološki sortiran popis akcija sa
  akterom, akcijom, timestampom i kratkim detaljima.
- Audit log mora imati pregledan/collapsible prikaz kako stranica detalja dokumenta ne bi postala nepregledna.
- Operator i odobravatelj ne smiju imati pristup audit logu.
- Audit log mora biti append-only; nije dozvoljen update ni delete.
- Audit log ne smije sadržavati osjetljive podatke kao što su lozinke, tokeni ili SMTP kredencijali.

---

### US-9.8 - Generalni komentari na dokumentu

**Opis** - Kao korisnik sistema, želim ostaviti slobodan komentar na dokumentu koji nije vezan za promjenu
statusa, kako bih mogao komunicirati napomene i pojašnjenja ostalim učesnicima.

**Acceptance Criteria**

- Kada korisnik otvori detalje dokumenta, tada sistem mora prikazati sekciju za komentare sa svim postojećim
  komentarima.
- Korisnik mora moći unijeti i poslati novi komentar bez promjene statusa dokumenta.
- Sistem mora sačuvati uz svaki komentar autora, sadržaj i timestamp.
- Komentari moraju biti prikazani hronološki i vidljivi svim korisnicima koji imaju pristup dokumentu.
- Sistem ne smije dozvoliti slanje praznog komentara.

---

### US-9.9 - Dodjela zadatka korisniku

**Opis** - Kao Admin ili Manager, želim dodijeliti dokument konkretnom operateru ili odobravatelju putem zadatka, kako
bi bilo jasno ko je odgovoran za obradu dokumenta.

**Acceptance Criteria**

- Kada Admin ili Manager kreira zadatak, tada sistem mora kreirati Task zapis sa odgovarajućim tipom, dodijeljenim
  korisnikom i opcionim rokom.
- Sistem mora validirati da je dodijeljeni korisnik iz iste firme i da ima odgovarajuću ulogu za tip zadatka.
- Sistem mora validirati da tip taska ima smisla za trenutni status dokumenta.
- Sistem mora dozvoliti dodjelu više različitih taskova za isti dokument kada to ima smisla u workflow-u.
- Sistem ne smije dozvoliti kreiranje duplog aktivnog zadatka istog tipa za isti dokument.
- Sistem mora prikazati zadatak korisniku kojem je dodijeljen na My Tasks stranici.
- Sistem mora zabilježiti dodjelu u audit logu.

---

### US-9.10 - Pregled mojih zadataka

**Opis** - Kao operator ili odobravatelj, želim imati pregled zadataka koji su mi dodijeljeni, kako bih znao šta trebam
raditi.

**Acceptance Criteria**

- Kada korisnik otvori stranicu "Moji zadaci", tada sistem mora prikazati samo zadatke dodijeljene trenutnom korisniku.
- Korisnik mora moći pokrenuti zadatak ako je zadatak u statusu `OPEN`.
- Klik na zadatak mora otvoriti detalje relevantnog dokumenta.
- Kada nema aktivnih zadataka, sistem treba prikazati odgovarajuću poruku praznog stanja.
- Sistem mora automatski završiti task nakon odgovarajuće workflow akcije.

---

### US-9.11 - Zaštita zadatka od neovlaštenog preuzimanja

**Opis** - Kao korisnik kome je zadatak dodijeljen, želim da samo ja mogu raditi na svom zadatku, kako drugi korisnici
ne bi mogli neovlašteno pokrenuti ili preuzeti posao koji mi je dodijeljen.

**Acceptance Criteria**

- Kada korisnik pokuša pokrenuti zadatak koji nije dodijeljen njemu, tada sistem mora odbiti tu akciju sa jasnom
  porukom greške.
- Sistem mora dozvoliti pokretanje zadatka isključivo korisniku kome je zadatak direktno dodijeljen.
- Backend mora enforceovati ovo ograničenje bez obzira na frontend prikaz.
- Admin i Manager mogu otkazati zadatak i dodijeliti ga drugom korisniku ako je to potrebno, ali ne mogu ga
  pokrenuti u ime drugog korisnika.
- Sistem mora zabilježiti pokušaj neovlaštenog pristupa zadatku u audit logu.

---

### US-9.12 - Slanje linka za ažuriranje lozinke na email

**Opis** - Kao korisnik sistema, želim da prilikom kreiranja korisnika ili resetovanja lozinke dobijem email sa linkom
za ažuriranje lozinke, kako bih na siguran način mogao postaviti novu lozinku i pristupiti sistemu.

**Acceptance Criteria**

- Kada se kreira novi korisnik ili resetuje lozinka, sistem mora poslati email korisniku sa jedinstvenim linkom za
  ažuriranje lozinke.
- Link za ažuriranje lozinke mora biti vremenski ograničen i isteći nakon definisanog perioda.
- Sistem ne smije slati privremene šifre putem emaila.
- Email mora sadržavati jasne instrukcije za postavljanje nove lozinke putem dostavljenog linka.

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
