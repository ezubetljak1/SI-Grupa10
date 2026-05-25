# Definition of Done (DoD)

## Opis
Definition of Done definiše uslove pod kojima se jedan User Story smatra završenim, odnosno kada tim, Product Owner i auditorij mogu potvrditi da je funkcionalnost spremna za demonstraciju i dalji razvoj.

---

## Kriteriji za DONE

### 1. Funkcionalna implementacija
- Funkcionalnost je implementirana u skladu sa opisom User Story-a
- Svi acceptance kriteriji su u potpunosti zadovoljeni
- Feature radi u targetiranom okruženju (lokalno ili dev)
- Pokriveni su osnovni i ključni edge case-ovi

### 2. Kvalitet koda i standardi
- Kod je napisan u skladu sa dogovorenim standardima tima
- Nema očiglednih bugova ili tehničkih grešaka
- Struktura koda je jasna i razumljiva
- Izbjegnuto hardkodiranje gdje nije potrebno

### 3. Code review
- Kod je pregledan od strane najmanje jednog člana tima
- Sve primjedbe iz review-a su adresirane
- Review je evidentiran (PR, komentari, itd.)

### 4. Testiranje
- Implementirani su testovi prema test strategiji (unit/integracioni gdje ima smisla)
- Svi testovi prolaze uspješno
- Ručno testiranje potvrđuje ispravno ponašanje

### 5. Integracija i repozitorij
- Kod je commit-an na feature branch
- Feature branch je uspješno merge-an na dev branch
- Nema konflikata niti build grešaka
- Projekat se može pokrenuti bez problema

### 6. Evidencija i artefakti
- User Story status je ažuriran na Done
- Ažurirani su relevantni artefakti:
    - Product Backlog
    - Decision Log
    - AI Usage Log (ako je korišten AI)
- Postoji jasan trag implementacije i odluka

### 7. Spremnost za demo
- Funkcionalnost je spremna za demonstraciju
- Postoji jasan demo scenarij
- Tim može objasniti implementaciju i odluke

### 8. Validacija
- Product Owner prihvata implementaciju
- Funkcionalnost pruža očekivanu vrijednost

---
