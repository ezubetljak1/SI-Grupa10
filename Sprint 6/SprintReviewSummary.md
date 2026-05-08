## Sprint 6

# 1. Planirani sprint goal

Omogućiti OCR obradu, osnovno AI izdvajanje i prikaz izdvojenih podataka.

# 2. Šta je završeno

Svi planirani zadaci su završeni, uključujući implementaciju OCR obrade i osnovnog AI izdvajanja.
Osim toga, omogućen je prikaz izdvojenih podataka kao i status dokumenta u zavisnosti od rezultata obrade.

# 3. Šta nije završeno

- Nema nezavršenih zadataka, svi planirani zadaci su uspješno realizovani.

# 4. Demonstrirane funkcionalnosti ili artefakti

Demonstrirane funkcionalnosti uključuju:

- Ručno pokretanje OCR obrade nad dokumentom.
- Prikaz izdvojenih podataka nakon OCR obrade.
- Klasifikacija tipa dokumenta na osnovu rezultata obrade.
- Prikaz statusa dokumenta
- Poruka o tome da li je obrada uspješna ili nije.

Demonstrirani artefakti uključuju:

- AI Usage Log
- Decision Log
- Sprint Review
- Sprint Retrospective
- Sprint Backlog
- Test Book

# 5. Glavni problemi i blokeri

Značajnijih problema tokom sprinta nije bilo. Jedini problem je bio oko izbora OCR/AI servisa jer on direktno utiče
na kvalitet i funkcionalnosti koje možemo implementirati. Nakon detaljne analize, tim je donio odluke koje su
dokumentovane u Decision Logu.

# 6. Ključne odluke donesene u sprintu

Sve odluke su dokumentovane u Decision Logu, a ključne odluke uključuju:

- Izbor OCR/AI servisa: Tim je odlučio koristiti Google Document AI.
- Ručno pokretanje OCR obrade: Tim je odlučio da se OCR obrada pokreće ručno, kako bi se izbjeglo nepotrebno trošenje
  resursa.
- Izdvajanje OCR logike u poseban provider sloj: Tim je odlučio izdvojiti OCR logiku dok konkretna implementacija
  koristi GoogleDocumentAiProvider
- Testiranje ekstrakcije bez stvarnog pozivanja Google servisa: Tim je odlučio koristiti Integration testove uz
  mockovani Ocr provider.

# 7. Povratna informacija Product Ownera

Product Owner je izrazio zadovoljstvo isporučenim funkcionalnostima i artefaktima, te je ocijenio sprint maksimalnim
brojem bodova.

# 8. Zaključak za naredni sprint

U ovom sprintu je integracija OCR/AI servisa uspješno implementirana i testirana tako da se tim u narednom sprintu može
fokusirati na implementaciju dodatnih funkcionalnosti.