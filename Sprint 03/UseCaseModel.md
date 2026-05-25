# Use Case Model

## Uvod

U nastavku je prikazan model slučajeva korištenja sistema za obradu poslovnih dokumenata uz podršku OCR/AI obrade, validacije, odobravanja, generisanja XML izlaza i administracije korisnika.  
Use caseovi su grupisani prema funkcionalnim cjelinama sistema kako bi pregled modela bio jasniji i lakši za praćenje.

---

## 1. Registracija i pristup sistemu

### UC-1 Registracija firme

**Akter:** Predstavnik firme  
**Naziv use casea:** Registracija firme  
**Kratak opis:** Predstavnik firme registruje organizaciju u sistem i kreira prvi administratorski nalog.  
**Preduslovi:** Firma nije već registrovana u sistemu.

**Glavni tok:**
1. Predstavnik firme otvara formu za registraciju.
2. Unosi osnovne podatke o firmi.
3. Unosi podatke za prvi administratorski nalog.
4. Sistem validira unesene podatke.
5. Sistem kreira zapis firme.
6. Sistem kreira prvi administratorski nalog i povezuje ga sa firmom.
7. Sistem prikazuje potvrdu uspješne registracije.

**Alternativni tokovi:**
- Ako firma već postoji u sistemu, registracija se odbija.
- Ako neki obavezni podaci nisu uneseni, sistem prikazuje grešku.
- Ako podaci administratorskog naloga nisu validni, sistem ne završava registraciju.

**Ishod:** Firma i prvi administratorski nalog su uspješno kreirani.

---

### UC-2 Prijava u sistem

**Akter:** Korisnik firme  
**Naziv use casea:** Prijava u sistem  
**Kratak opis:** Korisnik se prijavljuje u sistem korištenjem validnih pristupnih podataka.  
**Preduslovi:** Korisnički nalog postoji i aktivan je.

**Glavni tok:**
1. Korisnik otvara login formu.
2. Unosi email i lozinku.
3. Sistem provjerava ispravnost pristupnih podataka.
4. Sistem autentificira korisnika.
5. Sistem kreira sesiju ili token.
6. Korisnik dobija pristup funkcionalnostima sistema.

**Alternativni tokovi:**
- Ako su pristupni podaci netačni, sistem prikazuje poruku o grešci.
- Ako korisnički nalog nije aktivan, sistem ne dozvoljava prijavu.
- Ako dođe do tehničke greške pri autentifikaciji, sistem prikazuje odgovarajuću poruku.

**Ishod:** Korisnik je uspješno prijavljen ili dobija poruku o neuspješnoj prijavi.

---

### UC-3 Reset lozinke

**Akter:** Korisnik  
**Naziv use casea:** Reset lozinke  
**Kratak opis:** Korisnik resetuje lozinku kako bi povratio pristup sistemu.  
**Preduslovi:** Korisnik ima registrovan nalog.

**Glavni tok:**
1. Korisnik pokreće reset lozinke.
2. Sistem generiše token i šalje link.
3. Korisnik otvara link.
4. Unosi novu lozinku.
5. Sistem validira i sprema lozinku.

**Alternativni tokovi:**
- Token je istekao te sistem odbija zahtjev.
- Ako je nova lozinka nevalidna, sistem prikazuje grešku.

**Ishod:** Korisnik uspješno resetuje lozinku i može se prijaviti.

---

### UC-4 Odjava iz sistema

**Akter:** Korisnik  
**Naziv use casea:** Odjava iz sistema  
**Kratak opis:** Korisnik se odjavljuje iz sistema kako bi završio radnu sesiju na siguran način.  
**Preduslovi:** Korisnik je prijavljen u sistem.

**Glavni tok:**
1. Korisnik odabire opciju za odjavu.
2. Sistem prekida aktivnu sesiju ili poništava token.
3. Sistem preusmjerava korisnika na login ekran ili početnu stranicu.

**Alternativni tokovi:**
- Sistem ne uspije pravilno zatvoriti sesiju, ali korisniku i dalje onemogućava dalji pristup zaštićenim funkcionalnostima.

