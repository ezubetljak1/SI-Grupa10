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

Demonstrirane funkcionalnosti uključuju:

- Registracija firme i kreiranje administratorskog naloga
- Autentifikacija korisnika
- Upravljanje korisnicima i rolama unutar firme
- Dashboard za menadžere sa pregledom korisnika i njihovih aktivnosti
- Prošireni tok obrade dokumenata sa podrškom za više tipova dokumenata, odvojene Google Document AI procesore,
  auto-klasifikaciju,

Demonstrirani artefakti uključuju:

- Sprint Goal
- Sprint Backlog
- Sprint Review Summary
- TestBook

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

Product Owner je izrazio zadovoljstvo demonstriranim funkcionalnostima i napretkom ostvarenim tokom sprinta.
Također, Product Owner je rekao da svaki član tima popuni uplatnicu sa svojim rukopisom, kako bi se pokazalo na koji
način ekstrakcija radi sa različitim rukopisima.

# 8. Zaključak za naredni sprint

Obzirom na uspješno završene zadatke i pozitivnu povratnu informaciju Product Ownera, tim može nastaviti sa
implementacijom sljedeće faze sistema. Fokus će biti na razvijanju funkcionalnosti vezanih za odobravatelja dokumenata u
sistemu (APPROVER rola).
