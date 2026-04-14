TestStrategy_v3.md
# Test Strategy

## Naziv projekta

**Sistem za AI-asistirano prepoznavanje i obradu računa i ulaznih dokumenata**

## Uvod

Test strategy definiše pristup testiranju i osiguravanju kvaliteta softverskog sistema.  
S obzirom na to da sistem uključuje AI-asistiranu obradu dokumenata, testiranje ima posebno važnu ulogu zbog kompleksnosti obrade i mogućnosti pojave grešaka u automatskom prepoznavanju i ekstrakciji podataka.

U ovom dokumentu definisani su ciljevi testiranja, nivoi testiranja, dodatne vrste testiranja, veza sa acceptance kriterijima, način evidentiranja rezultata testiranja i glavni rizici kvaliteta.

---

## Cilj testiranja

Cilj testiranja je osigurati da sistem funkcionalno i nefunkcionalno ispunjava zahtjeve definisane kroz user story-je, acceptance kriterije i poslovna pravila.

Glavni ciljevi testiranja su:

- osigurati da sistem ispunjava funkcionalne zahtjeve definisane user story-jima
- provjeriti ispunjenost nefunkcionalnih zahtjeva, uključujući performanse, sigurnost i pouzdanost
- identifikovati i otkloniti greške u ranim fazama razvoja
- validirati tačnost rezultata AI/OCR komponenti kroz dodatne kontrole
- osigurati ispravnost poslovnog toka sistema
- provjeriti stabilnost sistema nakon izmjena kroz regresiono testiranje
- osigurati ispravnost korisničkog interfejsa i korisničkog iskustva
- potvrditi da sistem zadovoljava acceptance kriterije prije isporuke
- smanjiti rizik od grešaka u produkciji
- obezbijediti pouzdan i kvalitetan rad kompletnog sistema

---

## Nivoi testiranja

### Pregled nivoa testiranja

| Nivo testiranja | Fokus testiranja |
|---|---|
| Unit testiranje | Validacija unosa, poslovna logika, pomoćne funkcije, XML mapiranje i logika obrade podataka |
| Integraciono testiranje | Komunikacija između modula, povezivanje backend-a sa bazom, OCR/AI modulom i autentifikacijom |
| Sistemsko testiranje | Cjelokupni tok sistema, workflow, sigurnost, performanse i stabilnost |
| Prihvatno testiranje | Korisnički scenariji i provjera acceptance kriterija iz perspektive krajnjeg korisnika |

### Unit testiranje

**Šta se testira:**
- validacija unosa podataka
- poslovna logika obrade podataka
- funkcije za parsiranje i mapiranje izdvojenih podataka
- pomoćne funkcije
- funkcije za generisanje XML strukture
- validacija poslovnih pravila

**Vrste testova koje će se implementirati:**
- testovi validacije unosa
- testovi poslovne logike funkcija
- testovi parsiranja i mapiranja podataka
- testovi XML generisanja
- testovi graničnih slučajeva

### Integraciono testiranje

**Šta se testira:**
- upload dokumenta i backend obrada
- OCR/AI modul i baza podataka
- autentifikacija i autorizacija
- workflow i promjene statusa dokumenta
- komunikacija frontend-backend
- validacija formata dokumenta

**Vrste testova koje će se implementirati:**
- testovi integracije upload-a i backend-a
- testovi komunikacije OCR/AI modula i baze podataka
- testovi autentifikacije i autorizacije
- testovi workflow prelaza i statusa dokumenta

### Sistemsko testiranje

**Šta se testira:**
- cijeli proces od uploada do generisanja XML-a
- obrada dokumenata kroz sve faze
- performanse sistema
- sigurnost i pristup podacima
- stabilnost sistema

**Vrste testova koje će se implementirati:**
- end-to-end testovi
- testovi performansi
- testovi sigurnosti
- testovi stabilnosti

### Prihvatno testiranje

**Šta se testira:**
- acceptance kriteriji iz user story-ja
- ključni korisnički scenariji
- ispravnost rezultata iz perspektive korisnika
- validacija poslovnih procesa

**Vrste testova koje će se implementirati:**
- testovi bazirani na acceptance kriterijima
- testovi korisničkih scenarija
- validacija poslovnih procesa od strane korisnika

---

## Dodatne vrste testiranja

Pored osnovnih nivoa testiranja, važnu ulogu u projektu imaju i dodatne vrste testiranja koje se provode kroz više nivoa testiranja, zavisno od prirode funkcionalnosti koja se provjerava.

### Regresiono testiranje

**Šta se testira:**
- ranije implementirane funkcionalnosti
- stabilnost sistema nakon izmjena
- ispravnost osnovnih funkcionalnosti nakon nadogradnje
- ključni MVP tokovi
- uticaj novih izmjena na postojeće funkcionalnosti