**Ishod:** Korisnik je uspješno odjavljen iz sistema.

---

## 2. Upravljanje dokumentima i pregled

### UC-5 Upload dokumenta

**Akter:** Operater  
**Naziv use casea:** Upload dokumenta  
**Kratak opis:** Operater uploaduje PDF ili skenirani dokument putem sistema kako bi započeo njegovu obradu.  
**Preduslovi:** Operater je prijavljen u sistem.

**Glavni tok:**
1. Operater otvara formu za upload dokumenta.
2. Odabire dokument sa lokalnog uređaja.
3. Sistem provjerava tip i veličinu fajla.
4. Operater potvrđuje upload.
5. Sistem pohranjuje dokument.
6. Sistem pohranjuje osnovne metapodatke dokumenta.
7. Sistem dodjeljuje početni status dokumentu.

**Alternativni tokovi:**
- Ako je format dokumenta nepodržan, sistem odbija upload.
- Ako je fajl prevelik ili oštećen, sistem prikazuje grešku.
- Ako pohrana ne uspije, sistem ne kreira dokument u listi.

**Ishod:** Dokument je uspješno zaprimljen i spreman za obradu.

---

### UC-6 Pregled liste i pretraga dokumenata

**Akter:** Računovođa / Operater / Menadžment / Osoba za odobravanje  
**Naziv use casea:** Pregled i pretraga dokumenata  
**Kratak opis:** Korisnik pregledava listu dokumenata i koristi pretragu i filtere kako bi pronašao željeni dokument.  
**Preduslovi:**  
- Korisnik je prijavljen u sistem.  
- Postoje dokumenti u sistemu.

**Glavni tok:**
1. Korisnik otvara listu dokumenata.
2. Sistem prikazuje dokumente povezane sa firmom.
3. Korisnik unosi kriterij pretrage ili filtere.
4. Sistem filtrira i prikazuje rezultate.
5. Korisnik odabire dokument za detaljan pregled.

**Alternativni tokovi:**
- Nema rezultata pa sistem prikazuje poruku „Nema rezultata”.
- Nema dokumenata pa se prikazuje prazno stanje.

**Ishod:** Korisnik uspješno pronalazi i otvara željeni dokument.

---

### UC-7 Pregled detalja dokumenta i historije

**Akter:** Svi korisnici prema dozvolama  
**Naziv use casea:** Pregled detalja dokumenta  
**Kratak opis:** Korisnik otvara dokument i vidi njegov sadržaj, status, podatke i historiju promjena.  
**Preduslovi:**  
- Dokument postoji.  
- Korisnik ima pristup dokumentu.

**Glavni tok:**
1. Korisnik otvara dokument.
2. Sistem prikazuje originalni dokument.
3. Sistem prikazuje ekstraktovane podatke.
4. Sistem prikazuje trenutni status.
5. Sistem prikazuje historiju statusa.
6. Korisnik pregledava informacije.

**Alternativni tokovi:**
- Dokument ne postoji ili korisnik nema pristup, pa sistem vraća grešku.

**Ishod:** Korisnik dobija kompletan uvid u stanje i tok dokumenta.

---

### UC-8 Dodavanje komentara na dokument

**Akter:** Računovođa / Osoba za odobravanje / Administrator  
**Naziv use casea:** Dodavanje komentara na dokument  
**Kratak opis:** Korisnik dodaje komentar na dokument radi komunikacije ili pojašnjenja.  
**Preduslovi:**  
- Korisnik je prijavljen.  
- Ima pristup dokumentu.

**Glavni tok:**
1. Korisnik otvara dokument.
2. Unosi komentar.
3. Potvrđuje unos.
4. Sistem sprema komentar i prikazuje ga u listi.

**Alternativni tokovi:**
- Ako je komentar prazan, sistem odbija unos.

**Ishod:** Komentar je uspješno sačuvan i vidljiv korisnicima.

---

## 3. Obrada, ekstrakcija i validacija podataka

### UC-9 Automatska obrada dokumenta

