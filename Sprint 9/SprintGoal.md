# Sprint Goal - Sprint 9

## Sprint cilj

Omogućiti kompletan approval workflow nad dokumentima kroz pregled dokumenata za odobravanje,
odobrenje/odbijanje akcije, komentare i historiju statusa. Uz to, omogućiti upravljanje radnim zadacima
(task assignment i My Tasks), ručno dodavanje ekstraktovanih polja, notification centar sa email
reminderima, audit log za ključne akcije te slanje privremene šifre na mail.

## Ključne stavke koje tim želi završiti

Tim želi završiti implementaciju approval workflow-a i prateće infrastrukture što uključuje sljedeće
stavke:

**Approval workflow**

- Pregled dokumenata koji su na čekanju odobrenja
- Mogućnost odobrenja, odbijanja ili vraćanja dokumenta na doradu uz komentar
- Praćenje historije statusa dokumenata
- Operator correction flow: uređivanje i potvrda dokumenta vraćenog na doradu

**Task management**

- Dodjela zadataka operaterima i odobravateljima od strane Admin/Manager korisnika
- Pregled vlastitih zadataka (My Tasks) sa navigacijom na relevantni dokument

**Ekstrakcija i obrada**

- Ručno dodavanje ekstraktovanog polja koje OCR nije vratio, uz očuvanje canonical ključeva za buduću
  XML generaciju u Sprintu 10

**Notifikacije**

- In-app notification centar sa unread badge-om i navigacijom na relevantnu akciju
- Email digest reminder za stara nepročitana obavještenja putem scheduled job-a

**Audit i sigurnost**

- Audit log za ključne poslovne i sigurnosne akcije vidljiv samo Admin/Manager korisnicima
- Slanje privremene šifre na mail pri kreiranju korisnika ili resetu lozinke

**Testiranje**

- Testiranje implementiranog workflow-a kako bi se osigurala ispravnost svih tokova i regresija

## Rizici i zavisnosti

Zavisnost ovog sprinta je od prethodno implementiranog role/auth sistema iz Sprinta 8.

Potencijalni problemi i rizici uključuju:

- Probleme sa SMTP/email integracijom (Spring Mail konfiguracija odvojena od Keycloak SMTP)
- Nejasno ponašanje pri vraćanju dokumenta na doradu (`NEEDS_CORRECTION` vs `REJECTED` semantika)
- Rizik neusklađenosti permission logike između backend-a i frontend-a; backend mora biti source of truth
- Postojeći DB enum constraints mogu odbiti novi status `NEEDS_CORRECTION` bez eksplicitne SQL migracije
- Canonical `fieldName` vrijednosti se ne smiju proizvoljno preimenovati zbog kompatibilnosti sa Sprint 10
  XML generatorom
- Notification spam: email reminder se smije slati samo jednom po korisniku zahvaljujući `emailSentAt`
  polju
- SMTP kredencijali ne smiju biti commitovani u kod, dokumentaciju niti application.properties
