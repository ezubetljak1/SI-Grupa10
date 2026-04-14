# Architecture Overview

**Sistem za AI asistirano prepoznavanje i obradu računa i ulaznih dokumenata**

---

## Kratak opis arhitektonskog pristupa

Sistem je zasnovan na **slojevitoj (layered) klijent-server arhitekturi** sa jasno odvojenim slojevima: prezentacijski sloj (frontend), aplikacijski/poslovni sloj (backend API) i sloj podataka (baza podataka). Pored toga, sistem uključuje integraciju sa eksternim servisima za OCR i AI obradu dokumenata, koji se pozivaju asinhrono kako bi obrada dokumenata ne bi blokirala korisničko sučelje.

Ovaj pristup odabran je jer:

- omogućava jasno razdvajanje odgovornosti između slojeva
- olakšava testiranje poslovne logike nezavisno od UI-a
- podržava buduće horizontalno skaliranje (NFR-11)
- omogućava zamjenu eksternih servisa (OCR/AI) bez mijenjanja jezgre sistema

---

## Glavne komponente sistema

1. **Frontend aplikacija** — web UI kroz koji korisnici interaguju sa sistemom
2. **Backend API** — REST API koji sadrži poslovnu logiku, upravljanje tokovima i koordinaciju servisa
3. **OCR/AI servis** — eksterni servis za ekstrakciju teksta i podataka iz dokumenata
4. **Baza podataka** — pohrana dokumenata, metapodataka, korisnika, firmi, statusa i audit logova
5. **Sistem za pohranu fajlova** — pohrana originalnih PDF i slikovnih dokumenata
6. **Auth modul** — autentifikacija i autorizacija korisnika i rola unutar firme

---

## Odgovornosti komponenti

**Frontend aplikacija** odgovorna je za prikaz korisničkog interfejsa, upload dokumenata, prikaz ekstraktovanih podataka, ručnu korekciju, workflow akcije (slanje na odobrenje, odobravanje, odbijanje) i preuzimanje XML izlaza.

**Backend API** odgovoran je za primanje zahtjeva sa frontenda, primjenu poslovne logike (validacija, tranzicije statusa, workflow pravila), koordinaciju poziva prema OCR/AI servisu, generisanje XML izlaza, upravljanje korisnicima i rolama, te evidentiranje audit loga i historije statusa.

**OCR/AI servis** odgovoran je za prepoznavanje teksta iz skeniranih ili PDF dokumenata i izdvajanje ključnih polja: dobavljač, datum, broj računa, ukupan iznos i PDV. Vraća confidence score za svako polje.

**Baza podataka** odgovorna je za trajnu pohranu svih entiteta sistema: dokumenti i metapodaci, izdvojena polja, korisnici, firme, role, statusi i historija statusa, audit log, komentari i XML zapisi.

**Sistem za pohranu fajlova** odgovoran je za fizičku pohranu originalnih fajlova (PDF, JPG, PNG) i generisanih XML fajlova, sa pristupom putem referenciranog puta u bazi podataka.

**Auth modul** odgovoran je za registraciju i prijavu korisnika, upravljanje sesijama ili tokenima, hashiranje lozinki i primjenu role-based access controla (operater, računovođa, osoba za odobravanje, administrator).

---

## Tok podataka i interakcija

Osnovni tok obrade dokumenta odvija se na sljedeći način:

**Upload i pohrana:** Korisnik (operater) uploaduje dokument putem frontend forme → frontend šalje fajl na backend API → backend validira tip i veličinu fajla → original se pohranjuje u sistem za pohranu fajlova → metapodaci se upisuju u bazu sa statusom *Uploaded*.

**OCR/AI obrada:** Backend asinhrono poziva eksterni OCR/AI servis → servis vraća ekstraktovani tekst i izdvojena polja sa confidence scoreovima → backend mapira rezultate u interne modele → podaci se pohranjuju u bazu, status se mijenja u *Processed* (ili *Failed* ako obrada nije uspjela).

**Korekcija i validacija:** Računovođa pregleda podatke putem frontenda → koriguje polja → backend provodi validaciju (obavezna polja, formati, matematička provjera iznosa) → potvrđeni podaci se pohranjuju, dokument je spreman za naredni korak.

**Workflow i odobravanje:** Računovođa šalje dokument na odobravanje → status se mijenja → osoba za odobravanje pregleda i odobrava ili odbija uz komentar → backend evidentira promjenu u historiji statusa i audit logu.

**Generisanje XML-a:** Na odobrenom dokumentu računovođa inicira generisanje XML-a → backend primjenjuje mapping pravila i generiše XML fajl → XML se pohranjuje uz dokument → korisnik može pregledati i preuzeti fajl → dokument dobiva završni status.

---

## Ključne tehničke odluke

**Odvojena pohrana fajlova od baze podataka** — originalni dokumenti i XML fajlovi pohranjuju se u sistem za fajlove (filesystem ili cloud storage), a u bazi se čuva samo referenca. Razlog: baze podataka nisu optimizirane za binarne fajlove, a ovaj pristup olakšava skaliranje i pristup fajlovima.

**Asinhrona OCR/AI obrada** — poziv prema eksternom OCR/AI servisu odvija se asinhrono kako korisničko sučelje ne bi bilo blokirano tokom obrade. Status dokumenta se ažurira nakon završetka obrade.

**Role-based access control (RBAC)** — sistem razlikuje četiri uloge: operater, računovođa, osoba za odobravanje i administrator. Svaka uloga ima tačno definisan skup akcija. Razlog: različiti poslovni akteri imaju različite odgovornosti u toku obrade dokumenata, što je direktno izvedeno iz stakeholder analize.

**Multi-tenant model** — svaka firma u sistemu ima izolovane podatke i korisnike. Razlog: sistem je predviđen za korištenje od strane više organizacija, a izolacija podataka je ključan sigurnosni zahtjev (NFR-02).

**Standardizovani XML izlaz** — generisanje XML-a zasnovano je na unaprijed definisanim mapping pravilima. Razlog: standardizovani format olakšava buduću integraciju sa računovodstvenim i ERP sistemima, što je identificirano kao moguće proširenje van MVP-a.

---

## Ograničenja i rizici arhitekture

- Tačnost ekstrakcije podataka direktno zavisi od kvaliteta ulaznog dokumenta i pouzdanosti eksternog OCR/AI servisa, što je van kontrole sistema (NFR-07).
- Oslanjanje na eksterni servis za OCR/AI uvodi zavisnost — ako servis nije dostupan, osnovna funkcionalnost sistema je blokirana.
- U MVP fazi nije implementirano horizontalno skaliranje, iako je arhitektura dizajnirana da ga podržava u budućnosti (NFR-11).
- Sigurnost i GDPR usklađenost zahtijevaju posebnu pažnju pri pohrani osjetljivih poslovnih dokumenata (NFR-01, NFR-03, NFR-04).

---

## Otvorena pitanja

- Koji konkretni OCR/AI servis će biti korišten (vlastita implementacija, cloud API, open-source model)?
- Gdje će biti pohranjen fajl sistem — lokalno ili cloud storage (npr. S3)?
- Koje konkretne tehnologije će biti odabrane za frontend i backend?
- Da li XML struktura treba biti usklađena sa nekim konkretnim standardom (npr. e-račun format)?
- Da li će sistem u budućnosti podržavati batch obradu više dokumenata odjednom?
