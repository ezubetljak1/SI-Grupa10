# Sprint Backlog

## Product Backlog stavke za Sprint 9

| ID  | Naziv stavke                                 | Opis                                                                                                        | Tip | Prioritet | Složenost | Status | 
|-----|----------------------------------------------|-------------------------------------------------------------------------------------------------------------|-----|-----------|-----------|--------|
| PB1 | Ograničenja osobe za odobravanje             | Osoba za odobravanje treba da ima ograničene akcije u sistemu kao i ograničen pregled detalja o dokumentima | F   | P1        | M         | TODO   |
| PB2 | Lista dokumenata na čekanju	                 | Osoba za odobravanje treba samo da vidi listu dokumenata koji čekaju na odobrenje, a ne sve dokumente       | F   | P2        | S         | TODO   |
| PB3 | Odobravanje/odbijanje ili vraćanje dokumenta | Donošenje odluke o dokumentu uz komentar                                                                    | F   | P1        | M         | TODO   | 
| PB4 | Historija statusa dokumenta	                 | Prikaz historije statusa dokumenta, uključujući odobrenja, odbijanja i vraćanja na doradu                   | F   | P2        | M         | TODO   |
| PB5 | Obavještenja o dokumentima	                  | Obavještenja za odobravanje, odbijanje i vraćanje na doradu                                                 | F   | P2        | M         | TODO   |
| PB6 | Slanje privremene šifre na mail              | Slanje privremene šifre na mail prilikom kreiranja korisnika ili reseta lozinke                             | F   | P1        | S         | TODO   |

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

- Kada osoba za odobravanje otvori listu dokumenata, tada sistem mora prikazati samo dokumente u statusu “na
  odobrenju”.
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

<!-- Ovaj US diskutovati i implementirati ako nije nepotrebno komplikovanje -->

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
  `REJECTED`. <!-- Status REJECTED podrazumijeva kao kada je status UPLOADED (mora se opet pokrenut ekstrakcija da bi se dobila polja itd) -->
- Sistem mora sačuvati odbijanje i komentar u historiji statusa.
- Kada je dokument odbijen, sistem ga automatski vraća u prethodnu fazu
- Sistem ne smije dozvoliti odbijanje dokumenta bez navođenja komentara.

---

### US-9.6 - Vraćanje dokumenta na doradu

<!-- Ključna razlika između ovog US-a i prethodnog je u tome što odbijanje podrazumijeva da je dokument besmislen, ekstraktovani podaci nikako ne valjaju i slično. Vraćanje na NEEDS CORRECTION podrazumijeva da je operteru "promakla" sitna greška, slovo broj i slično pa da nema potrebe za ponovnom ekstrakcijom i slično --> 

**Opis** - Kao osoba za odobravanje, želim vratiti dokument na doradu uz komentar bez finalnog odbijanja, kako bi
operater mogao izvršiti manje korekcije.

**Acceptance Criteria**

- Kada approver odabere “Return for correction”, tada sistem mora promijeniti status dokumenta u `NEEDS CORRECTION`
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

<!-- Trebamo se dogovoriti hoće li obavještenja biti u sistemu real-time ili će obavještenja dolaziti na mail -->

**Opis** - Kao osoba za odobravanje, želim dobiti obavještenje kada dokument čeka moju akciju, kako bih mogao
pravovremeno reagovati.

**Acceptance Criteria**

- Kada dokument pređe u status `READY FOR APPROVAL`, tada sistem mora generisati obavještenje za korisnika koji ima
  ulogu za odobravanje dokumenata.
- Sistem mora omogućiti da obavještenje bude povezano sa konkretnim dokumentom
- Korisnik treba dobiti signal da postoji nova stavka koja traži akciju.

---

### US-9.9 - Obavještenje o odbijenom ili vraćenom dokumentu

<!-- Trebamo se dogovoriti hoće li obavještenja biti u sistemu real-time ili će obavještenja dolaziti na mail -->

**Opis** - Kao operater, želim dobiti obavještenje kada je dokument odbijen ili vraćen, kako bih znao da je potrebna
ponovna ili dodatna obrada.

**Acceptance Criteria**

- Kada dokument promijeni status u `REJECTED` ili `NEEDS CORRECTION`, tada sistem mora generisati obavještenje za
  odgovornog operatera.
- Kada postoji komentar pri odbijanju, tada sistem mora povezati obavještenje sa tim komentarom.
- Korisnik treba dobiti jasnu informaciju da je potrebna dodatna obrada dokumenta.

---

### US-9.10 - Slanje privremene šifre na mail

**Opis** - Kao korisnik sistema, želim da se privremene šifre šalju na odgovarajuču mail adresu, prilikom kreiranja
korisnika ili reseta lozinke, kako bih mogao pristupiti sistemu.

**Acceptance Criteria**

- Kada se kreira novi korisnik ili resetuje lozinka, tada sistem mora generisati privremenu šifru i poslati je na
  mail adresu korisnika.
- Sistem mora osigurati da privremena šifra bude sigurna i da se ne šalje u nešifriranom obliku.
- Korisnik treba dobiti jasne instrukcije u mailu o tome kako koristiti privremenu šifru te da odmah treba postaviti
  novu šifru.

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