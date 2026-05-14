# AI Usage Log

Napomena: Ovaj AI Usage Log je živi dokument i ažurira se kroz sprintove.

---

## Unos #1

| Polje                                      | Detalji                                                                                                                                                                                                                   |
|--------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 25.04.2026                                                                                                                                                                                                                |
| Sprint broj                                | Sprint 5                                                                                                                                                                                                                  |
| Alat koji je korišten                      | Codex 5.4                                                                                                                                                                                                                 |
| Svrha korištenja                           | Pomoć pri implementaciji API podrške za upload i preuzimanje dokumenata.                                                                                                                                                  |
| Kratak opis zadatka ili upita              | AI alat je korišten za proširenje postojećeg Document modula. Na osnovu već definisanog Document entiteta i postojećih CRUD operacija, cilj je bio dodati endpoint-e za upload dokumenta i preuzimanje originalnog fajla. |
| Šta je AI predložio ili generisao          | AI je predložio početnu implementaciju endpoint-a za upload dokumenta, download fajla i osnovnu obradu uploadovanog sadržaja.                                                                                             |
| Šta je tim prihvatio                       | Tim je prihvatio većinu osnovne strukture rješenja, uključujući ideju da se upload i download implementiraju kroz posebne API endpoint-e i povežu sa postojećim Document modulom.                                         |
| Šta je tim izmijenio                       | Tim je doradio validaciju prema prethodno dogovorenim poslovnim pravilima, uskladio greške sa postojećim formatom API odgovora i prilagodio implementaciju postojećoj arhitekturi backend slojeva.                        |
| Šta je tim odbacio                         | Odbačeni su dijelovi validacije i error handlinga koji nisu bili usklađeni sa postojećim standardima projekta, posebno rješenja koja su uvodila paralelnu ili nekonzistentnu validacijsku logiku.                         |
| Rizici, problemi ili greške koje su uočene | AI nije u potpunosti koristio već postojeće pomoćne metode, validacijski sloj i standardizovane error poruke. Zbog toga je bilo potrebno ručno provjeriti predloženi kod i uskladiti ga sa postojećim dizajnom sistema.   |
| Ko je koristio alat                        | Emina Zubetljak                                                                                                                                                                                                           |

---

## Unos #2

| Polje                                      | Detalji                                                                                                                                                                                        |
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 27.04.2026                                                                                                                                                                                     |
| Sprint broj                                | Sprint 5                                                                                                                                                                                       |
| Alat koji je korišten                      | Codex 5.4                                                                                                                                                                                      |
| Svrha korištenja                           | Implementacija stranice za upload dokumenata.                                                                                                                                                  |
| Kratak opis zadatka ili upita              | Korišten AI za razdvajanje upload procesa na posebnu stranicu na kojoj se također vrši validacija te prikaz rezultata o uspješnosti upload-a.                                                  |
| Sta je AI predložio ili generisao          | Kod za stranicu uključujući HTML, CSS i TS file-ove (document-upload-page.html, document-upload-page.scss, document-upload-page.ts). Osim toga modifikovao je rute aplikacije (app.routes.ts). |
| Sta je tim prihvatio                       | Većinu koda, uz izmjene frontend dijela (CSS-a).                                                                                                                                               |
| Sta je tim izmijenio                       | Izmijenjen je dizajn stranice, jer ga AI nije generisao u skladu sa globalnim dizajnom koji je definisan.                                                                                      |
| Sta je tim odbacio                         | Dvostruki prikaz poruke o statusu upload-a.                                                                                                                                                    |
| Rizici, problemi ili greške koje su uočene | AI nije prilagodio dizajn stranice u skladu sa predefinisanim globalnim fajlovima. Osim toga, na dva mjesta je ispisivao poruke o statusu uspješnosti upload-a što je bilo nepotrebno.         |
| Ko je koristio alat                        | Mirza M. Halilović                                                                                                                                                                             |

---

## Unos #3

