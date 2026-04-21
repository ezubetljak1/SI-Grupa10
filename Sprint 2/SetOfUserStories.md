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
| SB-3.1 | Identifikacija glavnih domenskih entiteta | Emina Zubetljak, Mirza M. Halilović | Done | Temelj za model sistema |
| SB-3.2 | Modelovanje relacija među entitetima | Emina Zubetljak, Mirza M. Halilović | Done | Važno za bazu i poslovnu logiku |
| SB-3.3 | Definisanje glavnih use caseova sistema | Emina Zubetljak, Mirza M. Halilović | Done | Pokriva glavne tokove rada |
| SB-3.4 | Izrada Architecture Overview dokumenta | Emina Mušinović, Azra Kovač | Done | Arhitektonski pravac sistema |
| SB-3.5 | Izrada Risk Register dokumenta | Irhad Žiga, Muhamed Hatunić | Done | Evidencija ključnih rizika |
| SB-3.6 | Definisanje Test Strategy dokumenta | Amar Breščić | Done | Osnova za kasnije testove |
| SB-3.7 | Povezivanje acceptance criteria sa pristupom testiranju | Amar Breščić | Done | Veza zahtjeva i verifikacije |

---

# Sprint 4

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 4 | Postaviti razvojne temelje projekta kroz Definition of Done, release plan, osnovni skeleton sistema i pravila rada. | DoD, release plan, frontend i backend skeleton, repozitorij i razvojna pravila. | Neusklađen razvojni setup, nejasna pravila timskog rada, kašnjenje u tehničkom postavljanju projekta. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-4.1 | Izrada Definition of Done | Azra Kovač, Emina Mušinović, Irhad Žiga | Done | Jasni kriteriji završetka rada |
| SB-4.2 | Izrada Initial Release Plan dokumenta | Mirza M. Halilović, Amar Breščić, Muhamed Hatunić | Done | Planiranje isporuke |
| SB-4.3 | Postavljanje frontend skeletona | Emina Zubetljak | Done | Osnova za UI razvoj |
| SB-4.4 | Postavljanje backend skeletona | Emina Zubetljak | Done | Osnova za API i poslovnu logiku |
| SB-4.5 | Osnovni tehnički setup projekta | Emina Zubetljak | Done | Alati, okruženje, konfiguracija |
| SB-4.6 | Definisanje strukture repozitorija | Cijeli tim | Done | Pregledna organizacija koda |
| SB-4.7 | Dogovor oko branch strategije i code review pristupa | Cijeli tim | Done | Pravila timskog rada |

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


# Sprint 6

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 6 | Omogućiti OCR obradu, osnovno AI izdvajanje i prikaz izdvojenih podataka. | OCR integracija, AI izdvajanje osnovnih polja, klasifikacija tipa dokumenta i prikaz rezultata obrade. | Izbor OCR/AI servisa, greške u integraciji eksternih servisa, neuspješna obrada dokumenata. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-6.1 | Integracija sa OCR servisom |  | Planned | Tekstualna obrada dokumenta |
| SB-6.2 | Integracija sa AI servisom |  | Planned | Izdvajanje poslovnih podataka |
| SB-6.3 | Mapping OCR/AI odgovora u interne modele |  | Planned | Potrebno za čuvanje rezultata |
| SB-6.4 | Spremanje izdvojenih polja |  | Planned | Povezano sa prikazom podataka |
| SB-6.5 | UI za prikaz izdvojenih podataka |  | Planned | Računovođa pregleda rezultate |
| SB-6.6 | Osnovna klasifikacija dokumenta |  | Planned | Račun ili ostalo |
| SB-6.7 | Error handling za eksterne servise |  | Planned | Važno za stabilnost |
| SB-6.8 | Integracioni testovi za obradu dokumenata |  | Planned | Provjera cjelokupnog toka obrade |

## User Stories

### US-6.1 — Pokretanje OCR obrade nakon uploada

