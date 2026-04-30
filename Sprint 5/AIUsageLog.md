# AI Usage Log – Sprint 5

Napomena: Ovaj AI Usage Log je živi dokument i ažurira se kroz sprintove.

---

## Unos #1

| Polje | Detalji |
|---|---|
| Datum | 25.04.2026 |
| Sprint broj | Sprint 5 |
| Alat koji je korišten | Codex 5.4 |
| Svrha korištenja | Pomoć pri implementaciji API podrške za upload i preuzimanje dokumenata. |
| Kratak opis zadatka ili upita | AI alat je korišten za proširenje postojećeg Document modula. Na osnovu već definisanog Document entiteta i postojećih CRUD operacija, cilj je bio dodati endpoint-e za upload dokumenta i preuzimanje originalnog fajla. |
| Šta je AI predložio ili generisao | AI je predložio početnu implementaciju endpoint-a za upload dokumenta, download fajla i osnovnu obradu uploadovanog sadržaja. |
| Šta je tim prihvatio | Tim je prihvatio većinu osnovne strukture rješenja, uključujući ideju da se upload i download implementiraju kroz posebne API endpoint-e i povežu sa postojećim Document modulom. |
| Šta je tim izmijenio | Tim je doradio validaciju prema prethodno dogovorenim poslovnim pravilima, uskladio greške sa postojećim formatom API odgovora i prilagodio implementaciju postojećoj arhitekturi backend slojeva. |
| Šta je tim odbacio | Odbačeni su dijelovi validacije i error handlinga koji nisu bili usklađeni sa postojećim standardima projekta, posebno rješenja koja su uvodila paralelnu ili nekonzistentnu validacijsku logiku. |
| Rizici, problemi ili greške koje su uočene | AI nije u potpunosti koristio već postojeće pomoćne metode, validacijski sloj i standardizovane error poruke. Zbog toga je bilo potrebno ručno provjeriti predloženi kod i uskladiti ga sa postojećim dizajnom sistema. |
| Ko je koristio alat | Emina Zubetljak |


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
