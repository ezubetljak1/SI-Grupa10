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
**Razlog izbora:** Standalone komponente, ugrađen TypeScript, modularna arhitektura pogodna za timski rad, bogat ekosistem (Angular Router, HttpClient, FormsModule).  
**Posljedice odluke:** Veća početna složenost za članove tima koji nisu upoznati s Angularom. Stroga struktura projekta ubrzava onboarding.  
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
**Razlog izbora:** Zrela platforma za enterprise aplikacije, bogat ekosistem (Spring Data JPA, Spring Validation, SpringDoc), jednostavna integracija s PostgreSQL-om, dobra podrška za multipart upload.  
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
**Razlog izbora:** Pouzdana relacijska baza, odlična podrška u Spring ekosistemu putem Hibernate ORM-a, lokalno pokretanje putem Dockera.  
**Posljedice odluke:** Potreban Docker za lokalni razvoj. Sve izmjene sheme upravljaju se putem Hibernate DDL auto mehanizma.  
**Status:** Aktivna

---

### DL-004 – Pohrana fajlova – lokalni filesystem
**Datum:** 25.04.2026  
**Opis problema:** Trebalo je odlučiti gdje fizički pohranjivati uploadovane dokumente (PDF, slike).  
**Razmatrane opcije:**
1. Lokalni filesystem
2. AWS S3
3. MinIO

**Odabrana opcija:** Lokalni filesystem (`C:/docflow-uploads` lokalno, `/app/uploads` na serveru)  
**Razlog izbora:** Jednostavnost implementacije za trenutnu fazu razvoja, nema eksternih zavisnosti ni troškova.  
**Posljedice odluke:** Skaliranje je ograničeno na jedan server. Za produkcijsko okruženje preporučuje se migracija na object storage (S3/MinIO).  
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
**Razlog izbora:** Frontend validacija poboljšava korisničko iskustvo (brza povratna informacija), backend validacija osigurava integritet podataka bez obzira na izvor zahtjeva.  
**Posljedice odluke:** Dupliranje logike (dozvoljeni tipovi: PDF, JPG, PNG; max veličina: 10MB) na frontendu i backendu. Oba sloja moraju biti sinhronizovana pri promjenama pravila.  
**Status:** Aktivna

---

### DL-006 – API dokumentacija – SpringDoc/Swagger
**Datum:** 25.04.2026  
**Opis problema:** Trebalo je odlučiti kako dokumentovati REST API za lakšu komunikaciju između frontend i backend članova tima.  
**Razmatrane opcije:**
1. SpringDoc OpenAPI (Swagger UI)
2. Postman kolekcija
3. Ručna dokumentacija

**Odabrana opcija:** SpringDoc OpenAPI  
**Razlog izbora:** Automatska generacija dokumentacije iz koda, interaktivni Swagger UI dostupan na `/swagger-ui/index.html`, nema ručnog održavanja.  
**Posljedice odluke:** Swagger UI je dostupan u svim okruženjima – u produkciji ga treba onemogućiti.  
**Status:** Aktivna

---

### DL-007 – Brisanje fizičkog fajla ako upload transakcija ne uspije
**Datum:** 26.04.2026  
**Opis problema:** Kod upload procesa može se desiti da se fajl uspješno snimi na filesystem, ali da upis metapodataka u bazu ne uspije. U tom slučaju nastao bi fajl bez odgovarajućeg zapisa u bazi.  
**Razmatrane opcije:**
1. Ne raditi cleanup i ostaviti fajl na disku
2. Periodično čistiti orphan fajlove
3. Odmah obrisati fajl ako nakon storage koraka dođe do greške

**Odabrana opcija:** Ako se fajl snimi, ali kasniji dio upload procesa ne uspije, backend briše već sačuvani fajl.  
**Razlog izbora:** Ovo čuva konzistentnost između baze i filesystem storage-a i sprječava nakupljanje nepotrebnih fajlova.  
**Posljedice odluke:**
- Upload servis mora voditi računa o redoslijedu operacija.
- Greške u upload procesu ne ostavljaju nepotrebne fajlove.
- Potrebno je testirati i uspješan upload i negativne validacijske scenarije.

**Status:** Aktivna

---

### DL-008 – Brisanje dokumenta uklanja i bazni zapis i fizički fajl
**Datum:** 26.04.2026  
**Opis problema:** Dokument se sastoji od metapodataka u bazi i originalnog fajla na filesystemu. Potrebno je odlučiti šta znači brisanje dokumenta.  
**Razmatrane opcije:**
1. Brisati samo zapis iz baze
2. Brisati samo fizički fajl
3. Brisati i zapis iz baze i fizički fajl

**Odabrana opcija:** Delete operacija uklanja i metadata zapis iz baze i fizički fajl iz storage foldera.  
**Razlog izbora:** Za trenutni MVP, korisničko brisanje dokumenta treba značiti potpuno uklanjanje dokumenta iz sistema.  
**Posljedice odluke:**
- Delete endpoint mora imati pristup i bazi i storage servisu.
- Potrebno je čuvati storage path u bazi.
- Testovi trebaju provjeriti da nakon brisanja ne postoje ni DB zapis ni fizički fajl.

**Status:** Aktivna

---

### DL-009 – API/integration testovi za document module
**Datum:** 27.04.2026  
**Opis problema:** Document module uključuje više slojeva: REST controller, validaciju, bazu, storage na filesystemu i response format. Samo unit testovi ne bi dovoljno potvrdili da cijeli tok radi ispravno.  
**Razmatrane opcije:**
1. Pisati samo unit testove za validaciju
2. Testirati ručno kroz Swagger/Postman
3. Pisati integration/API testove koristeći MockMvc

**Odabrana opcija:** Za document module koriste se integration/API testovi koji prolaze kroz stvarne endpointe.  
**Razlog izbora:** MockMvc testovi bolje provjeravaju stvarno ponašanje API-ja, uključujući status kodove, response body, validacijske greške, upis u bazu i rad sa fajlovima.  
**Posljedice odluke:**
- Testovi su nešto sporiji od čistih unit testova.
- Dobija se veće povjerenje da kompletan korisnički tok radi.
- Testovi služe kao dokumentacija očekivanog ponašanja API-ja.

**Status:** Aktivna