| Polje                                      | Detalji                                                                                                                                                                                                                      |
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 28.04.2026                                                                                                                                                                                                                   |
| Sprint broj                                | Sprint 5                                                                                                                                                                                                                     |
| Alat koji je korišten                      | Claude Sonnet 4.6                                                                                                                                                                                                            |
| Svrha korištenja                           | Pomoć pri implementaciji stranice za prikaz detalja dokumenta.                                                                                                                                                               |
| Kratak opis zadatka ili upita              | Korišten AI za kreiranje nove Angular komponente `document-detail-page` koja prikazuje sve metapodatke pojedinog dokumenta, sa mogućnošću preuzimanja fajla i navigacije nazad na listu dokumenata.                          |
| Šta je AI predložio ili generisao          | AI je predložio početnu strukturu rješenja i smjer implementacije, te davao prijedloge tokom razvoja.                                                                                                                        |
| Šta je tim prihvatio                       | Prihvaćen je opći koncept i smjer rješenja koji je AI predložio.                                                                                                                                                             |
| Šta je tim izmijenio                       | AI prijedlozi su služili kao polazna osnova koja je u određenoj mjeri prilagođena, dorađena i ispravljena prema stvarnim potrebama projekta i ponašanju istog.                                                               |
| Šta je tim odbacio                         | Odbačeni su neki od inicijalnih prijedloga koji nisu davali željene rezultate, a tim je samostalno pronalazio alternativna rješenja za konzistentan prikaz podataka.                                                         |
| Rizici, problemi ili greške koje su uočene | AI je u pojedinim dijelovima predložio kod koji nije bio usklađen s postojećim shared komponentama i globalnim stilovima, što je zahtijevalo ručne izmjene. Dodatno su se pojavili problemi s lokalnim razvojnim okruženjem. |
| Ko je koristio alat                        | Emina Mušinović                                                                                                                                                                                                              |

---

## Unos #4

| Polje                                      | Detalji                                                                                                                                                                                                   |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 06.05.2026                                                                                                                                                                                                |
| Sprint broj                                | Sprint 6                                                                                                                                                                                                  |
| Alat koji je korišten                      | Codex 5.4                                                                                                                                                                                                 |
| Svrha korištenja                           | Pomoć pri implementaciji backend integracije sa Google Document AI OCR/AI servisom i čuvanju rezultata ekstrakcije prema planiranom ERD modelu.                                                           |
| Kratak opis zadatka ili upita              | Korišten AI za pomoć pri povezivanju Spring Boot backend aplikacije sa Google Document AI servisom, definisanju konfiguracije preko environment varijabli i implementaciji endpointa za ekstrakciju.      |
| Šta je AI predložio ili generisao          | AI je predložio početnu strukturu rješenja: konfiguraciju za Google Document AI, OCR provider sloj, modele za OCR rezultat, entitete za ekstrakciju, DAO/service sloj i REST endpoint-e za ekstrakciju.   |
| Šta je tim prihvatio                       | Prihvaćen je opći koncept izdvajanja OCR logike u poseban provider sloj, korištenje Google Document AI client biblioteke i čuvanje rezultata u tabelama extraction i extraction_field.                    |
| Šta je tim izmijenio                       | Rješenje je prilagođeno postojećem ERD-u, imenovanju i arhitekturi projekta. Dodatno su prilagođeni error handling, status dokumenta PROCESSING_FAILED, transakcijsko ponašanje i Docker/.env setup.      |
| Šta je tim odbacio                         | Odbačeni su prijedlozi koji nisu bili u trenutnom Sprint 6 scope-u, kao i prijedlozi koji bi podrazumijevali čuvanje osjetljivih Google vrijednosti u repozitoriju.                                       |
| Rizici, problemi ili greške koje su uočene | Uočeni su problemi sa transakcijskim ponašanjem kod grešaka, kao i potreba da se Google credentials i service account JSON fajl čuvaju isključivo lokalno ili na deployment serveru, a ne u repozitoriju. |
| Ko je koristio alat                        | Emina Zubetljak                                                                                                                                                                                           |

---

## Unos #5

