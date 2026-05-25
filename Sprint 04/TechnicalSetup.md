# Technical Setup

## Uvod

Ovaj dokument definiše osnovni tehnički setup projekta. Njegova svrha je da precizno opiše tehnologije koje će se koristiti u implementaciji sistema, organizaciju izvornog koda, način pohrane podataka i fajlova, pristup deploymentu, planiranu infrastrukturu, kao i branching strategiju koja će se koristiti u razvoju.

Dokument predstavlja tehničku osnovu projekta i služi kao referenca za tim pri donošenju implementacijskih i organizacijskih odluka.

---

## 1. Branching strategija

Za upravljanje izvornim kodom koristi se Git kao distribuirani sistem za verzionisanje, uz GitHub kao centralni udaljeni repozitorij.

Za projekat se usvaja **Gitflow** strategija grananja.

Razlog izbora je to što Gitflow omogućava jasnije razdvajanje:
- stabilne verzije sistema
- aktivnog razvojnog rada
- novih funkcionalnosti
- bug fixeva
- pripreme release verzija

Ovakav pristup je koristan jer tim radi paralelno na više zadataka, a istovremeno želi održati stabilnu glavnu granu projekta.

### Pregled grana

| Grana | Namjena |
|---|---|
| `main` | Stabilna, produkcijski spremna verzija sistema |
| `develop` | Glavna razvojna grana |
| `feature/*` | Razvoj novih funkcionalnosti i user story-ja |
| `release/*` | Priprema verzije za stabilnu isporuku |
| `hotfix/*` | Hitne ispravke na stabilnoj verziji |

### Main grana

`main` predstavlja stabilnu, produkcijski spremnu verziju sistema.

Karakteristike:
- sadrži samo provjerene i završene promjene
- koristi se kao referenca za trenutno stabilno stanje projekta
- u nju ne idu direktni neprovjereni develop commit-i

### Develop grana

`develop` predstavlja glavnu razvojnu granu.

Karakteristike:
- iz nje se kreiraju feature grane
- ona sadrži integrisane razvojne promjene
- služi kao centralno mjesto za spajanje završenih funkcionalnosti prije release-a

### Feature grane

Feature grane služe za razvoj novih funkcionalnosti i user story-ja.

Pravila:
- kreiraju se iz `develop`
- nakon završetka se mergeaju nazad u `develop`

Primjeri naziva:
- `feature/SB-5-1`
- `feature/SB-7-2`

### Release grane

Release grane služe za pripremu verzije koja ide prema stabilnoj isporuci.

Pravila:
- kreiraju se iz `develop`
- koriste se za finalne provjere, manje dorade i stabilizaciju verzije
- po završetku se mergeaju u `main` i nazad u `develop`

Primjeri naziva:
- `release/v1.0.0`
- `release/v1.1.0`

### Hotfix grane

Hotfix grane služe za hitne ispravke problema na stabilnoj verziji.

Pravila:
- kreiraju se iz `main`
- nakon ispravke se mergeaju u `main` i `develop`

Primjeri naziva:
- `hotfix/login-error`
- `hotfix/file-upload-bug`

### Pravila rada sa granama

Novi branch se otvara:
- kada član tima preuzima novi user story
- kada se radi nova funkcionalnost
- kada se radi korekcija buga
- kada se priprema release
- kada se radi hitna ispravka stabilne verzije

Grane se kreiraju na sljedeći način:
- feature branch → iz `develop`
- release branch → iz `develop`
- hotfix branch → iz `main`

Nazivi grana trebaju biti:
- kratki
- opisni
- dosljedni
- na engleskom jeziku

Preporučeni format:
- `feature/SB-broj-broj`
- `release/verzija`
- `hotfix/opis-problema`

### Merge pravila

Merge se ne radi direktno bez provjere. Za svaku značajniju promjenu treba:

1. pushati branch na udaljeni repozitorij
2. otvoriti Pull Request
3. član tima koji nije autor grane vrši review
4. po potrebi izvršiti korekcije
5. nakon odobrenja uraditi merge

