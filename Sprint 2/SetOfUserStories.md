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

# Sprint 10

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 10 | Uspostaviti osnovni workflow dokumenta kroz statuse, slanje na odobravanje, odobravanje, odbijanje i historiju promjena statusa. | Statusi dokumenta, tranzicije statusa, slanje na odobravanje, pregled čekajućih dokumenata, odobravanje, odbijanje i historija statusa. | Loše definisane tranzicije statusa, neusklađen workflow sa poslovnim procesom, nejasno ponašanje pri vraćanju dokumenta na doradu. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-10.1 | Model statusa i status history |  | Planned | Praćenje toka dokumenta |
| SB-10.2 | Pravila tranzicije statusa |  | Planned | Workflow pravila |
| SB-10.3 | UI akcija „poslati za odobrenje” |  | Planned | Prelaz u odobravanje |
| SB-10.4 | Lista dokumenata na čekanju |  | Planned | Za osobu za odobravanje |
| SB-10.5 | Odobravanje dokumenta |  | Planned | Ključna workflow akcija |
| SB-10.6 | Odbijanje dokumenta sa komentarom |  | Planned | Povrat na doradu |
| SB-10.7 | Prikaz historije statusa |  | Planned | Transparentnost procesa |
| SB-10.8 | Testovi workflow-a |  | Planned | Validacija glavnog toka |

## User Stories

### US-10.1 — Pregled trenutnog statusa dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.1 |
| Naziv storyja | Pregled trenutnog statusa dokumenta |
| Opis | Kao korisnik sistema, želim vidjeti trenutni status dokumenta, kako bih znao u kojoj se fazi obrade nalazi. |
| Poslovna vrijednost | Omogućava transparentnost procesa i lakše praćenje napretka dokumenta. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je finalno definisati listu statusa. |
| Veze sa drugim storyjima ili zavisnostima | US-10.2, US-10.4, US-10.5 |

**Acceptance Criteria**
- Kada korisnik otvori listu ili detalje dokumenta, tada sistem mora prikazati trenutni status dokumenta.
- Kada se status promijeni, tada sistem mora odmah ažurirati prikaz statusa u UI-u.
- Korisnik treba dobiti konzistentan status kroz listu i detaljni prikaz.

### US-10.2 — Slanje validiranog dokumenta na odobravanje

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.2 |
| Naziv storyja | Slanje validiranog dokumenta na odobravanje |
| Opis | Kao računovođa, želim poslati validiran dokument na odobravanje, kako bi odgovorna osoba mogla donijeti odluku. |
| Poslovna vrijednost | Uvodi formalni poslovni korak od provjere prema odobravanju dokumenta. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li se dokument može vratiti iz ovog stanja na doradu. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 7, US-10.1 |

**Acceptance Criteria**
- Kada su podaci dokumenta validirani, ako računovođa odabere slanje na odobravanje, tada sistem mora promijeniti status dokumenta u odgovarajuće stanje čekanja odobrenja.
- Sistem mora evidentirati ovu promjenu statusa.
- Sistem ne smije dozvoliti slanje na odobravanje dokumenta koji nije validiran.
- Kada je akcija uspješna, tada korisnik treba dobiti potvrdu o uspješnom slanju.

### US-10.3 — Pregled dokumenata koji čekaju odobravanje

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.3 |
| Naziv storyja | Pregled dokumenata koji čekaju odobravanje |
| Opis | Kao osoba za odobravanje, želim vidjeti dokumente koji čekaju moju akciju, kako bih znao šta trebam pregledati. |
| Poslovna vrijednost | Omogućava fokusiran rad odobravatelja i smanjuje rizik da dokumenti ostanu neobrađeni. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je odlučiti koji podaci se prikazuju u listi čekajućih dokumenata. |
| Veze sa drugim storyjima ili zavisnostima | US-10.2 |

