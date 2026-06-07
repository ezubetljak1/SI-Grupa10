# Dokumentovanje rada — Završni izvještaj o radu tima

---

## 1. Svrha projekta

DocFlow je web aplikacija namijenjena kompanijama koje svakodnevno primaju, obrađuju i arhiviraju poslovne dokumente kao
što su fakture, računi (receipts), bankovni izvodi i razne poslovne forme. Svrha projekta je zamjena ručnog i vremenski
zahtjevnog procesa obrade ulaznih dokumenata modernim, automatiziranim sistemom koji koristi tehnologije optičkog
prepoznavanja teksta (OCR) i vještačke inteligencije (AI).

Sistem je izgrađen kao multi-tenant platforma, što znači da ga može koristiti više kompanija istovremeno, a podaci svake
kompanije su potpuno izolirani. Svaka kompanija se registruje samostalno, kreira vlastite korisničke naloge i
prilagođava tok obrade dokumentima unutar svog radnog okruženja.

Primarni cilj projekta je smanjenje opterećenja računovođa i administrativnog osoblja kroz automatsko prepoznavanje
ključnih podataka iz dokumenata (kao što su naziv dobavljača, datum, broj računa, iznos i PDV) te vođenje tih podataka
kroz jasno definisan poslovni tok odobravanja koji završava generisanjem standardiziranog XML izlaza.

---

## 2. Problem koji sistem rješava

Kompanije svakodnevno primaju veliki broj ulaznih dokumenata u različitim formatima: skeniranim PDF dokumentima,
skeniranim slikovnim dokumentima (JPG, PNG) ili digitalno kreiranim PDF fakturama. Tradicionalni proces obrade takvih
dokumenata podrazumijeva ručni unos podataka, što je sporo, podložno greškama i zahtijeva stalnu angažovanost
zaposlenih.

Konkretni problemi koje DocFlow rješava su sljedeći:

**Ručni unos podataka** - predstavlja najzahtjevniji dio procesa. Računovođa mora ručno prepisivati naziv dobavljača,
datum
fakture, broj fakture, iznos i PDV iz svakog dokumenta u računovodstveni sistem. Ovaj proces je monoton, zahtijeva
koncentraciju i visoko je podložan greškama u slučaju umora odgovorne osobe.

**Nepreglednost statusa dokumenta** - drugi veliki problem. Kada dokument uđe u kompaniju, teško je pratiti u kojoj se
fazi obrade nalazi — da li je zaprimljen, ko ga je pregledao, da li je odobren ili odbijen, i kada će biti proknjižen.
DocFlow uvodi jasne workflow statuse i historiju promjena koja svim učesnicima u procesu daje potpuni uvid u tok obrade.

**Nekonzistentnost i nedostatak revizijskog traga** - posebno kritični za kompanije koje prolaze finansijsku reviziju.
Ručni procesi ne ostavljaju jasan trag o tome ko je šta i kada odobrio. DocFlow bilježi sve ključne akcije u audit log
koji je dostupan korisnicima sistema.

**Spor proces odobravanja** - ovo je problem koji nastaje kada dokument fizički ili digitalno putuje od jedne do druge
osobe na potpis. DocFlow uvodi elektronski workflow s jasno definiranim rolama, notifikacijama i zadacima (taskovima),
čime se ubrzava cijeli proces.

---

## 3. Glavne korisničke uloge

Sistem razlikuje četiri primarne uloge korisnika, a svaka rola ima jasno definiran skup dozvola i akcija unutar sistema.

**Administrator (ADMIN)** je odgovorna osoba za upravljanje korisnicima unutar kompanije. Administrator kreira nove
korisničke naloge, dodjeljuje im odgovarajuće role, mijenja status naloga (aktivan/neaktivan), resetuje lozinke i ima
potpuni pregled korisnika firme. Administrator nije direktno uključen u procesiranje dokumenta iako ima pristup svim
funkcionalnostima koje sistem nudi.

**Menadžer (MANAGER)** prati stanje procesa obrade dokumenata na višem nivou. Menadžer ima pristup dashboardu s
agregatnim statistikama, može pregledati listu svih dokumenata, koristiti pretragu i filtere, dodjeljivati taskove
korisnicima i pratiti tok rada. Menadžer može generisati XML izlaz za odobrene dokumente i finalizirati obradu
dokumenta.

**Operater (OPERATOR)** je osoba koja unosi dokumente u sistem. Operater uploaduje PDF i slikovne dokumente, pokreće
OCR/AI ekstrakciju podataka, pregledava i koriguje izdvojena polja, popunjava placeholder vrijednosti za polja koja OCR
nije prepoznao, pregledava polja s niskim confidence score-om i potvrđuje ekstrakciju kada su svi podaci verifikovani.
Operater ne može odobravati dokumente.