| Polje                                      | Detalji                                                                                                                                                                                                                                                                                               |
|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 06.05.2026                                                                                                                                                                                                                                                                                            |
| Sprint broj                                | Sprint 6                                                                                                                                                                                                                                                                                              |
| Alat koji je korišten                      | Codex 5.4                                                                                                                                                                                                                                                                                             |
| Svrha korištenja                           | Pomoć pri implementaciji frontend (Angular) UI prikaza rezultata ekstrakcije i integraciji sa backend endpointima za ekstrakciju.                                                                                                                                                                     |
| Kratak opis zadatka ili upita              | Korišten AI za analizu postojećeg koda (`Document details page` + `DocumentApiService`), mapiranje backend response formata (`fields` niz) i dodavanje UI sekcije “Extracted fields” sa akcijama Run/Retry/Refresh, te loading/empty/error stanjima.                                                  |
| Šta je AI predložio ili generisao          | Predloženi su Angular modeli (`Extraction`, `ExtractionField`), nove metode u `DocumentApiService` za pozive `/extraction`, `/extraction/retry`, `/extraction/fields`, te izmjene `document-detail-page` (TS/HTML/SCSS) za prikaz tabele izdvojenih polja (`field`/`value`/`confidence`/`corrected`). |
| Šta je tim prihvatio                       | Prihvaćen je pristup da se u UI koristi `fields` niz (bez parsiranja `rawJson`) i da se prikaz i akcije dodaju na stranicu detalja dokumenta radi jednostavnijeg toka testiranja i korištenja.                                                                                                        |
| Šta je tim izmijenio                       | Tok testiranja je prilagođen lokalnom okruženju bez Google credentials (ručni unos testnih `extraction_field` zapisa u bazu radi provjere UI prikaza). Također je napravljen PR prema `develop` grani (ne `main`).                                                                                    |
| Šta je tim odbacio                         | Odbačena je potreba da se u UI parsira ili prikazuje `rawJson` osim eventualno za debug; nije uveden “fake OCR provider” u backend jer nije bio dio FE scope-a.                                                                                                                                       |
| Rizici, problemi ili greške koje su uočene | Lokalna ekstrakcija može vraćati `PROCESSING_FAILED`/`EXTRACTION_FAILED` bez Google konfiguracije i credentials; potrebno je osigurati da UI korektno prikaže grešku i da se osjetljive konfiguracije ne nalaze u repozitoriju.                                                                       |
| Ko je koristio alat                        | Amar Breščić                                                                                                                                                                                                                                                                                          |


---

## Unos #6

| Polje | Detalji |
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum | 08.05.2026 |
| Sprint broj | Sprint 7 |
| Alat koji je korišten | ChatGPT GPT-5.5 Thinking |
| Svrha korištenja | Analiza postojećeg backend koda, planiranje implementacije, te review pristupa. |
| Kratak opis zadatka ili upita | Alat je korišten za analizu postojećeg extraction backend modula i Sprint 7 zahtjeva vezanih za ručno editovanje izdvojenih polja i potvrdu ekstrakcije. Korišten je za razmatranje koji endpointi i service/DAO metode su potrebni za US 7.1 i US 7.5. |
| Šta je AI predložio ili generisao | Predložena je struktura rješenja: endpoint za izmjenu jednog extraction field-a, endpoint za potvrdu ekstrakcije, DAO provjera kombinacije `fieldId` i `extractionId`, te placeholder metode za backend validacije koje su planirane kroz druge user stories. |
| Šta je tim prihvatio | Prihvaćen je opšti pristup sa posebnim PATCH endpointom za edit jednog polja i posebnim confirm endpointom za potvrdu ekstrakcije. Prihvaćeno je i da se nakon uspješnog edita polje označava sa `corrected = true`, kao i da confirm endpoint mijenja status dokumenta u `READY_FOR_APPROVAL`. |
| Šta je tim izmijenio | Implementacija je ručno prilagođena postojećoj arhitekturi projekta, postojećem `ApiResponse` formatu i DAO/service/controller slojevima. |
| Šta je tim odbacio | Nije implementirana stvarna validacija vrijednosti i formata extraction field-a, jer je taj dio izdvojen za drugi backend validacijski task. Nije mijenjana struktura baze dodavanjem nove kolone za tip polja, jer se trenutna validacija može osloniti na postojeći `fieldName`. |
| Rizici, problemi ili greške koje su uočene | Uočeno je da confirm endpoint ne smije koristiti retry/process logiku, jer retry može zamijeniti postojeća extraction fields i time obrisati ručne korekcije. Također je uočeno da validacije vrijednosti i obaveznih polja trebaju ostati odvojene kroz placeholder metode kako bi ih kolege kasnije mogle implementirati u okviru svog taska. |
| Ko je koristio alat | Emina Zubetljak |