**Acceptance Criteria**
- Kada osoba za odobravanje otvori listu čekajućih dokumenata, tada sistem mora prikazati samo dokumente u statusu “na odobrenju”.
- Kada nema dokumenata za odobravanje, tada treba ispisati poruku.
- Sistem mora omogućiti otvaranje detalja dokumenta sa liste čekanja.

### US-10.4 — Odobravanje dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.4 |
| Naziv storyja | Odobravanje dokumenta |
| Opis | Kao osoba za odobravanje, želim odobriti dokument, kako bi mogao prijeći u završnu fazu. |
| Poslovna vrijednost | Omogućava završetak ključnog poslovnog koraka prije generisanja izlaza i knjiženja. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li odobravanje zahtijeva dodatni komentar ili ne. |
| Veze sa drugim storyjima ili zavisnostima | US-10.3 |

**Acceptance Criteria**
- Kada osoba za odobravanje odobri dokument, tada sistem mora promijeniti status u “odobren”.
- Sistem mora evidentirati ko je izvršio odobravanje i kada.
- Sistem ne smije dozvoliti odobravanje dokumenta koji nije u odgovarajućem statusu.

### US-10.5 — Odbijanje dokumenta uz komentar

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.5 |
| Naziv storyja | Odbijanje dokumenta uz komentar |
| Opis | Kao osoba za odobravanje, želim odbiti dokument uz komentar, kako bi prethodni korisnik znao šta treba ispraviti. |
| Poslovna vrijednost | Omogućava povratnu informaciju i vraćanje dokumenta na doradu bez gubitka konteksta. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li je komentar obavezan pri odbijanju. |
| Veze sa drugim storyjima ili zavisnostima | US-10.3 |

**Acceptance Criteria**
- Kada osoba za odobravanje odbije dokument, tada sistem mora promijeniti status dokumenta u odbijen ili vraćen na doradu.
- Kada je dokument odbijen, tada sistem mora omogućiti njegov povrat u fazu korekcije
- Sistem mora omogućiti unos komentara uz odbijanje.
- Korisnik koji nastavlja obradu treba dobiti komentar kao objašnjenje razloga odbijanja.

### US-10.6 — Pregled historije statusa dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-10.6 |
| Naziv storyja | Pregled historije statusa dokumenta |
| Opis | Kao korisnik sistema, želim vidjeti historiju statusa dokumenta, kako bih mogao pratiti tok obrade. |
| Poslovna vrijednost | Omogućava pregled prethodnih koraka i lakše razumijevanje toka rada nad dokumentom. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koji detalji o promjeni statusa će biti vidljivi korisnicima. |
| Veze sa drugim storyjima ili zavisnostima | US-10.1, US-10.2, US-10.4, US-10.5 |

**Acceptance Criteria**
- Kada korisnik otvori detalje dokumenta, tada sistem mora prikazati historiju svih promjena statusa.
- Kada ne postoji historija (npr. novi dokument), tada sistem treba prikazati samo inicijalni status.
- Sistem mora čuvati historiju statusa trajno i ne smije dozvoliti njeno brisanje od strane korisnika.
- Sistem mora omogućiti pregled redoslijeda ključnih koraka obrade.
- Korisnik treba dobiti barem informaciju o statusu, vremenu promjene i vrsti akcije.

---

# Sprint 11

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 11 | Zatvoriti osnovni end-to-end tok generisanjem XML izlaza, te dodati audit trag i osnovne funkcionalnosti za efikasniji rad sa većim brojem dokumenata. | XML generisanje, pregled i pohrana XML-a, finalni status dokumenta, audit log, pretraga i filtriranje dokumenata. | Nejasno definisana XML struktura, neusaglašen završni status dokumenta, preširok scope sprinta ako pretraga i XML ne budu ograničeni na osnovni nivo. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-11.1 | XML mapping pravila |  | Planned | Definisanje izlazne strukture |
| SB-11.2 | Generator XML-a |  | Planned | Formiranje izlaznog fajla |
| SB-11.3 | Spremanje XML izlaza |  | Planned | Veza sa dokumentom |
| SB-11.4 | UI pregled XML-a |  | Planned | Provjera prije završetka |
| SB-11.5 | Download XML-a |  | Planned | Korištenje izvan sistema |
| SB-11.6 | Finalni workflow status |  | Planned | Zatvaranje obrade |
| SB-11.7 | Model audit loga i evidentiranje ključnih akcija |  | Planned | Transparentnost i kontrola |
| SB-11.8 | Search API |  | Planned | Pretraga po ključnim kriterijima |
| SB-11.9 | Filteri i query parametri |  | Planned | Fokusiran pregled liste |
| SB-11.10 | Testovi za XML, audit i osnovnu pretragu |  | Planned | Potvrda proširenog toka |