### Code review pravila

Prije merge-a promjene moraju proći pregled od strane barem jednog drugog člana tima.

Code review obuhvata:
- provjeru da li funkcionalnost radi ono što je planirano
- provjeru čitljivosti i konzistentnosti koda
- provjeru da nema očiglednih regresija
- provjeru da su poštovane dogovorene konvencije
- provjeru da izmjene ne narušavaju postojeću arhitekturu

Ako reviewer smatra da promjena nije spremna, autor grane vrši korekcije prije merge-a.

### Rješavanje konflikata

Kod konflikata pri spajanju grana primjenjuju se sljedeća pravila:

- konflikt rješava autor branch-a koji se mergea
- prije merge-a branch treba uskladiti sa ciljnom granom
- nakon rješavanja konflikta obavezno se ponovo testira funkcionalnost
- ako konflikt utiče na poslovnu logiku ili integraciju više modula, članovi tima zajednički pregledaju rješenje

Cilj je da nakon merge-a stanje grane ostane stabilno i razumljivo.

---
## 2. Tehnološki stack

Sistem je zasnovan na slojevitoj web arhitekturi koja se sastoji od frontend sloja, backend sloja, relacijske baze podataka i posebnog servisa za OCR/AI obradu dokumenata. Ovakav pristup omogućava jasno razdvajanje odgovornosti između korisničkog interfejsa, poslovne logike, pohrane podataka i obrade dokumenata.

### Pregled tehnološkog stack-a

| Sloj | Odabrana tehnologija |
|---|---|
| Backend | Spring Boot |
| Frontend | Angular |
| Baza podataka | PostgreSQL |
| OCR / AI obrada | Google Document AI |

U nastavku je dat detaljniji pregled svake komponente sistema, zajedno sa ključnim tehnologijama i razlozima njihovog izbora.

### 2.1 Backend

Backend dio sistema razvija se korištenjem **Spring Boot** frameworka. Kao osnova backend implementacije koriste se i sljedeće prateće tehnologije i biblioteke:

- **Java 17**
- **Spring Web**
- **Spring Data JPA**
- **Spring Validation**
- **Spring Security**
- **MapStruct**
- **Lombok**
- **Springdoc OpenAPI / Swagger**

#### Ključne karakteristike backend sloja

Backend sloj je zadužen za:
- implementaciju poslovne logike sistema
- obradu zahtjeva koji dolaze sa frontend aplikacije
- komunikaciju sa bazom podataka
- validaciju ulaznih podataka
- autentifikaciju korisnika
- autorizaciju i kontrolu pristupa resursima
- integraciju sa eksternim servisima
- izlaganje REST API endpointa

Spring Boot je odabran zato što omogućava razvoj stabilnih i dobro organizovanih poslovnih aplikacija. Njegova prednost u ovom projektu ogleda se u jednostavnoj konfiguraciji, jasnoj slojevitoj strukturi i dobroj integraciji sa bazom podataka i vanjskim servisima.

Dodatno:
- **Spring Web** omogućava izgradnju REST API-ja
- **Spring Data JPA** pojednostavljuje rad sa bazom podataka i mapiranje entiteta
- **Spring Validation** koristi se za provjeru ispravnosti ulaznih podataka i poslovnih pravila
- **Spring Security** služi za implementaciju autentifikacije, autorizacije i zaštite pristupa sistemu
- **MapStruct** smanjuje količinu ručno pisanog koda pri mapiranju između entiteta i DTO modela
- **Lombok** pojednostavljuje definisanje modela i servisa
- **Swagger / OpenAPI** olakšava dokumentovanje i testiranje API endpointa tokom razvoja

Ovakav backend pristup je pogodan za projekat jer omogućava dobru održivost, jasnu organizaciju koda i lakše proširenje sistema u narednim fazama razvoja.

### 2.2 Frontend

Frontend dio sistema razvija se korištenjem **Angular** frameworka. Uz Angular se koriste i sljedeće tehnologije:

