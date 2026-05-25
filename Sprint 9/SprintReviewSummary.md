## Sprint 9

# 1. Planirani sprint goal

Omogućiti kompletan approval workflow nad dokumentima kroz pregled dokumenata za odobravanje,
odobrenje/odbijanje/vraćanje akcije sa komentarima, generalne komentare i historiju statusa.
Uz to, omogućiti upravljanje radnim zadacima sa zaštitom dodjele,
audit log za ključne akcije te slanje linka za ažuriranje lozinke na mail.

# 2. Šta je završeno

Tim je uspješno realizovao sve planirane funkcionalnosti navedene u sprint goalu.

# 3. Šta nije završeno

Nema nezavršenih zadataka, sve planirane funkcionalnosti su implementirane i testirane.

# 4. Demonstrirane funkcionalnosti ili artefakti

Tim je funkcionalnosti demonstrirao prateći User Story-e iz Sprint Backlog dokumenta. To uključuje logiku i akcije
approval uloge u sistemu, task assignment, status history, audit log te komentare. Također, demonstrirana je AI
ekstrakcija podataka iz uplatnica koje su članovi tima ručno popunili.

Od artefakata, demonstrirana je sva potrebna dokumentacija.

# 5. Glavni problemi i blokeri

Najveći izazov tokom sprinta je bio kraći vremenski rok za implementaciju i testiranje svih funkcionalnosti, ali tim je
uspio efikasno organizirati rad i isporučiti sve na vrijeme. Nije bilo značajnih blokera koji su utjecali na napredak
sprinta.

# 6. Ključne odluke donesene u sprintu

Detaljan opis ključnih odluka se nalazi u Decision Log dokumentu, počevši od odluke DL-033. Odluke uključuju:

- Uvođenje workflow foundation modela kroz posebne tabele
- Centralizacija promjena statusa kroz DocumentStatusTransitionService
- Status history i komentari kao odvojeni user-facing workflow trag
- Centralni WorkflowPermissionService i audit log ograničen na Admin/Manager
- Task assignment kao zaseban workflow sloj iznad document/extraction flow-a

# 7. Povratna informacija Product Ownera

Product Owner je izrazio zadovoljstvo isporučenim funkcionalnostima i artefaktima te je ocijenio sprint sa maksimalnim
brojem bodova.

# 8. Zaključak za naredni sprint

Tim može ući u završnu fazu funkcionalnosti sistema, obzirom da su sve ključne funkcionalnosti do sada ispravno
implementirane. U narednom sprintu fokus će biti na finalizaciji end-to-end obrade dokumenata te optimizaciji određenih
postojećih funkcionalnosti.