**Akter:** Sistem / AI-OCR modul  
**Naziv use casea:** Automatska obrada dokumenta  
**Kratak opis:** Sistem vrši OCR obradu, izdvajanje ključnih podataka i osnovnu klasifikaciju dokumenta.  
**Preduslovi:** Dokument je uspješno uploadovan i dostupan za obradu.

**Glavni tok:**
1. Sistem preuzima uploadovani dokument.
2. Pokreće OCR obradu dokumenta.
3. Sistem vrši izdvajanje tekstualnog sadržaja.
4. Pokreće AI analizu nad izdvojenim sadržajem.
5. Pokušava izdvojiti ključne podatke.
6. Klasifikuje dokument kao račun ili ostalo.
7. Sprema rezultat obrade uz dokument.
8. Ažurira status dokumenta.

**Alternativni tokovi:**
- OCR obrada ne uspije.
- AI izdvajanje ne vrati dovoljno podataka.
- Klasifikacija dokumenta nije moguća.
- Sistem označava dokument kao neuspješno ili djelimično obrađen.

**Ishod:** Dokument je obrađen, a izdvojeni podaci i status obrade su evidentirani.

---

### UC-10 Pregled izdvojenih podataka

**Akter:** Računovođa  
**Naziv use casea:** Pregled izdvojenih podataka  
**Kratak opis:** Računovođa pregledava automatski izdvojene podatke sa dokumenta.  
**Preduslovi:** Dokument je uspješno obrađen ili ima dostupne rezultate izdvajanja.

**Glavni tok:**
1. Računovođa otvara dokument iz liste.
2. Sistem prikazuje originalni dokument.
3. Sistem prikazuje automatski izdvojene podatke.
4. Računovođa pregledava prikazane vrijednosti.
5. Računovođa procjenjuje da li su podaci dovoljno tačni za nastavak rada.

**Alternativni tokovi:**
- Obrada dokumenta nije završena.
- Podaci nisu uspješno izdvojeni.
- Sistem prikazuje status neuspjele ili nepotpune obrade.

**Ishod:** Računovođa ima uvid u rezultat automatske obrade dokumenta.

---

### UC-11 Korekcija i validacija podataka

**Akter:** Računovođa  
**Naziv use casea:** Korekcija i validacija podataka  
**Kratak opis:** Računovođa koriguje i validira automatski izdvojene podatke prije slanja dokumenta dalje u proces.  
**Preduslovi:** Dokument ima dostupne izdvojene podatke i otvoren je za pregled.

**Glavni tok:**
1. Računovođa otvara dokument.
2. Sistem prikazuje izdvojene podatke.
3. Računovođa pregleda sva relevantna polja.
4. Po potrebi koriguje pojedina polja.
5. Sistem validira obavezna polja.
6. Sistem validira format unosa.
7. Sistem provjerava osnovnu matematičku ispravnost iznosa.
8. Računovođa sprema potvrđene podatke.

**Alternativni tokovi:**
- Neka obavezna polja nisu popunjena.
- Uneseni datum ili iznos nisu u ispravnom formatu.
- Matematička provjera iznosa ne uspije.
- Spremanje izmjena ne uspije.

**Ishod:** Dokument sadrži validirane i potvrđene podatke.

---

### UC-12 Ponovna obrada dokumenta

**Akter:** Računovođa / Ovlašteni korisnik  
**Naziv use casea:** Ponovna obrada dokumenta  
**Kratak opis:** Ovlašteni korisnik pokreće ponovnu OCR/AI obradu dokumenta kada prethodna obrada nije bila uspješna ili nije dala zadovoljavajuće rezultate.  
**Preduslovi:**  
- Dokument postoji u sistemu.  
- Dokument je prethodno obrađen ili je obrada završila neuspješno.  
- Korisnik ima pravo pokretanja ponovne obrade.

**Glavni tok:**
1. Korisnik otvara detalje dokumenta.
2. Pregledava rezultate prethodne obrade.
3. Odabire opciju za ponovnu obradu dokumenta.
4. Sistem pokreće OCR/AI obradu nad postojećim dokumentom.
5. Sistem ažurira izdvojene podatke i rezultat obrade.
6. Sistem evidentira akciju u audit tragu i po potrebi u historiji statusa.
7. Sistem prikazuje ažurirani rezultat obrade.

