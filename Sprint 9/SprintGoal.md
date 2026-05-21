# Sprint Goal - Sprint 9

## Sprint cilj

Omogućiti kompletan approval workflow nad dokumentima kroz pregled dokumenata za odobravanje, odobrenje/odbijanje
akcije, komentare, i historiju statusa.
Također, cilj je omogućiti da se privremena šifra šalje korisniku na mail.

## Ključne stavke koje tim želi završiti

Tim želi završiti implementaciju approval workflow-a što uključuje sljedeće stavke:

- Pregled dokumenata koji su na čekanju odobrenja
- Mogućnost odobrenja ili odbijanja dokumenata
- Dodavanje komentara prilikom odobravanja ili odbijanja
- Praćenje historije statusa dokumenata
- Testiranje implementiranog workflow-a kako bi se osigurala ispravnost

## Rizici i zavisnosti

Zavisnost ovog sprinta je od prethodno implementiranog role/auth sistema iz Sprinta 8.
Potencijalni problemi i rizici uključuju probleme sa SMTP/email integracijom, nejasno ponašanje pri vraćanju dokumenta
na doradu, te rizik neusklađenosti permission logike između backend-a i frontend-a. 
