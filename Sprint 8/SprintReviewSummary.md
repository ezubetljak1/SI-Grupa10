## Sprint 8

# 1. Planirani sprint goal

Uvesti organizacioni model pristupa sistemu kroz registraciju firme, kreiranje administratorskog naloga i osnovnu
autentifikaciju korisnika. Pored toga, cilj je uspostaviti model upravljanja korisnicima i rolama unutar firme, te
osigurati pristup funkcionalnostima prema odgovornostima korisnika.

Pored organizacionog i sigurnosnog modela, Sprint 8 uključuje i proširenje ranije implementiranog toka obrade
dokumenata. Postojeći upload, OCR/AI extraction, klasifikacija, validacija i review flow prošireni su podrškom za više
tipova dokumenata, odvojene Google Document AI procesore, auto-klasifikaciju, ručni pregled klasifikacije i validaciju
ekstraktovanih polja u zavisnosti od tipa dokumenta.

# 2. Šta je završeno

Svi planirani zadaci su završeni što uključuje autentifikaciju, autorizaciju na osnovu rola, upravljanje korisnicima,
i dashboard za menadžere. Osim tih sigunosnih i organizacionih funkcionalnosti, završeni su i svi zadaci vezani za
proširenje toka obrade dokumenata, uključujući podršku za više tipova dokumenata, odvojene Google Document AI procesore,
auto-klasifikaciju, ručni pregled klasifikacije i validaciju ekstraktovanih polja u zavisnosti od tipa dokumenta.

# 3. Šta nije završeno

Za sprint 8 nema nezavršenih zadataka, svi planirani zadaci su uspješno realizovani.

# 4. Demonstrirane funkcionalnosti ili artefakti

# 5. Glavni problemi i blokeri

Za implementaciju organizacionog i sigurnosnog modela problema nije bilo. Manji problem se javio zbog ograničenja kako
Google Document AI parsira podatke te je trebalo odlučiti na koji način korisniku predstaviti da mora validirati datum.

# 6. Ključne odluke donesene u sprintu

Sve ključne odluke su navedene u dokumentu DecisionLog, počevši od LD-025, a uključuju:

- Odvojeni Google Document AI procesori po tipu dokumenta
- Uvođenje auto-klasifikacije dokumenata nakon pokretanja OCR ekstrakcije
- Čuvanje rola u sistemu kroz aplikacijsku bazu
- Provjera autorizacije na nivou servisa koristeći odgovarajuće metode

# 7. Povratna informacija Product Ownera

# 8. Zaključak za naredni sprint