**Vrste testova koje će se implementirati:**
- ponovno izvršavanje postojećih test slučajeva
- testovi ključnih funkcionalnosti nakon izmjena
- automatizovani regresioni testovi za MVP tokove

### UI testiranje

**Šta se testira:**
- forme za unos i upload podataka
- prikaz OCR rezultata
- validacione poruke
- navigacija kroz sistem
- korisničko iskustvo

**Vrste testova koje će se implementirati:**
- testovi validacije formi
- testovi prikaza podataka
- testovi navigacije
- testovi korisničkih interakcija

### Performansno testiranje

**Šta se testira:**
- vrijeme odziva sistema
- obrada većeg broja zahtjeva
- stabilnost pod opterećenjem
- skalabilnost sistema

**Vrste testova koje će se implementirati:**
- load testovi
- stress testovi
- testovi vremena odziva
- testovi skalabilnosti

### Sigurnosno testiranje

**Šta se testira:**
- autentifikacija i autorizacija
- zaštita osjetljivih podataka
- mogućnost neovlaštenog pristupa
- ranjivosti sistema

**Vrste testova koje će se implementirati:**
- testovi autentifikacije i autorizacije
- testovi pristupnih prava
- testovi zaštite podataka
- osnovni testovi ranjivosti

### Testiranje upotrebljivosti

**Šta se testira:**
- jednostavnost korištenja sistema
- jasnoća korisničkog interfejsa
- intuitivnost navigacije
- korisničko iskustvo pri radu

**Vrste testova koje će se implementirati:**
- testovi korisničkih scenarija
- evaluacija interfejsa od strane korisnika
- testovi navigacije i toka korištenja

---

## Povezanost dodatnih vrsta testiranja sa nivoima testiranja

| Vrsta testiranja | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Regresiono testiranje | ✓ | ✓ | ✓ | ✓ |
| UI testiranje |  |  | ✓ | ✓ |
| Performansno testiranje |  |  | ✓ |  |
| Sigurnosno testiranje |  | ✓ | ✓ |  |
| Testiranje upotrebljivosti |  |  |  | ✓ |

---

## Šta se testira u kojem nivou

| Oblast sistema | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Validacija unosa i poslovna pravila | ✓ |  |  |  |
| Parsiranje i mapiranje izdvojenih podataka | ✓ | ✓ |  |  |
| Upload dokumenta i pohrana metapodataka |  | ✓ | ✓ | ✓ |
| OCR/AI obrada i ekstrakcija |  | ✓ | ✓ | ✓ |
| Workflow i promjene statusa |  | ✓ | ✓ | ✓ |
| Autentifikacija i autorizacija |  | ✓ | ✓ | ✓ |
| Generisanje XML izlaza | ✓ | ✓ | ✓ | ✓ |
| UI forme, poruke i navigacija |  |  | ✓ | ✓ |
| Performanse, sigurnost i stabilnost |  |  | ✓ |  |

---

## Veza sa acceptance kriterijima

Acceptance kriteriji grupisani su prema funkcionalnim cjelinama sistema kako bi se omogućila jasna i pregledna veza između zahtjeva i odgovarajućih nivoa testiranja.

### Upload dokumenta

**Povezani storyji:**
- US-5.1 — Upload dokumenta
- US-5.2 — Podrška za osnovne formate dokumenta
- US-5.3 — Prikaz greške pri neispravnom uploadu
- US-5.4 — Pohrana originalnog dokumenta i metapodataka

**Ključne vrste testiranja:** regresiono testiranje, UI testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Sistem mora omogućiti uspješan upload validnog dokumenta |  | ✓ |  |  |
| Sistem ne smije dozvoliti upload bez odabranog fajla |  | ✓ |  |  |
| Sistem mora prihvatiti samo podržane formate (PDF, JPG, JPEG, PNG) |  | ✓ |  |  |
| Sistem mora odbiti nepodržane formate i fajlove koji prelaze dozvoljenu veličinu |  | ✓ |  |  |
| Sistem mora prikazati jasnu poruku o uspješnom uploadu ili grešci |  |  |  | ✓ |
| Sistem mora sačuvati originalni dokument i osnovne metapodatke |  | ✓ |  |  |
| Sistem ne smije prikazati dokument u listi ako pohrana nije uspješno završena |  |  | ✓ |  |

### OCR / AI ekstrakcija podataka

**Povezani storyji:**
- US-6.1 — Pokretanje OCR obrade nakon uploada
- US-6.2 — Prikaz automatski izdvojenih podataka
- US-6.3 — Ekstrakcija osnovnih polja dokumenta
- US-6.4 — Osnovna klasifikacija tipa dokumenta
- US-6.5 — Označavanje neuspjele obrade