**Alternativni tokovi:**
- Korisnik nema pravo pokretanja ponovne obrade.
- Dokument nije u stanju koje dozvoljava ponovnu obradu.
- OCR/AI obrada ponovo ne uspije.
- Sistem ne uspije sačuvati novi rezultat obrade.

**Ishod:** Dokument je ponovo obrađen, a rezultat obrade je ažuriran u sistemu.

---

## 4. Odobravanje i završna obrada

### UC-13 Slanje dokumenta na odobravanje

**Akter:** Računovođa  
**Naziv use casea:** Slanje dokumenta na odobravanje  
**Kratak opis:** Računovođa šalje validiran dokument odgovornoj osobi na odobravanje.  
**Preduslovi:** Dokument je validiran i spreman za naredni korak procesa.

**Glavni tok:**
1. Računovođa otvara dokument.
2. Pregledava validirane podatke.
3. Odabire opciju za slanje na odobravanje.
4. Sistem provjerava da li su svi uslovi ispunjeni.
5. Sistem mijenja status dokumenta u status čekanja odobrenja.
6. Sistem evidentira promjenu statusa.
7. Dokument postaje dostupan osobi za odobravanje.

**Alternativni tokovi:**
- Dokument nije validiran.
- Nedostaju obavezna polja.
- Sistem ne uspije promijeniti status dokumenta.

**Ishod:** Dokument je poslan na odobravanje.

---

### UC-14 Odobravanje dokumenta

**Akter:** Osoba za odobravanje  
**Naziv use casea:** Odobravanje dokumenta  
**Kratak opis:** Osoba za odobravanje pregledava dokument i odobrava ga za završnu obradu.  
**Preduslovi:** Dokument je u statusu čekanja odobrenja.

**Glavni tok:**
1. Osoba za odobravanje otvara listu dokumenata na čekanju.
2. Odabire dokument za pregled.
3. Sistem prikazuje dokument, podatke i historiju obrade.
4. Osoba za odobravanje pregleda sadržaj.
5. Odabire opciju odobravanja.
6. Sistem mijenja status dokumenta u odobren.
7. Sistem evidentira akciju u historiji i audit tragu.

**Alternativni tokovi:**
- Dokument više nije u statusu čekanja.
- Korisnik nema odgovarajuću rolu.
- Sistem ne uspije sačuvati promjenu statusa.

**Ishod:** Dokument je odobren i spreman za završne korake obrade.

---

### UC-15 Odbijanje dokumenta

**Akter:** Osoba za odobravanje  
**Naziv use casea:** Odbijanje dokumenta  
**Kratak opis:** Osoba za odobravanje odbija dokument i ostavlja komentar za doradu.  
**Preduslovi:** Dokument je u statusu čekanja odobrenja.

**Glavni tok:**
1. Osoba za odobravanje otvara dokument.
2. Pregledava originalni dokument i izdvojene podatke.
3. Odabire opciju odbijanja.
4. Unosi komentar ili razlog odbijanja.
5. Sistem mijenja status dokumenta u odbijen ili vraćen na doradu.
6. Sistem sprema komentar.
7. Sistem evidentira promjenu u historiji i audit tragu.
8. Relevantni korisnik dobija informaciju da je dokument vraćen na doradu.

**Alternativni tokovi:**
- Komentar je obavezan, a nije unesen.
- Dokument nije u odgovarajućem statusu.
- Sistem ne uspije sačuvati komentar ili promjenu statusa.

**Ishod:** Dokument je odbijen i vraćen na doradu sa evidentiranim razlogom.

---

### UC-16 Generisanje i preuzimanje XML zapisa

**Akter:** Računovođa  
**Naziv use casea:** Generisanje i preuzimanje XML zapisa  
**Kratak opis:** Sistem generiše XML izlaz iz validiranih i odobrenih podataka dokumenta, a korisnik ga pregledava i po potrebi preuzima.  
**Preduslovi:** Dokument je validiran i odobren.