## User Stories

### US-11.1 — Generisanje XML izlaza

| Polje | Sadržaj |
|---|---|
| ID storyja | US-11.1 |
| Naziv storyja | Generisanje XML izlaza |
| Opis | Kao računovođa, želim da sistem generiše XML iz validiranih i odobrenih podataka, kako bi dokument bio spreman za dalju upotrebu. |
| Poslovna vrijednost | Omogućava standardizovan izlaz koji može biti osnova za dalju razmjenu i obradu. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je finalno definisati XML strukturu za sistem. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 10 i odobren dokument |

**Acceptance Criteria**
- Kada je dokument u statusu “odobren”, ako računovođa inicira generisanje XML-a, tada sistem mora generisati XML fajl na osnovu validiranih podataka.
- Kada dokument nije u statusu “odobren”, tada sistem ne smije dozvoliti generisanje XML-a.
- Kada generisanje ne uspije (npr. nedostaju podaci), tada sistem mora prikazati grešku i ne smije kreirati neispravan XML.
- Sistem mora koristiti definisana mapping pravila za popunjavanje XML strukture.

### US-11.2 — Pregled i preuzimanje XML-a

| Polje | Sadržaj |
|---|---|
| ID storyja | US-11.2 |
| Naziv storyja | Pregled i preuzimanje XML-a |
| Opis | Kao računovođa, želim pregledati i preuzeti generisani XML, kako bih provjerio njegov sadržaj i koristio ga izvan sistema. |
| Poslovna vrijednost | Omogućava dodatnu kontrolu i praktičnu upotrebu XML izlaza u drugim procesima. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li će XML biti prikazan kao sirovi tekst ili strukturirani pregled. |
| Veze sa drugim storyjima ili zavisnostima | US-11.1 |

**Acceptance Criteria**
- Kada je XML uspješno generisan, tada sistem mora omogućiti njegov pregled u UI-u.
- Kada korisnik odabere preuzimanje, sistem mora omogućiti download ispravnog XML fajla.
- Korisnik treba dobiti fajl u očekivanom XML formatu.

### US-11.3 — Pohrana XML-a i završetak obrade dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-11.3 |
| Naziv storyja | Pohrana XML-a i završetak obrade dokumenta |
| Opis | Kao računovođa, želim da generisani XML bude sačuvan uz dokument i da dokument dobije završni status, kako bi bio spreman za knjiženje ili dalju poslovnu upotrebu. |
| Poslovna vrijednost | Zatvara osnovni tok sistema i omogućava formalni završetak obrade dokumenta. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati naziv i značenje finalnog statusa dokumenta. |
| Veze sa drugim storyjima ili zavisnostima | US-11.1 |

**Acceptance Criteria**
- Kada sistem generiše XML, ako je generisanje uspješno, tada mora sačuvati XML uz odgovarajući dokument.
- Kada korisnik potvrdi završetak obrade, sistem mora promijeniti status dokumenta u finalno stanje.
- Sistem ne smije dozvoliti finalizaciju dokumenta bez uspješnog XML izlaza. 

### US-11.4 — Pregled audit traga nad dokumentom