**Ključne vrste testiranja:** regresiono testiranje, UI testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Nakon uspješnog uploada sistem mora automatski pokrenuti OCR obradu |  | ✓ |  |  |
| Sistem mora evidentirati status obrade dokumenta |  |  | ✓ |  |
| Nakon uspješne obrade sistem mora sačuvati izdvojeni tekst i izdvojena polja |  | ✓ |  |  |
| Sistem mora prikazati izdvojene podatke u korisničkom interfejsu |  |  |  | ✓ |
| Sistem mora pokušati izdvojiti osnovna polja kroz vlastitu logiku parsiranja i mapiranja rezultata | ✓ |  |  |  |
| Sistem mora prikazati praznu ili nepopunjenu vrijednost kada polje nije moguće izdvojiti |  |  | ✓ |  |
| Sistem mora omogućiti prikaz klasifikacije dokumenta |  |  | ✓ |  |
| Kada OCR ili AI obrada ne uspije, sistem mora jasno označiti dokument kao neuspješno obrađen |  |  | ✓ |  |
| Sistem ne smije prikazivati nepouzdane podatke kao validne |  |  | ✓ |  |

### Ručna korekcija i validacija podataka

**Povezani storyji:**
- US-7.1 — Ručna korekcija izdvojenih polja
- US-7.2 — Validacija obaveznih polja
- US-7.3 — Validacija formata podataka
- US-7.4 — Matematička provjera iznosa
- US-7.5 — Spremanje potvrđenih podataka

**Ključne vrste testiranja:** regresiono testiranje, UI testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Sistem mora omogućiti ručnu korekciju izdvojenih polja |  |  |  | ✓ |
| Nakon izmjene vrijednosti sistem mora ažurirati prikaz |  |  | ✓ |  |
| Sistem ne smije dozvoliti spremanje nevalidnih podataka | ✓ |  |  |  |
| Sistem mora jasno označiti obavezna polja koja nedostaju |  |  |  | ✓ |
| Sistem mora validirati format datuma i numeričkih vrijednosti | ✓ |  |  |  |
| Sistem mora izvršiti osnovnu matematičku provjeru iznosa | ✓ |  |  |  |
| Kada validaciona pravila nisu zadovoljena, sistem mora prikazati poruku greške |  |  |  | ✓ |
| Kada su svi uslovi zadovoljeni, sistem mora sačuvati potvrđene vrijednosti |  | ✓ |  |  |
| Sistem ne smije izgubiti unesene korekcije nakon spremanja |  |  | ✓ |  |

### Autentifikacija i autorizacija korisnika

**Povezani storyji:**
- US-8.1 — Registracija firme u sistem
- US-8.2 — Kreiranje prvog administratorskog naloga firme
- US-8.3 — Prijava korisnika firme
- US-8.4 — Odjava korisnika iz sistema
- US-8.5 — Ograničenje pristupa na podatke vlastite organizacije
- US-8.6 — Prikaz poruke pri neuspješnoj prijavi
- US-9.1 — Kreiranje korisničkih naloga unutar firme
- US-9.2 — Dodjela korisničkih rola
- US-9.3 — Ograničenje akcija prema ulozi
- US-9.4 — Pregled korisnika firme

**Ključne vrste testiranja:** regresiono testiranje, sigurnosno testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Sistem mora omogućiti registraciju firme i admin naloga |  | ✓ |  |  |
| Sistem mora spriječiti duplikate |  | ✓ |  |  |
| Sistem mora sigurno pohraniti lozinku | ✓ |  |  |  |
| Sistem mora omogućiti prijavu validnim korisnicima |  | ✓ |  |  |
| Sistem ne smije dozvoliti prijavu nevalidnim korisnicima |  | ✓ |  |  |
| Sistem mora ograničiti broj pokušaja prijave |  |  | ✓ |  |
| Sistem mora omogućiti odjavu korisnika |  | ✓ |  |  |
| Sistem mora prikazivati samo podatke vlastite organizacije |  |  | ✓ |  |
| Sistem ne smije dozvoliti pristup drugim organizacijama |  |  | ✓ |  |
| Sistem mora primijeniti prava pristupa po roli |  |  | ✓ |  |
| Sistem mora prikazati jasne poruke greške |  |  |  | ✓ |

### Workflow i statusi dokumenta

**Povezani storyji:**
- US-10.1 — Pregled trenutnog statusa dokumenta
- US-10.2 — Slanje validiranog dokumenta na odobravanje
- US-10.3 — Pregled dokumenata koji čekaju odobravanje
- US-10.4 — Odobravanje dokumenta
- US-10.5 — Odbijanje dokumenta uz komentar
- US-10.6 — Pregled historije statusa dokumenta

