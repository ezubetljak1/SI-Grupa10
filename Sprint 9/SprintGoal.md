# Sprint Goal - Sprint 9

## Sprint cilj

Omogućiti kompletan approval workflow nad dokumentima kroz pregled dokumenata za odobravanje,
odobrenje/odbijanje/vraćanje akcije sa komentarima, generalne komentare i historiju statusa.
Uz to, omogućiti upravljanje radnim zadacima sa zaštitom dodjele,
audit log za ključne akcije te slanje linka za ažuriranje lozinke na mail.

## Ključne stavke koje tim želi završiti

Tim želi završiti implementaciju approval workflow-a što uključuje sljedeće stavke:

**Approval workflow**

- Pregled i ograničen pristup dokumentima koji su na čekanju odobrenja
- Mogućnost odobrenja, odbijanja ili vraćanja dokumenta na doradu uz komentar
- Komentar approver-a kao obavezan ili opcion element uz svaku approval akciju
- Praćenje historije statusa dokumenata
- Operator correction flow: uređivanje i potvrda dokumenta vraćenog na doradu

**Komentari**

- Generalni komentari na dokumentu koje korisnici mogu ostavljati bez vezivanja za statusnu promjenu
- Komentari uz approval akcije sačuvani u historiji statusa sa autorom i timestampom

**Task management**

- Dodjela zadataka operaterima i odobravateljima od strane Admin/Manager korisnika
- Pregled vlastitih zadataka (My Tasks) sa navigacijom na relevantni dokument
- Zaštita zadatka: samo korisnik kome je zadatak dodijeljen može ga pokrenuti; neovlašteni pristup se odbija i bilježi

**Audit i sigurnost**

- Audit log za ključne poslovne i sigurnosne akcije vidljiv samo Admin/Manager korisnicima
- Slanje sigurnog email linka za postavljanje lozinke pri kreiranju korisnika ili resetu lozinke

**Testiranje**

- Testiranje implementiranog workflow-a kako bi se osigurala ispravnost svih tokova i regresija

## Rizici i zavisnosti

Zavisnost ovog sprinta je od prethodno implementiranog role/auth sistema iz Sprinta 8.

Potencijalni problemi i rizici uključuju:

- Probleme sa SMTP/email integracijom (Spring Mail konfiguracija odvojena od Keycloak SMTP)
- Nejasno ponašanje pri vraćanju dokumenta na doradu (`NEEDS_CORRECTION` vs `REJECTED` semantika)
- Rizik neusklađenosti permission logike između backend-a i frontend-a; backend mora biti source of truth
- Postojeći DB enum constraints mogu odbiti novi status `NEEDS_CORRECTION` bez eksplicitne SQL migracije
- SMTP kredencijali ne smiju biti commitovani u kod, dokumentaciju niti application.properties
