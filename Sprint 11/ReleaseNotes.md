# Docflow – Release Notes

## Verzija

**v1.0.0**

## Datum release-a

**08.06.2026**

## Finalni Commit SHA

**dc9295f03d76839becd254808dae0431b5408bac**

## Javni URL

Frontend: https://docflow.page

Keycloak: https://auth.docflow.page

---

# Pregled sistema

Docflow je platforma za obradu i upravljanje dokumentima koja omogućava kompanijama upload, klasifikaciju, ekstrakciju podataka, pregled, odobravanje i izvoz dokumenata kroz siguran workflow zasnovan na korisničkim ulogama.

Ova verzija predstavlja finalnu isporučenu verziju sistema.

---

# Realizacija u odnosu na Initial Release Plan

Početnim release planom u Sprintu 4 definisano je pet inkremenata sistema. Svi planirani inkrementi uključeni su u finalnu verziju.

| Planirani inkrement | Status | Isporučeni sadržaj |
| --- | --- | --- |
| **Inkrement 1 – Upravljanje dokumentima** | Isporučeno | Upload i validacija dokumenata, pohrana datoteka i metapodataka, lista dokumenata, pregled detalja, preview i download originalnog dokumenta. |
| **Inkrement 2 – OCR i AI ekstrakcija** | Isporučeno i prošireno | OCR/AI obrada putem Google Document AI servisa, izdvajanje strukturiranih podataka, klasifikacija dokumenata, podrška za invoice, receipt, bank statement i form procesore te ručni pregled klasifikacije kada automatska klasifikacija nije dovoljno pouzdana. |
| **Inkrement 3 – Validacija i korekcija podataka** | Isporučeno | Pregled i ručna korekcija ekstrahovanih polja, validacija obaveznih vrijednosti, formata datuma i numeričkih iznosa, matematička validacija, upozorenja za low-confidence polja te dodavanje i brisanje polja. |
| **Inkrement 4 – Organizacioni model pristupa sistemu** | Isporučeno i prošireno | Registracija kompanije, kreiranje korisnika, autentifikacija putem Keycloaka, upravljanje rolama, multi-tenant izolacija podataka, dodjela taskova, approval workflow, komentari, audit log i historija statusa. |
| **Inkrement 5 – Efikasan rad i završna stabilizacija sistema** | Isporučeno | XML generisanje, preview i download XML izlaza, finalizacija obrade dokumenta, pretraga i filtriranje, dashboard, in-app notifikacije, email reminder scheduler, testiranje, CI/CD pipeline i produkcijski deployment. |

Funkcionalni opseg planiranih inkremenata je zadržan i isporučen.

---

# Najvažnije implementirane funkcionalnosti

## Keycloak autentifikacija i role-based pristup

* Integracija sa Keycloak sistemom za autentifikaciju i autorizaciju.
* Role-based kontrola pristupa.
* Podržane korisničke uloge:

    * Admin
    * Manager
    * Operator
    * Approver
* Zaštićeni API endpointi i frontend rute.

## Registracija kompanije i upravljanje korisnicima

* Registracija novih kompanija.
* Izolacija podataka po kompanijama.
* Upravljanje korisnicima unutar kompanije.
* Dodjela uloga i upravljanje statusom korisničkih računa.

## Upload i čuvanje dokumenata

* Siguran upload dokumenata.
* Pohrana i upravljanje datotekama.
* Pregled dokumenata unutar sistema.
* Evidencija metapodataka o dokumentima.

## OCR obrada i podrška za više tipova dokumenata

* OCR proces za ekstrakciju podataka iz dokumenata.
* Podrška za više tipova poslovnih dokumenata.
* Evidencija confidence vrijednosti ekstrahovanih polja.
* Retry mehanizam za neuspjele obrade.

## Automatska klasifikacija i classification review

* Automatska klasifikacija dokumenata.
* Workflow za pregled i potvrdu klasifikacije.
* Ručna korekcija tipa dokumenta kada je potrebno.

## Extraction review i ručne korekcije

* Pregled ekstrahovanih podataka.
* Ručna izmjena i korekcija polja.
* Dodavanje novih polja po potrebi.
* Validacija obaveznih podataka prije nastavka workflow procesa.

## Workflow taskovi i approval tok