| Polje | Sadržaj |
|---|---|
| ID storyja | US-6.1 |
| Naziv storyja | Pokretanje OCR obrade nakon uploada |
| Opis | Kao operater, želim da se nakon uploada pokrene OCR obrada dokumenta, kako bi tekst bio spreman za dalju analizu. |
| Poslovna vrijednost | Omogućava prelazak sa obične pohrane dokumenta na automatsku obradu sadržaja. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | OCR servis i način integracije još trebaju biti finalno odabrani. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 5, posebno US-5.1 i US-5.4 |

**Acceptance Criteria**
- Kada je dokument uspješno uploadovan, tada sistem mora automatski pokrenuti OCR obradu bez dodatne akcije korisnika.
- Kada se OCR obrada pokrene, tada sistem mora evidentirati status obrade.
- Kada OCR obrada uspješno završi, tada sistem mora sačuvati izdvojeni tekst u bazi podataka.
- Kada OCR servis nije dostupan ili dođe do greške, tada sistem mora obavijestiti korisnika da je OCR neuspješan.

### US-6.2 — Prikaz automatski izdvojenih podataka

| Polje | Sadržaj |
|---|---|
| ID storyja | US-6.2 |
| Naziv storyja | Prikaz automatski izdvojenih podataka |
| Opis | Kao računovođa, želim vidjeti automatski izdvojene podatke sa dokumenta, kako bih ih mogao provjeriti. |
| Poslovna vrijednost | Omogućava korisniku da odmah procijeni kvalitet automatske obrade. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati prikaz polja i redoslijed informacija u interfejsu. |
| Veze sa drugim storyjima ili zavisnostima | US-6.1, US-6.3 |

**Acceptance Criteria**
- Kada je obrada dokumenta završena, tada sistem mora prikazati izdvojene podatke u UI-u.
- Sistem mora omogućiti pregled osnovnih izdvojenih vrijednosti na jednom mjestu.
- Korisnik treba dobiti jasan prikaz koji razlikuje originalni dokument od izdvojenih podataka.


### US-6.3 — Izdvajanje osnovnih polja dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-6.3 |
| Naziv storyja | Izdvajanje osnovnih polja dokumenta |
| Opis | Kao računovođa, želim da sistem izdvoji osnovna polja kao što su dobavljač, datum, broj računa, ukupan iznos i PDV, kako bih izbjegao ručni unos. |
| Poslovna vrijednost | Smanjuje količinu ručnog rada i ubrzava obradu dokumenata. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Nije još definisano da li sistem obuhvata i dodatna polja poput valute ili broja stavki. |
| Veze sa drugim storyjima ili zavisnostima | US-6.1 |

**Acceptance Criteria**
- Kada OCR/AI obrada uspješno završi, ako dokument sadrži tražena polja, tada sistem mora pokušati izdvojiti dobavljača, datum, broj računa, ukupan iznos i PDV.
- Sistem mora omogućiti spremanje izdvojenih polja uz dokument.
- Sistem treba prikazati praznu ili nepopunjenu vrijednost za polje koje nije moglo biti izdvojeno, umjesto proizvoljnog podatka.
- Sistem mora omogućiti da svako polje ima status pouzdanosti (confidence score) ako je dostupan iz AI servisa.

### US-6.4 — Osnovna klasifikacija tipa dokumenta

| Polje | Sadržaj |
|---|---|
| ID storyja | US-6.4 |
| Naziv storyja | Osnovna klasifikacija tipa dokumenta |
| Opis | Kao računovođa, želim da sistem prepozna tip dokumenta kao račun ili ostalo, kako bih znao kako dalje postupati. |
| Poslovna vrijednost | Omogućava razlikovanje osnovnih poslovnih tokova obrade dokumenata. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | U sistemu je klasifikacija ograničena na račun i ostalo. |
| Veze sa drugim storyjima ili zavisnostima | US-6.1 |

**Acceptance Criteria**
- Kada sistem obradi dokument, ako klasifikacija uspije, tada mora označiti dokument kao "račun" ili "ostalo".
- Sistem mora omogućiti prikaz klasifikacije u detaljima dokumenta.
- Sistem ne smije ostaviti korisnika bez informacije o tipu dokumenta ako je klasifikacija izvršena.
- Kada klasifikacija nije sigurna, tada sistem mora označiti dokument kao “nepoznat tip”.

### US-6.5 — Označavanje neuspjele obrade

