# Decision Log – DocFlow

---

### DL-001 – Odabir frontend frameworka

**Datum:** 21.04.2026  
**Opis problema:** Trebalo je odabrati frontend framework za razvoj web aplikacije DocFlow.  
**Razmatrane opcije:**

1. Angular
2. React
3. Vue.js

**Odabrana opcija:** Angular  
**Razlog izbora:** Standalone komponente, ugrađen TypeScript, modularna arhitektura pogodna za timski rad, bogat
ekosistem (Angular Router, HttpClient, FormsModule).  
**Posljedice odluke:** Veća početna složenost za članove tima koji nisu upoznati s Angularom. Stroga struktura projekta
ubrzava onboarding.  
**Status:** Aktivna

---

### DL-002 – Odabir backend frameworka

**Datum:** 21.04.2026  
**Opis problema:** Trebalo je odabrati backend tehnologiju za REST API i upravljanje dokumentima.  
**Razmatrane opcije:**

1. Spring Boot (Java)
2. Node.js + Express
3. Django (Python)

**Odabrana opcija:** Spring Boot (Java 17+)  
**Razlog izbora:** Zrela platforma za enterprise aplikacije, bogat ekosistem (Spring Data JPA, Spring Validation,
SpringDoc), jednostavna integracija s PostgreSQL-om, dobra podrška za multipart upload.  
**Posljedice odluke:** Zahtijeva Java znanje od članova tima. Maven build može biti sporiji od alternativa.  
**Status:** Aktivna

---

### DL-003 – Odabir baze podataka

**Datum:** 21.04.2026  
**Opis problema:** Trebalo je odabrati sistem za upravljanje bazom podataka za pohranu metapodataka dokumenata.  
**Razmatrane opcije:**

1. PostgreSQL
2. MySQL
3. MongoDB

**Odabrana opcija:** PostgreSQL  
**Razlog izbora:** Pouzdana relacijska baza, odlična podrška u Spring ekosistemu putem Hibernate ORM-a, lokalno
pokretanje putem Dockera.  
**Posljedice odluke:** Potreban Docker za lokalni razvoj. Sve izmjene sheme upravljaju se putem Hibernate DDL auto
mehanizma.  
**Status:** Aktivna

---

### DL-004 – Pohrana fajlova – lokalni filesystem

**Datum:** 25.04.2026  
**Opis problema:** Trebalo je odlučiti gdje fizički pohranjivati uploadovane dokumente (PDF, slike).  
**Razmatrane opcije:**

1. Lokalni filesystem
2. AWS S3
3. MinIO

**Odabrana opcija:** Lokalni filesystem (C:/docflow-uploads lokalno, /app/uploads na serveru)  
**Razlog izbora:** Jednostavnost implementacije za trenutnu fazu razvoja, nema eksternih zavisnosti ni troškova.  
**Posljedice odluke:** Skaliranje je ograničeno na jedan server. Za produkcijsko okruženje preporučuje se migracija na
object storage (S3/MinIO).  
**Status:** Aktivna

---

### DL-005 – Strategija validacije dokumenata

**Datum:** 25.04.2026  
**Opis problema:** Trebalo je odlučiti gdje i kako validirati uploadovane fajlove (tip, veličina, ekstenzija).  
**Razmatrane opcije:**

1. Validacija samo na frontendu
2. Validacija samo na backendu
3. Validacija na oba sloja

**Odabrana opcija:** Validacija na oba sloja  
**Razlog izbora:** Frontend validacija poboljšava korisničko iskustvo (brza povratna informacija), backend validacija
osigurava integritet podataka bez obzira na izvor zahtjeva.  
**Posljedice odluke:** Dupliranje logike (dozvoljeni tipovi: PDF, JPG, PNG; max veličina: 10MB) na frontendu i backendu.
Oba sloja moraju biti sinhronizovana pri promjenama pravila.  
**Status:** Aktivna

---

### DL-006 – API dokumentacija – SpringDoc/Swagger

**Datum:** 25.04.2026  
**Opis problema:** Trebalo je odlučiti kako dokumentovati REST API za lakšu komunikaciju između frontend i backend
članova tima.  
**Razmatrane opcije:**

1. SpringDoc OpenAPI (Swagger UI)
2. Postman kolekcija
3. Ručna dokumentacija