**Odobravatelj (APPROVER)** je osoba koja donosi konačnu poslovnu odluku o dokumentu. Odobravatelj pregledava dokument i
sva ekstraktovana polja, te može odobriti dokument (status APPROVED), odbiti ga s obrazloženim komentarom (status
REJECTED) ili ga vratiti operateru na korekciju (status NEEDS_CORRECTION). Odobravatelj ima read-only pregled
ekstrakcije i ne može mijenjati ekstraktovana polja.

Pored navedenih uloga, inicijalni registracijski proces obavlja **predstavnik firme** koji nije posebna uloga unutar
sistema, već slijedi javni registracijski tok koji kreira kompaniju i prvog administratorskog naloga.

---

## 4. Glavne implementirane funkcionalnosti

### 4.1. Registracija kompanije i upravljanje korisnicima

Sistem podržava registraciju kompanije kroz javni endpoint na koji nije potrebna prethodna prijava. Registracijom se
kreira kompanija (organizacija) i prvi administratorski nalog koji dobija link za postavljanje lozinke putem emaila.
Svaka kompanija ima potpuno izolirane podatke od drugih kompanija (multi-tenant model).

Administrator unutar kompanije može kreirati nove korisnike, dodijeliti im jednu od četiri role (ADMIN, MANAGER,
OPERATOR, APPROVER) i upravljati statusom naloga. Kad se kreira novi korisnik ili resetuje lozinka, sistem šalje email s
jedinstvenim vremenski ograničenim linkom za postavljanje lozinke. Implementiran je i mehanizam privremenog blokiranja
naloga nakon prevelikog broja neuspješnih pokušaja prijave.

### 4.2. Upload i pohrana dokumenata

Korisnik s rolom OPERATOR ili ADMIN može uploadovati dokumente putem web interfejsa. Sistem prihvata PDF, JPG, JPEG i
PNG fajlove do maksimalne veličine od 10 MB. Validacija se provodi na oba sloja — frontend i backend — kako bi se
osigurao integritet podataka.

Svaki uploadovani dokument dobija automatski inicijalni status UPLOADED i prati se u listi dokumenata s vidljivim
statusom. Originalni fajlovi pohranjuju se na filesystem storage servera, dok se metapodaci (naziv, tip fajla, veličina,
datum uploada, kompanija) čuvaju u bazi podataka. Sistem pri brisanju dokumenta uklanja i fizički fajl i sve
povezane zapise (ekstrakcija, XML izlaz, taskovi, komentari, status historija, audit log).

### 4.3. OCR i AI ekstrakcija podataka

Ekstrakcija se pokreće ručno na stranici detalja dokumenta, čime se izbjegava nepotrebno trošenje Google kredita.
Sistem koristi Google Document AI kao eksterni OCR/AI servis i podržava pet tipova procesora:

- **Invoice procesor** za fakture
- **Receipt/Expense procesor** za račune i troškove
- **Bank Statement procesor** za bankovne izvode
- **Form Parser** za obrasce
- **Classifier procesor** za automatsku klasifikaciju dokumenata uploadovanih kao `OTHER`

Korisnik pri uploadu bira tip dokumenta. Za direktno označene tipove (INVOICE, RECEIPT, BANK_STATEMENT, FORM) sistem
odmah poziva odgovarajući procesor. Za dokumente označene kao `OTHER` sistem prvo poziva classifier; ako classifier
prepozna podržan tip s confidence vrijednošću od najmanje 70%, ekstrakcija se nastavlja odgovarajućim parserom. U
suprotnom, dokument prelazi u status `NEEDS_CLASSIFICATION_REVIEW` i korisnik mora ručno potvrditi tip.

Rezultati ekstrakcije pohranjuju se u sistemu pri čemu su vidljivi korisnicima sistema sa nazivom,
vrijednošću i confidence score-om. Za svaki tip dokumenta definirana su obavezna polja.

### 4.4. Pregled, korekcija i validacija ekstrakcije

Računovođa (OPERATOR) pregledava ekstraktovana polja kroz interaktivnu tabelu na stranici detalja dokumenta. Sistem
automatski kreira placeholder redove za obavezna polja koja OCR nije uspio prepoznati, čime korisnik odmah vidi koja
polja treba ručno popuniti.

Polja sa confidence score-om ispod praga od 70% označavaju se kao "Review needed" i korisnik ih mora eksplicitno
pregledati ili korigovati prije potvrde ekstrakcije. Potvrda ekstrakcije (confirm extraction) je blokirana dok god
postoje nepopunjena obavezna polja ili nepregledan low-confidence sadržaj.

Validacija vrijednosti provodi se na backendu kao finalna validacija:

- Datumi moraju biti u formatu `YYYY-MM-DD`, `DD.MM.YYYY` ili `DD/MM/YYYY`
- Numeričke vrijednosti moraju biti čisti brojevi bez valutnih simbola (dozvoljeni su decimalni separatori tačka i
  zarez)
- Za invoice dokumente provjera se konzistentnost između `net_amount`, `vat_amount` i `total_amount` s tolerancijom od
  0.01