| Polje | Sadržaj |
|---|---|
| ID storyja | US-6.5 |
| Naziv storyja | Označavanje neuspjele obrade |
| Opis | Kao računovođa, želim da sistem jasno označi kada obrada dokumenta nije uspjela, kako bih znao da su potrebni dodatna provjera ili ponovni pokušaj obrade. |
| Poslovna vrijednost | Omogućava korisniku da prepozna problem i reaguje bez gubitka vremena. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati kako se u sistemu prikazuje status neuspjele obrade. |
| Veze sa drugim storyjima ili zavisnostima | US-6.1, US-6.2, US-6.3 |

**Acceptance Criteria**
- Kada OCR ili AI obrada ne uspije, tada sistem mora jasno označiti dokument statusom “obrada neuspješna”.
- Sistem mora omogućiti prikaz statusa neuspjele obrade u listi i detaljima dokumenta.
- Korisnik treba dobiti jasnu informaciju da automatska obrada nije završena uspješno.
- Sistem ne smije prikazivati djelimične ili nepouzdane podatke kao validne bez odgovarajuće oznake.

---

# Sprint 7

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 7 | Omogućiti ručnu korekciju i validaciju izdvojenih podataka prije daljeg toka obrade. | Edit forma, validacije obaveznih i formatnih polja, matematička provjera iznosa i spremanje korigovanih podataka. | Neusklađena validaciona pravila, loše definisana editabilna polja, nedovoljno pouzdana provjera ispravnosti podataka. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-7.1 | Edit forma za izdvojena polja |  | Planned | Ručna korekcija podataka |
| SB-7.2 | Server-side i client-side validacije |  | Planned | Potrebno za konzistentnost |
| SB-7.3 | Validacija obaveznih polja |  | Planned | Sprječava nepotpun unos |
| SB-7.4 | Validacija formata |  | Planned | Datum, brojčane vrijednosti |
| SB-7.5 | Matematička provjera subtotal/PDV/ukupan iznos |  | Planned | Logička provjera podataka |
| SB-7.6 | Spremanje pregledanih i korigovanih podataka |  | Planned | Priprema za naredne korake |
| SB-7.7 | Test scenariji za validaciju i korekciju |  | Planned | Testabilnost izmjena |

## User Stories

### US-7.1 — Ručna korekcija izdvojenih polja

| Polje | Sadržaj |
|---|---|
| ID storyja | US-7.1 |
| Naziv storyja | Ručna korekcija izdvojenih polja |
| Opis | Kao računovođa, želim moći ručno korigovati izdvojena polja, kako bih ispravio potencijalne greške automatske obrade. |
| Poslovna vrijednost | Omogućava human-in-the-loop pristup i podiže pouzdanost podataka prije nastavka procesa. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koja polja su editabilna. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 6, posebno US-6.2 i US-6.3 |

**Acceptance Criteria**
- Kada računovođa otvori izdvojene podatke, ako odabere polje za izmjenu, tada sistem mora omogućiti ručnu korekciju.
- Kada računovođa izmijeni vrijednost polja, tada sistem mora ažurirati prikaz nakon potvrde izmjene.
- Kada se unese nevalidna vrijednost, tada sistem ne smije dozvoliti spremanje i mora prikazati validacionu poruku (npr. neispravan format datuma).
- Korisnik treba dobiti potvrdu da su izmjene uspješno sačuvane.

### US-7.2 — Validacija obaveznih polja

| Polje | Sadržaj |
|---|---|
| ID storyja | US-7.2 |
| Naziv storyja | Validacija obaveznih polja |
| Opis | Kao računovođa, želim da sistem provjeri da li su obavezna polja popunjena, kako dokument ne bi ostao nepotpun. |
| Poslovna vrijednost | Sprječava da dokument sa nepotpunim podacima uđe u naredne faze procesa. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je finalno usaglasiti skup obaveznih polja. |
| Veze sa drugim storyjima ili zavisnostima | US-7.1 |

**Acceptance Criteria**
- Kada korisnik pokuša spremiti dokument, tada sistem mora provjeriti da li su sva obavezna polja popunjena.
- Kada neko od obaveznih polja nije popunjeno, tada sistem ne smije dozvoliti spremanje i mora jasno označiti koja polja nedostaju.
- Sistem ne smije dozvoliti prelazak dokumenta u naredni korak bez obaveznih podataka.

