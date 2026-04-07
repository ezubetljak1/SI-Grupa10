# Set of User Stories / Sprint Backlog

## Naziv projekta
**Sistem za AI asistirano prepoznavanje i obradu računa i ulaznih dokumenata**

## Uvod

Ovaj dokument predstavlja skup user storyja raspoređenih po sprintovima, zajedno sa sprint ciljevima, sprint backlogom i acceptance kriterijima. Dokument je izveden iz Product Backloga i usklađen sa Product Vision, Stakeholder Map i planom rada po sprintovima.

Cilj dokumenta je da:
- prikaže kako se Product Backlog razlaže na manje, konkretne i provjerljive cjeline
- rasporedi razvoj sistema po sprintovima
- poveže funkcionalnosti sa tipovima korisnika sistema
- definiše jasan razvojni put od inicijalnog razumijevanja problema do finalne demonstracije sistema

Sprintovi 1–4 fokusirani su na human-first fazu projekta i obuhvataju analizu problema, razradu zahtjeva, modeliranje, arhitektonske odluke i pripremu razvojnih temelja. Sprintovi 5–11 obuhvataju jezgro implementacije i proširenje ključnih funkcionalnosti sistema. Sprintovi 12–13 fokusirani su na završne operativne dorade, dokumentaciju, stabilizaciju i završnu pripremu sistema.

## Glavni tipovi korisnika obuhvaćeni storyjima

- **Predstavnik firme** – inicijalno registruje firmu u sistem
- **Administrator firme** – upravlja korisnicima, dodjeljuje role i nadzire pristup sistemu
- **Operater** – uploaduje dokumente i pokreće početne korake obrade
- **Računovođa** – pregleda, koriguje i validira izdvojene podatke, te priprema dokument za dalju obradu
- **Osoba za odobravanje** – odobrava ili odbija dokumente
- **Menadžment** – prati osnovne pokazatelje i stanje procesa
- **Korisnik sistema** – generički akter kada funkcionalnost nije vezana za samo jednu poslovnu ulogu

---

# Sprint 1

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 1 | Razumjeti problem koji sistem rješava, identifikovati glavne korisnike i stakeholder grupe, te definisati početni smjer razvoja sistema. | Analiza problema, identifikacija stakeholdera, Product Vision, Stakeholder Map, početni MVP scope, početni Product Backlog. | Nejasan obim sistema, nedovoljno precizno definisani stakeholderi, neusklađenost početnog backloga sa product vision dokumentom. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-1.1 | Upoznavanje sa temom projekta | Cijeli tim | Done | Početno razumijevanje domene i problema |
| SB-1.2 | Analiza problema i poslovnog konteksta | Cijeli tim | Done | Fokus na stvarnom toku obrade dokumenata |
| SB-1.3 | Identifikacija primarnih i sekundarnih stakeholdera | Azra Kovač, Emina Mušinović | Done | Osnova za Stakeholder Map |
| SB-1.4 | Izrada Product Vision dokumenta | Emina Zubetljak, Amar Breščić, Mirza M.Halilović | Done | Definisanje svrhe i cilja sistema |
| SB-1.5 | Izrada Stakeholder Map dokumenta | Azra Kovač, Emina Mušinović | Done | Povezano sa analizom korisnika |
| SB-1.6 | Definisanje početnog MVP scopea | Mirza M. Halilović | Done | Ograničiti prvu verziju sistema |
| SB-1.7 | Izrada početnog Product Backloga | Muhamed Hatunić, Irhad Žiga | Done | Osnova za naredne sprintove |
| SB-1.8 | Interno usaglašavanje pravca razvoja sistema | Cijeli tim | Done | Važno za zajedničko razumijevanje projekta |
| SB-1.9 | Team Charter | Irhad Žiga | Done | Definisanje misije, ciljeva uloga i odgovornosti unutar tima |

---

# Sprint 2

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 2 | Ažurirati product backlog, definisati acceptance criteria i uspostaviti početnu prioritizaciju zahtjeva. | Razrada user storyja, acceptance criteria, definisanje korisničkih tipova, prioritizacija i revizija backloga. | Preširoko definisani storyji, nedovoljno testabilni kriteriji prihvata, neusklađen prioritet funkcionalnosti. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-2.1 | Formiranje user storyja iz product backloga | Emina Zubetljak, Amar Breščić | Done | Storyji trebaju biti jasni i provjerljivi |
| SB-2.2 | Definisanje acceptance criteria za prioritetne storyje | Mirza M. Halilović, Emina Zubetljak | Done | Kriteriji moraju biti testabilni |
| SB-2.3 | Definisanje tipova korisnika u sistemu | Emina Zubetljak | Done | Veza sa stakeholder analizom |
| SB-2.4 | Prioritizacija backlog stavki i storyja | Emina Zubetljak, Mirza M. Halilović | Done | Fokus na MVP i realan razvojni redoslijed |
| SB-2.5 | Identifikacija početnih nefunkcionalnih zahtjeva | Azra Kovač, Emina Mušinović | Done | Sigurnost, performanse, dostupnost |
| SB-2.6 | Povezivanje storyja sa planiranim sprintovima | Emina Zubetljak, Amar Breščić | Done | Priprema sprint strukture |
| SB-2.7 | Revizija i dorada Product Backloga | Muhamed Hatunić, Irhad Žiga | Done | Usklađivanje sa rezultatima analize |