- **TypeScript**
- **SCSS**
- **RxJS**
- **Angular CLI**
- **Vitest**

#### Ključne karakteristike frontend sloja

Frontend sloj je zadužen za:
- prikaz korisničkog interfejsa
- interakciju korisnika sa sistemom
- validaciju određenih korisničkih unosa na klijentskoj strani
- komunikaciju sa backend API-jem
- prikaz rezultata obrade i statusa dokumenata

Angular je odabran jer je pogodan za razvoj većih i strukturiranih web aplikacija. Njegove glavne prednosti u ovom projektu su:
- komponentna arhitektura
- dobra organizacija modula, servisa i ruta
- podrška za izgradnju formi i validaciju
- jednostavno povezivanje sa REST API backendom
- dobra skalabilnost kako sistem raste

Dodatno:
- **TypeScript** povećava sigurnost i preglednost koda
- **SCSS** omogućava bolju organizaciju stilova i lakšu prilagodbu interfejsa
- **RxJS** olakšava rad sa asinhronim operacijama i HTTP zahtjevima
- **Angular CLI** pojednostavljuje razvoj, build i upravljanje projektom
- **Vitest** se koristi za testiranje frontend dijela sistema

Ovakav izbor frontend tehnologija omogućava razvoj preglednog, modularnog i održivog korisničkog interfejsa.

### 2.3 Baza podataka

Za pohranu podataka koristi se **PostgreSQL** kao relacijska baza podataka.

#### Ključne karakteristike baze podataka

Baza podataka je zadužena za:
- pohranu strukturiranih poslovnih podataka
- čuvanje odnosa između ključnih entiteta sistema
- očuvanje integriteta i konzistentnosti podataka
- podršku za kasnije proširenje sistema bez promjene osnovne podatkovne arhitekture

PostgreSQL je odabran kao pouzdano i robusno rješenje za relacijske sisteme. Prirodan je izbor za projekat poput DocFlow-a, jer se u sistemu pojavljuju jasno definisani entiteti i odnosi između njih, kao što su korisnici, firme, dokumenti, statusi i rezultati obrade.

Prednosti ovog izbora su:
- stabilnost i pouzdanost
- dobra podrška za relacione modele
- očuvanje integriteta podataka
- mogućnost budućeg proširenja bez promjene osnovne strukture sistema

### 2.4 OCR / AI sloj

Za obradu dokumenata planirano je korištenje servisa **Google Document AI**.

#### Ključne karakteristike OCR / AI sloja

OCR / AI sloj je zadužen za:
- OCR obradu uploadovanih dokumenata
- ekstrakciju teksta iz dokumenata
- klasifikaciju dokumenata
- potencijalnu ekstrakciju strukturiranih podataka i polja

Ovaj sloj predstavlja posebnu komponentu sistema koja nije direktno vezana za osnovnu CRUD logiku aplikacije, već proširuje sistem naprednim mogućnostima automatizovane obrade dokumenata.

Google Document AI je trenutno odabran kao inicijalno rješenje za OCR i obradu dokumenata, uz mogućnost zamjene drugim provajderom u kasnijim fazama projekta.

Zbog toga OCR/AI komponenta neće biti tvrdo vezana za jednog provajdera, nego će biti implementirana kroz apstraktni servisni sloj. Na taj način će biti moguće zamijeniti provajdera uz minimalne promjene u ostatku sistema, ukoliko se pokaže da drugo rješenje bolje odgovara tehničkim, funkcionalnim ili finansijskim zahtjevima projekta.

Moguće alternativne opcije uključuju:
- Tesseract
- EasyOCR
- AWS Textract
- PaddleOCR

---

## 3. Deployment pristup

Za deployment sistema predviđena je **jedna virtuelna mašina (VM)**.

Planirani deployment setup je:
- **1 VM**
- **Ubuntu 24.04 LTS x64**
- deployment putem **Docker kontejnera**
- aplikacija hostana na **DigitalOcean Droplet** infrastrukturi