| Polje | Sadržaj |
|---|---|
| ID storyja | US-11.4 |
| Naziv storyja | Pregled audit traga nad dokumentom |
| Opis | Kao administrator firme, želim imati pregled ključnih akcija nad dokumentom, kako bih mogao pratiti ko je izvršio koju akciju i kada. |
| Poslovna vrijednost | Omogućava odgovornost, transparentnost i lakšu internu kontrolu procesa. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koje akcije ulaze u audit trag. |
| Veze sa drugim storyjima ili zavisnostima | Ključne akcije iz sprintova 5–11 |

**Acceptance Criteria**
- Kada administrator otvori detalje dokumenta, tada sistem mora prikazati audit trag svih ključnih akcija.
- Sistem mora omogućiti pregled barem tipa akcije, korisnika i vremena izvršenja.
- Sistem ne smije izostaviti ključne workflow akcije iz audit traga ako su definisane kao obavezne.
- Sistem ne smije dozvoliti izmjenu ili brisanje audit log zapisa od strane korisnika.

### US-11.5 — Pretraga i filtriranje dokumenata

| Polje | Sadržaj |
|---|---|
| ID storyja | US-11.5 |
| Naziv storyja | Pretraga i filtriranje dokumenata |
| Opis | Kao računovođa, želim pretražiti i filtrirati dokumente po osnovnim kriterijima, kako bih brže pronašao traženi dokument i lakše radio sa većim brojem zapisa. |
| Poslovna vrijednost | Ubrzava rad korisnika kada broj dokumenata u sistemu poraste i čini listu preglednijom. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati minimalni skup podržanih kriterija pretrage i filtera. |
| Veze sa drugim storyjima ili zavisnostima | Lista dokumenata iz Sprinta 5 |

**Acceptance Criteria**
- Kada korisnik unese kriterij pretrage, tada sistem mora vratiti listu dokumenata koji odgovaraju kriteriju.
- Kada korisnik primijeni filtere (npr. status, datum, iznos), tada sistem mora prikazati samo dokumente koji zadovoljavaju sve odabrane filtere.
- Kada nema rezultata pretrage, tada korisnik treba dobiti odgovarajuću poruku.
- Sistem ne smije vraćati dokumente iz drugih firmi.
- Sistem mora ograničiti rezultate po stranici radi performansi.

---

# Sprint 12

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 12 | Dodati završne operativne dorade, poboljšati svakodnevni rad korisnika i pripremiti dokumentaciju za završnu verziju sistema. | My pending items, komentari, obavještenja, reset lozinke, osnovni dashboard, manje dorade user managementa i dokumentacija. | Preopterećenost sprinta lakšim, ali brojnim funkcionalnostima; nejasna pravila notifikacija i komentara; sigurnosni izazovi kod reseta lozinke. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-12.1 | “My pending items” prikaz |  | Planned | Personalizovan pregled zadataka |
| SB-12.2 | Comments model i UI |  | Planned | Interna komunikacija |
| SB-12.3 | Notification mehanizam za ključne workflow događaje |  | Planned | Pravovremena reakcija korisnika |
| SB-12.4 | Reset password flow |  | Planned | Povrat pristupa nalogu |
| SB-12.5 | Osnovni dashboard |  | Planned | Pregled stanja procesa |
| SB-12.6 | Lagane dorade user management funkcionalnosti |  | Planned | Održavanje korisničkih naloga |
| SB-12.7 | Korisnička dokumentacija |  | Planned | Podrška krajnjim korisnicima |
| SB-12.8 | Tehnička dokumentacija |  | Planned | Održivost sistema |
| SB-12.9 | Release notes draft |  | Planned | Priprema završne verzije |
| SB-12.10 | UI polishing i završne manje dorade |  | Planned | Stabilizacija prije finala |

## User Stories

### US-12.1 — Pregled dokumenata koji čekaju moju akciju

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.1 |
| Naziv storyja | Pregled dokumenata koji čekaju moju akciju |
| Opis | Kao osoba za odobravanje, želim vidjeti dokumente koji čekaju moju akciju, kako bih imao fokusiran pregled zadataka. |
| Poslovna vrijednost | Pomaže odobravatelju da efikasnije upravlja sopstvenim obavezama u sistemu. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koje statusne grupe ulaze u “moji zadaci”. |
| Veze sa drugim storyjima ili zavisnostima | Workflow iz Sprinta 10 |