Operater može i ručno dodavati extraction polja koja OCR nije prepoznao — bilo kao kanonske ključeve (npr.
`payment_reference`) ili kao prilagođena custom polja s prefiksom `custom.` i čitljivom labelom (npr.
`custom.project_code` s labelom `Project code`). Opciona polja se mogu fizički brisati, a za obavezna kanonska polja
čišćenjem vrijednosti ostaje placeholder red koji korisnik mora ponovo popuniti.

### 4.5. Workflow odobravanja

Nakon što operater potvrdi ekstrakciju, dokument prelazi u status `READY_FOR_APPROVAL` i postaje dostupan osobama s
rolom APPROVER. Odobravatelj vidi dokument u svom pregledu dokumenata i može:

- **Odobriti** dokument (status `APPROVED`) — uz obavezan komentar koji može sadržavati dodatne napomene
- **Odbiti** dokument (status `REJECTED`) — uz obavezan komentar koji objašnjava razlog odbijanja
- **Vratiti na korekciju** (status `NEEDS_CORRECTION`) — uz obavezan komentar s opisom što treba ispraviti

Kada je dokument vraćen na korekciju, operater može pregledati ekstraktovana polja, izvršiti izmjene i ponovo pokrenuti
reconfirm ekstrakcije čime dokument ulazi u novi krug odobravanja.

### 4.6. Task management

Administratori i menadžeri mogu dodjeljivati dokumente konkretnim korisnicima kroz workflow taskove. Svaki task ima
tip (EXTRACTION, CORRECTION, APPROVAL), status (OPEN, IN_PROGRESS, COMPLETED, CANCELLED) i opcioni rok (due date).

Ključna sigurnosna karakteristika: ako za određeni dokument postoji aktivan task dodijeljen konkretnom korisniku, samo
taj korisnik može izvršiti odgovarajuću akciju (npr. pokrenut ekstrakciju). Drugi korisnici s istom rolom ne mogu vršiti
akciju. Ovo sprječava konfliktne izmjene, ali sistem i dalje podržava "free-for-all" mode kada za dokument nije
dodijeljen task.

Korisnik može pregledati sve svoje dodijeljene taskove na stranici "My Tasks", pokrenuti task koji mu je dodijeljen i
navigirati direktno na odgovarajući dokument.

### 4.7. Komentari, status historija i audit log

Svaki korisnik s pristupom dokumentu može ostaviti slobodni komentar koji nije vezan za promjenu statusa. Pored toga,
odobravatelj ostavlja obavezne komentare uz rejection i return-for-correction akcije.

Sistem bilježi sve promjene statusa dokumenta. Historiju ne može brisati
nijedan korisnik. Svaki zapis sadrži stari status, novi status, tip akcije, korisnika koji je izvršio akciju i
timestamp.

Audit log je detaljniji od status historije i evidentira sve ključne poslovne i sigurnosne akcije: upload dokumenta,
pokretanje ekstrakcije, editovanje ekstraktovanog polja, dodavanje/brisanje polja, assignment taska,
approve/reject/return, generisanje XML-a i finalizaciju dokumenta.

### 4.8. Notifikacije i email reminderi

Sistem automatski kreira notifikacije za ključne workflow događaje: kada dokument prijeđe u status
`READY_FOR_APPROVAL` (notifikacija za APPROVER), kada je dokument odbijen ili vraćen na korekciju (notifikacija za
OPERATOR) i kada je dodijeljen task korisniku.

Korisnik vidi unread badge u navigacijskoj traci koji prikazuje broj nepročitanih notifikacija. Notification centar
prikazuje sve notifikacije s opcijama označavanja jedne ili svih kao pročitanih. Klik na notifikaciju otvara
odgovarajuću stranicu dokumenta i označava notifikaciju pročitanom.

Pored in-app notifikacija, sistem šalje i email reminder poruke. Backend scheduler periodično provjerava nepročitane
notifikacije starije od definisanog praga i šalje jedan email po korisniku koji sadrži sve nepročitane
notifikacije. Sistem sprječava dupliranje — jednom poslana notifikacija
ne šalje se ponovo.

### 4.9. Generisanje XML izlaza i finalizacija

Nakon odobravanja dokumenta, menadžer ili administrator može generisati XML izlaz. XML sadrži metadata sekciju sa
informacijama o dokumentu i kolekciju svih potvrđenih extraction polja s njihovim vrijednostima. Prazni placeholder
redovi i fizički obrisana opciona polja ne ulaze u XML.

Generator koristi standardni Java XML API, a XML fajl se
pohranjuje na filesystem storage. Korisnik može pregledati XML sadržaj u aplikaciji i preuzeti fajl. Moguće je
regenerisati XML izlaz (npr. nakon korekcije podataka), pri čemu novi XML zamjenjuje prethodni bez kreiranja duplikata.

