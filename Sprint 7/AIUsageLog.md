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