**Acceptance Criteria**
- Kada korisnik sa odgovarajućom rolom otvori svoj pregled zadataka, ako postoje dokumenti koji traže njegovu akciju, tada sistem mora prikazati te dokumente.
- Sistem mora omogućiti brz pristup relevantnim dokumentima iz tog pregleda.
- Sistem ne smije prikazivati nepovezane dokumente u “moji zadaci” pogledu.

### US-12.2 — Komentari na dokumentu

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.2 |
| Naziv storyja | Komentari na dokumentu |
| Opis | Kao korisnik sistema, želim ostavljati komentare na dokumentu, kako bih pojasnio problem, odluku ili naredni korak. |
| Poslovna vrijednost | Omogućava bolju komunikaciju unutar procesa rada na dokumentu. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati ko sve može vidjeti i dodavati komentare. |
| Veze sa drugim storyjima ili zavisnostima | Workflow storyji iz Sprinta 10 |

**Acceptance Criteria**
- Kada korisnik otvori dokument, ako ima dozvolu za komentarisanje, tada sistem mora omogućiti unos komentara.
- Sistem mora sačuvati komentar uz odgovarajući dokument.
- Sistem ne smije dozvoliti unos praznog komentara.
- Korisnik treba dobiti pregled prethodno ostavljenih komentara ako su mu dostupni.

### US-12.3 — Obavještenje o dokumentu koji čeka odobravanje

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.3 |
| Naziv storyja | Obavještenje o dokumentu koji čeka odobravanje |
| Opis | Kao osoba za odobravanje, želim dobiti obavještenje kada dokument čeka moju akciju, kako bih mogao pravovremeno reagovati. |
| Poslovna vrijednost | Smanjuje kašnjenja u procesu odobravanja. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati kanal obavještenja u ovoj fazi sistema. |
| Veze sa drugim storyjima ili zavisnostima | US-12.1 |

**Acceptance Criteria**
- Kada dokument pređe u status “na odobrenju”, tada sistem mora generisati obavještenje za korisnika koji ima ulogu za odobravanje dokumenata.
- Sistem mora omogućiti da obavještenje bude povezano sa konkretnim dokumentom ili zadatkom.
- Korisnik treba dobiti signal da postoji nova stavka koja traži akciju.

### US-12.4 — Obavještenje o odbijenom ili vraćenom dokumentu

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.4 |
| Naziv storyja | Obavještenje o odbijenom ili vraćenom dokumentu |
| Opis | Kao računovođa, želim dobiti obavještenje kada je dokument odbijen ili vraćen, kako bih znao da je potrebna dodatna obrada. |
| Poslovna vrijednost | Omogućava bržu reakciju na dokumente koji zahtijevaju doradu. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li se “vraćen” dokument modeluje posebnim statusom. |
| Veze sa drugim storyjima ili zavisnostima | US-10.5 |

**Acceptance Criteria**
- Kada dokument promijeni status u “odbijen” ili “na doradi”, tada sistem mora generisati obavještenje za odgovornog računovođu.
- Kada postoji komentar pri odbijanju, tada sistem mora povezati obavještenje sa tim komentarom.
- Korisnik treba dobiti jasnu informaciju da je potrebna dodatna obrada dokumenta.

### US-12.5 — Reset lozinke

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.5 |
| Naziv storyja | Reset lozinke |
| Opis | Kao korisnik sistema, želim resetovati lozinku, kako bih mogao povratiti pristup nalogu. |
| Poslovna vrijednost | Smanjuje operativni problem gubitka pristupa i potrebu za ručnom intervencijom administratora. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li se reset radi putem emaila ili administrativne intervencije. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 8 |

