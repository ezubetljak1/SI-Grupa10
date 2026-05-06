## Sprint 5

# 1. Planirani sprint goal

Isporučiti prvi funkcionalni inkrement: osnovni unos dokumenta i pregled zaprimljenog dokumenta.

# 2. Šta je završeno

Tim je uspješno realizirao sve planirane aktivnosti uključujući:

- Upload dokumenta

- Validaciju uploadovanog dokumenta

- Pohranu dokumenta i metapodataka

- Prikaz liste uploadovanih dokumenata

- Prikaz detalja pojedinačnog dokumenta

- Deployment i testiranje

- Decision log i AI log

# 3. Šta nije završeno

Sve stavke za Sprint 5 su završene.

# 4. Demonstrirane funkcionalnosti ili artefakti

- AI Usage Log
- Decision Log
- Upload dokumenta
- Prikaz liste dokumenata i detalja dokumenta
- Validacije dokumenata po veličini i tipu
- Pohrana dokumenata i metapodataka

# 5. Glavni problemi i blokeri

Nije bilo značajnih problema ili blokera tokom Sprinta 5. Tim je uspješno surađivao i rješavao sve izazove koji su se
pojavljivali.

# 6. Ključne odluke donesene u sprintu

- Fizička pohrana uploadovanih fajlova - tim je odlučio da se uploadovani dokumenti pohranjuju na fizičkom disku umjesto
  u bazi podataka, što je pojednostavilo implementaciju i smanjilo kompleksnost sistema.
- Validacija dokumenata - tim je odlučioda implementira validaciju i na frontend-u (brza povratna informacija) i na
  backend-u (integritet podataka)
- API dokumentacija - tim je odlučio da koristi Swagger za dokumentaciju API-ja, što je olakšalo razvoj i testiranje.
- Brisanje fizičkog fajla u slučaju neuspjele transakcije - tim je implementirao mehanizam za brisanje fizičkog fajla
  ako dođe do greške tokom obrade.
- API/integration testovi za document module - tim je odlučio da implementira API i integration testove za document
  module kako bi osigurao stabilnost i pouzdanost sistema.

# 7. Povratna informacija Product Ownera

Product Owner je ukazao na neažuriran Sprint Backlog file, konkretno za statuse taskova. Osim toga,
Product Owner je zadovoljan sa demonstriranim inkrementom, te je dao maksimalan broj bodova.

# 8. Zaključak za naredni sprint

Obzirom da su sve planirane stavke za Sprint 5 uspješno obavljene, tim može preći na sljedeći sprint u kojem je glavni
cilj OCR obrada i AI ekstrakcija podataka iz dokumnata.  