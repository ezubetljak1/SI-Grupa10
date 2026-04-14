# Product Backlog
## Legenda
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

| ID | Naziv stavke | Opis | Tip | Prioritet | Složenost | Status | Sprint |
|----|--------------|-------------|-----|-----------|------------|--------|--------|
| S1 | Team Charter | Definisanje strukture tima, načina komunikacije, odgovornosti i pravila rada tokom projekta | D | P1 | S | DONE | 1 |
| S2 | Product Vision | Opis problema, ciljeva sistema, ciljne grupe korisnika i definisanje MVP opsega | D | P1 | S | DONE | 1 |
| S3 | Stakeholder Map | Identifikacija svih relevantnih stakeholdera, njihovih interesa i uticaja na sistem | R | P1 | S | DONE | 1 |
| S4 | Početni Product Backlog | Kreiranje inicijalne liste funkcionalnosti i zadataka za razvoj sistema | D | P1 | M | DONE | 1 |
| S5 | User Stories | Razrada funkcionalnosti sistema kroz user stories iz perspektive korisnika | D | P1 | M | DONE | 2 |
| S6 | Acceptance Criteria | Definisanje mjerljivih kriterija prihvata za svaki user story | D | P1 | M | DONE | 2 |
| S7 | Prioritizacija backloga | Rangiranje backlog stavki prema poslovnoj vrijednosti i važnosti za MVP | R | P1 | S | DONE | 2 |
| S8 | NFR lista | Definisanje nefunkcionalnih zahtjeva (performanse, sigurnost, pouzdanost) | D | P2 | S | DONE | 2 |
| S9 | Risk Register | Identifikacija potencijalnih rizika projekta i definisanje strategija mitigacije | D | P1 | M | DONE | 3 |
| S10 | Domain Model | Modeliranje ključnih entiteta sistema (dokument, korisnik, firma, status) i njihovih relacija | R | P1 | M | DONE | 3 |
| S11 | Use Case Model | Definisanje glavnih scenarija korištenja sistema kroz use case dijagrame | D | P1 | M | DONE | 3 |
| S12 | Architecture Overview | Definisanje arhitekture sistema, komponenti i međusobne komunikacije | T | P1 | L | DONE | 3 |
| S13 | Test Strategy | Definisanje pristupa testiranju (unit, integraciono, sistemsko testiranje) | D | P1 | M | DONE | 3 |
| S14 | Definition of Done | Definisanje kriterija kada se zadatak smatra završenim i spremnim za isporuku | D | P3 | S | TODO | 4 |
| S15 | Initial Release Plan | Planiranje inkremenata i raspodjela funkcionalnosti po sprintovima | D | P1 | M | TODO | 4 |
| S16 | Backend skeleton | Postavljanje osnovne strukture backend aplikacije i API sloja | T | P1 | L | TODO | 4 |
| S17 | Frontend skeleton | Postavljanje osnovne strukture frontend aplikacije i UI komponenti | T | P1 | M | TODO | 4 |
| S18 | Repo setup | Postavljanje repozitorija, branch strategije i osnovnih razvojnih pravila | T | P2 | S | TODO | 4 |
| S19 | Upload dokumenta | Implementacija funkcionalnosti za upload PDF i slikovnih dokumenata putem UI-a | F | P1 | M | TODO | 5 |
| S20 | Validacija fajla | Provjera tipa, veličine i ispravnosti dokumenta prije prihvatanja | T | P1 | S | TODO | 5 |
| S21 | Pohrana dokumenta | Spremanje dokumenta i metapodataka u sistem za dalju obradu | T | P1 | M | TODO | 5 |
| S22 | Lista dokumenata | Prikaz svih uploadovanih dokumenata sa osnovnim informacijama i statusima | F | P1 | M | TODO | 5 |
| S23 | Detalji dokumenta | Prikaz pojedinačnog dokumenta i njegovog sadržaja korisniku | F | P1 | M | TODO | 5 |
| S24 | Error handling upload | Prikaz jasnih poruka o greškama prilikom neuspješnog uploada | F | P2 | S | TODO | 5 |
| S25 | Testovi upload | Pisanje testova za upload i prikaz dokumenata | T | P2 | M | TODO | 5 |
| S26 | Decision Log | Evidentiranje ključnih tehničkih i projektnih odluka tokom razvoja | D | P1 | S | TODO | 5 |
| S27 | AI Usage Log | Evidentiranje načina i svrhe korištenja AI alata tokom projekta | D | P1 | S | TODO | 5 |
| S28 | OCR integracija | Integracija OCR servisa za izdvajanje teksta iz dokumenata | T | P1 | L | TODO | 6 |
| S29 | AI izdvajanje (ekstrakcija) | Automatsko izdvajanje ključnih podataka iz dokumenta (dobavljač, iznos, datum) | F | P1 | L | TODO | 6 |
| S30 | Mapping podataka | Mapiranje izdvojenih podataka u interne modele sistema | T | P1 | M | TODO | 6 |
| S31 | Spremanje podataka | Čuvanje izdvojenih podataka u bazi | T | P1 | M | TODO | 6 |
| S32 | UI izdvajanje | Prikaz automatski izdvojenih podataka korisniku | F | P1 | M | TODO | 6 |
| S33 | Klasifikacija dokumenta | Razlikovanje tipa dokumenta (račun ili ostalo) | F | P2 | S | TODO | 6 |
| S34 | Error handling AI | Obrada grešaka iz eksternih servisa (OCR/AI) | T | P2 | M | TODO | 6 |
| S35 | Test OCR | Testiranje procesa izdvajanja (ekstrakcije) i obrade dokumenata | T | P2 | M | TODO | 6 |
| S36 | Edit podataka | Omogućavanje korisniku da ručno ispravi izdvojene podatke | F | P1 | M | TODO | 7 |
| S37 | Validacija polja | Provjera da su sva obavezna polja popunjena | T | P1 | S | TODO | 7 |
| S38 | Validacija formata | Validacija formata datuma i numeričkih vrijednosti | T | P1 | S | TODO | 7 |
| S39 | Matematička validacija | Provjera konzistentnosti iznosa (PDV, subtotal, ukupno) | T | P2 | M | TODO | 7 |
| S40 | Spremanje validacije | Čuvanje validiranih i korigovanih podataka | T | P1 | M | TODO | 7 |
| S41 | Test validacije | Testiranje validacije i korekcije podataka | T | P2 | M | TODO | 7 |
| S42 | Model firme | Definisanje modela organizacije koja koristi sistem | T | P1 | M | TODO | 8 |
| S43 | Model korisnika | Definisanje korisničkog modela i veze sa firmom | T | P1 | M | TODO | 8 |
| S44 | Registracija firme | Omogućavanje registracije nove firme u sistemu | F | P1 | M | TODO | 8 |
| S45 | Admin nalog | Kreiranje prvog administratorskog korisnika firme | F | P1 | M | TODO | 8 |
| S46 | Login | Implementacija prijave korisnika u sistem | F | P1 | M | TODO | 8 |
| S47 | Logout | Omogućavanje odjave korisnika | F | P1 | S | TODO | 8 |
| S48 | Auth sigurnost | Implementacija sigurnosti (hashiranje lozinki, tokeni) | T | P1 | M | TODO | 8 |
| S49 | Multi-tenant zaštita | Osiguranje da korisnici vide samo podatke svoje firme | T | P1 | M | TODO | 8 |
| S50 | Test auth | Testiranje autentifikacije i autorizacije | T | P2 | M | TODO | 8 |
| S51 | Role model | Definisanje korisničkih rola u sistemu | T | P1 | M | TODO | 9 |
| S52 | Kreiranje korisnika | Omogućavanje adminu da dodaje nove korisnike | F | P1 | M | TODO | 9 |
| S53 | Dodjela rola | Dodjela rola korisnicima | F | P1 | M | TODO | 9 |
| S54 | Status model | Definisanje statusa dokumenta i tranzicija | T | P1 | M | TODO | 9 |
| S55 | Status history | Evidentiranje historije promjena statusa | F | P3 | M | TODO | 9 |
| S56 | Slanje na odobrenje | Omogućavanje slanja dokumenta na odobrenje | F | P1 | M | TODO | 9 |
| S57 | Odobravanje/odbijanje | Donošenje odluke o dokumentu uz komentar | F | P1 | M | TODO | 9 |
| S58 | Pending lista | Prikaz dokumenata koji čekaju akciju korisnika | F | P2 | S | TODO | 9 |
| S59 | Test workflow | Testiranje workflow logike i rola | T | P2 | M | TODO | 9 |
| S60 | XML mapping | Mapiranje validiranih podataka u XML strukturu | T | P1 | M | TODO | 10 |
| S61 | XML generator | Generisanje XML fajla iz podataka | F | P1 | M | TODO | 10 |
| S62 | Pregled XML | Prikaz XML sadržaja korisniku | F | P3 | S | TODO | 10 |
| S63 | Download XML | Omogućavanje preuzimanja XML fajla | F | P1 | S | TODO | 10 |
| S64 | Spremanje XML | Čuvanje XML fajla uz dokument | T | P1 | M | TODO | 10 |
| S65 | Final status | Završetak obrade dokumenta | F | P2 | S | TODO | 10 |
| S66 | Audit log | Evidencija svih ključnih akcija nad dokumentom | T | P1 | M | TODO | 10 |
| S67 | UI audit log | Prikaz audit zapisa korisniku | F | P3 | S | TODO | 10 |
| S68 | Test XML | Testiranje XML generisanja i završnog toka | T | P2 | M | TODO | 10 |
| S69 | Search | Pretraga dokumenata po različitim kriterijima | F | P2 | M | TODO | 11 |
| S70 | Filteri | Filtriranje dokumenata po statusu, tipu i datumu | F | P2 | M | TODO | 11 |
| S71 | Komentari | Dodavanje komentara na dokumente | F | P2 | M | TODO | 11 |
| S72 | Pending view | Fokusirani prikaz zadataka korisnika | F | P3 | S | TODO | 11 |
| S73 | Notifikacije | Obavještenja o promjenama statusa dokumenata | F | P3 | M | TODO | 11 |
| S74 | Testovi | Testiranje naprednih funkcionalnosti | T | P2 | M | TODO | 11 |
| S75 | Reset password | Omogućavanje resetovanja lozinke | F | P2 | M | TODO | 12 |
| S76 | Dashboard | Prikaz osnovnih statistika sistema | F | P3 | M | TODO | 12 |
| S77 | User management | Upravljanje korisničkim podacima | F | P2 | M | TODO | 12 |
| S78 | Korisnička dokumentacija | Izrada uputstva za korisnike sistema | D | P1 | M | TODO | 12 |
| S79 | Tehnička dokumentacija | Dokumentacija arhitekture i implementacije | D | P1 | M | TODO | 12 |
| S80 | Release Notes | Opis funkcionalnosti finalne verzije | D | P1 | S | TODO | 12 |
| S81 | Testiranje sistema | End-to-end testiranje ključnih tokova | T | P1 | L | TODO | 13 |
| S82 | Bug fixing | Ispravka identifikovanih grešaka | B | P1 | L | TODO | 13 |
| S83 | Stabilizacija sistema | Refaktorisanje i optimizacija sistema | T | P1 | M | TODO | 13 |
| S84 | Tehnički dug | Dokumentovanje ograničenja sistema | D | P3 | S | TODO | 13 |
| S85 | Završna demonstracija | Priprema i prezentacija finalnog rješenja | D | P1 | M | TODO | 13 |