* Dodjela zadataka korisnicima.
* Praćenje statusa zadataka.
* Approval workflow za odobravanje dokumenata.
* Vraćanje dokumenta na doradu.
* Odbijanje dokumenta.
* Kontrolisan životni ciklus dokumenta kroz workflow.

## Audit log, komentari i status history

* Audit log za praćenje važnih poslovnih akcija.
* Historija promjena statusa dokumenta.
* Sistem komentara.
* Evidencija korisnika koji su izvršili određene akcije.

## Notification center i email reminder

* In-app notification centar.
* Evidencija pročitanih i nepročitanih notifikacija.
* Funkcionalnosti označavanja notifikacija kao pročitanih.
* Email podsjetnici za korisnike sa neriješenim zadacima i nepročitanim notifikacijama.

## XML generisanje, preview, download i complete tok

* Generisanje XML izlaza za odobrene dokumente.
* Pregled XML sadržaja prije preuzimanja.
* Preuzimanje XML datoteka.
* Complete workflow nakon uspješnog generisanja XML-a.

## Javni deployment i automatizovani CD pipeline

* Javno dostupna produkcijska verzija sistema.
* GitHub Actions CI/CD pipeline.
* Automatizovani deployment procesa.
* Produkcijsko okruženje dostupno korisnicima.

---

# Poznata ograničenja

## Funkcionalna ograničenja

* Tačnost OCR i AI ekstrakcije zavisi od kvaliteta skena, fotografije i strukture ulaznog dokumenta. Dokumenti slabijeg kvaliteta ili nestandardnog rasporeda mogu zahtijevati ručnu korekciju ekstrahovanih polja.

* Automatska klasifikacija i ekstrakcija prilagođene su trenutno podržanim tipovima poslovnih dokumenata: fakturama, računima, bankovnim izvodima i obrascima. Ostali ili nedovoljno pouzdano klasifikovani dokumenti zahtijevaju ručnu potvrdu tipa dokumenta.

* Podržan je upload dokumenata u PDF, JPG, JPEG i PNG formatima maksimalne veličine 10 MB.

* Ekstrakcija podataka pokreće se ručnom akcijom korisnika nakon uploada dokumenta. Time se izbjegava nepotrebna OCR obrada pogrešno uploadovanih ili nerelevantnih dokumenata i omogućava racionalnije korištenje resursa Google Document AI servisa.

* Sistem koristi unaprijed definisan workflow obrade dokumenata. Proizvoljno konfigurisanje novih workflow koraka, tipova taskova i pravila odobravanja nije omogućeno kroz korisnički interfejs.

* Korisnički interfejs prvenstveno je prilagođen korištenju na desktop uređajima. Prikaz na mobilnim uređajima nije detaljno testiran, zbog čega su moguće manje nepravilnosti u rasporedu elemenata i korisničkom iskustvu na manjim ekranima.


## Tehnička i operativna ograničenja

* OCR obrada i automatska klasifikacija zavise od dostupnosti vanjskog Google Document AI servisa. Privremena nedostupnost servisa može onemogućiti ili odgoditi obradu dokumenata.

* Dostava email podsjetnika zavisi od dostupnosti konfigurisanog SMTP servisa. U slučaju problema sa email dostavom, korisnici i dalje mogu pregledati notifikacije unutar aplikacije.

* Uploadovani dokumenti i generisani XML fajlovi čuvaju se na filesystemu produkcijskog servera. Cloud object storage nije uveden u trenutnoj verziji.

* Produkcijski deployment može izazvati kratak prekid dostupnosti sistema tokom ponovnog kreiranja kontejnera.


---

# Poznati bugovi

U trenutku objave finalne verzije nisu evidentirani otvoreni reproducibilni bugovi koji značajno otežavaju ili onemogućavaju korištenje sistema.

---

# Funkcionalnosti koje nisu dio finalne verzije

Tokom razvoja projekta nije ostala nijedna funkcionalnost iz definisanog scope-a koja je planirana za implementaciju, a nije uključena u finalni release.

Sve stavke predviđene backlogom i odobrenim sprint planovima uspješno su implementirane, testirane i isporučene.

---

# Sažetak release-a

Docflow v1.0.0 predstavlja kompletno rješenje za upravljanje dokumentima koje uključuje autentifikaciju korisnika, OCR obradu, klasifikaciju, ekstrakciju podataka, workflow upravljanje, approval procese, audit evidenciju, notifikacije, XML generisanje i automatizovani deployment.
