# Sprint Backlog

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

| ID  | Naziv stavke                | Opis                                                                           | Tip | Prioritet | Složenost | Status | Sprint |
|-----|-----------------------------|--------------------------------------------------------------------------------|-----|-----------|-----------|--------|--------|
| S28 | OCR integracija             | Integracija OCR servisa za izdvajanje teksta iz dokumenata                     | T   | P1        | L         | DONE   | 6      |
| S29 | AI izdvajanje (ekstrakcija) | Automatsko izdvajanje ključnih podataka iz dokumenta (dobavljač, iznos, datum) | F   | P1        | L         | DONE   | 6      |
| S30 | Mapping podataka            | Mapiranje izdvojenih podataka u interne modele sistema                         | T   | P1        | M         | DONE   | 6      |
| S31 | Spremanje podataka          | Čuvanje izdvojenih podataka u bazi                                             | T   | P1        | M         | DONE   | 6      |
| S32 | UI izdvajanje               | Prikaz automatski izdvojenih podataka korisniku                                | F   | P1        | M         | DONE   | 6      |
| S33 | Klasifikacija dokumenta     | Razlikovanje tipa dokumenta (račun ili ostalo)                                 | F   | P2        | S         | DONE   | 6      |
| S34 | Error handling AI           | Obrada grešaka iz eksternih servisa (OCR/AI)                                   | T   | P2        | M         | DONE   | 6      |
| S35 | Test OCR                    | Testiranje procesa izdvajanja (ekstrakcije) i obrade dokumenata                | T   | P2        | M         | DONE   | 6      |

---

