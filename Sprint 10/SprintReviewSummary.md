## Sprint 10

# 1. Planirani sprint goal

Finalizirati end-to-end tok obrade dokumenata kroz generisanje, pregled, preuzimanje i pohranu XML izlaza te završetak
obrade dokumenta.

Uz to, unaprijediti postojeći extraction flow mogućnošću ručnog dodavanja i brisanja polja, omogućiti pregled i
filtriranje dokumenata te uvesti in-app notifikacije i email reminder poruke.

# 2. Šta je završeno

Tim je uspješno realizovao sve planirane funkcionalnosti navedene u sprint goalu.

# 3. Šta nije završeno

Nema nezavršenih zadataka, sve planirane funkcionalnosti su implementirane i testirane.

# 4. Demonstrirane funkcionalnosti ili artefakti



# 5. Glavni problemi i blokeri

Tokom sprinta nije bilo značajnih blokera koji su utjecali na realizaciju planiranih funkcionalnosti.

Najveći tehnički izazovi odnosili su se na usklađivanje frontend i backend ponašanja prilikom brisanja obaveznih i
opcionih extraction polja, kao i na konfiguraciju SMTP servisa za slanje email reminder poruka na deployment serveru.


# 6. Ključne odluke donesene u sprintu

Detaljan opis ključnih odluka nalazi se u Decision Log dokumentu, počevši od odluke `DL-042`. Odluke uključuju:

- ručno dodavanje extraction polja i razlikovanje canonical i custom polja,
- strategiju brisanja extraction polja uz zaštitu obaveznih vrijednosti,
- in-app notifikacije i email reminder digest poruke,
- generički XML izlaz kao završni mašinski čitljiv rezultat obrade dokumenta,
- pohranu jednog aktivnog XML izlaza po dokumentu i mogućnost regenerisanja,
- eksplicitnu finalizaciju dokumenta nakon generisanja XML izlaza,
- server-side pretragu i kombinovanje filtera za listu dokumenata.

# 7. Povratna informacija Product Ownera



# 8. Zaključak za naredni sprint