### US-7.3 — Validacija formata podataka

| Polje | Sadržaj |
|---|---|
| ID storyja | US-7.3 |
| Naziv storyja | Validacija formata podataka |
| Opis | Kao računovođa, želim da sistem validira format datuma i numeričkih vrijednosti, kako bih lakše uočio neispravan unos. |
| Poslovna vrijednost | Smanjuje rizik od neispravnih podataka u daljem toku obrade. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati očekivane formate datuma i brojčanih vrijednosti. |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2 |

**Acceptance Criteria**
- Kada korisnik unese datum ili brojčanu vrijednost, ako format nije ispravan, tada sistem mora prikazati validacionu poruku.
- Sistem mora omogućiti spremanje samo validno formatiranih podataka.
- Korisnik treba dobiti informaciju koje polje ima neispravan format.

### US-7.4 — Matematička provjera iznosa

| Polje | Sadržaj |
|---|---|
| ID storyja | US-7.4 |
| Naziv storyja | Matematička provjera iznosa |
| Opis | Kao računovođa, želim da sistem provjeri osnovnu matematičku ispravnost iznosa, kako bih otkrio nelogičnosti. |
| Poslovna vrijednost | Pomaže u otkrivanju grešaka u iznosima prije odobravanja i XML generisanja. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Nije još definisano da li se provjeravaju samo ukupan iznos i PDV ili i dodatna računanja. |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2, US-7.3 |

**Acceptance Criteria**
- Kada se unesu relevantni iznosi, ako korisnik sačuva dokument, tada sistem mora izvršiti osnovnu matematičku provjeru.
- Kada matematička provjera nije zadovoljena, tada sistem mora upozoriti korisnika i označiti problematična polja.
- Kada su iznosi validni, tada sistem mora dozvoliti spremanje bez upozorenja.

### US-7.5 — Spremanje potvrđenih podataka

| Polje | Sadržaj |
|---|---|
| ID storyja | US-7.5 |
| Naziv storyja | Spremanje potvrđenih podataka |
| Opis | Kao računovođa, želim spremiti pregledane i eventualno korigovane podatke, kako bi dokument bio spreman za naredni korak. |
| Poslovna vrijednost | Omogućava prelazak iz faze provjere u narednu fazu procesa. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je odlučiti da li sistem čuva i oznaku da su polja ručno mijenjana. |
| Veze sa drugim storyjima ili zavisnostima | US-7.1, US-7.2, US-7.3 |

**Acceptance Criteria**
- Kada su sva validaciona pravila zadovoljena, ako računovođa sačuva podatke, tada sistem mora spremiti potvrđene vrijednosti.
- Kada je spremanje uspješno, tada korisnik treba dobiti tekstualnu potvrdu.
- Sistem mora omogućiti da dokument bude označen kao spreman za naredni korak.
- Sistem ne smije izgubiti prethodno unesene korekcije nakon uspješnog spremanja.


---

# Sprint 8

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 8 | Uvesti organizacioni model pristupa sistemu kroz registraciju firme, kreiranje administratorskog naloga i osnovnu autentifikaciju korisnika. | Registracija firme, prvi admin nalog, login/logout, izolacija podataka po organizaciji i osnovna sigurnost pristupa. | Nejasna pravila registracije, problemi sa autentifikacijom, nedovoljna izolacija organizacijskih podataka. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-8.1 | Model firme / organizacije |  | Planned | Multi-tenant osnova |
| SB-8.2 | Model korisnika povezanog sa firmom |  | Planned | Veza korisnik-organizacija |
| SB-8.3 | Registracija firme |  | Planned | Ulaz u sistem za organizaciju |
| SB-8.4 | Kreiranje prvog administratorskog naloga |  | Planned | Upravljanje pristupom |
| SB-8.5 | Login endpoint i UI |  | Planned | Prijava korisnika |
| SB-8.6 | Logout |  | Planned | Zatvaranje sesije |
| SB-8.7 | Session/token menadžment |  | Planned | Osnovna autentifikacija |
| SB-8.8 | Zaštita ruta |  | Planned | Ograničenje pristupa |
| SB-8.9 | Osnovno hashiranje lozinke |  | Planned | Sigurnost korisničkih naloga |
| SB-8.10 | Testovi za auth i organizacioni model |  | Planned | Verifikacija pristupa |