**Odabrana opcija:** SpringDoc OpenAPI  
**Razlog izbora:** Automatska generacija dokumentacije iz koda, interaktivni Swagger UI dostupan na
/swagger-ui/index.html, nema ručnog održavanja.  
**Posljedice odluke:** Swagger UI je dostupan u svim okruženjima – u produkciji ga treba onemogućiti.  
**Status:** Aktivna

---

### DL-007 – Brisanje fizičkog fajla ako upload transakcija ne uspije

**Datum:** 26.04.2026  
**Opis problema:** Kod upload procesa može se desiti da se fajl uspješno snimi na filesystem, ali da upis metapodataka u
bazu ne uspije. U tom slučaju nastao bi fajl bez odgovarajućeg zapisa u bazi.  
**Razmatrane opcije:**

1. Ne raditi cleanup i ostaviti fajl na disku
2. Periodično čistiti orphan fajlove
3. Odmah obrisati fajl ako nakon storage koraka dođe do greške

**Odabrana opcija:** Ako se fajl snimi, ali kasniji dio upload procesa ne uspije, backend briše već sačuvani fajl.  
**Razlog izbora:** Ovo čuva konzistentnost između baze i filesystem storage-a i sprječava nakupljanje nepotrebnih
fajlova.  
**Posljedice odluke:**

- Upload servis mora voditi računa o redoslijedu operacija.
- Greške u upload procesu ne ostavljaju nepotrebne fajlove.
- Potrebno je testirati i uspješan upload i negativne validacijske scenarije.

**Status:** Aktivna

---

### DL-008 – Brisanje dokumenta uklanja i bazni zapis i fizički fajl

**Datum:** 26.04.2026  
**Opis problema:** Dokument se sastoji od metapodataka u bazi i originalnog fajla na filesystemu. Potrebno je odlučiti
šta znači brisanje dokumenta.  
**Razmatrane opcije:**

1. Brisati samo zapis iz baze
2. Brisati samo fizički fajl
3. Brisati i zapis iz baze i fizički fajl

**Odabrana opcija:** Delete operacija uklanja i metadata zapis iz baze i fizički fajl iz storage foldera.  
**Razlog izbora:** Za trenutni MVP, korisničko brisanje dokumenta treba značiti potpuno uklanjanje dokumenta iz
sistema.  
**Posljedice odluke:**

- Delete endpoint mora imati pristup i bazi i storage servisu.
- Potrebno je čuvati storage path u bazi.
- Testovi trebaju provjeriti da nakon brisanja ne postoje ni DB zapis ni fizički fajl.

**Status:** Aktivna

---

### DL-009 – API/integration testovi za document module

**Datum:** 27.04.2026  
**Opis problema:** Document module uključuje više slojeva: REST controller, validaciju, bazu, storage na filesystemu i
response format. Samo unit testovi ne bi dovoljno potvrdili da cijeli tok radi ispravno.  
**Razmatrane opcije:**

1. Pisati samo unit testove za validaciju
2. Testirati ručno kroz Swagger/Postman
3. Pisati integration/API testove koristeći MockMvc

**Odabrana opcija:** Za document module koriste se integration/API testovi koji prolaze kroz stvarne endpointe.  
**Razlog izbora:** MockMvc testovi bolje provjeravaju stvarno ponašanje API-ja, uključujući status kodove, response
body, validacijske greške, upis u bazu i rad sa fajlovima.  
**Posljedice odluke:**

- Testovi su nešto sporiji od čistih unit testova.
- Dobija se veće povjerenje da kompletan korisnički tok radi.
- Testovi služe kao dokumentacija očekivanog ponašanja API-ja.

**Status:** Aktivna

--- 

### DL-010 – Odabir Google Document AI servisa za OCR/AI ekstrakciju

**Datum:** 06.05.2026

**Opis problema:**  
U Sprintu 6 bilo je potrebno omogućiti automatsku ekstrakciju teksta i ključnih podataka iz uploadovanih dokumenata.

**Razmatrane opcije:**  
1. Implementirati vlastitu OCR logiku  
2. Koristiti klasični OCR servis samo za tekst  
3. Koristiti Google Document AI za OCR i strukturiranu ekstrakciju podataka