---

# Sprint 3

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 3 | Prevesti zahtjeve sistema u domenski model, use case logiku, arhitektonski pravac i test strategiju. | Domenski entiteti, relacije, use caseovi, arhitektura, risk register i test strategy. | Pogrešno modelovani entiteti, nejasne granice sistema, nedovoljno definisana test strategija. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-3.1 | Identifikacija glavnih domenskih entiteta |  | Planned | Temelj za model sistema |
| SB-3.2 | Modelovanje relacija među entitetima |  | Planned | Važno za bazu i poslovnu logiku |
| SB-3.3 | Definisanje glavnih use caseova sistema |  | Planned | Pokriva glavne tokove rada |
| SB-3.4 | Izrada Architecture Overview dokumenta |  | Planned | Arhitektonski pravac sistema |
| SB-3.5 | Izrada Risk Register dokumenta |  | Planned | Evidencija ključnih rizika |
| SB-3.6 | Definisanje Test Strategy dokumenta |  | Planned | Osnova za kasnije testove |
| SB-3.7 | Povezivanje acceptance criteria sa pristupom testiranju |  | Planned | Veza zahtjeva i verifikacije |

---

# Sprint 4

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 4 | Postaviti razvojne temelje projekta kroz Definition of Done, release plan, osnovni skeleton sistema i pravila rada. | DoD, release plan, frontend i backend skeleton, repozitorij i razvojna pravila. | Neusklađen razvojni setup, nejasna pravila timskog rada, kašnjenje u tehničkom postavljanju projekta. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-4.1 | Izrada Definition of Done |  | Planned | Jasni kriteriji završetka rada |
| SB-4.2 | Izrada Initial Release Plan dokumenta |  | Planned | Planiranje isporuke |
| SB-4.3 | Postavljanje frontend skeletona |  | Planned | Osnova za UI razvoj |
| SB-4.4 | Postavljanje backend skeletona |  | Planned | Osnova za API i poslovnu logiku |
| SB-4.5 | Osnovni tehnički setup projekta |  | Planned | Alati, okruženje, konfiguracija |
| SB-4.6 | Definisanje strukture repozitorija |  | Planned | Pregledna organizacija koda |
| SB-4.7 | Dogovor oko branch strategije i code review pristupa |  | Planned | Pravila timskog rada |
| SB-4.8 | Priprema strukture za Decision Log i AI Usage Log |  | Planned | Praćenje odluka i upotrebe AI alata |

---

# Sprint 5

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 5 | Isporučiti prvi funkcionalni inkrement: osnovni unos dokumenta i pregled zaprimljenog dokumenta. | Upload dokumenta, validacija fajla, pohrana dokumenta i metapodataka, lista i detaljni prikaz dokumenata. | Nejasna pravila za format i veličinu fajla, problemi pri pohrani dokumenata, nepotpun error handling. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-5.1 | UI forma za upload dokumenta |  | Planned | Prvi ulaz u sistem |
| SB-5.2 | Validacija tipa i veličine fajla |  | Planned | Sprječava neispravan unos |
| SB-5.3 | Backend endpoint za upload |  | Planned | Potreban za prihvat fajla |
| SB-5.4 | Pohrana dokumenta i metapodataka |  | Planned | Osnova za dalju obradu |
| SB-5.5 | Lista dokumenata |  | Planned | Pregled zaprimljenih dokumenata |
| SB-5.6 | Detaljni prikaz dokumenta |  | Planned | Uvid u originalni fajl |
| SB-5.7 | Osnovni error handling |  | Planned | Jasne poruke korisniku |
| SB-5.8 | Osnovni testovi za upload i pregled |  | Planned | Provjera osnovnog toka |

## User Stories

### US-5.1 — Upload dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.1 |
| Naziv storyja | Upload dokumenta |
| Opis | Kao operater, želim uploadovati dokument, kako bih započeo obradu. |
| Poslovna vrijednost | Omogućava početak end-to-end toka obrade dokumenta. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Podržani formati i maksimalna veličina fajla biće definisani u pravilima sistema. |
| Veze sa drugim storyjima ili zavisnostima | US-5.2, US-5.3, US-5.4 |

**Acceptance Criteria**
- Kada je operater prijavljen u sistem, ako odabere validan fajl i klikne na “Upload”, tada sistem mora omogućiti uspješan upload dokumenta i prikazati potvrdu o uspjehu.
- Kada operater pokuša upload bez odabranog fajla, tada sistem ne smije dozvoliti slanje zahtjeva i mora prikazati validacionu poruku.
- Sistem mora omogućiti upload jednog dokumenta kroz korisnički interfejs.
- Kada je upload uspješan, tada korisnik treba dobiti tekstualnu potvrdu.