### Konfiguracija virtuelne mašine

| Stavka | Vrijednost |
|---|---|
| Provider | DigitalOcean |
| Plan | Basic |
| CPU opcija | Premium Intel |
| vCPU | 2 |
| RAM | 4 GB |
| Disk | 120 GB SSD |
| Bandwidth | 4 TB mjesečno |
| Region | New York, Datacenter 1 (NYC1) |
| Operativni sistem | Ubuntu 24.04 LTS x64 |

### Razlog izbora jedne VM

Za potrebe studentskog projekta i MVP faze, deployment na jednoj VM predstavlja optimalno rješenje jer:
- infrastruktura ostaje jednostavna za upravljanje
- troškovi su niži nego kod višestrukih instanci
- deployment i održavanje su jednostavniji
- svi servisi se nalaze u jednom kontrolisanom okruženju
- sistem je dovoljno snažan za očekivani obim korištenja u ranoj fazi projekta

---

## Dockerizacija sistema

Sistem će biti dockeriziran i pokretan kroz tri odvojena kontejnera.

### Planirani kontejneri

- **Backend container**
- **Frontend container**
- **Database container**

### Obrazloženje

Takav setup omogućava:
- odvajanje odgovornosti između komponenti
- lakše pokretanje sistema lokalno i na serveru
- jednostavniji deployment
- lakše održavanje i zamjenu pojedinačnih dijelova sistema
- stabilnije i konzistentnije okruženje nezavisno od lokalne mašine developera

Za upravljanje kontejnerima koristiće se **Docker Compose**.

Docker Compose omogućava da se cijeli sistem pokrene jednom komandom, što je značajno i za lokalni razvoj i za deployment na VM.

---

## Raspored komponenti na infrastrukturi

Sve tri glavne komponente sistema nalaze se na istoj virtuelnoj mašini:
- frontend kontejner
- backend kontejner
- PostgreSQL kontejner

U ovoj fazi projekta baza podataka neće biti izdvojena na poseban server ili poseban managed DB servis, nego će se nalaziti na istoj VM kao i backend.

Ovakav pristup je prihvatljiv jer:
- sistem je još u ranoj fazi
- očekivano opterećenje nije veliko
- deployment ostaje jednostavan
- administracija i održavanje su lakši

Ako projekat bude rastao, moguće je:
- izdvojiti bazu na poseban server ili managed servis
- uvesti poseban storage servis za fajlove
- skalirati backend i frontend nezavisno

---

## Korištenje resursa i operativna razmatranja

Pri izboru jedne VM i tri kontejnera vodi se računa o racionalnom korištenju resursa.

Za MVP i demonstracionu verziju sistema očekuje se da će:
- Angular frontend imati minimalno opterećenje u runtime-u
- Spring Boot backend preuzimati najveći dio procesiranja
- PostgreSQL imati umjereno opterećenje
- čuvanje fajlova na disku biti dovoljno za početnu fazu
- OCR/AI obrada dodatno opteretiti backend kroz komunikaciju sa eksternim servisom, ali ne i lokalni CPU u mjeri kao da se obrada vrši lokalno

Za studentski projekat, proof-of-concept i početni MVP:
- 2 vCPU i 4 GB RAM su dovoljni za rad tri kontejnera
- 120 GB diska daje dovoljno prostora za aplikaciju, bazu i uploadovane dokumente u početnoj fazi
- 4 TB bandwidtha je više nego dovoljno za očekivani obim saobraćaja

---

## Pohrana fajlova

Originalni fajlovi koji se uploaduju u sistem neće se čuvati direktno u bazi podataka kao binarni sadržaj. Umjesto toga, biće pohranjeni na disku virtuelne mašine.

### Planirani pristup

- dokument se fizički čuva na disku VM-a
- baza čuva samo metapodatke o dokumentu
- baza čuva referencu na putanju fajla na disku
- po potrebi se dodatno čuvaju status obrade, datum uploada i rezultat OCR/AI obrade