Finalizacija dokumenta izvršava se eksplicitnom "Complete processing" akcijom nakon generisanja XML-a. Tek tada dokument
dobiva završni status `COMPLETED`. Finaliziran dokument ima read-only prikaz bez mogućnosti izmjene ekstrakcije, dodjele
taskova ili regenerisanja XML-a.

### 4.10. Pretraga i filtriranje dokumenata

Lista dokumenata sadrži filter panel kojem pristup imaju samo administrator i menadžer, a koji podržava:

- Pretragu po dijelu naziva dokumenta
- Pretragu po numeričkom ID-u dokumenta
- Filtriranje po tipu dokumenta (INVOICE, RECEIPT, BANK_STATEMENT, FORM, OTHER)
- Filtriranje po statusu dokumenta
- Filtriranje po rasponu datuma uploada
- Filtriranje po korisniku kojemu je dodijeljen aktivan task

Svi filteri mogu se kombinovati, a multi-tenant izolacija je garantirana — korisnik ne može dobiti dokumente druge
kompanije čak ni kroz pretragu. Rezultati su paginirani radi performansi.

---

## 5. Pregled rada kroz sprintove

### Sprint 1 — Analiza i planiranje

Tim je u prvom sprintu uspostavio zajednički razumevanje projekta. Kreirani su temeljni artefakti: Product Vision koji
definiše problem, ciljne korisnike i MVP scope; Stakeholder Map s identifikacijom svih relevantnih aktera i njihovih
interesa; te Team Charter s pravilima komunikacije, rada i rješavanja neslaganja. Izrađen je i inicijalni Product
Backlog. Na kraju sprinta identifikovana je neusklađenost između backloga i Product Vision dokumenta, što je postalo
akcija za naredni sprint.

### Sprint 2 — Zahtjevi i prioritizacija

Fokus je bio na razradi zahtjeva sistema. Izvršena je kompletna prioritizacija backlog stavki, definirani su user
stories s acceptance kriterijima za sve planirane funkcionalnosti kroz Sprintove, te identificirani ključni
nefunkcionalni zahtjevi (sigurnost, performanse, upotrebljivost, skalabilnost). Strukturiran je plan rada kroz
sprintove sa jasnim ciljevima i zavisnostima.

### Sprint 3 — Arhitektura i modeliranje

Tim je u trećem sprintu preveo zahtjeve u tehničke artefakte. Izrađen je domain model s entitetima Company, User, Role,
Document, Extraction, ExtractionField, StatusHistory, Comment, XmlOutput, AuditLog, Notification i Task, te ERD dijagram
s vezama između entiteta. Definirana je arhitektura sistema (slojevita klijent-server arhitektura s integriranim
eksternim OCR/AI servisom). Napravljen je Use Case Model s 23 use casea, Risk Register s identifikacijom i mitigacijom
23 rizika, te Test Strategy koja definiše nivoe i vrste testiranja.

### Sprint 4 — Tehnički setup i razvojni temelji

Uspostavljeni su razvojni temelji projekta. Definiran je Definition of Done, napravljen Initial Release Plan s pet
inkremenata, te Technical Setup dokument koji specificira cijeli tehnološki stack: Spring Boot (Java 17) za backend,
Angular (TypeScript) za frontend, PostgreSQL za bazu podataka i Google Document AI za OCR/AI obradu. Postavljeni su
frontend i backend skeleton projekti, repozitorij na GitHubu s Gitflow branch strategijom, te dogovori oko code review
procesa.

### Sprint 5 — Upravljanje dokumentima

Isporučen je prvi funkcionalni inkrement sistema. Implementiran je upload dokumenta s validacijom tipa fajla (PDF, JPG,
JPEG, PNG) i veličine (max 10 MB) na oba sloja. Originalni fajlovi se pohranjuju na filesystem, a metapodaci u bazu.
Implementirana je lista uploadovanih dokumenata s paginacijom i prikaz detalja dokumenta s preuzimanjem fajla. Korišten
je Swagger za API dokumentaciju. Implementirani su integracijski testovi za kompletan upload tok.

### Sprint 6 — OCR i AI ekstrakcija

Integrisan je Google Document AI servis za OCR obradu i ekstrakciju podataka iz dokumenata. Ekstrakcija se pokreće
ručno, a OCR logika je izdvojena u poseban provider sloj (OcrProvider/GoogleDocumentAiProvider) što omogućava lako
zamjenjivanje servisa. Rezultati ekstrakcije pohranjuju se u relacijski model. Implementiran
je UI prikaz ekstraktovanih polja s confidence score-om. Pokriveni su scenariji neuspjele ekstrakcije s jasnim statusom
PROCESSING_FAILED. Testovi koriste mockovani OCR provider bez stvarnih Google cloud poziva.

### Sprint 7 — Validacija i korekcija ekstrakcije