### US-5.2 — Podrška za osnovne formate dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.2 |
| Naziv storyja | Podrška za osnovne formate dokumenta |
| Opis | Kao operater, želim da sistem prihvati PDF, JPG, JPEG i PNG dokumente, kako bih mogao unijeti tipične poslovne dokumente. |
| Poslovna vrijednost | Omogućava praktičnu primjenu sistema nad uobičajenim ulaznim dokumentima. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Nije još definisano da li MVP uključuje višestranične slike ili samo PDF i pojedinačne slike. |
| Veze sa drugim storyjima ili zavisnostima | US-5.1 |

**Acceptance Criteria**
- Kada operater pokuša upload fajla tipa PDF, JPG, JPEG ili PNG, tada sistem mora omogućiti upload bez greške.
- Kada operater pokuša upload fajla koji nije jedan od podržanih formata, kao na primjer DOCX, XLSX, tada sistem ne smije dozvoliti upload.
- Sistem mora validirati ekstenziju fajla prije prihvatanja.
- Sistem mora odbiti fajlove čija veličina prelazi definisani limit.

### US-5.3 — Prikaz greške pri neispravnom uploadu

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.3 |
| Naziv storyja | Prikaz greške pri neispravnom uploadu |
| Opis | Kao operater, želim dobiti poruku greške kada je fajl neispravan ili nepodržan, kako bih znao zašto upload nije uspio. |
| Poslovna vrijednost | Smanjuje zabunu korisnika i pomaže bržem ispravljanju greške pri unosu. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je usaglasiti minimalni skup validacionih poruka. |
| Veze sa drugim storyjima ili zavisnostima | US-5.1, US-5.2 |

**Acceptance Criteria**
- Kada operater pokuša upload nepodržanog fajla, tada sistem mora prikazati jasnu poruku greške.
- Kada operater pokuša upload fajla koji prelazi dozvoljenu veličinu, tada sistem mora prikazati odgovarajuću poruku.
- Sistem mora omogućiti korisniku da nakon greške pokuša novi upload.
- Sistem ne smije prikazivati nejasne ili tehnički nerazumljive poruke bez objašnjenja.

### US-5.4 — Pohrana originalnog dokumenta i metapodataka

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.4 |
| Naziv storyja | Pohrana originalnog dokumenta i metapodataka |
| Opis | Kao operater, želim da sistem sačuva originalni dokument i osnovne metapodatke, kako bi dokument bio dostupan za dalju obradu. |
| Poslovna vrijednost | Osigurava da dokument ostane dostupan za OCR, validaciju i kasniji pregled. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koje metapodatke sistem čuva u MVP-u. |
| Veze sa drugim storyjima ili zavisnostima | US-5.1 |

**Acceptance Criteria**
- Kada je upload uspješan, tada sistem mora sačuvati originalni dokument.
- Sistem mora omogućiti pohranu osnovnih metapodataka uz dokument.
- Sistem ne smije prikazati dokument u listi ako pohrana nije uspješno završena.
- Kada operater ponovo pristupi sistemu, tada sistem mora omogućiti pristup prethodno uploadovanim dokumentima.


### US-5.5 — Pregled liste zaprimljenih dokumenata

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.5 |
| Naziv storyja | Pregled liste zaprimljenih dokumenata |
| Opis | Kao operater, želim vidjeti listu zaprimljenih dokumenata, kako bih mogao otvoriti željeni dokument. |
| Poslovna vrijednost | Korisniku omogućava osnovni operativni pregled nad dokumentima u sistemu. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koje kolone će biti prikazane u listi. |
| Veze sa drugim storyjima ili zavisnostima | US-5.4 |

**Acceptance Criteria**
- Kada operater otvori stranicu za pregled dokumenata, tada sistem mora prikazati listu svih zaprimljenih dokumenata.
- Sistem mora omogućiti prikaz osnovnih podataka o dokumentu u listi.
- Kada nema zaprimljenih dokumenata, tada korisnik treba dobiti odgovarajuću tekstualnu poruku.
- Korisnik treba dobiti mogućnost otvaranja detalja dokumenta iz liste.

### US-5.6 — Detaljni prikaz originalnog dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-5.6 |
| Naziv storyja | Detaljni prikaz originalnog dokumenta |
| Opis | Kao operater, želim otvoriti detaljan prikaz originalnog dokumenta, kako bih provjerio sadržaj fajla. |
| Poslovna vrijednost | Omogućava provjeru da je dokument ispravno zaprimljen i pohranjen. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je odlučiti da li detaljni prikaz uključuje i osnovne metapodatke. |
| Veze sa drugim storyjima ili zavisnostima | US-5.4, US-5.5 |

**Acceptance Criteria**
- Kada operater klikne na dokument iz liste, tada sistem mora omogućiti otvaranje detaljnog prikaza dokumenta.
- Sistem mora omogućiti pregled dokumenta bez potrebe za ponovnim uploadom.
- Sistem mora omogućiti prikaz osnovnih metapodataka uz dokument.

---