**Glavni tok:**
1. Računovođa otvara dokument.
2. Odabire opciju za generisanje XML-a.
3. Sistem formira XML zapis prema definisanim pravilima.
4. Sistem sprema XML zapis uz dokument.
5. Sistem prikazuje sadržaj XML-a.
6. Računovođa po potrebi preuzima XML fajl.
7. Sistem dokumentu dodjeljuje završni status obrade.

**Alternativni tokovi:**
- Dokument nije odobren.
- Podaci nisu potpuni za generisanje XML-a.
- Generisanje XML-a ne uspije.
- Pohrana XML-a ne uspije.

**Ishod:** XML je generisan, sačuvan i dostupan za pregled ili preuzimanje.

---

## 5. Dodjela zadataka i operativni rad

### UC-17 Dodjela dokumenta korisniku

**Akter:** Administrator firme / Osoba za odobravanje / Ovlašteni korisnik  
**Naziv use casea:** Dodjela dokumenta korisniku  
**Kratak opis:** Ovlašteni korisnik dodjeljuje dokument konkretnom korisniku radi obrade, provjere ili odobravanja.  
**Preduslovi:**  
- Dokument postoji u sistemu.  
- Korisnik je prijavljen i ima pravo dodjele.  
- Postoji korisnik kojem se dokument može dodijeliti.

**Glavni tok:**
1. Ovlašteni korisnik otvara dokument ili listu dokumenata.
2. Odabire opciju za dodjelu dokumenta.
3. Sistem prikazuje listu dostupnih korisnika.
4. Korisnik odabire korisnika kojem se dokument dodjeljuje.
5. Sistem kreira ili ažurira zapis zadatka.
6. Sistem evidentira dodjelu dokumenta.
7. Sistem po potrebi generiše obavještenje dodijeljenom korisniku.

**Alternativni tokovi:**
- Nema dostupnih korisnika za dodjelu.
- Dokument je već dodijeljen i ne može se ponovo dodijeliti bez promjene postojećeg zadatka.
- Korisnik nema odgovarajuća prava za dodjelu.
- Sistem ne uspije sačuvati dodjelu.

**Ishod:** Dokument je uspješno dodijeljen konkretnom korisniku i evidentiran u sistemu.

---

### UC-18 Pregled mojih zadataka

**Akter:** Operater / Računovođa / Osoba za odobravanje  
**Naziv use casea:** Pregled mojih zadataka  
**Kratak opis:** Korisnik pregledava dokumente i zadatke koji su mu dodijeljeni za dalju obradu ili odlučivanje.  
**Preduslovi:**  
- Korisnik je prijavljen u sistem.  
- Korisniku postoje dodijeljeni zadaci ili dokumenti na čekanju.

**Glavni tok:**
1. Korisnik otvara prikaz svojih zadataka.
2. Sistem prikazuje listu dodijeljenih zadataka.
3. Sistem prikazuje osnovne informacije o dokumentima i statusima zadataka.
4. Korisnik koristi filtere ili sortira listu po potrebi.
5. Korisnik odabire zadatak ili dokument za detaljan pregled.

**Alternativni tokovi:**
- Korisnik nema dodijeljenih zadataka.
- Sistem ne uspije učitati zadatke.

**Ishod:** Korisnik dobija pregled dokumenata i zadataka koji zahtijevaju njegovu akciju.

---

## 6. Administracija, nadzor i obavještenja

### UC-19 Upravljanje korisnicima i rolama

**Akter:** Administrator firme  
**Naziv use casea:** Upravljanje korisnicima i rolama  
**Kratak opis:** Administrator firme kreira korisnike, dodjeljuje im role i održava pristup sistemu.  
**Preduslovi:** Administrator firme je prijavljen u sistem i ima odgovarajuća prava pristupa.

**Glavni tok:**
1. Administrator otvara modul za upravljanje korisnicima.
2. Pregledava postojeće korisnike firme.
3. Kreira novog korisnika ili otvara postojećeg.
4. Unosi ili ažurira osnovne podatke korisnika.
5. Dodjeljuje korisničku rolu.
6. Sistem sprema promjene.
7. Sistem prikazuje ažuriranu listu korisnika.