---

## Unos #7

| Polje | Detalji                                                                                                                                                                                                                                                                                                                                            |
|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum | 13.05.2026                                                                                                                                                                                                                                                                                                                                         |
| Sprint broj | Sprint 7                                                                                                                                                                                                                                                                                                                                           |
| Alat koji je korišten | ChatGPT GPT-5.5                                                                                                                                                                                                                                                                                                                                    |
| Svrha korištenja | Pomoć pri implementaciji validacije ekstraktovanih OCR polja prije potvrde ekstrakcije dokumenta.                                                                                                                                                                                                                                                  |
| Kratak opis zadatka ili upita | Korišten AI za analizu postojećeg extraction validation flow-a (ExtractionValidation, ExtractionFieldEntity, DocumentType, frontend prikaz extraction fields) i definisanje pravila validacije za Sprint 7 User Story 7.2. Cilj je bio implementirati validaciju obaveznih polja, praznih vrijednosti i low-confidence polja prije confirm akcije. |
| Šta je AI predložio ili generisao | AI je predložio backend validacijski pristup kroz proširenje postojeće ExtractionValidation klase, uključujući provjeru obaveznih invoice polja (invoice_id, invoice_date, supplier_name, total_amount, currency), validaciju da nijedno ekstraktovano polje ne ostane prazno, te provjeru low-confidence polja ispod 70% confidence threshold-a.  |
| Šta je tim prihvatio | Prihvaćen je pristup da se sva validacija izvršava centralizovano kroz postojeći validation layer prije confirm extraction koraka, bez uvođenja dodatnih schema/model slojeva ili izmjena OCR extraction pipeline-a.                                                                                                                               |
| Šta je tim izmijenio | Validacijska pravila su prilagođena stvarnoj arhitekturi projekta i postojećem extraction flow-u. Dodatno su izmijenjeni AI prijedlozi za frontend kako bi se izbjeglo uvođenje novih UI obrazaca i zadržao postojeći dizajn aplikacije.                                                                                                           |
| Šta je tim odbacio | Odbačeni su prijedlozi koji su uvodili warning sisteme za prazna polja, dodatne DTO modele i kompleksnije schema-based validacije koje nisu bile usklađene sa trenutnim scope-om i arhitekturom projekta.                                                                                                                                          |
| Rizici, problemi ili greške koje su uočene | AI je u pojedinim iteracijama predlagao previše generičku semantičku validaciju (amount, date) koja nije bila dovoljno precizna za finalna poslovna pravila projekta. Također je bilo potrebno ručno uskladiti validaciju sa postojećim confidence review mehanizmom i status flow-om ekstrakcije.                                                 |
| Ko je koristio alat | Irhad Žiga  |

---

## Unos #8