**Ključne vrste testiranja:** regresiono testiranje, UI testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Sistem mora prikazati trenutni status dokumenta |  |  |  | ✓ |
| Kada se status promijeni, sistem mora ažurirati prikaz |  |  | ✓ |  |
| Sistem mora dozvoliti slanje na odobravanje samo validiranog dokumenta |  | ✓ |  |  |
| Sistem mora evidentirati promjene statusa |  |  | ✓ |  |
| Sistem mora prikazati dokumente koji čekaju odobravanje |  |  |  | ✓ |
| Sistem mora dozvoliti odobravanje samo u odgovarajućem statusu |  |  | ✓ |  |
| Sistem mora evidentirati ko je odobrio dokument i kada |  |  | ✓ |  |
| Sistem mora omogućiti odbijanje uz komentar |  | ✓ |  |  |
| Sistem mora trajno čuvati historiju statusa |  |  | ✓ |  |

### Generisanje, pregled i pohrana XML izlaza

**Povezani storyji:**
- US-11.1 — Generisanje XML izlaza
- US-11.2 — Pregled i preuzimanje XML-a
- US-11.3 — Pohrana XML-a i završetak obrade dokumenta

**Ključne vrste testiranja:** regresiono testiranje, UI testiranje

| Acceptance kriterij | Unit | Integraciono | Sistemsko | Prihvatno |
|---|:---:|:---:|:---:|:---:|
| Sistem mora dozvoliti generisanje XML-a samo za odobren dokument |  |  | ✓ |  |
| Sistem mora koristiti definisana mapping pravila | ✓ |  |  |  |
| Kada generisanje ne uspije, sistem mora prikazati grešku |  |  |  | ✓ |
| Sistem mora omogućiti pregled i preuzimanje XML-a |  |  |  | ✓ |
| Sistem mora sačuvati XML uz dokument |  | ✓ |  |  |
| Sistem ne smije dozvoliti finalizaciju bez XML-a |  |  | ✓ |  |
| Sistem mora promijeniti status u finalno stanje |  |  | ✓ |  |

### Zaključak o vezi sa acceptance kriterijima

Na ovaj način acceptance kriteriji iz user story-ja povezani su sa funkcionalnim cjelinama sistema i odgovarajućim nivoima testiranja.  
Takva organizacija olakšava planiranje testiranja, izradu test slučajeva i kasnije izvršavanje testova.

---

## Način evidentiranja rezultata testiranja

Rezultati testiranja evidentiraće se kroz testne slučajeve sa jasno označenim statusom izvršenja.

### Statusi testova

| Status | Značenje |
|---|---|
| Passed | Test je uspješno izvršen i očekivani rezultat je postignut |
| Failed | Test je izvršen, ali rezultat ne odgovara očekivanom |
| Blocked | Test se ne može izvršiti zbog vanjskog problema ili zavisnosti |
| Cannot Run | Test trenutno nije moguće pokrenuti zbog tehničkih ili organizacijskih razloga |

### Podaci koji se evidentiraju za svaki test

- naziv testnog slučaja
- opis testa
- ulazni podaci
- očekivani rezultat
- stvarni rezultat
- status testa

U slučaju greške evidentira se i:
- opis problema
- logovi
- screenshotovi ili drugi relevantni dokazi

Rezultati testiranja i uočeni problemi pratiće se kroz tabelu test slučajeva i kroz mehanizam za evidentiranje zadataka i grešaka u okviru repozitorija projekta, kako bi se omogućilo pravovremeno otklanjanje uočenih problema.

---

## Glavni rizici kvaliteta

### 1. Rizici vezani za obradu dokumenata i AI/OCR komponentu
- nepouzdana AI/OCR ekstrakcija podataka
- loš kvalitet ulaznih dokumenata
- neispravnosti u generisanju XML izlaza

### 2. Rizici vezani za funkcionalnost sistema
- neispravna validacija podataka
- greške u workflow procesu
- regresije nakon izmjena sistema
- nedovoljna pokrivenost testovima

### 3. Rizici vezani za sigurnost i korištenje
- sigurnosni rizici i neovlašten pristup
- problemi u korisničkom interfejsu
- kvalitet testova

### 4. Projektni rizici
- nedostatak vremena i resursa

---

## Zaključak

Definisana test strategija pruža osnovu za plansko i sistematično testiranje sistema za AI-asistirano prepoznavanje i obradu računa i ulaznih dokumenata.  
Posebna pažnja posvećena je tačnosti obrade podataka, ispravnosti poslovnog toka, sigurnosti pristupa i pouzdanosti završnih izlaza sistema.  
Na taj način testiranje postaje važan mehanizam kontrole kvaliteta i smanjenja rizika u razvoju sistema.