**Acceptance Criteria**
- Kada korisnik pokrene proceduru resetovanja lozinke, ako ispuni definisane uslove, tada sistem mora omogućiti postavljanje nove lozinke.
- Sistem mora osigurati da stara lozinka više ne važi nakon uspješnog resetovanja.
- Korisnik treba dobiti potvrdu da je lozinka uspješno promijenjena.

### US-12.6 — Osnovni dashboard stanja procesa

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.6 |
| Naziv storyja | Osnovni dashboard stanja procesa |
| Opis | Kao menadžment, želim vidjeti osnovni dashboard sa brojem dokumenata po statusu, kako bih imao pregled stanja procesa. |
| Poslovna vrijednost | Omogućava rukovodstvu brz uvid u opterećenje i stanje obrade dokumenata. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koje metrike ulaze u osnovni dashboard. |
| Veze sa drugim storyjima ili zavisnostima | Workflow statusi iz Sprinta 10 |

**Acceptance Criteria**
- Kada korisnik sa odgovarajućom ulogom otvori dashboard, ako podaci postoje, tada sistem mora prikazati osnovne pokazatelje stanja procesa.
- Sistem mora omogućiti pregled barem broja dokumenata po statusu.
- Korisnik treba dobiti pregled koji je jasan i dovoljno sažet za operativno praćenje.

### US-12.7 — Ažuriranje osnovnih podataka o korisnicima

| Polje | Sadržaj |
|---|---|
| ID storyja | US-12.7 |
| Naziv storyja | Ažuriranje osnovnih podataka o korisnicima |
| Opis | Kao administrator firme, želim pregledati i ažurirati osnovne podatke o korisnicima, kako bih lakše održavao aktivne naloge. |
| Poslovna vrijednost | Pomaže održavanju tačnih i ažurnih korisničkih naloga u organizaciji. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koji se podaci mogu mijenjati bez uticaja na sigurnost. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 9 |

**Acceptance Criteria**
- Kada administrator otvori korisnički nalog, ako ima odgovarajuću dozvolu, tada sistem mora omogućiti izmjenu definisanih osnovnih podataka.
- Sistem mora sačuvati ažurirane podatke korisnika.
- Sistem ne smije dozvoliti izmjenu podataka koji su zaštićeni pravilima sigurnosti bez dodatnih uslova.

---

# Sprint 13

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 13 | Stabilizovati, demonstrirati i argumentovano predstaviti finalnu verziju sistema. | Regresiono testiranje, ispravke bugova, validacija dokumentacije i artefakata, priprema demonstracije i odbrane. | Otkrivanje kritičnih bugova kasno u projektu, nedovoljno spremna demonstracija, neusklađeni završni artefakti. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-13.1 | Regression test ključnih tokova |  | Planned | Završna provjera sistema |
| SB-13.2 | Ispravka prioritetnih bugova |  | Planned | Fokus na stabilnosti |
| SB-13.3 | Finalna validacija dokumentacije |  | Planned | Usklađivanje završnih materijala |
| SB-13.4 | Finalna validacija backloga, logova i artefakata |  | Planned | Potpunost projektne dokumentacije |
| SB-13.5 | Priprema demo scenarija |  | Planned | Prezentacija glavnih funkcionalnosti |
| SB-13.6 | Priprema odgovora za odbranu |  | Planned | Argumentovana prezentacija rada |
| SB-13.7 | Finalni pregled tehničkog duga i ograničenja |  | Planned | Realna evaluacija sistema |
| SB-13.8 | Individualna refleksija i peer evaluation |  | Planned | Završna timska evaluacija |

---

## Završna napomena

Ovakva raspodjela sprintova omogućava:
- da se najvažnije funkcionalnosti sistema razvijaju ranije
- da autentifikacija i pristup budu modelovani kroz firmu i korisnike unutar firme
- da upravljanje korisnicima i rolama bude odvojeno od workflow logike
- da workflow i XML budu raspoređeni u zasebne sprintove kako bi opseg rada bio realniji
- da završni sprintovi budu fokusirani na operativna poboljšanja, dokumentaciju i stabilizaciju sistema