**Odabrana opcija:**  
Google Document AI

**Razlog izbora:**  
Google Document AI omogućava obradu dokumenta i vraćanje strukturiranih polja, što odgovara cilju Sprinta 6: izdvajanje podataka kao što su dobavljač, datum i iznos. Ovo je bolje od običnog OCR pristupa jer sistem ne dobija samo sirovi tekst, nego i prepoznata polja.

**Posljedice odluke:**  
- Backend zavisi od eksternog Google servisa.
- Potrebna je konfiguracija preko environment varijabli.
- Potrebno je sigurno čuvati service account JSON fajl.
- U slučaju greške eksternog servisa dokument prelazi u status PROCESSING_FAILED.

**Status:** Aktivna

---

### DL-011 – Izdvajanje OCR logike u poseban provider sloj

**Datum:** 06.05.2026

**Opis problema:**  
Bilo je potrebno integrisati eksterni OCR/AI servis, ali bez vezivanja kompletne extraction logike direktno za konkretnu Google implementaciju.

**Razmatrane opcije:**  
1. Pozivati Google Document AI direktno iz extraction servisa  
2. Implementirati poseban OCR provider sloj  
3. Smjestiti OCR poziv direktno u controller

**Odabrana opcija:**  
Poseban OCR provider sloj

**Razlog izbora:**  
OCR logika je izdvojena kroz OcrProvider, dok konkretna implementacija koristi GoogleDocumentAiProvider. Time extraction servis ostaje fokusiran na poslovni tok: učitavanje dokumenta, pozivanje OCR providera, mapiranje rezultata i spremanje u bazu. Ovakav pristup olakšava i testiranje, jer se OCR provider može mockovati bez pozivanja stvarnog Google servisa. 

**Posljedice odluke:**  
- Kod je modularniji i lakši za testiranje.
- U budućnosti je moguće zamijeniti OCR servis bez velikih promjena u ostatku aplikacije.
- Potrebno je održavati interni model rezultata OCR obrade.

**Status:** Aktivna

---

### DL-012 – Modeliranje ekstrakcije kroz extraction i extraction_field tabele

**Datum:** 06.05.2026

**Opis problema:**  
Rezultat ekstrakcije može sadržavati različita polja, zavisno od tipa dokumenta i kvaliteta OCR/AI prepoznavanja. Bilo je potrebno odlučiti da li čuvati konkretne kolone za invoice podatke ili fleksibilniji model.

**Razmatrane opcije:**  
1. Dodati konkretne kolone u tabelu dokumenta, npr. supplier, amount, invoice date  
2. Napraviti posebnu tabelu za extraction i posebnu tabelu za extracted fields  
3. Čuvati samo raw JSON bez pojedinačnih polja

**Odabrana opcija:**  
Posebne tabele extraction i extraction_field

**Razlog izbora:**  
Model sa tabelama extraction i extraction_field prati planirani ERD i omogućava fleksibilno čuvanje različitih polja. extraction čuva vezu na dokument, raw JSON i vrijeme ekstrakcije, dok extraction_field čuva naziv polja, vrijednost, confidence i informaciju da li je polje korigovano. 

**Posljedice odluke:**  
- Model nije ograničen samo na invoice dokumente.
- UI može prikazati listu izdvojenih polja bez promjene šeme baze za svako novo polje.
- Za ručno ispravljanje polja u narednim sprintovima već postoji atribut is_corrected.

**Status:** Aktivna

---

### DL-013 – Ručno pokretanje ekstrakcije umjesto automatske obrade nakon uploada

**Datum:** 06.05.2026

**Opis problema:**  
User story US-6.1 je prvobitno predviđao da se OCR/AI obrada automatski pokrene odmah nakon uploadanja dokumenta. Tokom implementacije je uočeno da takav pristup može dovesti do nepotrebnog korištenja eksternog Google Document AI servisa, jer bi se obrada pokretala za svaki uploadovani dokument, uključujući i dokumente koji su uploadovani greškom ili za koje korisnik ne želi odmah pokrenuti ekstrakciju.

**Razmatrane opcije:**  
1. Zadržati automatsko pokretanje OCR/AI obrade odmah nakon uploada dokumenta.  
2. Omogućiti korisniku da nakon uploada ručno pokrene OCR/AI obradu sa stranice detalja dokumenta.

