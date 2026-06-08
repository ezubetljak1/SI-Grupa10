# Docflow – Release Notes

## Verzija

**v1.0.0**

## Datum release-a

**07.06.2026**

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

* Tačnost OCR obrade zavisi od kvaliteta ulaznog dokumenta.
* Dokumenti sa nestandardnim rasporedom elemenata mogu zahtijevati ručne korekcije.
* Dostava email poruka zavisi od dostupnosti SMTP servisa.
* Workflow je prilagođen trenutno podržanim poslovnim procesima.

---

# Poznati bugovi

U trenutku release-a nisu poznati kritični bugovi koji onemogućavaju korištenje sistema.

Moguće su manje vizuelne ili validacione nepravilnosti u specifičnim rubnim slučajevima koje ne utiču na osnovnu funkcionalnost sistema.

---

# Funkcionalnosti koje nisu dio finalne verzije

Tokom razvoja projekta nije ostala nijedna funkcionalnost iz definisanog scope-a koja je planirana za implementaciju, a nije uključena u finalni release.

Sve stavke predviđene backlogom i odobrenim sprint planovima uspješno su implementirane, testirane i isporučene.

---

# Sažetak release-a

Docflow v1.0.0 predstavlja kompletno rješenje za upravljanje dokumentima koje uključuje autentifikaciju korisnika, OCR obradu, klasifikaciju, ekstrakciju podataka, workflow upravljanje, approval procese, audit evidenciju, notifikacije, XML generisanje i automatizovani deployment.