## User Stories

### US-8.1 — Registracija firme u sistem

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.1 |
| Naziv storyja | Registracija firme u sistem |
| Opis | Kao predstavnik firme, želim registrovati svoju firmu u sistem, kako bih mogao uspostaviti radno okruženje za svoju organizaciju. |
| Poslovna vrijednost | Omogućava da sistem koristi više organizacija uz odvojene podatke i korisnike. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koja organizacijska polja su obavezna pri registraciji. |
| Veze sa drugim storyjima ili zavisnostima | US-8.2 |

**Acceptance Criteria**
- Kada predstavnik firme unese validne podatke o firmi (npr. naziv, email), i potvrdi registraciju, tada sistem mora kreirati novu organizaciju u bazi.
- Kada se pokuša registracija sa već postojećim emailom firme, tada sistem ne smije dozvoliti duplikat i mora prikazati poruku greške.
- Sistem mora omogućiti jedinstvenu identifikaciju registrovane firme.
- Korisnik treba dobiti potvrdu da je registracija firme uspješna.

### US-8.2 — Kreiranje prvog administratorskog naloga firme

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.2 |
| Naziv storyja | Kreiranje prvog administratorskog naloga firme |
| Opis | Kao predstavnik firme, želim kreirati prvi administratorski nalog firme prilikom registracije, kako bih mogao upravljati pristupom sistemu. |
| Poslovna vrijednost | Omogućava da svaka firma odmah ima odgovornu osobu za korisnike i pristup. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je odlučiti da li sistem podržava samostalnu aktivaciju ili ručno potvrđivanje naloga. |
| Veze sa drugim storyjima ili zavisnostima | US-8.1 |

**Acceptance Criteria**
- Kada se firma registruje, tada sistem mora omogućiti kreiranje prvog korisnika sa administratorskom ulogom.
- Kada korisnik unese validne podatke, tada sistem mora kreirati administratorski nalog povezan sa firmom.
- Sistem mora povezati administratorski nalog sa odgovarajućom firmom.
- Sistem mora sigurno pohraniti lozinku koristeći heširanje.

### US-8.3 — Prijava korisnika firme

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.3 |
| Naziv storyja | Prijava korisnika firme |
| Opis | Kao korisnik firme, želim prijaviti se u sistem, kako bih mogao pristupiti funkcionalnostima koje su mi dostupne. |
| Poslovna vrijednost | Omogućava siguran pristup sistemu i rad sa dokumentima. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je finalno definisati pristup sesijama ili tokenima. |
| Veze sa drugim storyjima ili zavisnostima | US-8.2 |

**Acceptance Criteria**
- Kada korisnik unese validne pristupne podatke, tada sistem mora omogućiti pristup sistemu.
- Sistem mora kreirati validnu sesiju ili token nakon uspješne prijave.
- Sistem ne smije dozvoliti pristup za nevalidne pristupne podatke.
- Sistem mora ograničiti broj uzastopnih neuspješnih pokušaja prijave

### US-8.4 — Odjava korisnika iz sistema

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.4 |
| Naziv storyja | Odjava korisnika iz sistema |
| Opis | Kao korisnik firme, želim odjaviti se iz sistema, kako bih zaštitio svoj nalog. |
| Poslovna vrijednost | Smanjuje sigurnosni rizik pri radu u zajedničkim ili javnim okruženjima. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Nema dodatnih otvorenih pitanja u ovoj fazi. |
| Veze sa drugim storyjima ili zavisnostima | US-8.3 |

**Acceptance Criteria**
- Kada je korisnik prijavljen, ako odabere opciju odjave, tada sistem mora završiti aktivnu sesiju.
- Sistem mora onemogućiti pristup zaštićenim dijelovima nakon odjave.
- Korisnik treba dobiti povratnu informaciju da je uspješno odjavljen.