**Odabrana opcija:**  
Korisnik ručno pokreće obradu dokumenta nakon uploada, putem akcije na stranici detalja dokumenta.

**Razlog izbora:**  
Ručno pokretanje ekstrakcije smanjuje nepotrebne pozive prema eksternom Google Document AI servisu i daje korisniku veću kontrolu nad time koji dokumenti će se obrađivati. Korisnik prije pokretanja obrade može otvoriti detalje dokumenta, provjeriti metapodatke i preview fajla, te tek zatim odlučiti da li želi pokrenuti ekstrakciju.

**Posljedice odluke:**  
- Acceptance criteria za US-6.1 je potrebno uskladiti sa novim ponašanjem sistema.
- OCR/AI obrada se ne pokreće automatski nakon svakog uploada.
- Korisnik mora ručno pokrenuti obradu dokumenta.
- Smanjuje se nepotrebno korištenje eksternog API-ja i povezanih resursa.
- U budućnosti se funkcionalnost može proširiti batch-obradom više dokumenata odjednom.

**Status:** Aktivna

---

### DL-014 – Strategija retry ekstrakcije

**Datum:** 06.05.2026

**Opis problema:**  
Bilo je potrebno odlučiti šta se dešava kada se ekstrakcija ponovo pokrene nad dokumentom koji već ima rezultat ekstrakcije.

**Razmatrane opcije:**  
1. Kreirati novi extraction zapis pri svakom retry pokušaju  
2. Zadržati više historijskih extraction rezultata  
3. Ponovo koristiti postojeći extraction zapis i zamijeniti njegova polja

**Odabrana opcija:**  
Retry koristi postojeći extraction zapis i zamjenjuje povezana polja.

**Razlog izbora:**  
Za trenutni MVP nije potrebna historija svih extraction pokušaja. Jedan dokument ima jedan aktivni extraction rezultat, a retry osvježava taj rezultat i povezana polja. U entitetu je veza dokument–ekstrakcija definisana kao jedan-na-jedan, a komentari u kodu navode da retry treba updateovati/zamijeniti fields, ne kreirati drugi extraction red. 

**Posljedice odluke:**  
- Sprječava se dupliranje extraction rezultata za isti dokument.
- UI uvijek prikazuje trenutno važeći rezultat.
- Ako u budućnosti bude potrebna historija ekstrakcija, model će se morati proširiti.

**Status:** Aktivna

---

### DL-015 – Obrada greške pri OCR/AI ekstrakciji

**Datum:** 06.05.2026

**Opis problema:**  
Eksterni OCR/AI servis može biti nedostupan, pogrešno konfigurisan ili može vratiti grešku. Bilo je potrebno odlučiti kako sistem treba reagovati u tom slučaju.

**Razmatrane opcije:**  
1. Ostaviti dokument u prethodnom statusu  
2. Vratiti grešku bez promjene statusa dokumenta  
3. Postaviti dokument u status PROCESSING_FAILED i vratiti kontrolisanu API grešku

**Odabrana opcija:**  
Dokument se postavlja u status PROCESSING_FAILED, a backend vraća kontrolisanu grešku EXTRACTION_FAILED.

**Razlog izbora:**  
Korisnik i frontend moraju jasno vidjeti da obrada nije uspjela. Backend kod u slučaju greške postavlja status dokumenta na PROCESSING_FAILED, baca ExtractionException, a globalni exception handler vraća response sa kodom EXTRACTION_FAILED. Transakcijsko ponašanje je prilagođeno tako da se status ne rollbackuje zajedno sa exceptionom. 

**Posljedice odluke:**  
- UI može prikazati status greške i omogućiti ponovno pokretanje ekstrakcije.
- Greške eksternog servisa se ne prikazuju kao uspješan API rezultat.
- Potrebno je paziti na transakcije kako bi se status PROCESSING_FAILED stvarno sačuvao.

**Status:** Aktivna

---

### DL-016 – Backend endpointi za extraction flow

**Datum:** 06.05.2026

**Opis problema:**  
Frontend je trebao način da pokrene ekstrakciju, ponovi ekstrakciju i prikaže rezultat za konkretan dokument.

