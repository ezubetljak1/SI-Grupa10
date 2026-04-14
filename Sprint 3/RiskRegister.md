# Risk Register
## Sistem za AI asistirano prepoznavanje i obradu računa i ulaznih dokumenata

> **Napomena:** Risk Register je živ dokument i ažurira se tokom projekta.

## Legenda

- **Vjerovatnoća:**
  - Niska – mala šansa da se rizik desi
  - Srednja – može se desiti tokom projekta
  - Visoka – vrlo vjerovatno da će se desiti

- **Uticaj:**
  - Nizak - minimalan uticaj na sistem, ne utiče na ključne funkcionalnosti
  - Srednji – djelimičan uticaj na funkcionalnosti
  - Visok – značajan uticaj na sistem ili rokove

- **Prioritet rizika:**
  - Nizak - rizik ima minimalan uticaj i ne zahtijeva hitnu akciju, prati se povremeno
  - Srednji – pratiti i kontrolisati
  - Visok – zahtijeva pažnju
  - Kritičan – zahtijeva hitnu akciju

- **Status:**
  - Otvoren – rizik je identificiran, ali još nije riješen
  - U toku – aktivno se radi na mitigaciji rizika
  - Ublažen – rizik je djelimično kontrolisan, ali i dalje postoji
  - Zatvoren – rizik više nije relevantan ili je u potpunosti riješen

---

