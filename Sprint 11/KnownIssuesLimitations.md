# 10. Known Issues / Limitations

Ovaj dokument navodi poznata ogranicenja finalne verzije Docflow sistema. Ogranicenja nisu skrivena niti predstavljena kao potpuno zavrsene funkcionalnosti. Dio ogranicenja je prihvatljiv za studentski projekat i MVP opseg, dok bi za produkcijsko okruzenje sa vecim brojem korisnika bila potrebna dodatna dorada, hardening i operativna automatizacija.

## Ogranicenja koja se vec mogu navesti

| Ogranicenje | Opis i posljedica |
| --- | --- |
| Filesystem storage | Uploadovani dokumenti i XML izlazi cuvaju se na filesystemu servera. To je jednostavno za studentski projekat, ali ogranicava skaliranje na vise instanci i zahtijeva pazljiv backup server fajlova. |
| Jedan DigitalOcean Droplet | Deployment je postavljen na jednu serversku instancu. To predstavlja single point of failure, bez load balancera, automatskog skaliranja i visoke dostupnosti. |
| Keycloak start-dev rezim | Docker Compose koristi Keycloak `start-dev` i realm import. Za ozbiljnu produkciju potrebno je dodatno hardening podesavanje, strozi hostname/TLS setup, sigurnije tajne i produkcijski Keycloak rezim. |
| Migracije nisu formalizovane | Aplikacija koristi Hibernate `ddl-auto=update` i pojedine rucne SQL korake za constraint promjene. Dugorocno treba uvesti Flyway ili Liquibase da se promjene baze verzionisu i izvrsavaju kontrolisano. |
| Vanjski OCR servis | Google Document AI moze biti nedostupan, ima trosak i zavisi od tacne konfiguracije processor ID-jeva i credentials fajla. Slab kvalitet skena ili nestandardni dokumenti mogu zahtijevati rucnu korekciju. |
| Vanjski SMTP provider | Email delivery zavisi od dostupnosti i konfiguracije SMTP providera. Ako SMTP nije podesen ili je privremeno nedostupan, password setup/reset i email reminderi mogu kasniti ili ne biti isporuceni, dok in-app notifikacije ostaju primarni fallback kanal. |
| Upload ogranicenja | Podrzani formati su PDF, JPG, JPEG i PNG; maksimalna velicina fajla je 10 MB. Veci dokumenti i drugi formati nisu dio finalnog scope-a. |
| XML bez formalnog XSD-a | XML izlaz je genericki format dovoljan za projekat, ali integracija sa stvarnim ERP/racunovodstvenim sistemom bi zahtijevala strozi ugovor, XSD semu i mapiranje prema konkretnom eksternom formatu. |
| Jedan aktivni XML | Regenerisanje XML-a zamjenjuje prethodni aktivni XML izlaz. Puna historija XML verzija nije fokus trenutne implementacije. |
| Swagger u produkciji | Ako je OpenAPI/Swagger UI dostupan u produkcijskom okruzenju, pristup treba ograniciti ili iskljuciti. Dokumentacija endpointa je korisna za razvoj, ali ne treba nepotrebno otkrivati API povrsinu javno. |
| UI automatizacija | Postoje frontend unit/component testovi i Playwright smoke testovi, ali nije pokriven kompletan browser matrix niti svi end-to-end tokovi kroz stvarni Keycloak, OCR i SMTP provider. |
| Kratak prekid tokom deploymenta | Trenutni deployment moze izazvati kratak prekid dostupnosti dok se kontejneri ponovo buildaju i restartuju. Blue/green ili rolling deployment nije uveden. |
| Backup i restore procedura | Docker volume-i sadrze bazu, upload fajlove i Keycloak podatke, ali automatizovana backup/restore procedura nije u potpunosti implementirana kao dio aplikacije. |
| Mobilni prikaz | UI je prvenstveno optimizovan za desktop i demonstracijsko koristenje. Manji ekrani su djelimicno podrzani, ali nisu detaljno testirani kroz sve workflow sekcije. |
| Konkurentne izmjene | Task assignment smanjuje rizik konfliktnih akcija, ali sistem nema napredan optimistic locking UI za sve moguce paralelne izmjene istog dokumenta. |

## Funkcionalna ogranicenja

| Ogranicenje | Opis i posljedica |
| --- | --- |
| Podrzani tipovi dokumenata | Sistem je fokusiran na invoice, receipt, bank statement, form i classification review za `OTHER`. Dokumenti izvan tih kategorija mogu zahtijevati rucnu klasifikaciju ili nisu optimalno podrzani. |
| Rucno pokretanje ekstrakcije | OCR/AI ekstrakcija se ne pokrece automatski nakon svakog uploada. To smanjuje nepotrebne troskove Google Document AI servisa, ali korisnik mora eksplicitno pokrenuti obradu. |
| Predefinisan workflow | Workflow statusi, task tipovi i approval pravila su unaprijed definisani. Korisnik ne moze kroz UI konfigurirati nove statuse, dodatne approval nivoe ili custom workflow grane. |
| Validacija extraction polja | Validacija je prilagodjena trenutnim poljima i poslovnim pravilima. Kompleksnije domenske provjere, npr. integracija sa dobavljacima, poreznim pravilima ili racunovodstvenim kontnim planom, nisu dio scope-a. |
| Email reminder logika | Email reminderi su digest pristup za neprocitane notifikacije. Nema naprednih korisnickih preferenci za ucestalost, tihi period, kanale slanja ili eskalacije. |

## Tehnicka i operativna ogranicenja

| Ogranicenje | Opis i posljedica |
| --- | --- |
| Osjetljive konfiguracije | Google Document AI service account, SMTP credentials, Keycloak admin tajne i DB lozinke moraju se rucno podesiti kroz environment varijable i server fajlove. Ne smiju se commitati u repozitorij. |
| Lokalno okruzenje nije isto kao server | Lokalno pokretanje bez Google credentials ili SMTP konfiguracije moze dati greske pri ekstrakciji ili slanju emaila. To ne znaci da je workflow neispravan, nego da zavisi od eksternih servisa. |
| CI ne pokriva stvarne eksterne servise | Automatizovani testovi uglavnom koriste mock/test konfiguraciju za vanjske zavisnosti. Time se izbjegavaju troskovi i nestabilnost, ali stvarni Google Document AI i SMTP se moraju provjeriti smoke testom na deployment okruzenju. |
| Monitoring i alerting | CI/CD i deployment postoje, ali napredan produkcijski monitoring, alerting, centralizovani logovi i health dashboard nisu dio finalnog scope-a. |
| Performanse pod velikim opterecenjem | Sistem nije load-testiran za veliki broj firmi, korisnika i dokumenata. Paginacija i filteri pomazu, ali horizontalno skaliranje i objektni storage bi bili potrebni za veci produkcijski obim. |

## Dopuniti nakon zavrsnog smoke testa

- Nema poznatih bugova.


## Zakljucak

Finalna verzija Docflow sistema isporucuje planirani funkcionalni scope, ali nije zamjena za potpuno produkcijski hardened enterprise sistem. Najvaznija ogranicenja su vezana za eksterni OCR/SMTP, filesystem storage, single-server deployment, formalne migracije baze i obim UI/E2E automatizacije. Ta ogranicenja su poznata, dokumentovana i predstavljaju prirodne naredne korake ako bi se projekat nastavio razvijati izvan studentskog MVP okvira.
