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

<!-- 

| S58 | Proširenje tipova dokumenata | Proširenje postojećeg upload i document metadata toka na invoice, receipt, bank statement, form i Other/Auto classify | F | P1 | M | DONE |
| S59 | Multi-processor OCR/AI routing | Proširenje postojećeg OCR/AI pipeline-a tako da koristi odgovarajući Google Document AI procesor prema tipu dokumenta | T | P1 | L | DONE |
| S60 | Auto-klasifikacija i manual review | Proširenje osnovne klasifikacije dokumenta u classifier flow sa ručnom potvrdom kada sistem nije dovoljno siguran | F | P1 | L | DONE |
| S61 | Classification metadata | Čuvanje i prikaz detektovanog tipa dokumenta, confidence vrijednosti i korištenog procesora | T | P2 | M | DONE |
| S62 | Type-aware extraction validacija | Proširenje ranijih extraction validacija tako da pravila zavise od tipa dokumenta | T | P1 | L | DONE |
| S63 | FE multi-type document flow | Proširenje upload, list i detail stranica za nove tipove dokumenata, classification review i korisničke poruke | F | P1 | M | DONE |
| S64 | Korekcije Sprint 7 validacije | Dorada ranijih formatnih i matematičkih validacija prema PO komentarima | B | P1 | M | DONE |
| S65 | Test multi-type processing flow | Integracijski testovi za prošireni document type, classifier, manual review i type-aware validation flow | T | P2 | M | DONE |

 -->

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

<!--

### US-8.13 - Proširenje tipova dokumenata pri uploadu

**Opis** - Kao operater, želim pri uploadu odabrati precizniji tip dokumenta, kako bi sistem mogao primijeniti odgovarajući OCR/AI tok obrade umjesto osnovne MVP podjele na račun i ostalo.

**Veza sa ranijim storyjima** - Proširenje US-5.1, US-5.4 i US-6.4.

**Acceptance Criteria**

- Kada korisnik uploaduje dokument, tada sistem mora omogućiti izbor tipa dokumenta.
- Sistem mora podržati tipove dokumenta: invoice, receipt, bank statement, form i Other/Auto classify.
- Kada korisnik odabere konkretan tip dokumenta, tada sistem mora sačuvati taj tip uz dokument.
- Kada korisnik odabere Other/Auto classify, tada sistem mora tretirati dokument kao kandidat za automatsku klasifikaciju.
- Sistem ne smije dozvoliti upload sa internim ili nepodržanim tipom dokumenta.
- Korisnik treba vidjeti čitljiv naziv tipa dokumenta na listi i detaljima dokumenta.

---

### US-8.14 - Proširenje OCR/AI obrade na više Document AI procesora

**Opis** - Kao korisnik sistema, želim da sistem obradi različite tipove dokumenata odgovarajućim OCR/AI procesorom, kako bi ekstrakcija bila preciznija nego u osnovnoj MVP verziji obrade.

**Veza sa ranijim storyjima** - Proširenje US-6.1, US-6.2 i US-6.3.

**Acceptance Criteria**

- Kada je dokument tipa invoice, tada sistem mora koristiti invoice Document AI procesor.
- Kada je dokument tipa receipt, tada sistem mora koristiti receipt/expense Document AI procesor.
- Kada je dokument tipa bank statement, tada sistem mora koristiti bank statement Document AI procesor.
- Kada je dokument tipa form, tada sistem mora koristiti form parser Document AI procesor.
- Sistem mora centralizovano odrediti processor ID na osnovu tipa dokumenta.
- Ako processor ID nije konfigurisan, sistem mora vratiti kontrolisanu grešku umjesto neobrađene sistemske greške.
- Sistem mora sačuvati informaciju koji processor je korišten za obradu dokumenta.
- Postojeći extraction flow mora ostati isti za korisnika, bez uvođenja odvojenih stranica ili potpuno različitih tokova po tipu dokumenta.

---

### US-8.15 - Proširenje osnovne klasifikacije dokumenta na auto-classification flow

**Opis** - Kao računovođa, želim da sistem automatski pokuša prepoznati stvarni tip dokumenta kada dokument nije unaprijed precizno označen, kako bih imao bolji tok obrade od osnovne klasifikacije “račun ili ostalo”.

**Veza sa ranijim storyjima** - Proširenje US-6.4.

**Acceptance Criteria**

- Kada je dokument uploadovan kao Other/Auto classify, tada sistem mora prvo pokrenuti classifier procesor.
- Ako classifier prepozna podržan tip dokumenta sa dovoljnom confidence vrijednošću, tada sistem mora postaviti detektovani tip dokumenta.
- Ako classifier prepozna invoice, receipt, bank statement ili form sa dovoljnom sigurnošću, tada sistem mora nastaviti ekstrakciju odgovarajućim parserom.
- Sistem mora sačuvati detektovani tip dokumenta i confidence vrijednost klasifikacije.
- Sistem ne smije automatski nastaviti ekstrakciju ako classifier nije dovoljno siguran.
- Kada classifier nije dovoljno siguran, dokument mora preći u status koji jasno označava da je potreban ručni pregled tipa.
- Korisnik treba dobiti jasnu povratnu informaciju da dokument zahtijeva manual classification review.

---

### US-8.16 - Ručna potvrda tipa dokumenta nakon nesigurne klasifikacije