| ID | Opis rizika | Uzrok | Vjerovatnoća | Uticaj | Prioritet rizika | Plan mitigacije | Odgovorna osoba / uloga | Status |
|----|-------------|-------|--------------|--------|------------------|-----------------|--------------------------|--------|
| R-01 | Netačno izdvajanje podataka iz skeniranih računa | Loš kvalitet skena, mutne slike, različiti formati dokumenata | Visoka | Visok | Kritičan | Koristiti kvalitetniji OCR/AI alat, omogućiti ručnu korekciju podataka, testirati na više primjera dokumenata | AI/OCR developer | Otvoren |
| R-02 | Pogrešna klasifikacija tipa dokumenta | Nedovoljno jasan sadržaj dokumenta ili sličnost između tipova dokumenata | Srednja | Visok | Visok | Definisati jasna pravila klasifikacije, omogućiti ručnu promjenu tipa dokumenta, testirati klasifikaciju na uzorcima | Backend developer | Otvoren |
| R-03 | Kašnjenje implementacije AI funkcionalnosti | Složenost OCR i AI integracije veća od očekivane | Visoka | Visok | Kritičan | Fokus na MVP funkcionalnosti, koristiti gotove servise umjesto treniranja vlastitog modela, rano napraviti prototip | Scrum tim / Team lead | Otvoren |
| R-04 | Problemi pri generisanju ispravnog XML-a | Nejasno definisana XML struktura ili greške u mapiranju podataka | Srednja | Srednji | Srednji | Na početku definisati XML shemu, validirati XML na testnim primjerima, postepeno uvoditi obavezna polja | Backend developer | Otvoren |
| R-05 | Gubitak ili nepravilno čuvanje dokumenata | Greške u bazi, loša organizacija storage-a ili nepostojeći backup | Niska | Visok | Srednji | Uvesti sigurnu pohranu fajlova, redovan backup, testirati spremanje i dohvat dokumenata | Backend developer / DevOps | Otvoren |
| R-06 | Nedovoljno jasno definisani zahtjevi u ranoj fazi projekta | Nepotpuno razumijevanje problema i stakeholdera | Srednja | Visok | Visok | Uložiti više vremena u Product Vision, stakeholder mapu i backlog refinement, redovno validirati zahtjeve s mentorom | Product owner / cijeli tim | Otvoren |
| R-07 | Prevelik scope projekta za raspoloživo vrijeme | Dodavanje previše funkcionalnosti izvan MVP-a | Visoka | Visok | Kritičan | Jasno odvojiti MVP od dodatnih funkcionalnosti, redovno revidirati backlog i prioritete | Product owner / Team lead | Otvoren |
| R-08 | Slaba integracija između frontend i backend dijela sistema | Neusklađen API, kašnjenje implementacije jedne strane | Srednja | Srednji | Srednji | Rano definisati API ugovor, koristiti testne mock odgovore, redovna koordinacija tima | Frontend i backend developer | Otvoren |
| R-09 | Sigurnosni problem pri uploadu dokumenata | Upload neprovjerenih fajlova, nedostatak validacije tipa i veličine | Srednja | Visok | Visok | Validirati tip i veličinu fajla, ograničiti dozvoljene formate, uvesti serversku provjeru upload-a | Backend developer | Otvoren |
| R-10 | Nedovoljno testiranja ključnih funkcionalnosti | Fokus na implementaciji bez dovoljno vremena za testove | Srednja | Visok | Visok | Definisati test strategy rano, pisati testove paralelno s razvojem, ostaviti sprint za stabilizaciju | QA odgovorna osoba / cijeli tim | Otvoren |
| R-11 | Ovisnost o vanjskom AI/OCR servisu | Cloud servis može biti skup, spor ili privremeno nedostupan | Srednja | Srednji | Srednji | Uporediti više opcija, imati rezervno rješenje, ograničiti scope na minimalnu potrebnu funkcionalnost | Team lead / AI developer | Otvoren |
| R-12 | Tehnički dug zbog brzog razvoja MVP-a | Brza implementacija bez refaktorisanja i dokumentovanja | Visoka | Srednji | Visok | Voditi decision log, planirati refaktorisanje u kasnijim sprintovima, koristiti Definition of Done | Cijeli tim | Otvoren |
| R-13 | Probijanje budžeta projekta | Korištenje plaćenih AI/OCR servisa, duže trajanje razvoja | Srednja | Srednji | Srednji | Koristiti free tier gdje je moguće, pratiti troškove API poziva, ograničiti broj testiranja, planirati budžet unaprijed | Team lead | Otvoren |
| R-14 | Sporo procesiranje dokumenata | Veliki fajlovi i spor AI servis | Srednja | Srednji | Srednji | Uvesti async obradu i ograničiti veličinu fajlova | Backend developer | Otvoren |
| R-15 | Loš korisnički interfejs (UX) | Previše kompleksan dizajn | Srednja | Srednji | Srednji | Pojednostaviti UI i testirati sa korisnicima | Frontend developer | Otvoren |
| R-16 | Nedostatak raznovrsnih testnih podataka | Mali i nereprezentativan dataset | Srednja | Srednji | Srednji | Prikupiti različite tipove dokumenata | AI developer | Otvoren |
| R-17 | Problemi sa verzionisanjem i deployom | Loša CI/CD praksa | Niska | Srednji | Srednji | Uvesti verzionisanje i CI/CD pipeline | DevOps | Otvoren |
| R-18 | Neusklađen naming konvencija u kodu | Različiti stilovi programiranja u timu | Srednja | Nizak | Nizak | Definisati coding standarde i koristiti linting alate | Cijeli tim | Otvoren |
| R-19 | Manje greške u UI (tipfeleri, layout) | Nedovoljno UI testiranja | Srednja | Nizak | Nizak | UI pregled prije release-a, koristiti checklistu | Frontend developer | Otvoren |
| R-20 | Nedovoljna dokumentacija koda | Fokus na implementaciji | Srednja | Nizak | Nizak | Pisati osnovnu dokumentaciju uz razvoj | Cijeli tim | Otvoren |
| R-21 | Neoptimizovani logovi sistema | Previše ili premalo logovanja | Niska | Nizak | Nizak | Standardizovati logging pristup | Backend developer | Otvoren |
| R-22 | Sitne razlike u prikazu na različitim browserima | Različiti rendering engine-i | Niska | Nizak | Nizak | Testirati na više browsera | Frontend developer | Otvoren |
| R-23 | Neadekvatna zaštita osjetljivih podataka iz dokumenata | Nešifrovana pohrana, preširok pristup podacima, slaba kontrola pristupa | Srednja | Visok | Visok | Ograničiti pristup dokumentima po firmi i ulozi, zaštititi osjetljive podatke u storage-u i logovima, ne slati više podataka vanjskim servisima nego što je nužno | Backend developer / Team lead | Otvoren |