| Polje | Detalji |
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum | 14.05.2026 |
| Sprint broj | Sprint 7 |
| Alat koji je korišten | ChatGPT GPT-5.5 Thinking |
| Svrha korištenja | Pomoć pri proširenju backend i frontend extraction flow-a vezanog za potvrdu ekstrakcije, required polja, placeholder vrijednosti i prikaz validacijskih grešaka korisniku. |
| Kratak opis zadatka ili upita | Alat je korišten za analizu postojećeg extraction flow-a i planiranje rješenja za slučaj kada OCR/AI servis ne vrati sva required polja. Dodatno je korišten za razradu FE prikaza placeholder polja, confirm dugmeta, validacijskih poruka i statusa dokumenta nakon potvrde ekstrakcije. |
| Šta je AI predložio ili generisao | AI je predložio da backend nakon ekstrakcije automatski doda missing required polja kao placeholder redove u `extraction_field` tabeli, uz novu kolonu `is_placeholder`. Na FE strani predloženi su prikaz placeholder polja kroz posebne oznake u tabeli. |
| Šta je tim prihvatio | Prihvaćen je pristup sa placeholder poljima za missing required vrijednosti, dodatna kolona `is_placeholder`, validacija prije confirma, kao i FE prikaz koji korisniku jasno označava polja koja zahtijevaju ručni review. |
| Šta je tim izmijenio | Implementacija je ručno prilagođena postojećoj arhitekturi projekta, postojećem `ApiResponse`/validation error formatu, DAO/service/controller slojevima i postojećem frontend dizajnu. FE stilizacija je također prilagođena postojećem izgledu sistema. |
| Šta je tim odbacio | Odbačena je ideja da se uvodi poseban “Add field” endpoint za korisničko dodavanje missing polja, jer je jednostavnije i konzistentnije da backend automatski kreira placeholder redove koje korisnik zatim edituje kroz postojeći edit flow. Odbačen je i prikaz predugih ili višestrukih raw validation error poruka direktno u UI tabeli. |
| Rizici, problemi ili greške koje su uočene | Uočeno je da postojeća baza mora dobiti novu kolonu `is_placeholder` sa default vrijednošću `false`, posebno zbog već postojećih redova u tabeli. Također je uočeno da FE i BE moraju ostati usklađeni oko `placeholder` polja, statusa `READY_FOR_APPROVAL` i validacijskih kodova koje backend vraća. Posebna pažnja je potrebna da confirm ne obriše ručne korekcije i da retry flow ispravno zamijeni prethodna extraction polja novim OCR rezultatom i placeholder redovima. |
| Ko je koristio alat | Emina Zubetljak |

---

## Unos #9 

| Polje                                      | Detalji                                                                                                                                                                                                                   |
|--------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Datum                                      | 14.05.2026                                                                                                                                                                                                                |
| Sprint broj                                | Sprint 7                                                                                                                                                                                                                  |
| Alat koji je korišten                      | Cursor AI asistent u IDE-u (analitički režim)                                                                                                                                                                             |
| Svrha korištenja                           | Podrška pri implementaciji validacije numeričkih polja tijekom ručnog pregleda ekstraktovanih vrijednosti (US 7.4 – matematika) i proširenju integracijskih testova za negativne scenarije.                               |
| Kratak opis zadatka ili upita              | Dogovor oko polja `net_amount`, `vat_amount` i `total_amount`; implementacija metode `validateUpdatedFieldValue` u `ExtractionServiceImpl`; setovi DECIMAL_FIELDS, prazni DATE_FIELDS / INVOICE_NO_FIELDS koji su eventualno dopunjeni; greške se prijavljuju preko `ApiValidationException`; kreirani su negativni i pozitivni integracijski testovi; izvršena provjera `mvn test`; git workflow i PR prema develop branchu. |
| Šta je AI predložio ili generisao          | Predložena je struktura setova polja i pravila za numeričke vrijednosti (provjera praznih polja, separator, dozvoljena negativnost, maksimalno 2 decimale, opciona provjera `total = net + vat`), korištenje `ApiValidationException` za HTTP 400, primjer integracijskih testova, sugestije za tim i git korake. |
| Šta je tim prihvatio                       | Prihvaćen koncept validacije samo za dogovorena numerička polja, odvajanje od datuma i broja fakture te obaveznih polja na confirm, standardizirani `ApiValidationException`, dodani integracijski testovi za PATCH endpoint. |
| Šta je tim izmijenio                       | Usklađene su poruke grešaka s ostalim validacijama (kraći tekst na engleskom). U testu za neusklađen `total` dodan je **assert** na `message` u tijelu odgovora. U `validateUpdatedFieldValue` je mala izmjena redoslijeda provjera radi čitljivosti.                                                                                          |
| Šta je tim odbacio                         | Prijedlog da se tolerancija za `total ≈ net + vat` podigne na 0.02 — odbijeno jer 0.01 odgovara zaokruživanju na dvije decimale. Prijedlog da se dozvole razmaci u broju (npr. `1 234,56`) — ostavljeno za kasniji sprint zbog složenosti parsiranja.                                                                                                                                                            |
| Rizici, problemi ili greške koje su uočene | Potrebno je ručno uskladiti imena `field_name` s OCR/Document AI rezultatima i listom obaveznih polja. AI prijedlozi ne zamjenjuju provjeru da li PR ide na develop branch i da li commit autor slijedi pravila tima. |
| Ko je koristio alat                        | Amar Breščić |                                                                                                                                                                     |