### Razlog ovakvog pristupa

Ovakva organizacija je jednostavnija i pogodnija za MVP i početnu fazu projekta jer:
- smanjuje opterećenje baze podataka
- olakšava upravljanje većim fajlovima
- pojednostavljuje backup i restore procesa
- omogućava jasnije razdvajanje strukturiranih podataka od fizičkih dokumenata

### Napomena

Za početnu verziju sistema čuvanje fajlova na disku VM-a smatra se optimalnim kompromisom između jednostavnosti implementacije i stvarnih potreba projekta. U budućnosti je moguće preći na object storage pristup ako projekat bude zahtijevao veće skaliranje.

---

## Proces build-a i deploymenta

Za projekat je planiran jednostavan i izvodljiv deployment proces prilagođen studentskom timu i MVP fazi razvoja.

### Build proces

Frontend i backend se buildaju odvojeno:
- frontend aplikacija se builda kao produkcijska Angular aplikacija
- backend aplikacija se builda kao Spring Boot aplikacija
- nakon build-a obje komponente se pakuju u odgovarajuće Docker image-e

Za lokalni razvoj i za deployment koristi se Docker Compose, kako bi se cijeli sistem mogao pokrenuti na standardizovan način.

### Deployment proces

Deployment na server se planira kao polu-ručni proces:
1. najnovije izmjene se mergeaju u odgovarajuću stabilnu granu
2. kod se povlači na server ili se na server prenese najnovija verzija projekta
3. Docker image-i se buildaju ili osvježavaju
4. servisi se pokreću ili redeployaju pomoću Docker Compose komandi
5. provjerava se da li su frontend, backend i baza uspješno podignuti

Ovakav pristup je odabran zato što je jednostavan za implementaciju i održavanje, bez potrebe za dodatnim CI/CD alatima u ranoj fazi projekta.

### CI/CD napomena

U početnoj verziji projekta nije planirano uvođenje punog CI/CD pipeline-a, jer bi to povećalo kompleksnost iznad realnih potreba studentskog projekta.

Umjesto toga, tim koristi jednostavniji pristup:
- Pull Request review prije merge-a
- lokalno testiranje prije spajanja promjena
- kontrolisan ručni deployment stabilne verzije

U kasnijim fazama projekta moguće je proširenje sistema uvođenjem automatskog build-a i testiranja putem GitHub Actions ili sličnog alata.

---

## Backup i oporavak podataka

Iako se radi o studentskom projektu i početnoj verziji sistema, važno je predvidjeti osnovni pristup backup-u i oporavku podataka.

### Šta je potrebno čuvati

U sistemu postoje dvije ključne vrste podataka koje je potrebno zaštititi:
- podaci iz PostgreSQL baze
- uploadovani dokumenti i fajlovi pohranjeni na disku virtuelne mašine

### Planirani backup pristup

Za početnu fazu projekta planiran je jednostavan backup pristup:
- periodični backup baze podataka kroz export baze
- periodično kopiranje foldera u kojem se čuvaju uploadovani dokumenti
- čuvanje backup kopija odvojeno od aktivnih aplikacijskih fajlova kada god je to moguće

Ovakav pristup je dovoljan za MVP i razvojnu fazu, jer ne zahtijeva dodatnu kompleksnu infrastrukturu, a ipak omogućava osnovnu zaštitu od gubitka podataka.

### Oporavak sistema

U slučaju problema sa aplikacijom ili serverom, osnovni recovery postupak podrazumijeva:
1. ponovno podizanje Docker kontejnera
2. vraćanje PostgreSQL baze iz posljednjeg dostupnog backup-a
3. vraćanje uploadovanih fajlova iz backup kopije
4. provjeru ispravnosti rada aplikacije nakon oporavka

### Napomena

Za početni obim projekta nije planirano enterprise backup rješenje. Ipak, predviđeni osnovni backup pristup značajno smanjuje rizik potpunog gubitka podataka i predstavlja razuman balans između sigurnosti i kompleksnosti implementacije.