### US-8.5 — Ograničenje pristupa na podatke vlastite organizacije

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.5 |
| Naziv storyja | Ograničenje pristupa na podatke vlastite organizacije |
| Opis | Kao korisnik firme, želim da pristupam samo podacima svoje organizacije, kako bi podaci moje firme ostali odvojeni i sigurni. |
| Poslovna vrijednost | Osigurava osnovnu izolaciju podataka između različitih organizacija. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati pravila pristupa za administratorske i obične korisnike. |
| Veze sa drugim storyjima ili zavisnostima | US-8.1, US-8.2, US-8.3 |

**Acceptance Criteria**
- Kada je korisnik prijavljen, ako pristupa dokumentima ili korisnicima, tada sistem mora prikazati samo podatke njegove organizacije.
- Sistem ne smije dozvoliti pristup podacima druge organizacije.
- Sistem mora provoditi provjeru organizacijske pripadnosti na nivou poslovne logike i pristupa podacima.

### US-8.6 — Prikaz poruke pri neuspješnoj prijavi

| Polje | Sadržaj |
|---|---|
| ID storyja | US-8.6 |
| Naziv storyja | Prikaz poruke pri neuspješnoj prijavi |
| Opis | Kao korisnik firme, želim vidjeti jasnu poruku pri neuspješnoj prijavi, kako bih znao šta je problem. |
| Poslovna vrijednost | Pomaže korisnicima da razumiju zašto pristup nije odobren i smanjuje frustraciju pri korištenju sistema. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati nivo detalja u porukama kako se ne bi otkrivale osjetljive informacije. |
| Veze sa drugim storyjima ili zavisnostima | US-8.3 |

**Acceptance Criteria**
- Kada korisnik unese neispravne pristupne podatke, tada sistem mora prikazati poruku o neuspješnoj prijavi.
- Sistem ne smije prikazivati osjetljive sigurnosne detalje u poruci greške.
- Korisnik treba dobiti mogućnost da ponovo pokuša prijavu, ako nije prekoračio dozvoljeni broj pokušaja prijave.
- Kada korisnik prekorači dozvoljen broj pokušaja prijave, tada sistem mora privremeno blokirati pristup i prikazati odgovarajuću poruku.

---

# Sprint 9

## Sprint Goal

| Sprint broj | Sprint cilj | Ključne stavke koje tim želi završiti | Rizici i zavisnosti |
|---|---|---|---|
| 9 | Uspostaviti model upravljanja korisnicima i rolama unutar firme, te osigurati pristup funkcionalnostima prema odgovornostima korisnika. | Kreiranje korisničkih naloga, dodjela rola, pregled korisnika i ograničenje akcija prema ulozi. | Nejasna matrica rola i dozvola, preširoko postavljen administrativni scope, nedovoljno precizna pravila pristupa. |

## Sprint Backlog

| ID | Naziv zadatka ili storyja | Odgovorna osoba ili osobe | Status | Napomena |
|---|---|---|---|---|
| SB-9.1 | UI/API za kreiranje korisnika unutar firme |  | Planned | Administrativno upravljanje nalozima |
| SB-9.2 | Dodjela rola korisnicima |  | Planned | Operater, računovođa, osoba za odobravanje |
| SB-9.3 | Pregled i osnovno održavanje korisničkih naloga |  | Planned | Pregled aktivnih naloga firme |
| SB-9.4 | Zaštita akcija po ulozi |  | Planned | Role-based access control |
| SB-9.5 | Testovi za upravljanje korisnicima i role-based pristup |  | Planned | Verifikacija sigurnosti i pravilnog pristupa |

## User Stories

### US-9.1 — Kreiranje korisničkih naloga unutar firme

| Polje | Sadržaj |
|---|---|
| ID storyja | US-9.1 |
| Naziv storyja | Kreiranje korisničkih naloga unutar firme |
| Opis | Kao administrator firme, želim kreirati korisničke naloge unutar svoje firme, kako bih omogućio pristup drugim članovima organizacije. |
| Poslovna vrijednost | Omogućava uključivanje stvarnih poslovnih aktera u proces obrade dokumenata. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je definisati da li admin ručno kreira lozinke ili se koristi inicijalna aktivacija naloga. |
| Veze sa drugim storyjima ili zavisnostima | Sprint 8 |