Implementirana je interaktivna tabela za ručnu korekciju ekstraktovanih polja s validacijom formata (datum, numeričke
vrijednosti). Uvedeni su placeholder redovi za obavezna polja koja OCR nije prepoznao i mehanizam review-a polja s
niskim confidence score-om. Potvrda ekstrakcije (confirm extraction) je implementirana s blokiranim pristupom dok sve
validacije nisu zadovoljene. Dodata je nova kolona `is_placeholder` u tabelu extraction_field. Product Owner je na
pregledu sprinta ukazao na potrebu za poboljšanjem prikaza podržanih formata datuma i numeričkih vrijednosti, što je
odmah implementirano.

### Sprint 8 — Autentifikacija, role i multi-tenancy

Implementiran je kompletan sigurnosni model sistema baziran na Keycloak identity provideru. Korisnici se autentificiraju
putem JWT tokena, a role se čuvaju u aplikacijskoj bazi kao jedinom izvoru istine. Na svakom API zahtjevu backend
provjerava status korisnika i kompanijsku pripadnost kroz CurrentUserService. Implementirani su registracija kompanije,
kreiranje korisnika, dodjela rola, reset lozinke i multi-tenant izolacija. U istom sprintu proširena je i obrada
dokumenata na više tipova s odvojenim Google Document AI procesorima, auto-klasifikacijom i ručnim pregledom
klasifikacije.

### Sprint 9 — Approval workflow i task management

Implementiran je kompletan workflow odobravanja dokumenata: approve, reject i return-for-correction akcije s obaveznim
komentarima. Uveden je novi status `NEEDS_CORRECTION`. Implementiran je centralizovani `DocumentStatusTransitionService`
koji bilježi sve promjene statusa u `status_history` tabeli. Dodani su slobodni komentari na dokumentima, audit log
ograničen na Admin/Manager korisnike, task assignment sistem s My Tasks pregledom i zaštitom od neovlaštenog preuzimanja
tuđih taskova. Implementirano je i slanje email linka za postavljanje lozinke. Tim je uveo CI pipeline i CODEOWNERS fajl
za automatizovano regresijsko testiranje na Pull Requestovima.

### Sprint 10 — XML izlaz, notifikacije i pretraga

Implementiran je kompletan XML izlaz kao završni rezultat obrade dokumenta. Korisnici s ulogom Manager/Admin mogu
generisati, pregledati, preuzeti i regenerisati XML fajl, te finalizirati obradu (status COMPLETED). Implementiran je
in-app notification sistem s unread badge-om i notification centrom, te email reminder scheduler koji šalje digest
poruke za nepročitane notifikacije. Dodani su ručno dodavanje i brisanje extraction polja. Implementirana je pretraga i
filtriranje dokumenata s kombinovanjem više kriterija istovremeno.

### Sprint 11 — Stabilizacija, dokumentacija i završna isporuka

Finalni sprint je usmjeren na stabilizaciju sistema i pripremu kompletne završne isporuke. Tokom sprinta izrađena je
završna projektna dokumentacija, uključujući izvještaj o radu tima, deployment proceduru, korisnički priručnik, tehnički
pregled arhitekture, Release Notes, AI Usage Summary te dokument poznatih ograničenja sistema. Pripremljen je i završni
QA izvještaj koji obuhvata 530 evidentiranih
testnih scenarija, pregled automatizovanih testova (259 backend, 35 frontend i 4 Playwright testa) te rezultate završnog
testiranja ključnih korisničkih tokova. Također je dokumentovan CI/CD pipeline koji omogućava
automatizovan i ponovljiv deployment kompletnog sistema na DigitalOcean infrastrukturu nakon uspješnog prolaska svih
provjera, čime je osigurano da sistem može biti pokrenut, korišten, testiran i evaluiran bez dodatnih neformalnih
objašnjenja tima.

---

## 6. Šta je završeno, djelimično završeno ili nije završeno

### Potpuno završene funkcionalnosti

Sve funkcionalnosti planiranje u MVP scopeu i extended scopeu su u potpunosti implementirane i testirane:

- Registracija kompanije i prvi admin nalog s email linkom
- Autentifikacija i autorizacija
- Multi-tenant izolacija podataka
- Upravljanje korisnicima i rolama
- Reset lozinke kroz email link
- Upload PDF, JPG, JPEG i PNG dokumenata
- Pohrana dokumenata na filesystem s metapodacima u bazi
- OCR/AI ekstrakcija s Google Document AI (invoice, receipt, bank statement, form procesori)
- Auto-klasifikacija dokumenata uploadovanih kao OTHER
- Status NEEDS_CLASSIFICATION_REVIEW i ručna potvrda tipa
- Pregled, korekcija i validacija ekstraktovanih polja
- Placeholder polja za missing required vrijednosti
- Low-confidence review mehanizam
- Potvrda ekstrakcije (confirm extraction)
- Ručno dodavanje canonical i custom extraction polja
- Brisanje opcionih i čišćenje required polja
- Workflow odobravanja (approve, reject, return for correction)
- Task assignment s My Tasks pregledom
- Zaštita od neovlaštenog preuzimanja tuđih taskova
- Komentari na dokumentu
- Status historija dokumenta
- Audit log (Admin/Manager)
- In-app notifikacije s unread badge-om i notification centrom
- Email reminder digest scheduler
- XML generisanje, preview, download, regenerisanje i finalizacija
- Pretraga i filtriranje dokumenata s kombinovanjem filtera
- Dashboard sa agregatnim statistikama
- CI/CD pipeline na GitHub Actions
- Deployment na DigitalOcean s CD automatizacijom
- Playwright UI smoke testovi
- Backend integration testovi (259 testova)
- Frontend unit/component testovi (35 testova)

