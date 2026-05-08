# Sprint Backlog

## Product Backlog stavke za Sprint 7

| ID  | Naziv stavke           | Opis                                                      | Tip | Prioritet | Složenost | Status | Sprint |
|-----|------------------------|-----------------------------------------------------------|-----|-----------|-----------|--------|--------|
| S36 | Edit podataka          | Omogućavanje korisniku da ručno ispravi izdvojene podatke | F   | P1        | M         | TODO   | 7      |
| S37 | Validacija polja       | Provjera da su sva obavezna polja popunjena               | T   | P1        | S         | TODO   | 7      |
| S38 | Validacija formata     | Validacija formata datuma i numeričkih vrijednosti        | T   | P1        | S         | TODO   | 7      |
| S39 | Matematička validacija | Provjera konzistentnosti iznosa (PDV, subtotal, ukupno)   | T   | P2        | M         | TODO   | 7      |
| S40 | Spremanje validacije   | Čuvanje validiranih i korigovanih podataka                | T   | P1        | M         | TODO   | 7      |
| S41 | Test validacije        | Testiranje validacije i korekcije podataka                | T   | P2        | M         | TODO   | 7      |

---

## User Stories za Sprint 7

### US-7.1 — Ručna korekcija izdvojenih polja

| Polje                                     | Sadržaj                                                                                                               |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| ID storyja                                | US-7.1                                                                                                                |
| Naziv storyja                             | Ručna korekcija izdvojenih polja                                                                                      |
| Opis                                      | Kao računovođa, želim moći ručno korigovati izdvojena polja, kako bih ispravio potencijalne greške automatske obrade. |
| Poslovna vrijednost                       | Omogućava human-in-the-loop pristup i podiže pouzdanost podataka prije nastavka procesa.                              |
| Prioritet                                 | Visok                                                                                                                 |
| Pretpostavke i otvorena pitanja           | Potrebno je definisati koja polja su editabilna.                                                                      |
| Veze sa drugim storyjima ili zavisnostima | Sprint 6, posebno US-6.2 i US-6.3                                                                                     |

**Acceptance Criteria**

- Kada računovođa otvori izdvojene podatke, ako odabere polje za izmjenu, tada sistem mora omogućiti ručnu korekciju.
- Kada računovođa izmijeni vrijednost polja, tada sistem mora ažurirati prikaz nakon potvrde izmjene.
- Kada se unese nevalidna vrijednost, tada sistem ne smije dozvoliti spremanje i mora prikazati validacionu poruku (npr.
  neispravan format datuma).
- Korisnik treba dobiti potvrdu da su izmjene uspješno sačuvane.

### US-7.2 — Validacija obaveznih polja

| Polje                                     | Sadržaj                                                                                                         |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| ID storyja                                | US-7.2                                                                                                          |
| Naziv storyja                             | Validacija obaveznih polja                                                                                      |
| Opis                                      | Kao računovođa, želim da sistem provjeri da li su obavezna polja popunjena, kako dokument ne bi ostao nepotpun. |
| Poslovna vrijednost                       | Sprječava da dokument sa nepotpunim podacima uđe u naredne faze procesa.                                        |
| Prioritet                                 | Visok                                                                                                           |
| Pretpostavke i otvorena pitanja           | Potrebno je finalno usaglasiti skup obaveznih polja.                                                            |
| Veze sa drugim storyjima ili zavisnostima | US-7.1                                                                                                          |

**Acceptance Criteria**

- Kada korisnik pokuša spremiti dokument, tada sistem mora provjeriti da li su sva obavezna polja popunjena.
- Kada neko od obaveznih polja nije popunjeno, tada sistem ne smije dozvoliti spremanje i mora jasno označiti koja polja
  nedostaju.
- Sistem ne smije dozvoliti prelazak dokumenta u naredni korak bez obaveznih podataka.

### US-7.3 — Validacija formata podataka

| Polje                                     | Sadržaj                                                                                                                |
|-------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| ID storyja                                | US-7.3                                                                                                                 |
| Naziv storyja                             | Validacija formata podataka                                                                                            |
| Opis                                      | Kao računovođa, želim da sistem validira format datuma i numeričkih vrijednosti, kako bih lakše uočio neispravan unos. |
| Poslovna vrijednost                       | Smanjuje rizik od neispravnih podataka u daljem toku obrade.                                                           |
| Prioritet                                 | Visok                                                                                                                  |
| Pretpostavke i otvorena pitanja           | Potrebno je definisati očekivane formate datuma i brojčanih vrijednosti.                                               |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2                                                                                                         |

**Acceptance Criteria**

- Kada korisnik unese datum ili brojčanu vrijednost, ako format nije ispravan, tada sistem mora prikazati validacionu
  poruku.
- Sistem mora omogućiti spremanje samo validno formatiranih podataka.
- Korisnik treba dobiti informaciju koje polje ima neispravan format.

### US-7.4 — Matematička provjera iznosa

| Polje                                     | Sadržaj                                                                                                       |
|-------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| ID storyja                                | US-7.4                                                                                                        |
| Naziv storyja                             | Matematička provjera iznosa                                                                                   |
| Opis                                      | Kao računovođa, želim da sistem provjeri osnovnu matematičku ispravnost iznosa, kako bih otkrio nelogičnosti. |
| Poslovna vrijednost                       | Pomaže u otkrivanju grešaka u iznosima prije odobravanja i XML generisanja.                                   |
| Prioritet                                 | Srednji                                                                                                       |
| Pretpostavke i otvorena pitanja           | Nije još definisano da li se provjeravaju samo ukupan iznos i PDV ili i dodatna računanja.                    |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2, US-7.3                                                                                        |

**Acceptance Criteria**

- Kada se unesu relevantni iznosi, ako korisnik sačuva dokument, tada sistem mora izvršiti osnovnu matematičku provjeru.
- Kada matematička provjera nije zadovoljena, tada sistem mora upozoriti korisnika i označiti problematična polja.
- Kada su iznosi validni, tada sistem mora dozvoliti spremanje bez upozorenja.

### US-7.5 — Spremanje potvrđenih podataka

| Polje                                     | Sadržaj                                                                                                                   |
|-------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| ID storyja                                | US-7.5                                                                                                                    |
| Naziv storyja                             | Spremanje potvrđenih podataka                                                                                             |
| Opis                                      | Kao računovođa, želim spremiti pregledane i eventualno korigovane podatke, kako bi dokument bio spreman za naredni korak. |
| Poslovna vrijednost                       | Omogućava prelazak iz faze provjere u narednu fazu procesa.                                                               |
| Prioritet                                 | Visok                                                                                                                     |
| Pretpostavke i otvorena pitanja           | Potrebno je odlučiti da li sistem čuva i oznaku da su polja ručno mijenjana.                                              |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2, US-7.3                                                                                                    |

**Acceptance Criteria**

- Kada su sva validaciona pravila zadovoljena, ako računovođa sačuva podatke, tada sistem mora spremiti potvrđene
  vrijednosti.
- Kada je spremanje uspješno, tada korisnik treba dobiti tekstualnu potvrdu.
- Sistem mora omogućiti da dokument bude označen kao spreman za naredni korak.
- Sistem ne smije izgubiti prethodno unesene korekcije nakon uspješnog spremanja.

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