**Acceptance Criteria**
- Kada administrator unese validne podatke za novog korisnika (ime, email), tada sistem mora omogućiti kreiranje korisničkog naloga unutar iste firme.
- Kada administrator pokuša kreirati korisnika sa već postojećim emailom unutar iste firme, tada sistem ne smije dozvoliti duplikat i mora prikazati poruku greške.
- Sistem mora automatski povezati novog korisnika sa firmom administratora.
- Sistem mora omogućiti pregled kreiranih korisnika firme.
- Sistem ne smije dozvoliti kreiranje korisnika bez obaveznih podataka.

### US-9.2 — Dodjela korisničkih rola

| Polje | Sadržaj |
|---|---|
| ID storyja | US-9.2 |
| Naziv storyja | Dodjela korisničkih rola |
| Opis | Kao administrator firme, želim dodijeliti korisniku ulogu operatera, računovođe ili osobe za odobravanje, kako bi imao odgovarajuće odgovornosti u sistemu. |
| Poslovna vrijednost | Omogućava pravilnu raspodjelu zadataka i odgovornosti unutar procesa obrade dokumenata. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je finalno potvrditi skup rola u sistemu. |
| Veze sa drugim storyjima ili zavisnostima | US-9.1 |

**Acceptance Criteria**
- Kada administrator dodijeli korisniku rolu (operater, računovođa, odobravatelj), tada sistem mora sačuvati rolu povezanu sa korisnikom.
- Kada korisnik nema dodijeljenu rolu, tada sistem ne smije dozvoliti pristup zaštićenim funkcionalnostima.
- Kada administrator promijeni rolu korisnika, tada sistem mora odmah primijeniti nova prava pristupa.

### US-9.3 — Ograničenje akcija prema ulozi

| Polje | Sadržaj |
|---|---|
| ID storyja | US-9.3 |
| Naziv storyja | Ograničenje akcija prema ulozi |
| Opis | Kao administrator firme, želim da korisnici imaju pristup samo akcijama koje odgovaraju njihovoj ulozi, kako bi proces rada bio ispravan i siguran. |
| Poslovna vrijednost | Sprječava neovlaštene radnje i čuva integritet procesa. |
| Prioritet | Visok |
| Pretpostavke i otvorena pitanja | Potrebno je usaglasiti matricu rola i dozvoljenih akcija. |
| Veze sa drugim storyjima ili zavisnostima | US-9.2 |

**Acceptance Criteria**
- Kada korisnik pokuša izvršiti akciju, tada sistem mora provjeriti da li njegova rola ima dozvolu za tu akciju.
- Kada korisnik nema odgovarajuću rolu, tada sistem ne smije dozvoliti izvršenje akcije i mora prikazati grešku.
- Kada korisnik ima odgovarajuću rolu, tada sistem mora omogućiti izvršenje akcije bez dodatnih prepreka.
- Sistem mora omogućiti pristup samo onim akcijama koje su dozvoljene toj ulozi.
- Korisnik treba dobiti konzistentno ponašanje sistema u skladu sa svojom rolom.

### US-9.4 — Pregled korisnika firme

| Polje | Sadržaj |
|---|---|
| ID storyja | US-9.4 |
| Naziv storyja | Pregled korisnika firme |
| Opis | Kao administrator firme, želim vidjeti listu korisnika svoje firme, kako bih mogao upravljati postojećim nalozima. |
| Poslovna vrijednost | Omogućava administrativni pregled i lakše održavanje organizacijskog modela. |
| Prioritet | Srednji |
| Pretpostavke i otvorena pitanja | Potrebno je definisati koji podaci o korisnicima se prikazuju u listi. |
| Veze sa drugim storyjima ili zavisnostima | US-9.1, US-9.2 |

**Acceptance Criteria**
- Kada administrator otvori listu korisnika, tada sistem mora prikazati sve korisnike povezane sa njegovom firmom.
- Kada nema korisnika osim administratora, tada sistem treba prikazati odgovarajuću poruku
- Sistem mora omogućiti pregled osnovnih podataka i role korisnika.
- Sistem ne smije prikazivati korisnike drugih organizacija.

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