### Djelimično završene ili namjerno izostavljene stavke

Sve stavke koje su navedene u Product Backlog dokumentu su uspješno realizovane te nema djelimično završenih niti
izostavljenih stavki.

### Što nije završeno

Sve stavke koje su navedene u Product Backlog dokumentu su uspješno realizovane te nema nezavršanih stavki.

---

## 7. Glavne tehničke odluke

Tokom razvoja projekta tim je donio niz ključnih tehničkih odluka koje su dokumentovane u Decision Log dokumentu (DL-001
do DL-049). Najvažnije su sljedeće:

**DL-004 — Pohrana fajlova na lokalni filesystem umjesto cloud storage-a.** Tim je odlučio da se uploadovani dokumenti i
XML fajlovi pohranjuju na filesystem servera, a u bazi se čuva samo referenca (storage path). Ovo pojednostavljuje
implementaciju u MVP fazi i smanjuje troškove. Migracija na S3 ili MinIO je moguća u budućnosti bez izmjene poslovne
logike.

**DL-005 — Validacija dokumenta na oba sloja (frontend i backend).** Frontend validacija daje brzu povratnu informaciju
korisniku, dok backend validacija osigurava integritet podataka bez obzira na izvor zahtjeva. Oba sloja moraju biti
sinhronizovana pri promjenama pravila.

**DL-011 — OCR logika u posebnom provider sloju.** OcrProvider interfejs apstraktuje konkretnu implementaciju (
GoogleDocumentAiProvider), što omogućava mockovanje u testovima bez Google credentialsa i laku zamjenu providera u
budućnosti.

**DL-013 — Ručno pokretanje ekstrakcije umjesto automatske.** Ekstrakcija se pokreće ručnom akcijom korisnika, a ne
automatski nakon uploada. Ovo smanjuje nepotrebne Google API pozive za dokumente koji su uploadovani greškom ili za koje
korisnik još nije spreman.

**DL-022 — Placeholder polja za missing required vrijednosti.** Nakon svake ekstrakcije backend automatski kreira
placeholder redove u `extraction_field` tabeli za obavezna polja koja OCR nije vratio. Ovo integriše missing polja u
postojeći edit flow.

**DL-025 — Odvojeni Google Document AI procesori po tipu dokumenta.** Umjesto jednog generičkog procesora, sistem
koristi specijalizirane procesore za invoice, receipt, bank statement i form dokumente. Ovo povećava preciznost
ekstrakcije jer je svaki processor treniran na specifičnom tipu dokumenta.

**DL-030 — Role se čuvaju u aplikacijskoj bazi, a ne u Keycloaku.** JWT token sadrži samo Keycloak user ID i email. Role
su primarna odgovornost aplikacijske baze i CurrentUserService koji ih čita na svakom zahtjevu. Ovo daje punu kontrolu
nad role managementom bez ovisnosti o Keycloak konfiguraciji.

**DL-031 — Provjera statusa korisnika na svakom API zahtjevu.** CurrentUserService čita UserEntity iz baze na svakom
zahtjevu (umjesto oslanjanja isključivo na JWT token). Ako je korisnik deaktiviran ili promijenjen, sljedeći API poziv
odmah vraća 403 Forbidden, bez čekanja na istek tokena.

**DL-040 — Task assignment kao poseban workflow sloj.** Taskovi se modeliraju kroz posebnu `workflow_task` tabelu
umjesto jednostavnog `assignedUserId` polja na dokumentu. Ovo omogućava razlikovanje tipa odgovornosti (EXTRACTION,
CORRECTION, APPROVAL), praćenje historije dodjele i podršku za multiple taskove po dokumentu.

**DL-046 — Generički XML format za sve tipove dokumenata.** Umjesto posebnih XSD šema po tipu dokumenta, sistem koristi
jedan fleksibilni XML format s metadata sekcijom i kolekcijom ekstraktovanih polja. Ovo eliminiše dupliciranje logike za
generisanje i ostaje kompatibilno s ručno dodanim custom poljima.

---

## 8. Najveći problemi tokom razvoja i načini rješavanja

### Problem 1: Usklađenost Product Backloga s Product Vision-om (Sprint 1/2)

Na kraju prvog sprinta Product Owner je ukazao da inicijalni Product Backlog nije bio dobro usklađen s Product Vision
dokumentom. Stavke backloga nisu adekvatno reflektirale prioritete i opseg sistema opisanog u viziji.

