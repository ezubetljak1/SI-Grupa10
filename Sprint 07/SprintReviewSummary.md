## Sprint 7

# 1. Planirani sprint goal

Omogućiti ručnu korekciju i validaciju izdvojenih podataka prije daljeg toka obrade.

# 2. Šta je završeno

Svi planirani zadaci su završeni, uključujući implementaciju funkcionalnosti za ručnu korekciju i validaciju izdvojenih
podataka. Takođe, omogućeno je da se nakon korekcije i validacije ažuriraju podaci u sistemu i da se prikaže status
dokumenta nakon obrade.

# 3. Šta nije završeno

Nema nezavršenih zadataka, svi planirani zadaci su uspješno realizovani.

# 4. Demonstrirane funkcionalnosti ili artefakti

Demonstrirane funkcionalnosti uključuju:

- Ručna korekcija i validacija izdvojenih podataka putem korisničkog interfejsa
- Ažuriranje podataka u sistemu nakon korekcije i validacije
- Prikaz statusa dokumenta nakon obrade
- Isticanje nepopunjenih obaveznih polja i onih sa niskim confidence score-om na frontend-u

Demonstrirani artefakti uključuju:

- SprintGoal
- SprintBacklog
- SprintReview
- SprintRetrospective

# 5. Glavni problemi i blokeri

U ovom sprintu nije bilo značajnih problema ili blokera koji su uticali na realizaciju zadataka.

# 6. Ključne odluke donesene u sprintu

Ključne odluke donesene u ovom sprintu uključuju:

- Placeholder polja za missing required vrijednosti nakon OCR ekstrakcije
- Confirm ekstrakcije dozvoljen samo nakon review-a problematičnih polja
- Frontend prikaz review statusa i validacijskih grešaka u extraction tabeli

Detaljniji opis navedenih odluka se nalazi u DecisionLog dokuemtnu, počevši od DL-021.

# 7. Povratna informacija Product Ownera

Product Owner je izrazio zadovoljstvo nakon demonstracije funkcionalnosti i artefakata koji su realizovani u sprintu 7.
Također, naveo je tri napomene koje bi tim trebao da doradi, a to su:

- Format datuma - potrebno je korisniku koji ručno prepravlja podatke jasno naznačiti koji format datuma je podržan.
- Ekstrakcija numeričkih vrijednosti - potrebno je doraditi u smislu da OCR ne izdvaja tekst uz brojeve (na primjer
  "1500 KM"). Također, potrebno je doraditi matematičku validaciju nakon ručne korekcije.
- Testirati rad sistema u slučaju kada su neka polja u dokumentima prazna i kada dokumenti sadrže rukopis.

# 8. Zaključak za naredni sprint

Na osnovu povratne informacije Product Ownera, tim će se u narednom sprintu fokusirati na doradu funkcionalnosti za
ručnu korekciju. Osim toga, obzirom da su svi planirani zadaci realizovani, tim će se nakon dorade fokusirati na
sljedeći sprint.