**Razmatrane opcije:**  
1. Dodati extraction logiku u postojeće document CRUD endpoint-e  
2. Napraviti posebne endpoint-e u okviru document resource-a  
3. Napraviti potpuno odvojen extraction API bez veze sa document rutama

**Odabrana opcija:**  
Posebni endpointi pod /api/documents/{documentId}/extraction

**Razlog izbora:**  
Ekstrakcija je vezana za konkretan dokument, pa je najjasnije da endpointi budu ugniježđeni pod document rutom. Implementirani controller podržava pokretanje ekstrakcije, dohvat extraction rezultata, retry i dohvat izdvojenih polja za dokument. 

**Posljedice odluke:**  
- API je intuitivan za frontend.
- Extraction flow je odvojen od osnovnog document CRUD-a.
- Ako se kasnije doda editovanje polja, može se proširiti postojeća extraction API struktura.

**Status:** Aktivna

---

### DL-017 – Frontend prikaz ekstrakcije na document detail stranici

**Datum:** 06.05.2026

**Opis problema:**  
Korisniku je trebalo omogućiti da iz detalja dokumenta pokrene ekstrakciju i vidi izdvojena polja.

**Razmatrane opcije:**  
1. Prikazati extraction podatke direktno u listi dokumenata  
2. Napraviti posebnu stranicu samo za ekstrakciju  
3. Proširiti document detail stranicu extraction akcijama i tabelom polja

**Odabrana opcija:**  
Proširiti document detail stranicu.

**Razlog izbora:**  
Ekstrakcija je prirodno vezana za pojedinačni dokument. Detail page već prikazuje metapodatke i preview/download opcije, pa je logično da se tu nalaze i akcije Run extraction, Retry extraction, Refresh fields i tabela izdvojenih polja. Frontend servis koristi endpoint-e za process, retry, dohvat extraction rezultata i dohvat extraction fields. 

**Posljedice odluke:**  
- Korisnik može obraditi dokument iz konteksta u kojem već vidi njegove metapodatke.
- Lista dokumenata ostaje pregledna.
- Detail page postaje centralno mjesto za rad sa jednim dokumentom.

**Status:** Aktivna

---

### DL-018 – Testiranje ekstrakcije bez stvarnog pozivanja Google servisa

**Datum:** 06.05.2026

**Opis problema:**  
Bilo je potrebno testirati extraction flow, ali bez trošenja Google Cloud kredita i bez zavisnosti od service account JSON fajla u testnom okruženju.

**Razmatrane opcije:**  
1. U testovima pozivati stvarni Google Document AI  
2. Testirati samo ručno kroz Swagger  
3. Koristiti integration testove uz mockovani OCR provider

**Odabrana opcija:**  
Integration testovi uz mockovani OcrProvider.

**Razlog izbora:**  
Testovi provjeravaju kompletan backend flow kroz MockMvc, bazu i storage, ali ne pozivaju stvarni Google servis. OcrProvider je u testu zamijenjen mockom, pa se mogu simulirati i uspješan OCR rezultat i greška providera. 

**Posljedice odluke:**  
- Testovi se mogu pokretati lokalno i u CI okruženju bez Google credentialsa.
- Ne troše se Google Cloud krediti.
- Ručno testiranje stvarne Google integracije i dalje ostaje potrebno za deployment provjeru.

**Status:** Aktivna

---

### DL-019 – Deployment konfiguracija za Google Document AI credentials

**Datum:** 07.05.2026

**Opis problema:**  
Backend mora imati pristup Google service account JSON fajlu i Document AI konfiguraciji, ali osjetljivi fajlovi i stvarne vrijednosti ne smiju biti commitani u repozitorij.

**Razmatrane opcije:**  
1. Commitati JSON key u repozitorij  
2. Unijeti credentials direktno u application.properties  
3. Koristiti .env za konfiguraciju i mountati JSON key kao deployment secret

**Odabrana opcija:**  
Korištenje .env vrijednosti i Docker mounta za service account JSON.

**Razlog izbora:**  
docker-compose.yml prosljeđuje Google Document AI environment varijable backend containeru i mounta lokalni server fajl ./secrets/google-document-ai.json u container kao /app/secrets/google-document-ai.json. .env.example sadrži samo placeholder vrijednosti i upozorenje da se realni .env i service account JSON ne commitaju.