**Rješenje:** Tim je u Sprintu 2 proveo kompletnu reviziju backloga, uveo jasniju prioritizaciju (P1/P2/P3), razradio
user stories s acceptance kriterijima i postavio plan rada kroz sprintove.

### Problem 2: Izbor i konfiguracija OCR/AI servisa (Sprint 6)

Jedan od najvećih tehničkih izazova bio je izbor OCR/AI servisa koji daje dovoljno precizne rezultate za poslovne
dokumente. Tim je razmatrao Tesseract, EasyOCR, AWS Textract i Google Document AI. Dodatni izazov bila je sigurna
pohrana service account JSON fajla bez commitanja osjetljivih podataka u repozitorij.

**Rješenje:** Tim je odabrao Google Document AI zbog boljeg prepoznavanja strukturiranih poslovnih dokumenata. Service
account JSON fajl pohranjuje se isključivo na serveru i mounta se u Docker container, a `.env.example` sadrži samo
placeholder vrijednosti. U testovima se koristi mockovani OcrProvider koji ne poziva stvarni Google servis.

### Problem 3: Ograničenja formata koje Google Document AI vraća (Sprint 7/8)

Google Document AI procesori ne vraćaju podatke uvijek u konzistentnim formatima — datumi mogu biti `DD.MM.YYYY` ili
`YYYY-MM-DD`, iznosi mogu sadržavati tekst poput "1500 KM". Ovo je uzrokovalo validacijske greške i konfuziju korisnika.

**Rješenje:** Tim je implementirao type-aware validacijski sloj koji prihvata više formata datuma i normalizira ih, te
odbija iznose koji sadrže ne-numeričke karaktere s jasnom porukom korisniku. Portabl prikaz podržanih formata datuma
dodan je direktno u UI na osnovu povratne informacije Product Ownera nakon Sprint 7 pregleda.

### Problem 4: PostgreSQL enum constraint za novi status (Sprint 9)

Uvođenje novog statusa `NEEDS_CORRECTION` uzrokovalo je greške jer PostgreSQL check constraint za kolonu
`document_status` nije automatski ažuriran kroz Hibernate `ddl-auto=update` mehanizam.

**Rješenje:** Tim je identificirao ovaj problem i uveo ručne SQL migracije za ažuriranje PostgreSQL check constraintova
svaki put kada se uvodi novi enum status ili audit akcija. Ove SQL skripte su dokumentovane i moraju se pokrenuti pri
deploymentu na postojeće baze.

### Problem 5: Neusklađenost frontend i backend validacije (Sprint 8)

Kod implementacije role-based pristupa, frontend je počeo sakrivati određene UI elemente na osnovu role korisnika, ali
backend nije uvijek dosljednu provjeru provodio na API nivou. Ovo je moglo omogućiti pristup neovlaštenim akcijama
zaobilaženjem frontenda.

**Rješenje:** Tim je uveo princip da je backend uvijek finalni izvor istine za sigurnost. `WorkflowPermissionService`
centralizira sve permission provjere, a svaka zaštićena akcija provjerava dozvole na servisnom sloju neovisno o frontend
prikazu. Frontend skrivanje UI elemenata je dodatni sloj korisničkog iskustva, a ne zamjena za backend zaštitu.

### Problem 6: Konfiguracija SMTP servisa za email notifikacije (Sprint 10)

Hosting okruženje (DigitalOcean Droplet) blokiralo je standardni SMTP port 587, što je spriječilo slanje email reminder
poruka.

**Rješenje:** Tim je prešao na Brevo (bivši Sendinblue) kao SMTP relay servis koji podržava port 2525. Svi SMTP
parametri su konfigurirani kroz environment varijable bez commitanja u repozitorij. Ovaj izazov je dokumentovan u
Decision Log-u (DL-045) kao pouka za buduće deployment konfiguracije.

### Problem 7: Regresije u kompleksnom workflow-u (Sprint 9)

Nakon uvođenja centralizovanog `DocumentStatusTransitionService` koji je refaktorisao direktne status promjene kroz
cijeli sistem, javile su se regresije u nekim workflow tokovima koji su ranije funkcionirali.

**Rješenje:** Tim je uveo CI pipeline koji se automatski pokreće na svakom Pull Requestu prema develop grani i pokreće
sve backend integration testove. Dodan je CODEOWNERS fajl koji zahtijeva QA review before merge-a. Ove mjere su
eliminirale slučajeve gdje su sitni bugovi prolazili do deployment faze.

---

## 9. Šta bi tim unaprijedio da se projekat nastavlja

### Kratkoročna unapređenja

**Autentifikovani Playwright E2E testovi:** Trenutni Playwright testovi pokrivaju javne stranice. Da se projekat
nastavlja, sljedeći prioritet bi bio postavljanje dedicated testnog Keycloak realma s fiksnim testnim korisnicima kako
bi se kompletni poslovni workflow automatizirani end-to-end u browseru. Ovo bi omogućilo rano otkrivanje regresija u
složenim UI tokovima.