**Opis** - Kao računovođa, želim ručno potvrditi tip dokumenta kada ga sistem ne može dovoljno sigurno klasifikovati, kako bih mogao nastaviti OCR/AI obradu bez pogrešnog parsera.

**Veza sa ranijim storyjima** - Proširenje US-6.4 i nastavak US-7.8 u smislu kontrolisanog prelaska dokumenta u naredni korak.

**Acceptance Criteria**

- Kada classifier ne može dovoljno sigurno odrediti tip dokumenta, tada sistem mora postaviti dokument u status `NEEDS_CLASSIFICATION_REVIEW`.
- Kada je dokument u statusu `NEEDS_CLASSIFICATION_REVIEW`, tada sistem ne smije dozvoliti pokretanje ekstrakcije dok tip nije ručno potvrđen.
- Korisnik mora moći odabrati jedan od podržanih finalnih tipova: invoice, receipt, bank statement ili form.
- Sistem ne smije dozvoliti da se kao finalni ručno potvrđeni tip izabere Other ili Unknown.
- Nakon uspješne ručne potvrde tipa, sistem mora vratiti dokument u status iz kojeg se ekstrakcija može ponovo pokrenuti.
- Korisnik treba dobiti potvrdu da je tip dokumenta uspješno potvrđen.
- Ručna potvrda tipa ne smije brisati originalni dokument niti postojeće osnovne metapodatke dokumenta.

---

### US-8.17 - Type-aware validacija ekstraktovanih polja

**Opis** - Kao računovođa, želim da sistem validira ekstraktovana polja prema tipu dokumenta, kako pravila za fakture ne bi pogrešno blokirala račune, bankovne izvode ili forme.

**Veza sa ranijim storyjima** - Proširenje US-7.2, US-7.3, US-7.4, US-7.6 i US-7.7.

**Acceptance Criteria**

- Kada je dokument tipa invoice, tada sistem mora primijeniti invoice required field pravila.
- Kada je dokument tipa receipt, tada sistem mora primijeniti validaciju prilagođenu računima/expense dokumentima.
- Kada je dokument tipa bank statement, tada sistem mora provjeriti osnovna polja potrebna za bankovni izvod.
- Kada je dokument tipa form, tada sistem ne smije zahtijevati invoice-specific required polja.
- Placeholder required polja se trebaju kreirati samo za tipove dokumenata za koje postoje definisana required pravila.
- Low-confidence validacija mora zavisiti od tipa dokumenta.
- Confirm extraction akcija mora biti blokirana ako validacija za odgovarajući tip dokumenta ne prođe.
- Backend mora ostati finalna validacijska tačka, bez obzira na frontend validaciju.

---

### US-8.18 - Prikaz proširenog document-processing toka na frontendu

**Opis** - Kao korisnik sistema, želim da upload, lista dokumenata i detalji dokumenta jasno prikazuju nove tipove dokumenata, classification metadata i review status, kako bih razumio u kojem koraku obrade se dokument nalazi.

**Veza sa ranijim storyjima** - Proširenje US-5.5, US-5.6, US-6.2, US-6.5 i US-7.8.

**Acceptance Criteria**

- Upload forma mora prikazati sve podržane tipove dokumenata.
- Lista dokumenata mora prikazati čitljiv naziv tipa dokumenta.
- Lista dokumenata mora jasno označiti dokumente koji zahtijevaju classification review.
- Status badge komponenta mora podržati status `NEEDS_CLASSIFICATION_REVIEW`.
- Detail stranica mora prikazati classification metadata samo kada je classifier stvarno korišten ili kada dokument zahtijeva review.
- Detail stranica mora prikazati detektovani tip dokumenta i confidence vrijednost kada su dostupni.
- Run extraction akcija mora biti onemogućena ili sakrivena dok dokument zahtijeva potvrdu tipa.
- Frontend mora prikazati korisniku razumljivu poruku za backend grešku `DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED`.

---

### US-8.19 - Dorada validacijskih poruka i invoice amount consistency pravila

**Opis** - Kao računovođa, želim jasnije validacijske poruke za datume i numeričke vrijednosti, te provjeru konzistentnosti invoice iznosa, kako bih mogao sigurnije potvrditi ekstraktovane podatke.

**Veza sa ranijim storyjima** - Dorada US-7.3 i US-7.4 prema povratnoj informaciji Product Ownera.

**Acceptance Criteria**

- Poruka za neispravan datum mora jasno navesti podržane formate: `YYYY-MM-DD`, `DD.MM.YYYY` i `DD/MM/YYYY`.
- Sistem mora jasno naglasiti da se kod formata `DD.MM.YYYY` i `DD/MM/YYYY` koristi evropski redoslijed dana i mjeseca.
- Numerička polja moraju prihvatati vrijednosti bez valute i dodatnog teksta.
- Sistem mora odbiti vrijednosti poput `1500 KM` za numerička polja, ali uz jasnu validacijsku poruku koja govori da se iznos i valuta navode odvojeno.
- Sistem mora prihvatiti validne numeričke vrijednosti kao što su `1500`, `1500.50` ili `1500,50`.
- Sistem ne smije dozvoliti da `total_amount` bude manji od komponentnih iznosa kada su relevantna polja dostupna.
- Sistem mora provjeriti konzistentnost `total_amount`, `net_amount` i `vat_amount` kada su sva tri polja dostupna.
- Pravila moraju važiti i pri ručnoj izmjeni polja i pri confirm extraction koraku.

-->

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