**Posljedice odluke:**  
- Credentials nisu dio repozitorija.
- Deployment server mora ručno imati Project/secrets/google-document-ai.json.
- Dokumentacija deploymenta mora jasno navesti koje varijable se postavljaju u .env.

**Status:** Aktivna

---

### DL-020 – Brisanje dokumenta uklanja i povezane extraction podatke

**Datum:** 07.05.2026

**Opis problema:**  
Nakon dodavanja extraction modela, dokument koji ima povezanu ekstrakciju nije se mogao obrisati bez uklanjanja povezanih podataka.

**Razmatrane opcije:**  
1. Zabraniti brisanje dokumenata koji imaju extraction rezultat  
2. Dozvoliti brisanje dokumenta, ali ostaviti extraction podatke  
3. Pri brisanju dokumenta ukloniti i extraction podatke i originalni fajl

**Odabrana opcija:**  
Brisanje dokumenta uklanja povezane extraction podatke i fizički fajl.

**Razlog izbora:**  
Za MVP, brisanje dokumenta treba značiti potpuno uklanjanje dokumenta iz sistema. DocumentServiceImpl.delete prije brisanja dokumenta uklanja extraction podatke za taj dokument, zatim briše document zapis i fizički fajl. 

**Posljedice odluke:**  
- Nema FK greške pri brisanju extracted dokumenata.
- Sistem ostaje konzistentan: ne ostaju orphan extraction podaci.
- Ako se kasnije uvede audit/history, brisanje će možda trebati dodatno razmotriti.

**Status:** Aktivna


---

### DL-021 – Backend endpointi za ručno editovanje i potvrdu ekstrakcije

**Datum:** 08.05.2026

**Opis problema:**  
U Sprintu 7 bilo je potrebno omogućiti korisniku da nakon OCR/AI ekstrakcije ručno ispravi izdvojene podatke i potvrdi pregled ekstrakcije. Postojeći backend je podržavao pokretanje ekstrakcije, retry i dohvat izdvojenih polja, ali nije imao endpoint za edit pojedinačnog extraction field-a niti endpoint za potvrdu da je ekstrakcija pregledana.

**Razmatrane opcije:**  
1. Editovanje izdvojenih polja implementirati kroz update cijele ekstrakcije.  
2. Editovanje izdvojenih polja implementirati kroz poseban endpoint za pojedinačno polje.  
3. Potvrdu ekstrakcije vezati za postojeći retry/process flow.  
4. Potvrdu ekstrakcije implementirati kroz poseban confirm endpoint.  

**Odabrana opcija:**  
Implementiran je poseban PATCH endpoint za izmjenu vrijednosti jednog izdvojenog polja. Nakon uspješne izmjene polje se označava sa `corrected = true`.

Za potvrdu ekstrakcije implementiran je POST endpoint, koji provjerava da dokument i ekstrakcija postoje, poziva validaciju obaveznih polja i mijenja status dokumenta u `READY_FOR_APPROVAL`.

**Razlog izbora:**  
Poseban PATCH endpoint omogućava precizno editovanje jednog extraction field-a bez slanja cijele ekstrakcije. Provjera kombinacije `fieldId` i `extractionId` sprječava da se izmijeni polje koje ne pripada proslijeđenoj ekstrakciji.

Poseban POST confirm endpoint jasno odvaja potvrdu pregledane ekstrakcije od OCR/AI obrade i retry procesa. Ovo je važno jer retry zamjenjuje postojeća extraction fields, dok confirm mora sačuvati prethodne ručne korekcije.

**Posljedice odluke:**  
- Backend podržava ručno editovanje izdvojenih polja.
- Svako ručno izmijenjeno polje dobija `corrected = true`.
- Nije moguće editovati field koji ne pripada proslijeđenom `extractionId`.
- Potvrda ekstrakcije mijenja status dokumenta u `READY_FOR_APPROVAL`.
- Confirm endpoint ne pokreće OCR ponovo i ne briše prethodno korigovana polja.
- Validacija vrijednosti i validacija obaveznih polja ostaju odvojene kroz placeholder metode koje će biti implementirane u zasebnom backend validacijskom tasku.

**Status:** Aktivna