**Verzionisane migracije s Flyway ili Liquibase:** Trenutna konfiguracija koristi Hibernate `ddl-auto=update` za
kreiranje tabela i ručne SQL skripte za ažuriranje check constraintova. Uvođenje Flyway-a bi osiguralo kontrolisane,
verzionisane i reverzibilne migracije baze podataka, posebno korisne pri upgradu u produkciji.

**Code coverage threshold:** CI ne blokira merge na osnovu minimalnog procenta pokrivenosti kodom. Uvođenje JaCoCo
praga (npr. 70% linija za backend, 60% za frontend) bi obeshrabrilo dodavanje novih funkcionalnosti bez odgovarajućih
testova.

**Filtriranje approver liste dokumenata:** Approver trenutno vidi sve dokumente u statusu `READY_FOR_APPROVAL` u
kompaniji, ali filtriranje po dodijeljenom approval tasku bi poboljšalo preglednost u kompanijama s više odobravatelja.

### Arhitekturna unapređenja

**Migracija na cloud storage (S3/MinIO):** Fajlovi se trenutno pohranjuju na lokalnom filesystemu jednog servera, što
ograničava horizontalno skaliranje. Migracija na object storage bi omogućila distribuiranu pohranu, automatske backupe i
geografsku replikaciju bez izmjene poslovne logike (storage path u bazi bi ostao isti).

**Asinhrona obrada ekstrakcije:** OCR/AI ekstrakcija trenutno se izvršava sinhrono u HTTP request
ciklusu, što znači da korisnik čeka dok ekstrakcija ne završi. Za veće dokumente ili u periodu povećanog opterećenja ovo
može biti sporije. Uvođenje asinhrone OCR obrade bi poboljšalo korisničko
iskustvo i omogućilo scalabilniju arhitekturu.

**Redis cache za CurrentUserService:** Svaki API zahtjev trenutno izvršava database lookup za dohvat korisničkog
profila (UserEntity). U produkcijskom okruženju s velikim brojem korisnika ovo može biti ograničavajući faktor. Uvođenje
Redis
cachea s TTL-om od 5 minuta bi smanjilo opterećenje baze za česte API pozive.

**Aktivni health check za OCR servis:** Trenutno se greška OCR servisa otkriva tek pri pokušaju
ekstrakcije. Aktivni health check koji periodično provjerava dostupnost Google Document AI i prikazuje upozorenje u
adminskom dashboardu bi omogućio brže reakcije na probleme s eksternim servisom.

### Funkcionalna proširenja

**Integracija s računovodstvenim sistemima:** XML izlaz je dizajniran kao temelj za integraciju s ERP i računovodstvenim
softverima. Sljedeći korak bi bio definisanje XML XSD šeme prema standardu e-računa i implementacija direktnog API
push-a prema najčešće korištenim računovodstvenim sistemima na tržištu.

**Batch obrada dokumenata:** Mogućnost uploada i ekstrakcije više dokumenata odjednom i paralelna obrada u
pozadini bila bi značajna poboljšanje za kompanije s velikim volumenom dokumenata.

**Deduplicirana detekcija dokumenata:** Sistem bi mogao automatski prepoznati da je isti dokument uploadovan više puta (
npr. isti broj fakture od istog dobavljača) i upozoriti korisnika, čime bi se spriječila dvostruka obrada i knjiženje.

**Mobilna aplikacija:** Menadžeri i odobravatelji često trebaju brzo reagirati na dokumente koji čekaju odobravanje.
Mobilna aplikacija sa notifikacijama i brzim approve/reject akcijama bi ubrzala workflow, posebno u
okruženjima gdje odobravatelji nisu uvijek za kompjuterom.

**Napredna analitika i izvještavanje:** Trenutni dashboard prikazuje samo osnovne agregacije. Pravi analitički modul bi
trebao prikazivati trendove volumena dokumenata, prosječno vrijeme od uploada do finalizacije, procenat odbijenih
dokumenata po tipu, te identifikacija uskih grla u workflow procesu.

---

## 10. Zaključak

DocFlow je razvijen kroz 11 sprintova kao potpuna, produkcijski deployana web aplikacija koja automatizira obradu
ulaznih poslovnih dokumenata. Sistem je dostupan na adresi https://docflow.page i implementira kompletan end-to-end tok
od uploada dokumenta, OCR/AI ekstrakcije, workflow odobravanja, do generisanja standardiziranog XML izlaza i
finalizacije obrade.

Tim od sedam članova uspio je isporučiti sve planirane MVP funkcionalnosti i značajno ih proširiti u kasnijim
sprintovima. Ključni uspjesi su: multi-tenant arhitektura koja podržava rad više kompanija, type-aware
ekstrakcija s pet tipova dokumenata i auto-klasifikacijom, kompletan workflow s notification sistemom i audit tragom, te
automatizovani CI/CD pipeline koji osigurava kvalitet koda i ponovljivi deployment.