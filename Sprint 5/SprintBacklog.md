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

| ID  | Naziv stavke          | Opis                                                                           | Tip | Prioritet | Složenost | Status | Sprint |
|-----|-----------------------|--------------------------------------------------------------------------------|-----|-----------|-----------|--------|--------|
| S19 | Upload dokumenta      | Implementacija funkcionalnosti za upload PDF i slikovnih dokumenata putem UI-a | F   | P1        | M         | TODO   | 5      |
| S20 | Validacija fajla      | Provjera tipa, veličine i ispravnosti dokumenta prije prihvatanja              | T   | P1        | S         | TODO   | 5      |
| S21 | Pohrana dokumenta     | Spremanje dokumenta i metapodataka u sistem za dalju obradu                    | T   | P1        | M         | TODO   | 5      |
| S22 | Lista dokumenata      | Prikaz svih uploadovanih dokumenata sa osnovnim informacijama i statusima      | F   | P1        | M         | TODO   | 5      |
| S23 | Detalji dokumenta     | Prikaz pojedinačnog dokumenta i njegovog sadržaja korisniku                    | F   | P1        | M         | TODO   | 5      |
| S24 | Error handling upload | Prikaz jasnih poruka o greškama prilikom neuspješnog uploada                   | F   | P2        | S         | TODO   | 5      |
| S25 | Testovi upload        | Pisanje testova za upload i prikaz dokumenata                                  | T   | P2        | M         | TODO   | 5      |
| S26 | Decision Log          | Evidentiranje ključnih tehničkih i projektnih odluka tokom razvoja             | D   | P1        | S         | TODO   | 5      |
| S27 | AI Usage Log          | Evidentiranje načina i svrhe korištenja AI alata tokom projekta                | D   | P1        | S         | TODO   | 5      |