**Alternativni tokovi:**
- Obavezni podaci nisu uneseni.
- Dodijeljena rola nije validna.
- Sistem ne uspije sačuvati korisnika ili promjene.

**Ishod:** Korisnički nalozi i role su ažurirani u skladu sa pravilima firme.

---

### UC-20 Deaktivacija korisničkog naloga

**Akter:** Administrator firme  
**Naziv use casea:** Deaktivacija korisničkog naloga  
**Kratak opis:** Administrator firme deaktivira korisnički nalog kako bi korisniku ukinuo pristup sistemu bez brisanja historijskih podataka.  
**Preduslovi:**  
- Administrator firme je prijavljen u sistem.  
- Korisnički nalog postoji u okviru firme.

**Glavni tok:**
1. Administrator otvara modul za upravljanje korisnicima.
2. Pregledava listu korisnika firme.
3. Odabire korisnika kojeg želi deaktivirati.
4. Sistem prikazuje detalje korisničkog naloga.
5. Administrator potvrđuje deaktivaciju.
6. Sistem mijenja status naloga u neaktivan.
7. Sistem sprema promjenu i prikazuje ažuriranu listu korisnika.

**Alternativni tokovi:**
- Administrator pokuša deaktivirati vlastiti jedini administratorski nalog.
- Odabrani korisnik ne postoji ili ne pripada firmi administratora.
- Sistem ne uspije sačuvati promjenu statusa.

**Ishod:** Korisnički nalog je deaktiviran i korisnik više ne može pristupiti sistemu.

---

### UC-21 Pregled statusa i izvještaja

**Akter:** Menadžment  
**Naziv use casea:** Pregled statusa i izvještaja  
**Kratak opis:** Menadžment pregledava stanje procesa i osnovne pokazatelje obrade dokumenata.  
**Preduslovi:** Korisnik sa menadžerskom ulogom je prijavljen u sistem.

**Glavni tok:**
1. Menadžment otvara dashboard ili izvještajni prikaz.
2. Sistem prikazuje broj dokumenata po statusu.
3. Sistem prikazuje osnovne operativne pokazatelje.
4. Menadžment analizira prikazane podatke.
5. Menadžment koristi pregled za donošenje operativnih odluka.

**Alternativni tokovi:**
- Nema dovoljno podataka za prikaz izvještaja.
- Korisnik nema dozvolu za pristup dashboardu.
- Sistem ne uspije učitati agregirane podatke.

**Ishod:** Menadžment ima pregled stanja procesa i osnovnih pokazatelja sistema.

---

### UC-22 Upravljanje obavještenjima

**Akter:** Računovođa / Osoba za odobravanje  
**Naziv use casea:** Upravljanje obavještenjima  
**Kratak opis:** Računovođa prima i pregledava obavještenja u slučaju da je dokument odbijen ili vraćen. Osoba za odobravanje prima i pregledava obavještenja kada dokument čeka njegovu akciju.  
**Preduslovi:**  
- Postoje događaji koji generišu obavještenja.  
- Korisnik je prijavljen.

**Glavni tok:**
1. Sistem generiše obavještenje.
2. Korisnik vidi indikator novih obavještenja.
3. Korisnik otvara obavještenja.
4. Odabire obavještenje.
5. Sistem ga preusmjerava na dokument.

**Alternativni tokovi:**
- Ako nema obavještenja, prikazuje se prazno stanje.

**Ishod:** Računovođa ili osoba za odobravanje su informisani i mogu reagovati na vrijeme.

---

### UC-23 Pregled audit loga

**Akter:** Administrator firme  
**Naziv use casea:** Pregled audit loga  
**Kratak opis:** Administrator pregledava audit trag aktivnosti nad dokumentima.  
**Preduslovi:** Postoje audit zapisi.

**Glavni tok:**
1. Administrator otvara audit prikaz.
2. Sistem prikazuje listu aktivnosti.
3. Administrator filtrira ili pregleda detalje.

**Alternativni tokovi:**
- Ako nema zapisa, prikazuje se prazno stanje.

**Ishod:** Administrator ima uvid u sve ključne aktivnosti.
