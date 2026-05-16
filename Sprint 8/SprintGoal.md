# Sprint Goal - Sprint 8

## Sprint cilj

Uvesti organizacioni model pristupa sistemu kroz registraciju firme, kreiranje administratorskog naloga i osnovnu
autentifikaciju korisnika. Pored toga, cilj je uspostaviti model upravljanja korisnicima i rolama unutar firme, te
osigurati pristup funkcionalnostima prema odgovornostima korisnika. 

<!--

Pored organizacionog i sigurnosnog modela, Sprint 8 uključuje i proširenje ranije implementiranog MVP toka obrade dokumenata. Postojeći upload, OCR/AI extraction, klasifikacija, validacija i review flow prošireni su podrškom za više tipova dokumenata, odvojene Google Document AI procesore, auto-klasifikaciju, manual classification review i type-aware validaciju ekstraktovanih polja.

-->

## Ključne stavke koje tim želi završiti

- Registracija firme
- Kreiranje prvog administratorskog naloga
- Autentifikacija korisnika
- Izolacija podataka po organizaciji
- Kreiranje i upravljanje korisničkim nalozima
- Dodjela rola
- Ograničenja akcija prema ulozi
- Dashboard za menadžment firme

## Rizici i zavisnosti

Kada je u pitanju autentifikacija, rizike i zavisnosti predstavljaju nejasna pravila registracije, problemi sa
autentifikacijom, nedovoljna izolacija organizacijskih podataka.

U smislu modela upravljanaja korisnicima i rolama, rizici uključuju nejasne definicije uloga, nedovoljno precizna
pravila pristupa te preširoko postavljen administrativni opseg.