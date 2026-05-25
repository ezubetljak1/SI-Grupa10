# TestBook

## 1. Osnovne informacije 

Ovaj TestBook je organizovan prema vrstama i nivoima testiranja definisanim u Sprint 3 Test Strategy dokumentu.

Dokument posebno prati format evidentiranja rezultata: naziv/opis testa, ulazne podatke, očekivani rezultat, stvarni rezultat i status testa.

TestBook predstavlja živi dokument koji se ažurira nakon svakog završenog sprinta. Njegova svrha je interno evidentiranje provedenog testiranja i praćenje kvaliteta implementiranih funkcionalnosti kroz razvoj projekta.


---

## 2. Cilj dokumenta

Cilj ovog dokumenta je evidentirati načine testiranja funkcionalnosti implementiranih u sprintovima, u skladu sa prethodno definisanom testnom strategijom projekta.

Dokument obuhvata:

- automatizovane testove vidljive u kodu,
- backend/API testiranje,
- frontend/UI testiranje,
- manualno end-to-end testiranje,
- validacione scenarije,
- regresiono testiranje,
- deployment smoke testiranje,
- očekivane i stvarne ishode testiranja.

---

## 3. Korišteni nivoi i vrste testiranja

| Vrsta testiranja | Opis |
|---|---|
| Automatizovano smoke testiranje | Osnovna automatizovana provjera da se backend/frontend aplikacija može pokrenuti ili inicijalizovati bez greške. |
| Backend/API testiranje | Testiranje backend endpointa kroz Swagger/Postman i provjera odgovora API-ja. |
| Validaciono testiranje | Testiranje poslovnih pravila, obaveznih polja, dozvoljenih tipova fajlova, veličine fajla i nevalidnih ulaza. |
| Integration testiranje | Testiranje komunikacije između više komponenti sistema, npr. frontend-backend-baza-storage ili backend-OCR servis. |
| Frontend/UI testiranje | Ručno testiranje korisničkog interfejsa, prikaza stranica, formi, poruka i korisničkih akcija. |
| End-to-end testiranje | Testiranje kompletnog korisničkog toka kroz frontend, backend, bazu i storage. |
| Regresiono testiranje | Provjera da nove funkcionalnosti nisu pokvarile postojeće funkcionalnosti. |
| Deployment smoke testiranje | Osnovna provjera da aplikacija radi nakon pokretanja kroz Docker ili na serveru. |

---

## 4. Zajednički automatizovani smoke testovi vidljivi u kodu

Ova sekcija se odnosi na automatizovane testove koji nisu vezani isključivo za jedan sprint, nego služe kao osnovna provjera stabilnosti aplikacije tokom razvoja.

| ID testa | Lokacija | Vrsta testiranja | Šta testira | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| AUTO-001 | `Project/backend/src/test/.../DocflowBackendApplicationTests.java` | Automatizovano smoke testiranje | Učitavanje Spring Boot aplikacijskog konteksta | Backend aplikacija se može pokrenuti bez greške | Backend aplikacijski kontekst se uspješno učitao bez greške. | Pass |
| AUTO-002 | `Project/frontend/src/.../*.spec.ts` | Automatizovano smoke testiranje | Osnovno kreiranje frontend komponente/aplikacije | Angular test prolazi bez greške | Frontend aplikacija/komponenta se uspješno inicijalizovala i test je prošao bez greške. | Pass |


# Legenda statusa

| Status | Značenje |
|---|---|
| Pass | Test je uspješno prošao. |
| Fail | Test nije prošao i zahtijeva ispravku. |
| Blocked | Test trenutno nije moguće izvršiti zbog zavisnosti ili greške u okruženju. |
| Not Run | Test je definisan, ali još nije izvršen. |
| N/A | Test nije primjenjiv za dati scenario. |

---

# Sprint 5 – Testiranje funkcionalnosti za upload dokumenata

## 5.1 Funkcionalnosti koje se testiraju

U Sprintu 5 fokus testiranja je na funkcionalnostima vezanim za upload i upravljanje dokumentima.

Testirane funkcionalnosti uključuju:

- upload dokumenta,
- validaciju dokumenta,
- spremanje dokumenta na backend storage,
- spremanje metapodataka u bazu,
- prikaz liste dokumenata,
- prikaz detalja dokumenta,
- download ili preview dokumenta,
- error handling na frontend i backend strani,
- provjeru deployane aplikacije.

---

## 5.2 Backend/API testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Ulaz / koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S5-BE-001 | Backend/API testiranje | Upload validnog PDF dokumenta | Poslati multipart request sa validnim PDF fajlom | Dokument se uspješno uploaduje, metapodaci se spremaju u bazu, fajl se sprema na disk | Dokument je uspješno uploadovan, metapodaci su spremljeni u bazu, a fajl je spremljen na backend storage. | Pass |
| S5-BE-002 | Backend/API testiranje | Upload validne PNG slike | Poslati multipart request sa validnim PNG fajlom | Dokument se uspješno uploaduje | PNG dokument je uspješno uploadovan i evidentiran u sistemu. | Pass |
| S5-BE-003 | Backend/API testiranje | Upload validne JPG/JPEG slike | Poslati multipart request sa validnom slikom | Dokument se uspješno uploaduje | JPG/JPEG dokument je uspješno uploadovan i evidentiran u sistemu. | Pass |
| S5-BE-004 | Validaciono testiranje | Upload fajla nedozvoljenog tipa | Poslati fajl tipa `.txt`, `.exe` ili drugi nepodržani format | Backend vraća validacionu grešku | Backend je odbio fajl nedozvoljenog tipa i vratio validacionu grešku. | Pass |
| S5-BE-005 | Validaciono testiranje | Upload prevelikog fajla | Poslati fajl veći od dozvoljene maksimalne veličine | Backend vraća grešku za prekoračenje veličine fajla | Backend je odbio fajl veći od dozvoljenog limita i vratio grešku za prekoračenje veličine fajla. | Pass |
| S5-BE-006 | Validaciono testiranje | Upload bez fajla | Poslati upload request bez fajla | Backend vraća grešku da je fajl obavezan | Backend je vratio grešku da je fajl obavezan. | Pass |
| S5-BE-007 | Validaciono testiranje | Upload dokumenta bez naziva | Poslati request bez naziva dokumenta | Backend vraća validacionu grešku | Backend je vratio validacionu grešku za nedostajući naziv dokumenta. | Pass |
| S5-BE-008 | Validaciono testiranje | Upload dokumenta sa duplikatnim nazivom | Uploadati dokument sa nazivom koji već postoji za istu kompaniju | Backend odbija unos ili vraća odgovarajuću validacionu grešku | Backend je prepoznao duplikatni naziv dokumenta za istu kompaniju i vratio odgovarajuću validacionu grešku. | Pass |
| S5-BE-009 | Integration testiranje | Spremanje metapodataka u bazu | Uploadati validan dokument i provjeriti zapis u tabeli `document` | Baza sadrži ispravne metapodatke dokumenta | Nakon upload-a, baza sadrži ispravne metapodatke dokumenta. | Pass |
| S5-BE-010 | Integration testiranje | Spremanje fajla na disk | Uploadati validan dokument i provjeriti storage lokaciju | Fajl postoji na očekivanoj lokaciji na backend storage-u | Uploadani fajl postoji na očekivanoj storage lokaciji. | Pass |
| S5-BE-011 | Backend/API testiranje | Download dokumenta | Pozvati download endpoint za postojeći dokument | Backend vraća binarni sadržaj fajla | Backend je vratio binarni sadržaj postojećeg dokumenta. | Pass |
| S5-BE-012 | Backend/API testiranje | Download nepostojećeg dokumenta | Pozvati download endpoint sa nepostojećim ID-em | Backend vraća odgovarajuću grešku | Backend je vratio kontrolisanu grešku za nepostojeći dokument. | Pass |
| S5-BE-013 | Backend/API testiranje | Preview dokumenta | Pozvati preview endpoint za postojeći dokument | Backend vraća fajl ili preview sadržaj | Backend je vratio fajl/preview sadržaj za postojeći dokument. | Pass |
| S5-BE-014 | Automatizovano smoke testiranje | Pokretanje Spring Boot konteksta | Pokrenuti postojeći `contextLoads()` test | Aplikacijski kontekst se uspješno učita | Aplikacijski kontekst se uspješno učitao bez greške. | Pass |

---

## 5.3 Frontend/UI testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Koraci testiranja | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S5-UI-001 | Frontend/UI testiranje | Otvaranje upload stranice | Navigirati na stranicu za upload dokumenta | Stranica se učitava bez greške | Upload stranica se uspješno učitala bez greške. | Pass |
| S5-UI-002 | Frontend/UI testiranje | Prikaz upload forme | Otvoriti upload stranicu | Forma prikazuje potrebna polja i dugme za upload | Forma prikazuje potrebna polja i dugme za upload dokumenta. | Pass |
| S5-UI-003 | Frontend/UI testiranje | Odabir validnog fajla | Odabrati PDF/PNG/JPG fajl | Naziv fajla se prikazuje u formi | Nakon odabira fajla, naziv fajla je prikazan u formi. | Pass |
| S5-UI-004 | Frontend/UI testiranje | Upload validnog dokumenta | Popuniti formu i kliknuti upload | Prikazuje se success poruka i dokument se spašava | Dokument je uspješno uploadovan i prikazana je success poruka. | Pass |
| S5-UI-005 | Frontend/UI testiranje | Upload bez fajla | Kliknuti upload bez odabira fajla | Prikazuje se validaciona poruka | Prikazana je validaciona poruka da je potrebno odabrati fajl. | Pass |
| S5-UI-006 | Frontend/UI testiranje | Upload nedozvoljenog tipa fajla | Odabrati nepodržan fajl | Korisniku se prikazuje error poruka | Korisniku je prikazana error poruka za nepodržan tip fajla. | Pass |
| S5-UI-007 | Frontend/UI testiranje | Upload prevelikog fajla | Odabrati fajl veći od limita | Korisniku se prikazuje error poruka | Korisniku je prikazana error poruka za fajl veći od dozvoljenog limita. | Pass |
| S5-UI-008 | Frontend/UI testiranje | Prikaz liste dokumenata | Navigirati na listu dokumenata | Lista prikazuje uploadane dokumente | Lista dokumenata prikazuje uploadane dokumente. | Pass |
| S5-UI-009 | Frontend/UI testiranje | Empty state liste dokumenata | Otvoriti listu kada nema dokumenata | Prikazuje se odgovarajući empty state | Kada nema dokumenata, prikazuje se odgovarajući empty state. | Pass |
| S5-UI-010 | Frontend/UI testiranje | Prikaz detalja dokumenta | Kliknuti na postojeći dokument | Otvara se stranica sa detaljima dokumenta | Otvorena je stranica sa detaljima odabranog dokumenta. | Pass |
| S5-UI-011 | Frontend/UI testiranje | Sakrivanje internih polja | Otvoriti detalje dokumenta | Interna polja kao storage path i tehnički ID-evi nisu nepotrebno prikazani korisniku | Interna tehnička polja nisu nepotrebno prikazana korisniku. | Pass |
| S5-UI-012 | Frontend/UI testiranje | Download dokumenta iz UI-a | Kliknuti download dugme | Dokument se preuzima ili otvara u browseru | Dokument se uspješno preuzima ili otvara u browseru. | Pass |
| S5-UI-013 | Frontend/UI testiranje | Toastr success poruka | Uspješno uploadati dokument | Prikazuje se success toastr poruka | Nakon uspješnog upload-a prikazana je success toastr poruka. | Pass |
| S5-UI-014 | Frontend/UI testiranje | Toastr error poruka | Izazvati backend validacionu grešku | Prikazuje se error toastr poruka sa razumljivim tekstom | Nakon validacione greške prikazana je razumljiva error toastr poruka. | Pass |
| S5-UI-015 | Frontend/UI testiranje | Responsivnost upload stranice | Smanjiti širinu browsera ili koristiti mobile preview | Forma ostaje čitljiva i upotrebljiva | Upload forma ostaje čitljiva i upotrebljiva na manjim ekranima. | Pass |

---

## 5.4 End-to-end i regresiono testiranje

| ID testa | Vrsta testiranja | Scenario | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S5-E2E-001 | End-to-end testiranje | Kompletan upload flow | Otvoriti frontend, uploadati validan dokument, provjeriti listu i detalje | Dokument je vidljiv u listi i detaljima nakon upload-a | Dokument je nakon upload-a vidljiv u listi i na stranici detalja. | Pass |
| S5-E2E-002 | End-to-end testiranje | Upload + backend storage + baza | Uploadati dokument kroz UI, provjeriti backend storage i bazu | Fajl postoji na disku, a metapodaci postoje u bazi | Fajl je pronađen na storage lokaciji, a metapodaci su pronađeni u bazi. | Pass |
| S5-E2E-003 | End-to-end testiranje | Upload + download | Uploadati dokument, zatim kliknuti download | Preuzeti dokument odgovara uploadanom dokumentu | Preuzeti dokument odgovara prethodno uploadanom dokumentu. | Pass |
| S5-E2E-004 | Regresiono testiranje | Provjera postojećih stranica nakon dodavanja upload funkcionalnosti | Otvoriti postojeće stranice aplikacije | Postojeće stranice se i dalje učitavaju bez grešaka | Postojeće stranice se i dalje učitavaju bez grešaka nakon dodavanja upload funkcionalnosti. | Pass |

---

## 5.5 Manualno API testiranje kroz Swagger/Postman

| ID testa | Vrsta testiranja | Alat | Endpoint / funkcionalnost | Koraci | Očekivani rezultat | Stvarni rezultat | Status |
|---|---|---|---|---|---|---|---|
| S5-API-001 | Backend/API testiranje | Swagger | Upload dokumenta | Poslati validan multipart request | Dokument se uspješno kreira | Dokument je uspješno kreiran putem API-ja. | Pass |
| S5-API-002 | Validaciono testiranje | Swagger | Upload sa nevalidnim fajlom | Poslati fajl nedozvoljenog tipa | Vraća se validaciona greška | API je vratio validacionu grešku za nevalidan fajl. | Pass |
| S5-API-003 | Backend/API testiranje | Swagger | Download dokumenta | Pozvati download endpoint | Vraća se fajl | API je vratio fajl za download. | Pass |
| S5-API-004 | Backend/API testiranje | Swagger | Preview dokumenta | Pozvati preview endpoint | Vraća se fajl ili preview sadržaj | API je vratio fajl ili preview sadržaj. | Pass |

---

## 5.6 Deployment smoke testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S5-DEP-001 | Deployment smoke testiranje | Lokalno Docker okruženje | Pokretanje baze | Pokrenuti PostgreSQL container | Baza se uspješno pokreće | PostgreSQL container se uspješno pokrenuo. | Pass |
| S5-DEP-002 | Deployment smoke testiranje | Lokalno/Server | Pokretanje backend containera | Pokrenuti backend container | Backend se pokreće bez greške | Backend container se uspješno pokrenuo bez greške. | Pass |
| S5-DEP-003 | Deployment smoke testiranje | Lokalno/Server | Pokretanje frontend containera | Pokrenuti frontend container | Frontend je dostupan u browseru | Frontend container se uspješno pokrenuo i aplikacija je dostupna u browseru. | Pass |
| S5-DEP-004 | Deployment smoke testiranje | Server | Komunikacija FE-BE | Otvoriti frontend i izvršiti akciju koja poziva backend | Frontend uspješno komunicira sa backendom | Frontend je uspješno komunicirao sa backendom. | Pass |
| S5-DEP-005 | Deployment smoke testiranje | Server | Upload storage | Uploadati dokument na deployanoj aplikaciji | Fajl se sprema na server storage lokaciju | Uploadani fajl je spremljen na server storage lokaciju. | Pass |

---

# Zaključak testiranja

Tokom Sprinta 5 testirane su funkcionalnosti vezane za upload dokumenata, validaciju fajlova, pohranu dokumenata, prikaz liste i detalja dokumenata, download/preview dokumenta, error handling i osnovni deployment flow.

Zaključak:

- Upload validnih dokumenata je testiran kroz API i UI.
- Validacioni scenariji su testirani kroz negativne testove.
- Provjereno je spremanje fajla na disk i metapodataka u bazu.
- Testirani su osnovni korisnički tokovi kroz frontend.
- Testirani su download i preview scenariji.
- Izvršeno je regresiono testiranje postojećih funkcionalnosti.
- Svi evidentirani testovi za Sprint 5 imaju status `Pass`.

---

# Sprint 6 – Testiranje OCR i AI ekstrakcije podataka

## 6.1 Funkcionalnosti koje se testiraju

U Sprintu 6 fokus testiranja je na OCR/AI funkcionalnostima i ekstrakciji podataka iz dokumenata.

Testirane funkcionalnosti uključuju:

- pokretanje ekstrakcije nad uploadanim dokumentom,
- integraciju sa Google Document AI servisom,
- obradu OCR rezultata,
- mapiranje ekstraktovanih vrijednosti,
- spremanje rezultata ekstrakcije,
- prikaz ekstraktovanih podataka na frontend strani,
- handling grešaka u slučaju neuspjele ekstrakcije,
- testiranje ponašanja sistema bez validnog OCR rezultata.

---

## 6.2 Backend/API testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Ulaz / koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S6-BE-001 | Backend/API testiranje | Pokretanje ekstrakcije za postojeći dokument | Pozvati extraction endpoint za dokument koji postoji | Backend pokreće proces ekstrakcije | Backend je uspješno pokrenuo proces ekstrakcije za postojeći dokument. | Pass |
| S6-BE-002 | Backend/API testiranje | Pokretanje ekstrakcije za nepostojeći dokument | Pozvati extraction endpoint sa nepostojećim ID-em | Backend vraća grešku da dokument nije pronađen | Backend je vratio kontrolisanu grešku da dokument nije pronađen. | Pass |
| S6-BE-003 | Integration testiranje | Komunikacija sa OCR servisom | Pokrenuti ekstrakciju nad validnim dokumentom | OCR servis vraća rezultat ili se greška pravilno obradi | Komunikacija sa OCR servisom je izvršena, a rezultat ili greška su pravilno obrađeni. | Pass |
| S6-BE-004 | Integration testiranje | Obrada OCR rezultata | Pokrenuti ekstrakciju nad dokumentom koji sadrži čitljive podatke | Backend parsira OCR rezultat | Backend je uspješno parsirao OCR rezultat. | Pass |
| S6-BE-005 | Integration testiranje | Spremanje ekstraktovanih podataka | Pokrenuti ekstrakciju i provjeriti bazu | Ekstraktovani podaci su spremljeni uz odgovarajući dokument | Ekstraktovani podaci su spremljeni uz odgovarajući dokument u bazi. | Pass |
| S6-BE-006 | Validaciono testiranje | Ekstrakcija nad dokumentom bez fajla | Pokrenuti ekstrakciju za dokument bez validnog storage fajla | Backend vraća odgovarajuću grešku | Backend je vratio odgovarajuću grešku za dokument bez validnog storage fajla. | Pass |
| S6-BE-007 | Backend/API testiranje | Neuspješan odgovor OCR servisa | Simulirati ili izazvati grešku OCR servisa | Backend ne ruši aplikaciju i vraća kontrolisanu grešku | Backend se nije srušio i vratio je kontrolisanu grešku. | Pass |
| S6-BE-008 | Backend/API testiranje | Nedostajuća konfiguracija za OCR servis | Pokrenuti aplikaciju bez ispravnih OCR kredencijala | Sistem vraća jasnu grešku ili onemogućava ekstrakciju | Sistem je vratio jasnu grešku ili onemogućio ekstrakciju bez rušenja aplikacije. | Pass |
| S6-BE-009 | Regresiono testiranje | Upload dokumenta nakon dodavanja OCR funkcionalnosti | Uploadati novi dokument | Upload funkcionalnost i dalje radi kao u Sprintu 5 | Upload funkcionalnost i dalje radi nakon dodavanja OCR funkcionalnosti. | Pass |
| S6-BE-010 | Automatizovano smoke testiranje | Pokretanje aplikacije nakon OCR integracije | Pokrenuti backend aplikaciju/testove | Aplikacija se pokreće bez greške | Backend aplikacija se pokrenula bez greške nakon OCR integracije. | Pass |

---

## 6.3 Frontend/UI testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Koraci testiranja | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S6-UI-001 | Frontend/UI testiranje | Prikaz opcije za ekstrakciju | Otvoriti detalje dokumenta | Korisnik vidi opciju za pokretanje ekstrakcije ako je primjenjivo | Korisniku je prikazana opcija za pokretanje ekstrakcije na detaljima dokumenta. | Pass |
| S6-UI-002 | Frontend/UI testiranje | Pokretanje ekstrakcije iz UI-a | Kliknuti dugme za ekstrakciju | Sistem pokreće ekstrakciju i prikazuje loading/status | Sistem je pokrenuo ekstrakciju i prikazao loading/status korisniku. | Pass |
| S6-UI-003 | Frontend/UI testiranje | Prikaz uspješne ekstrakcije | Pokrenuti ekstrakciju nad validnim dokumentom | Ekstraktovani podaci se prikazuju korisniku | Ekstraktovani podaci su prikazani korisniku. | Pass |
| S6-UI-004 | Frontend/UI testiranje | Prikaz greške ekstrakcije | Pokrenuti ekstrakciju koja vraća grešku | Korisniku se prikazuje jasna error poruka | Korisniku je prikazana jasna error poruka za neuspješnu ekstrakciju. | Pass |
| S6-UI-005 | Frontend/UI testiranje | Prikaz praznih/neprepoznatih polja | Pokrenuti ekstrakciju nad dokumentom sa nedovoljno jasnim sadržajem | UI prikazuje da određeni podaci nisu pronađeni | UI je prikazao da određeni podaci nisu pronađeni ili nisu prepoznati. | Pass |
| S6-UI-006 | Frontend/UI testiranje | Refresh detalja nakon ekstrakcije | Nakon ekstrakcije osvježiti ili ponovo otvoriti detalje dokumenta | Ekstraktovani podaci ostaju prikazani | Ekstraktovani podaci su ostali prikazani nakon ponovnog otvaranja detalja dokumenta. | Pass |
| S6-UI-007 | Frontend/UI testiranje | Onemogućavanje duplog klika | Kliknuti dugme za ekstrakciju više puta brzo | Sistem ne šalje nepotrebne duple zahtjeve ili UI sprječava višestruko pokretanje | UI je spriječio nepotrebno višestruko pokretanje ili sistem nije kreirao nekontrolisane duple zahtjeve. | Pass |
| S6-UI-008 | Frontend/UI testiranje | Success toastr za ekstrakciju | Uspješno završiti ekstrakciju | Prikazuje se success toastr poruka | Nakon uspješne ekstrakcije prikazana je success toastr poruka. | Pass |
| S6-UI-009 | Frontend/UI testiranje | Error toastr za ekstrakciju | Izazvati grešku ekstrakcije | Prikazuje se error toastr poruka | Nakon greške ekstrakcije prikazana je error toastr poruka. | Pass |
| S6-UI-010 | Frontend/UI testiranje | Responsivnost prikaza ekstraktovanih podataka | Otvoriti detalje dokumenta na manjoj širini ekrana | Podaci ostaju pregledni i čitljivi | Ekstraktovani podaci su ostali pregledni i čitljivi na manjim ekranima. | Pass |

---

## 6.4 End-to-end i regresiono testiranje

| ID testa | Vrsta testiranja | Scenario | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S6-E2E-001 | End-to-end testiranje | Upload + OCR ekstrakcija | Uploadati dokument, otvoriti detalje, pokrenuti ekstrakciju | Dokument se uploaduje, ekstrakcija se izvršava, rezultati se prikazuju | Dokument je uploadovan, ekstrakcija je izvršena, a rezultati su prikazani korisniku. | Pass |
| S6-E2E-002 | End-to-end testiranje | OCR rezultat spremljen u bazu | Pokrenuti ekstrakciju i provjeriti bazu | Ekstraktovani podaci su trajno spremljeni | Ekstraktovani podaci su trajno spremljeni u bazu. | Pass |
| S6-E2E-003 | End-to-end testiranje | Ponovno otvaranje dokumenta nakon ekstrakcije | Pokrenuti ekstrakciju, napustiti stranicu i ponovo otvoriti detalje | Ranije ekstraktovani podaci su i dalje dostupni | Ranije ekstraktovani podaci su i dalje dostupni nakon ponovnog otvaranja dokumenta. | Pass |
| S6-E2E-004 | End-to-end testiranje | Neuspjela ekstrakcija bez rušenja aplikacije | Izazvati neuspješnu ekstrakciju | Aplikacija ostaje stabilna i korisnik dobija jasnu poruku | Aplikacija je ostala stabilna i korisnik je dobio jasnu poruku o grešci. | Pass |
| S6-E2E-005 | Regresiono testiranje | Provjera Sprint 5 funkcionalnosti nakon OCR integracije | Testirati upload, listu, detalje i download | Funkcionalnosti iz Sprinta 5 i dalje rade | Upload, lista, detalji i download i dalje rade nakon OCR integracije. | Pass |

---

## 6.5 Manualno API testiranje kroz Swagger/Postman

| ID testa | Vrsta testiranja | Alat | Endpoint / funkcionalnost | Koraci | Očekivani rezultat | Stvarni rezultat | Status |
|---|---|---|---|---|---|---|---|
| S6-API-001 | Backend/API testiranje | Swagger | Pokretanje ekstrakcije | Pozvati extraction endpoint | Ekstrakcija se pokreće | Ekstrakcija se uspješno pokrenula putem API-ja. | Pass |
| S6-API-002 | Backend/API testiranje | Swagger | Ekstrakcija za nepostojeći dokument | Pozvati endpoint sa nepostojećim ID-em | Vraća se kontrolisana greška | API je vratio kontrolisanu grešku za nepostojeći dokument. | Pass |
| S6-API-003 | Integration testiranje | Swagger | Ekstrakcija validnog dokumenta | Pozvati extraction endpoint za validan dokument | OCR rezultat se obrađuje i vraća/sprema | OCR rezultat je obrađen i rezultat je vraćen ili spremljen u sistem. | Pass |
| S6-API-004 | Regresiono testiranje | Swagger | Upload dokumenta nakon OCR integracije | Poslati validan multipart upload request | Dokument se uspješno kreira | Dokument se uspješno kreirao nakon OCR integracije. | Pass |

---

## 6.6 Deployment smoke testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S6-DEP-001 | Deployment smoke testiranje | Lokalno/Server | Pokretanje backend containera nakon OCR integracije | Pokrenuti backend container | Backend se pokreće bez greške | Backend container se uspješno pokrenuo nakon OCR integracije. | Pass |
| S6-DEP-002 | Deployment smoke testiranje | Lokalno/Server | Pokretanje frontend containera nakon OCR UI izmjena | Pokrenuti frontend container | Frontend je dostupan u browseru | Frontend container se uspješno pokrenuo i aplikacija je dostupna u browseru. | Pass |
| S6-DEP-003 | Deployment smoke testiranje | Server | Komunikacija FE-BE za OCR funkcionalnost | Otvoriti frontend i pokrenuti akciju ekstrakcije | Frontend uspješno komunicira sa backendom | Frontend je uspješno poslao zahtjev backendu za OCR funkcionalnost. | Pass |
| S6-DEP-004 | Deployment smoke testiranje | Server | OCR konfiguracija | Pokrenuti ekstrakciju na serveru | Sistem koristi ispravnu konfiguraciju ili vraća jasnu grešku | Sistem koristi OCR konfiguraciju ili vraća jasnu kontrolisanu grešku bez rušenja aplikacije. | Pass |
| S6-DEP-005 | Deployment smoke testiranje | Server | Regresiona provjera upload storage-a | Uploadati dokument na deployanoj aplikaciji | Fajl se sprema na server storage lokaciju | Fajl se uspješno sprema na server storage lokaciju nakon Sprint 6 izmjena. | Pass |

---

## Zaključak testiranja

Tokom Sprinta 6 testirane su funkcionalnosti vezane za OCR/AI ekstrakciju podataka iz dokumenata, uključujući pokretanje ekstrakcije, obradu rezultata, prikaz rezultata, error handling i regresionu provjeru Sprint 5 funkcionalnosti.

Zaključak:

- Testirano je pokretanje ekstrakcije nad postojećim dokumentima.
- Testirano je ponašanje sistema u slučaju nepostojećeg dokumenta ili greške OCR servisa.
- Provjeren je prikaz ekstraktovanih podataka na frontend strani.
- Izvršeno je end-to-end testiranje toka upload → ekstrakcija → prikaz rezultata.
- Izvršeno je regresiono testiranje Sprint 5 funkcionalnosti nakon dodavanja OCR funkcionalnosti.
- Svi evidentirani testovi za Sprint 6 imaju status `Pass`.

---

# Sprint 7 – Testiranje editovanja, validacije i potvrde ekstrakcije

## 7.1 Funkcionalnosti koje se testiraju

U Sprintu 7 fokus testiranja je na ručnom editovanju izdvojenih OCR/AI polja, validaciji vrijednosti i potvrdi ekstrakcije nakon korisničkog review-a.

Testirane funkcionalnosti uključuju:

- ručno editovanje jednog extraction field-a,
- validaciju vrijednosti za datume i numerička polja,
- potvrdu ekstrakcije kroz confirm endpoint,
- promjenu statusa dokumenta u `READY_FOR_APPROVAL`,
- blokiranje potvrde ako postoje nepregledana low-confidence polja,
- automatsko kreiranje placeholder redova za required polja koja OCR nije vratio,
- ručno popunjavanje placeholder required polja,
- frontend prikaz review statusa polja,
- toastr poruke za validation greške,
- regresionu provjeru retry extraction flow-a nakon uvođenja placeholder logike,
- deployment smoke provjeru backend/frontend containera i nove `is_placeholder` kolone.

---

## 7.2 Backend/API i integracijsko testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Ulaz / koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S7-BE-001 | Integration testiranje | Uspješan edit jednog extraction field-a | Postojeći dokument sa pokrenutom ekstrakcijom; poslati `PATCH /api/extractions/{extractionId}/fields/{fieldId}` sa body `{ "value": "125.50" }` | Backend vraća `200 OK`, ažuriranu vrijednost i `corrected = true` | Endpoint je vratio `200 OK`; vrijednost polja je promijenjena, a polje je označeno kao ručno korigovano | Pass |
| S7-BE-002 | Integration testiranje | Zaštita od editovanja polja koje ne pripada datoj ekstrakciji | `fieldId` iz prve ekstrakcije se šalje uz `extractionId` druge ekstrakcije | Backend vraća `404 NOT_FOUND`, a postojeća vrijednost polja ostaje nepromijenjena | Endpoint je vratio `404 NOT_FOUND`; prethodna vrijednost i status korekcije nisu promijenjeni | Pass |
| S7-BE-003 | Integration testiranje | Potvrda ekstrakcije nakon ručne korekcije | Dokument sa pokrenutom ekstrakcijom i prethodno editovanim poljem | Confirm endpoint vraća `200 OK`, dokument prelazi u `READY_FOR_APPROVAL`, a prethodne korekcije ostaju sačuvane | Endpoint je vratio `200 OK`; dokument je označen kao `READY_FOR_APPROVAL`, a editovana vrijednost i `corrected = true` su ostali sačuvani | Pass |
| S7-BE-004 | Backend/API testiranje | Potvrda ekstrakcije prije pokretanja OCR/AI obrade | Uploadovan dokument bez kreirane ekstrakcije; pozvati confirm endpoint | Backend vraća `404 NOT_FOUND` jer ne postoji extraction rezultat | Endpoint je vratio `404 NOT_FOUND`, jer za dokument još ne postoji extraction rezultat | Pass |
| S7-BE-005 | Validaciono testiranje | Odbijanje prazne vrijednosti za decimalno polje | Postojeći `total_amount` field; poslati body `{ "value": "" }` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_AMOUNT_INVALID`; prethodna vrijednost ostaje nepromijenjena | Endpoint je vratio `400 Bad Request`; vrijednost polja nije promijenjena | Pass |
| S7-BE-006 | Validaciono testiranje | Odbijanje nenumeričke vrijednosti za decimalno polje | Postojeći `total_amount` field; poslati body `{ "value": "abc" }` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_AMOUNT_INVALID`; prethodna vrijednost ostaje nepromijenjena | Endpoint je vratio `400 Bad Request`; vrijednost polja nije promijenjena | Pass |
| S7-BE-007 | Validaciono testiranje | Odbijanje decimalne vrijednosti sa previše decimala | Postojeći `total_amount` field; poslati body `{ "value": "117.001" }` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_AMOUNT_INVALID`; prethodna vrijednost ostaje nepromijenjena | Endpoint je vratio `400 Bad Request`; vrijednost polja nije promijenjena | Pass |
| S7-BE-008 | Validaciono testiranje | Odbijanje negativne vrijednosti za decimalno polje | Postojeći `total_amount` field; poslati body `{ "value": "-1.00" }` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_AMOUNT_INVALID`; negativna vrijednost se ne sprema | Endpoint je vratio `400 Bad Request`; negativna vrijednost nije prihvaćena | Pass |
| S7-BE-009 | Validaciono testiranje | Prihvatanje zareza kao decimalnog separatora | Postojeći `total_amount` field; poslati body `{ "value": "125,50" }` | Backend vraća `200 OK`, vrijednost se ažurira i polje dobija `corrected = true` | Endpoint je vratio `200 OK`; vrijednost `125,50` je prihvaćena i polje je označeno kao korigovano | Pass |
| S7-BE-010 | Validaciono testiranje | Odbijanje nevalidnog formata datuma pri editovanju | Postojeći `invoice_date` field; poslati body `{ "value": "06-05-2026" }` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_DATE_FORMAT_INVALID`; prethodna vrijednost ostaje nepromijenjena | Endpoint je vratio `400 Bad Request`; nevalidan format datuma nije prihvaćen | Pass |
| S7-BE-011 | Validaciono testiranje | Prihvatanje podržanog formata datuma pri editovanju | Postojeći `invoice_date` field; poslati body `{ "value": "06.05.2026" }` | Backend vraća `200 OK`, vrijednost se ažurira i polje dobija `corrected = true` | Endpoint je vratio `200 OK`; podržani format datuma je prihvaćen | Pass |
| S7-BE-012 | Validaciono testiranje | Odbijanje nekonzistentnog total amount iznosa | Ekstrakcija sadrži `net_amount = 100.00`, `vat_amount = 17.00`, `total_amount = 117.00`; pokušati update `total_amount` na `200.00` | Backend vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_AMOUNT_INCONSISTENT`; prethodna vrijednost ostaje nepromijenjena | Endpoint je vratio `400 Bad Request`; nekonzistentan total amount nije prihvaćen | Pass |
| S7-BE-013 | Validaciono testiranje | Blokiranje confirma kada postoji low-confidence polje koje nije ručno pregledano | Dokument sa ekstrakcijom gdje `total_amount` ima confidence score ispod praga i nije `corrected` | Confirm endpoint vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_LOW_CONFIDENCE`; dokument ostaje u statusu `EXTRACTED` | Confirm je odbijen; dokument je ostao u statusu `EXTRACTED` | Pass |
| S7-BE-014 | Integration testiranje | Uspješan confirm nakon ručne korekcije low-confidence polja | Low-confidence `total_amount` polje se ručno edituje, zatim se poziva confirm endpoint | Confirm endpoint vraća `200 OK`, a dokument prelazi u `READY_FOR_APPROVAL` | Confirm je uspješno izvršen nakon ručnog pregleda/korekcije polja | Pass |
| S7-BE-015 | Validaciono testiranje | Blokiranje confirma kada nedostaje required polje | OCR rezultat ne sadrži jedno required invoice polje | Confirm endpoint vraća `400 Bad Request` sa kodom `EXTRACTION_REQUIRED_FIELD_MISSING`; dokument ostaje u statusu `EXTRACTED` | Confirm je odbijen jer required polje nije validno popunjeno | Pass |
| S7-BE-016 | Validaciono testiranje | Blokiranje confirma kada je required polje prazno | OCR rezultat sadrži `supplier_name` kao prazan string | Confirm endpoint vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_EMPTY`; dokument ostaje u statusu `EXTRACTED` | Confirm je odbijen jer required polje ima praznu vrijednost | Pass |
| S7-BE-017 | Integration testiranje | Uspješan confirm kada su required polja validno popunjena | Dokument sa ekstrakcijom koja sadrži sva required invoice polja | Confirm endpoint vraća `200 OK`, a dokument prelazi u status `READY_FOR_APPROVAL` | Confirm je uspješno izvršen; dokument je dobio status `READY_FOR_APPROVAL` | Pass |
| S7-BE-018 | Validaciono testiranje | Blokiranje confirma zbog nevalidnog formata datuma | OCR rezultat sadrži `invoice_date = 06-05-2026` | Confirm endpoint vraća `400 Bad Request` sa kodom `EXTRACTION_FIELD_DATE_FORMAT_INVALID`; dokument ostaje u statusu `EXTRACTED` | Confirm je odbijen zbog nevalidnog formata datuma | Pass |
| S7-BE-019 | Integration testiranje | Normalizacija naziva polja pri confirm validaciji | OCR rezultat sadrži nazive polja različitog case-a i dodatne razmake, npr. ` Supplier_Name `, `Invoice_ID`, `INVOICE_DATE` | Backend normalizuje nazive polja i confirm prolazi ako su vrijednosti validne | Confirm je uspješno izvršen i dokument je prešao u `READY_FOR_APPROVAL` | Pass |
| S7-BE-020 | Integration testiranje | Kreiranje placeholder redova za missing required polja | OCR rezultat sadrži samo `invoice_id`, `invoice_date` i `total_amount`, bez `supplier_name` i `currency` | Backend nakon ekstrakcije automatski dodaje `supplier_name` i `currency` kao placeholder polja sa `placeholder = true` | Backend je kreirao placeholder redove za missing required polja | Pass |
| S7-BE-021 | Validaciono testiranje | Blokiranje confirma dok postoje required placeholder polja | Dokument ima ekstrakciju u kojoj `supplier_name` i `currency` postoje kao placeholder polja | Confirm endpoint vraća `400 Bad Request` sa kodom `EXTRACTION_REQUIRED_FIELD_MISSING`; dokument ostaje u statusu `EXTRACTED` | Confirm je odbijen dok placeholder required polja nisu ručno popunjena | Pass |
| S7-BE-022 | Integration testiranje | Editovanje placeholder polja | Placeholder polje `supplier_name` se edituje vrijednošću `Manual Supplier d.o.o.` | Backend vraća `200 OK`; polje dobija novu vrijednost, `corrected = true` i `placeholder = false` | Placeholder polje je uspješno popunjeno i više nije označeno kao placeholder | Pass |
| S7-BE-023 | Integration testiranje | Uspješan confirm nakon popunjavanja svih placeholder required polja | Dokument ima placeholder polja `supplier_name` i `currency`; oba polja se ručno popunjavaju prije confirma | Confirm endpoint vraća `200 OK`, dokument prelazi u `READY_FOR_APPROVAL`, a broj placeholder polja postaje 0 | Confirm je uspješno izvršen nakon popunjavanja placeholder polja | Pass |
| S7-BE-024 | Regresiono testiranje | Retry ekstrakcije nakon uvođenja placeholder logike | Dokument sa postojećom ekstrakcijom; retry OCR rezultat vraća samo dio required polja | Retry koristi isti extraction zapis, zamjenjuje prethodna polja novim rezultatom i dodaje missing required polja kao placeholder redove | Retry je zadržao isti extraction ID, zamijenio polja i dodao potrebne placeholder redove | Pass |

---

## 7.3 Frontend/UI testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Koraci testiranja | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S7-UI-001 | Frontend/UI testiranje | Prikaz Confirm extraction dugmeta | Otvoriti detalje dokumenta koji ima status `EXTRACTED` | Prikazuje se dugme `Confirm extraction`, odvojeno od akcija za retry/refresh | Dugme za confirm je prikazano na document detail stranici i dostupno nakon ekstrakcije | Pass |
| S7-UI-002 | Frontend/UI testiranje | Prikaz placeholder required polja u tabeli | Pokrenuti ekstrakciju nad dokumentom gdje OCR ne vrati sva required polja | Missing required polja se prikazuju u tabeli i jasno su označena kao polja koja korisnik mora popuniti | Placeholder polja su prikazana i označena kao `Missing required` / `Required` | Pass |
| S7-UI-003 | Frontend/UI testiranje | Ručno popunjavanje placeholder polja kroz UI | Kliknuti edit na placeholder polje, unijeti vrijednost i potvrditi izmjenu | Vrijednost se spašava, polje prestaje biti označeno kao placeholder i dobija status ručno pregledanog/korigovanog polja | Nakon editovanja, placeholder oznaka je uklonjena i polje je označeno kao pregledano/korigovano | Pass |
| S7-UI-004 | Frontend/UI testiranje | Pokušaj confirma dok postoje placeholder required polja | Kliknuti `Confirm extraction` dok missing required polja nisu popunjena | Confirm se ne izvršava; korisniku se prikazuje toastr upozorenje, a dokument ostaje u statusu `EXTRACTED` | Confirm je blokiran i prikazana je razumljiva toastr poruka | Pass |
| S7-UI-005 | Frontend/UI testiranje | Prikaz low-confidence polja u tabeli | Otvoriti ekstrakciju koja sadrži polje sa confidence score ispod praga | Polje se označava kao `Review needed` ili ekvivalentno upozorenje u tabeli | Low-confidence polje je jasno označeno kao polje koje zahtijeva review | Pass |
| S7-UI-006 | Frontend/UI testiranje | Pokušaj confirma bez review-a low-confidence polja | Kliknuti `Confirm extraction` dok postoji low-confidence polje koje nije ručno editovano/pregledano | Confirm se ne izvršava; korisniku se prikazuje toastr upozorenje da treba pregledati low-confidence polja | Confirm je blokiran i korisniku je prikazana odgovarajuća toastr poruka | Pass |
| S7-UI-007 | Frontend/UI testiranje | Prikaz validacione greške za nevalidan format polja | Pokušati editovati date/amount polje nevalidnom vrijednošću | UI ili backend validacija odbija vrijednost i korisniku prikazuje razumljivu poruku; prethodna vrijednost ostaje sačuvana | Nevalidna vrijednost nije sačuvana, a korisniku je prikazana validaciona poruka | Pass |
| S7-UI-008 | Frontend/UI testiranje | Uspješan confirm nakon popunjavanja i review-a svih problematičnih polja | Popuniti placeholder required polja, editovati low-confidence polja i kliknuti `Confirm extraction` | Confirm prolazi, prikazuje se success toastr, a dokument dobija status `READY_FOR_APPROVAL` | Confirm je uspješno izvršen i dokument je dobio status `READY_FOR_APPROVAL` | Pass |
| S7-UI-009 | Frontend/UI testiranje | Prikaz statusa `READY_FOR_APPROVAL` nakon confirma | Nakon uspješnog confirma otvoriti listu dokumenata ili detalje dokumenta | Status dokumenta se prikazuje kao `READY_FOR_APPROVAL` / Ready for approval badge | Status je prikazan kao spreman za odobrenje i vizuelno je označen odgovarajućim badge-om | Pass |
| S7-UI-010 | Frontend/UI testiranje | Prikaz extraction tabele nakon prelaska u `READY_FOR_APPROVAL` | Confirmati ekstrakciju, zatim refreshovati ili ponovo otvoriti detalje dokumenta | Extracted fields ostaju dostupni za pregled i nakon confirma | Tabela izdvojenih polja ostaje prikazana nakon što dokument pređe u `READY_FOR_APPROVAL` | Pass |

---

## 7.4 Manualno API testiranje kroz Swagger/Postman

| ID testa | Vrsta testiranja | Alat | Endpoint / funkcionalnost | Koraci | Očekivani rezultat | Stvarni rezultat | Status |
|---|---|---|---|---|---|---|---|
| S7-API-001 | Backend/API testiranje | Swagger | Upload dokumenta i pokretanje ekstrakcije kao priprema za edit | Pozvati `POST /api/documents/upload`, zatim `POST /api/documents/{documentId}/extraction` | Dokument se uspješno uploaduje, ekstrakcija se kreira i response sadrži `extractionId` i listu izdvojenih polja | Dokument je uspješno uploadovan, ekstrakcija je pokrenuta i polja su vraćena u response-u | Pass |
| S7-API-002 | Backend/API testiranje | Swagger | Ručna izmjena izdvojenog podatka | Pozvati `PATCH /api/extractions/{extractionId}/fields/{fieldId}` sa body `{ "value": "125.50" }` | Backend vraća ažurirano polje sa novom vrijednošću i `corrected = true` | Polje je uspješno ažurirano, nova vrijednost je vraćena u response-u i `corrected` je postavljen na `true` | Pass |
| S7-API-003 | Backend/API testiranje | Swagger | Provjera da je izmjena trajno sačuvana | Pozvati `GET /api/extractions/{extractionId}/fields` nakon PATCH zahtjeva | Lista polja sadrži prethodno ažuriranu vrijednost i `corrected = true` | GET endpoint prikazuje novu vrijednost i potvrđuje da je izmjena sačuvana | Pass |
| S7-API-004 | Backend/API testiranje | Swagger | Potvrda ekstrakcije nakon pregleda/korekcije | Pozvati `POST /api/documents/{documentId}/extraction/confirm` | Backend potvrđuje ekstrakciju bez ponovnog OCR procesa i dokument prelazi u status `READY_FOR_APPROVAL` | Confirm endpoint je uspješno izvršen, prethodne korekcije nisu izgubljene | Pass |
| S7-API-005 | Backend/API testiranje | Swagger | Provjera statusa dokumenta nakon potvrde ekstrakcije | Pozvati `GET /api/documents/{documentId}` nakon confirm zahtjeva | Dokument ima status `READY_FOR_APPROVAL` | Status dokumenta je `READY_FOR_APPROVAL` | Pass |

---

## 7.5 End-to-end i regresiono testiranje

| ID testa | Vrsta testiranja | Scenario | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S7-E2E-001 | End-to-end testiranje | Kompletan flow: upload → extraction → placeholder edit → confirm | Uploadati dokument, pokrenuti ekstrakciju, popuniti missing required polja, pregledati low-confidence polja i potvrditi ekstrakciju | Kompletan tok prolazi bez greške; dokument završava u statusu `READY_FOR_APPROVAL` | Kompletan tok je uspješno izvršen kroz API/UI i dokument je spreman za odobrenje | Pass |
| S7-E2E-002 | End-to-end testiranje | Confirm blokiran dok korisnik ne završi review | Pokrenuti ekstrakciju nad dokumentom sa placeholder ili low-confidence poljima, pokušati confirm, zatim popuniti/pregledati polja i ponoviti confirm | Prvi confirm je blokiran validacijom; nakon ručnog review-a confirm prolazi | Sistem je blokirao nepotpun review, a nakon popunjavanja/pregleda polja confirm je uspješno prošao | Pass |
| S7-E2E-003 | Regresiono testiranje | Provjera Sprint 6 extraction funkcionalnosti nakon dodavanja confirm flow-a | Testirati run extraction, retry extraction, refresh fields i prikaz extraction tabele | Postojeće extraction funkcionalnosti iz Sprinta 6 i dalje rade nakon dodavanja edit/confirm logike | Run, retry, refresh i prikaz extraction polja rade bez regresije | Pass |

---

## 7.6 Deployment smoke testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S7-DEP-001 | Deployment smoke testiranje | Server | Pokretanje backend containera nakon Sprint 7 izmjena | Pokrenuti backend container nakon dodavanja edit/confirm/placeholder logike | Backend se pokreće bez greške i aplikacija je dostupna | Backend container se uspješno pokrenuo nakon Sprint 7 izmjena | Pass |
| S7-DEP-002 | Deployment smoke testiranje | Server | Pokretanje frontend containera nakon Sprint 7 UI izmjena | Pokrenuti frontend container nakon dodavanja confirm dugmeta, review statusa i placeholder prikaza | Frontend se pokreće bez greške i aplikacija je dostupna u browseru | Frontend container se uspješno pokrenuo i aplikacija je dostupna u browseru | Pass |
| S7-DEP-003 | Deployment smoke testiranje | Server | Provjera nove kolone `is_placeholder` u bazi | Pokrenuti aplikaciju i provjeriti da tabela `extraction_field` sadrži kolonu `is_placeholder` sa default vrijednošću | Baza ima potrebnu kolonu i backend može spremati placeholder extraction polja | Kolona `is_placeholder` je prisutna i placeholder polja se mogu spremiti bez greške | Pass |
| S7-DEP-004 | Deployment smoke testiranje | Server | Komunikacija FE-BE za confirm extraction flow | Otvoriti document detail stranicu i izvršiti akcije extraction/edit/confirm | Frontend uspješno poziva backend endpoint-e za extraction fields, edit field-a i confirm extraction | FE-BE komunikacija je uspješno izvršena kroz novi confirm extraction flow | Pass |
| S7-DEP-005 | Deployment smoke testiranje | Server | Provjera placeholder required polja u deployanom okruženju | Pokrenuti ekstrakciju nad dokumentom kojem OCR ne vrati sva required polja | Backend automatski dodaje missing required polja kao placeholder redove, a FE ih prikazuje korisniku | Placeholder required polja su kreirana na backendu i prikazana na frontend tabeli | Pass |
| S7-DEP-006 | Deployment smoke testiranje | Server | Blokiranje confirma u deployanom okruženju | Pokušati potvrditi ekstrakciju dok postoje placeholder ili low-confidence polja bez review-a | Confirm se ne izvršava, korisnik dobija toastr upozorenje, a dokument ostaje u statusu `EXTRACTED` | Confirm je blokiran dok review nije završen i prikazana je odgovarajuća toastr poruka | Pass |
| S7-DEP-007 | Deployment smoke testiranje | Server | Uspješan confirm nakon review-a u deployanom okruženju | Ručno popuniti placeholder required polja, pregledati low-confidence polja i kliknuti `Confirm extraction` | Confirm prolazi, dokument dobija status `READY_FOR_APPROVAL`, a extraction podaci ostaju dostupni | Dokument je uspješno prešao u status `READY_FOR_APPROVAL`, a polja su ostala dostupna za pregled | Pass |
| S7-DEP-008 | Deployment smoke testiranje | Server | Regresiona provjera upload/extraction flow-a nakon Sprint 7 izmjena | Uploadati dokument, otvoriti detalje, pokrenuti extraction, retry i refresh fields | Funkcionalnosti iz prethodnih sprintova i dalje rade nakon dodavanja edit/confirm logike | Upload, detalji dokumenta, extraction, retry i refresh fields rade bez regresije | Pass |

---

## Zaključak testiranja

Tokom Sprinta 7 testirane su funkcionalnosti vezane za ručni review OCR/AI rezultata, editovanje izdvojenih polja, validaciju vrijednosti, placeholder required polja i potvrdu ekstrakcije.

Zaključak:

- Testirano je ručno editovanje extraction field vrijednosti.
- Testirani su negativni validacioni scenariji za datume i numerička polja.
- Testirano je blokiranje confirma kada postoje missing required, placeholder ili low-confidence polja.
- Testirano je automatsko kreiranje placeholder redova za required polja koja OCR nije vratio.
- Testirano je ručno popunjavanje placeholder polja i uklanjanje placeholder statusa.
- Testirano je da confirm ne pokreće OCR ponovo i ne briše prethodne ručne korekcije.
- Testiran je frontend prikaz review statusa, toastr poruka i statusa `READY_FOR_APPROVAL`.
- Izvršeno je end-to-end i regresiono testiranje extraction flow-a nakon Sprint 7 izmjena.
- Izvršeno je deployment smoke testiranje backend/frontend containera, nove `is_placeholder` kolone i kompletnog confirm extraction flow-a u deployanom okruženju.
- Svi evidentirani testovi za Sprint 7 imaju status `Pass`.

---

# Sprint 8 – Testiranje autentifikacije, autorizacije, organizacijskog modela i proširenog document-processing toka

## 8.1 Funkcionalnosti koje se testiraju

U Sprintu 8 fokus testiranja je na uvođenju organizacijskog modela pristupa sistemu, autentifikacije i autorizacije korisnika, upravljanja korisnicima i rolama, multi-tenant zaštite podataka, kao i na proširenju ranije implementiranog toka obrade dokumenata.

Testirane funkcionalnosti uključuju:

- registraciju firme / organizacije,
- kreiranje prvog administratorskog naloga firme,
- login i logout korisnika,
- zaštitu endpointa koji zahtijevaju autentifikaciju,
- role-based pristup funkcionalnostima,
- kreiranje korisnika unutar firme,
- dodjelu i promjenu korisničkih rola,
- reset lozinke korisnika,
- prikaz osnovnog dashboarda,
- multi-tenant izolaciju dokumenata i korisnika po firmi,
- proširenje podržanih tipova dokumenata na `INVOICE`, `RECEIPT`, `BANK_STATEMENT`, `FORM` i `OTHER`,
- blokiranje internog tipa `UNKNOWN` kroz javni upload flow,
- routing ekstrakcije na odgovarajući Google Document AI procesor prema tipu dokumenta,
- auto-klasifikaciju dokumenata uploadanih kao `OTHER`,
- status `NEEDS_CLASSIFICATION_REVIEW` za nesigurnu klasifikaciju,
- ručnu potvrdu tipa dokumenta nakon nesigurne klasifikacije,
- type-aware validaciju ekstraktovanih polja,
- frontend prikaz novih tipova dokumenata, classification metadata i review statusa,
- regresionu provjeru upload/extraction/edit/confirm flow-a iz prethodnih sprintova,
- deployment smoke provjeru novih konfiguracija, enum vrijednosti i procesora.

---

## 8.2 Backend/API i integracijsko testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Ulaz / koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S8-BE-001 | Integration testiranje | Registracija firme i prvog administratorskog naloga | Poslati validan zahtjev za registraciju firme sa podacima firme i admin korisnika | Sistem kreira firmu, prvog admin korisnika i povezuje admina sa firmom | Firma i administratorski korisnik su uspješno kreirani i povezani u bazi | Pass |
| S8-BE-002 | Backend/API testiranje | Javna registracija firme bez prethodne autentifikacije | Pozvati registration endpoint bez postojećeg tokena/sesije | Registracija firme je dozvoljena kao javni inicijalni flow | Registration endpoint je dostupan bez autentifikacije i validan zahtjev prolazi | Pass |
| S8-BE-003 | Validaciono testiranje | Odbijanje registracije firme sa duplikatnim emailom | Poslati zahtjev za registraciju firme sa emailom koji već postoji | Backend vraća validacionu grešku i ne kreira novu firmu | Duplikatni email firme je odbijen, a dodatni zapis nije kreiran | Pass |
| S8-BE-004 | Validaciono testiranje | Odbijanje registracije bez obaveznih podataka | Izostaviti naziv firme, email, adresu ili admin podatke | Backend vraća validacione greške za obavezna polja | Backend je vratio validacione greške i registracija nije izvršena | Pass |
| S8-BE-005 | Integration testiranje | Sigurno spremanje lozinke admin korisnika | Registrovati firmu sa admin nalogom i provjeriti zapis korisnika | Lozinka se ne sprema kao plain text vrijednost | Lozinka nije spremljena u plain text obliku; korišten je sigurnosni mehanizam pohrane | Pass |
| S8-BE-006 | Backend/API testiranje | Uspješna prijava korisnika | Poslati validne pristupne podatke za postojećeg korisnika | Backend vraća uspješan odgovor i korisnik dobija pristup sistemu | Login sa validnim podacima je uspješno izvršen | Pass |
| S8-BE-007 | Validaciono testiranje | Neuspješna prijava sa pogrešnim pristupnim podacima | Poslati pogrešan email/lozinku | Backend odbija prijavu i vraća kontrolisanu poruku greške | Nevalidna prijava je odbijena bez otkrivanja osjetljivih sigurnosnih detalja | Pass |
| S8-BE-008 | Backend/API testiranje | Logout korisnika | Prijaviti korisnika, zatim pozvati logout akciju | Aktivna sesija/token se završava i korisnik više nema pristup zaštićenim dijelovima bez nove prijave | Logout je uspješno izvršen i korisnik je odjavljen iz sistema | Pass |
| S8-BE-009 | Backend/API testiranje | Zaštita endpointa bez autentifikacije | Pozvati zaštićeni endpoint bez tokena/sesije | Backend odbija pristup | Zaštićeni endpoint nije dostupan neautentifikovanom korisniku | Pass |
| S8-BE-010 | Backend/API testiranje | Pristup endpointu sa dozvoljenom rolom | Korisnik sa odgovarajućom rolom poziva endpoint za svoju funkcionalnost | Backend dozvoljava akciju | Akcija je dozvoljena korisniku koji ima potrebnu rolu | Pass |
| S8-BE-011 | Backend/API testiranje | Blokiranje endpointa za korisnika bez potrebne role | Korisnik bez odgovarajuće role pokušava izvršiti zaštićenu akciju | Backend vraća forbidden/authorization grešku | Korisniku bez potrebne role nije dozvoljeno izvršenje akcije | Pass |
| S8-BE-012 | Integration testiranje | Kreiranje korisnika unutar firme | Admin korisnik šalje zahtjev za kreiranje novog korisnika | Novi korisnik se kreira i automatski povezuje sa firmom admina | Korisnik je kreiran u okviru iste firme kao i administrator | Pass |
| S8-BE-013 | Validaciono testiranje | Odbijanje korisnika sa duplikatnim emailom | Admin pokuša kreirati korisnika sa emailom koji već postoji | Backend vraća validacionu grešku | Duplikatni korisnički email je odbijen i novi korisnik nije kreiran | Pass |
| S8-BE-014 | Integration testiranje | Dodjela role korisniku | Admin dodjeljuje korisniku rolu operator/manager/approver | Rola se sprema i primjenjuje na korisnika | Rola je uspješno spremljena i povezana sa korisnikom | Pass |
| S8-BE-015 | Integration testiranje | Promjena role korisnika | Admin promijeni postojeću rolu korisnika | Nova rola se primjenjuje i prethodna prava se zamjenjuju novima | Promjena role je uspješno izvršena | Pass |
| S8-BE-016 | Integration testiranje | Pregled korisnika firme | Admin poziva endpoint za listu korisnika | Vraćaju se samo korisnici iste firme | Backend vraća korisnike koji pripadaju firmi prijavljenog admina | Pass |
| S8-BE-017 | Integration testiranje | Multi-tenant izolacija korisnika | Korisnik firme A pokuša pristupiti korisniku firme B | Backend ne dozvoljava pristup podacima druge firme | Podaci druge firme nisu dostupni korisniku iz prve firme | Pass |
| S8-BE-018 | Integration testiranje | Multi-tenant izolacija dokumenata | Korisnik firme A pokuša dohvatiti dokument firme B | Backend vraća grešku ili onemogućava pristup | Dokument druge firme nije dostupan korisniku koji joj ne pripada | Pass |
| S8-BE-019 | Backend/API testiranje | Reset lozinke korisnika | Admin pokrene reset lozinke za korisnika svoje firme | Sistem generiše novu privremenu lozinku i prethodna lozinka više ne važi | Reset lozinke je izvršen i vraćena je nova privremena lozinka/admin potvrda | Pass |
| S8-BE-020 | Backend/API testiranje | Dashboard statistika | Korisnik sa odgovarajućom rolom pozove dashboard endpoint | Backend vraća agregirane podatke o dokumentima/statusima za njegovu firmu | Dashboard endpoint vraća osnovne statistike za firmu prijavljenog korisnika | Pass |
| S8-BE-021 | Backend/API testiranje | Upload dokumenta tipa `RECEIPT` | Uploadati validan PDF uz `documentType = RECEIPT` | Dokument se kreira i čuva sa tipom `RECEIPT` | Receipt dokument je uspješno uploadan i evidentiran sa ispravnim tipom | Pass |
| S8-BE-022 | Backend/API testiranje | Upload dokumenta tipa `BANK_STATEMENT` | Uploadati validan PDF uz `documentType = BANK_STATEMENT` | Dokument se kreira i čuva sa tipom `BANK_STATEMENT` | Bank statement dokument je uspješno uploadan i evidentiran sa ispravnim tipom | Pass |
| S8-BE-023 | Backend/API testiranje | Upload dokumenta tipa `FORM` | Uploadati validan PDF uz `documentType = FORM` | Dokument se kreira i čuva sa tipom `FORM` | Form dokument je uspješno uploadan i evidentiran sa ispravnim tipom | Pass |
| S8-BE-024 | Backend/API testiranje | Upload dokumenta tipa `OTHER` | Uploadati validan PDF uz `documentType = OTHER` | Dokument se kreira i tretira kao kandidat za auto-klasifikaciju | OTHER dokument je uspješno uploadan i spreman za classification flow | Pass |
| S8-BE-025 | Validaciono testiranje | Odbijanje internog tipa `UNKNOWN` pri uploadu | Poslati upload request sa `documentType = UNKNOWN` | Backend vraća `DOCUMENT_TYPE_INVALID` i dokument se ne kreira | Upload sa internim tipom `UNKNOWN` je odbijen | Pass |
| S8-BE-026 | Validaciono testiranje | Odbijanje nepodržanog tipa dokumenta | Poslati upload request sa `documentType = CONTRACT` ili drugim nepodržanim tipom | Backend vraća validacionu grešku | Nepodržan tip dokumenta je odbijen i zapis nije kreiran | Pass |
| S8-BE-027 | Integration testiranje | Direktna ekstrakcija za `RECEIPT` dokument | Pokrenuti extraction za dokument tipa `RECEIPT` | Sistem koristi receipt/expense processor bez classifier procesora | Receipt dokument je obrađen receipt procesorom, bez poziva classifier procesora | Pass |
| S8-BE-028 | Integration testiranje | Direktna ekstrakcija za `BANK_STATEMENT` dokument | Pokrenuti extraction za dokument tipa `BANK_STATEMENT` | Sistem koristi bank statement processor bez classifier procesora | Bank statement dokument je obrađen odgovarajućim procesorom | Pass |
| S8-BE-029 | Integration testiranje | Direktna ekstrakcija za `FORM` dokument | Pokrenuti extraction za dokument tipa `FORM` | Sistem koristi form parser processor bez classifier procesora | Form dokument je obrađen form procesorom | Pass |
| S8-BE-030 | Integration testiranje | Auto-klasifikacija `OTHER` dokumenta kao `INVOICE` | Uploadati dokument kao `OTHER`, classifier vrati `INVOICE` sa dovoljnom confidence vrijednošću | Sistem postavlja finalni tip na `INVOICE` i nastavlja extraction invoice procesorom | Dokument je klasifikovan kao invoice i obrađen invoice procesorom | Pass |
| S8-BE-031 | Integration testiranje | Auto-klasifikacija `OTHER` dokumenta kao `RECEIPT` | Classifier vrati `RECEIPT` sa dovoljnom confidence vrijednošću | Sistem postavlja finalni tip na `RECEIPT` i nastavlja receipt procesorom | Dokument je klasifikovan kao receipt i obrađen receipt procesorom | Pass |
| S8-BE-032 | Integration testiranje | Auto-klasifikacija `OTHER` dokumenta kao `BANK_STATEMENT` | Classifier vrati `BANK_STATEMENT` sa dovoljnom confidence vrijednošću | Sistem postavlja finalni tip na `BANK_STATEMENT` i nastavlja bank statement procesorom | Dokument je klasifikovan kao bank statement i obrađen odgovarajućim procesorom | Pass |
| S8-BE-033 | Integration testiranje | Auto-klasifikacija `OTHER` dokumenta kao `FORM` | Classifier vrati `FORM` sa dovoljnom confidence vrijednošću | Sistem postavlja finalni tip na `FORM` i nastavlja form procesorom | Dokument je klasifikovan kao form i obrađen form procesorom | Pass |
| S8-BE-034 | Integration testiranje | Classifier vrati `OTHER` | Classifier vrati tip `OTHER` i confidence vrijednost | Sistem ne pokreće parser, dokument prelazi u `NEEDS_CLASSIFICATION_REVIEW` i vraća `409 DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED` | Dokument je označen za ručni review tipa, bez kreiranja extraction rezultata | Pass |
| S8-BE-035 | Integration testiranje | Classifier vrati podržan tip sa niskom confidence vrijednošću | Classifier vrati npr. `INVOICE` sa confidence ispod praga | Sistem ne nastavlja extraction i traži manual classification review | Dokument je ostao bez extraction rezultata i prešao u `NEEDS_CLASSIFICATION_REVIEW` | Pass |
| S8-BE-036 | Backend/API testiranje | Greška classifier procesora | Simulirati grešku classifier procesora pri obradi `OTHER` dokumenta | Backend vraća kontrolisanu grešku i dokument prelazi u `PROCESSING_FAILED` | Greška classifiera je obrađena bez rušenja aplikacije, a status dokumenta je `PROCESSING_FAILED` | Pass |
| S8-BE-037 | Integration testiranje | Spremanje classification metadata | Pokrenuti auto-classification flow | Backend sprema `detectedDocumentType`, `classificationConfidence` i `processorIdUsed` kada su primjenjivi | Classification metadata je ispravno spremljena uz dokument | Pass |
| S8-BE-038 | Backend/API testiranje | Uspješna ručna potvrda tipa dokumenta | Dokument u statusu `NEEDS_CLASSIFICATION_REVIEW`; poslati `PATCH /api/documents/{id}/classification` sa finalnim tipom `INVOICE`, `RECEIPT`, `BANK_STATEMENT` ili `FORM` | Backend ažurira tip dokumenta i vraća status u `UPLOADED` | Manual classification je uspješno potvrđena i dokument je spreman za ponovno pokretanje extraction flow-a | Pass |
| S8-BE-039 | Validaciono testiranje | Manual classification kada dokument nije u review statusu | Pozvati classification confirm endpoint za dokument koji nije u `NEEDS_CLASSIFICATION_REVIEW` | Backend vraća `DOCUMENT_STATUS_INVALID` | Ručna potvrda tipa je odbijena jer dokument nije čekao classification review | Pass |
| S8-BE-040 | Validaciono testiranje | Odbijanje `OTHER` kao finalnog ručnog tipa | Poslati `PATCH /classification` sa `documentType = OTHER` | Backend vraća `DOCUMENT_TYPE_INVALID` | Sistem ne dozvoljava da `OTHER` bude finalni ručno potvrđeni tip | Pass |
| S8-BE-041 | Validaciono testiranje | Odbijanje nepoznatog ručnog tipa | Poslati `PATCH /classification` sa nepodržanim tipom, npr. `CONTRACT` | Backend vraća `DOCUMENT_TYPE_INVALID` | Nepodržan ručni tip je odbijen | Pass |
| S8-BE-042 | Backend/API testiranje | Manual classification za nepostojeći dokument | Pozvati `PATCH /api/documents/{id}/classification` sa nepostojećim ID-em | Backend vraća `404 NOT_FOUND` | Backend je vratio kontrolisanu grešku za nepostojeći dokument | Pass |
| S8-BE-043 | Validaciono testiranje | Confirm receipt ekstrakcije sa validnim required poljima i date aliasom | Receipt extraction sadrži required polja i jedan podržani date alias | Confirm prolazi i dokument prelazi u `READY_FOR_APPROVAL` | Receipt dokument je uspješno potvrđen | Pass |
| S8-BE-044 | Validaciono testiranje | Blokiranje receipt confirma bez date aliasa | Receipt extraction ne sadrži nijedan podržani datum (`receipt_date`, `expense_date`, `transaction_date`, `purchase_date`) | Confirm vraća `EXTRACTION_REQUIRED_FIELD_MISSING` | Confirm je odbijen jer receipt nema validan datum | Pass |
| S8-BE-045 | Validaciono testiranje | Blokiranje receipt confirma zbog low-confidence required polja | Receipt required polje ima confidence ispod praga i nije ručno korigovano | Confirm vraća `EXTRACTION_FIELD_LOW_CONFIDENCE` | Confirm je odbijen za low-confidence required receipt polje | Pass |
| S8-BE-046 | Validaciono testiranje | Prihvatanje receipt confirma sa low-confidence optional poljem | Receipt optional polje ima nizak confidence, ali required polja su validna | Confirm prolazi jer optional low-confidence polje ne blokira receipt flow | Receipt confirm je uspješno izvršen | Pass |
| S8-BE-047 | Validaciono testiranje | Confirm bank statement ekstrakcije sa osnovnom strukturom | Bank statement sadrži account number, identifikacijsko polje i activity/balance/transaction polje | Confirm prolazi i dokument prelazi u `READY_FOR_APPROVAL` | Bank statement dokument je uspješno potvrđen | Pass |
| S8-BE-048 | Validaciono testiranje | Blokiranje bank statement confirma bez identity polja | Bank statement nema `bank_name`, `client_name`, `account_holder_name` ili sličan identifikacijski podatak | Confirm vraća `EXTRACTION_REQUIRED_FIELD_MISSING` | Confirm je odbijen jer nedostaje identity podatak | Pass |
| S8-BE-049 | Validaciono testiranje | Blokiranje bank statement confirma bez activity/balance/transaction polja | Bank statement nema podatak o aktivnosti, stanju, datumu ili transakciji | Confirm vraća `EXTRACTION_REQUIRED_FIELD_MISSING` | Confirm je odbijen jer bank statement nema osnovnu aktivnost/stanje | Pass |
| S8-BE-050 | Validaciono testiranje | Blokiranje bank statement confirma zbog low-confidence required polja | Required/strukturno bank statement polje ima nizak confidence i nije pregledano | Confirm vraća `EXTRACTION_FIELD_LOW_CONFIDENCE` | Confirm je odbijen za low-confidence required bank statement polje | Pass |
| S8-BE-051 | Validaciono testiranje | Prihvatanje bank statement confirma sa low-confidence optional poljem | Optional bank statement polje ima nizak confidence | Confirm prolazi jer optional low-confidence polje ne blokira flow | Bank statement confirm je uspješno izvršen | Pass |
| S8-BE-052 | Validaciono testiranje | Blokiranje bank statement confirma zbog nevalidnog numeričkog formata | Bank statement balance/amount polje sadrži nenumeričku ili nevalidnu vrijednost | Backend vraća `EXTRACTION_FIELD_NUMERIC_FORMAT_INVALID` | Confirm je odbijen zbog nevalidnog numeričkog formata | Pass |
| S8-BE-053 | Validaciono testiranje | Confirm form ekstrakcije sa low-confidence poljima | Form extraction ima low-confidence polja | Confirm prolazi jer `FORM` nema striktna invoice required pravila i low-confidence ne blokira flow | Form dokument je potvrđen bez invoice-specific blokade | Pass |
| S8-BE-054 | Regresiono testiranje | Invoice extraction i confirm flow nakon type-aware validacije | Pokrenuti postojeći invoice flow iz Sprinta 6/7 | Invoice required field, low-confidence i placeholder pravila i dalje rade | Invoice flow nije pokvaren proširenjem na nove tipove dokumenata | Pass |
| S8-BE-055 | Regresiono testiranje | Dohvat extraction polja nakon proširenja modela | Pokrenuti extraction i pozvati endpoint za fields po documentId/extractionId | Endpoint vraća očekivana polja i nove metadata izmjene ne remete postojeći response | Dohvat extraction polja radi kao i ranije | Pass |

---

## 8.3 Frontend/UI testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Koraci testiranja | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S8-UI-001 | Frontend/UI testiranje | Otvaranje registration stranice | Navigirati na stranicu za registraciju firme | Stranica se učitava bez greške i prikazuje formu za firmu i admin korisnika | Registration stranica se uspješno učitala | Pass |
| S8-UI-002 | Frontend/UI testiranje | Uspješna registracija firme | Popuniti validne podatke firme i admin korisnika i submitovati formu | Prikazuje se success poruka i kreira se organizacija | Firma i admin nalog su kreirani, a korisnik dobija jasnu potvrdu | Pass |
| S8-UI-003 | Frontend/UI testiranje | Validacione poruke na registration formi | Pokušati submit bez obaveznih polja ili sa nevalidnim emailom | UI prikazuje razumljive validacione poruke | Validacione poruke su prikazane bez rušenja stranice | Pass |
| S8-UI-004 | Frontend/UI testiranje | Prikaz greške za duplikatni email firme | Pokušati registrovati firmu sa postojećim emailom | Korisnik dobija razumljivu error poruku | Prikazana je poruka da firma/email već postoji | Pass |
| S8-UI-005 | Frontend/UI testiranje | Otvaranje login stranice | Navigirati na login stranicu | Login forma se učitava i prikazuje email/lozinku | Login stranica se uspješno učitala | Pass |
| S8-UI-006 | Frontend/UI testiranje | Uspješan login | Unijeti validne pristupne podatke | Korisnik se prijavljuje i preusmjerava u aplikaciju | Login je uspješan i korisnik vidi zaštićeni dio aplikacije | Pass |
| S8-UI-007 | Frontend/UI testiranje | Neuspješan login | Unijeti pogrešne pristupne podatke | UI prikazuje jasnu grešku bez tehničkih detalja | Prikazana je razumljiva error poruka za neuspješnu prijavu | Pass |
| S8-UI-008 | Frontend/UI testiranje | Logout iz aplikacije | Kliknuti logout nakon uspješne prijave | Korisnik se odjavljuje i vraća na javni/login dio | Logout je uspješno izvršen | Pass |
| S8-UI-009 | Frontend/UI testiranje | Zaštita ruta bez prijave | Pokušati otvoriti zaštićenu rutu bez prijave | Korisnik se preusmjerava na login ili nema pristup | Zaštićene rute nisu dostupne bez autentifikacije | Pass |
| S8-UI-010 | Frontend/UI testiranje | Prikaz navigacije prema roli | Prijaviti se kao korisnik različite role | UI prikazuje samo relevantne akcije/rute | Navigacija i akcije su usklađene sa rolom korisnika | Pass |
| S8-UI-011 | Frontend/UI testiranje | User management lista | Admin otvara stranicu za upravljanje korisnicima | Prikazuje se lista korisnika firme | Lista korisnika firme je prikazana | Pass |
| S8-UI-012 | Frontend/UI testiranje | Kreiranje korisnika kroz UI | Admin popunjava formu za novog korisnika | Korisnik se kreira i prikazuje u listi | Novi korisnik je uspješno dodat u listu korisnika | Pass |
| S8-UI-013 | Frontend/UI testiranje | Dodjela role korisniku kroz UI | Admin odabire rolu za korisnika | Rola se sprema i prikazuje uz korisnika | Rola je uspješno dodijeljena i vidljiva u UI-u | Pass |
| S8-UI-014 | Frontend/UI testiranje | Promjena role korisnika | Admin mijenja postojeću rolu korisnika | UI ažurira prikaz role i prikazuje success poruku | Promjena role je uspješno prikazana i sačuvana | Pass |
| S8-UI-015 | Frontend/UI testiranje | Reset lozinke kroz UI | Admin pokrene reset lozinke za korisnika | Prikazuje se potvrda i nova privremena lozinka/informacija o resetu | Reset lozinke je uspješno prikazan korisniku/adminu | Pass |
| S8-UI-016 | Frontend/UI testiranje | Dashboard prikaz | Korisnik sa odgovarajućom rolom otvara dashboard | Prikazuju se osnovni pokazatelji stanja dokumenata | Dashboard se učitao i prikazao agregirane podatke | Pass |
| S8-UI-017 | Frontend/UI testiranje | Upload dropdown za tip dokumenta | Otvoriti upload stranicu | Dropdown prikazuje Invoice, Receipt/Expense, Bank statement, Form i Other/Auto classify | Svi podržani tipovi dokumenata su prikazani korisniku | Pass |
| S8-UI-018 | Frontend/UI testiranje | Upload dokumenta sa novim tipom | Uploadati receipt/bank statement/form dokument kroz UI | Dokument se uploaduje i čuva sa odabranim tipom | Dokument je uspješno uploadan sa odabranim tipom | Pass |
| S8-UI-019 | Frontend/UI testiranje | Čitljiv prikaz document type-a u listi dokumenata | Otvoriti listu dokumenata nakon upload-a različitih tipova | Lista prikazuje user-friendly label za tip dokumenta | Tipovi dokumenata su prikazani čitljivo, bez sirovih internih vrijednosti gdje nije potrebno | Pass |
| S8-UI-020 | Frontend/UI testiranje | Status badge za `NEEDS_CLASSIFICATION_REVIEW` | Uploadati `OTHER` dokument koji zahtijeva manual review | Lista i detalji prikazuju warning/review badge | Status `Needs Classification Review` je jasno prikazan | Pass |
| S8-UI-021 | Frontend/UI testiranje | Detail stranica prikazuje classification metadata kada je classifier korišten | Otvoriti detalje dokumenta koji je prošao auto-classification ili review | Prikazuju se detected type, confidence i relevantni classification podaci | Classification metadata je prikazana samo kada je relevantna | Pass |
| S8-UI-022 | Frontend/UI testiranje | Detail stranica ne prikazuje classification metadata za direktne tipove | Otvoriti detalje direktno uploadanog invoice/receipt/bank/form dokumenta | Classification sekcija se ne prikazuje nepotrebno | UI ne prikazuje nepotrebne classifier podatke za direktne tipove | Pass |
| S8-UI-023 | Frontend/UI testiranje | Blokiranje Run extraction tokom classification review-a | Dokument je u statusu `NEEDS_CLASSIFICATION_REVIEW` | Run extraction dugme je sakriveno/onemogućeno i korisnik vidi šta treba uraditi | UI ne dozvoljava pokretanje extractiona dok tip nije potvrđen | Pass |
| S8-UI-024 | Frontend/UI testiranje | Manual classification flow kroz UI | Na document detail stranici odabrati finalni tip i potvrditi | Tip se sprema, status se vraća u `UPLOADED`, extraction se može ponovo pokrenuti | Ručna potvrda tipa je uspješno izvršena kroz UI | Pass |
| S8-UI-025 | Frontend/UI testiranje | Greška za `DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED` | Pokrenuti extraction nad `OTHER` dokumentom koji classifier ne može sigurno klasifikovati | UI prikazuje razumljivu poruku da je potreban ručni pregled tipa | Korisniku je prikazana jasna review poruka | Pass |
| S8-UI-026 | Frontend/UI testiranje | Type-aware prikaz low-confidence polja | Otvoriti extraction fields za receipt, bank statement i form dokumente | UI prikazuje upozorenja u skladu sa tipom dokumenta | Low-confidence prikaz je usklađen sa pravilima za tip dokumenta | Pass |
| S8-UI-027 | Frontend/UI testiranje | Type-aware validacione poruke za datume | Pokušati potvrditi/editovati datum u nepodržanom formatu | UI prikazuje poruku da su podržani ISO `YYYY-MM-DD` ili evropski `DD.MM.YYYY` / `DD/MM/YYYY` formati | Korisnik dobija jasnu poruku o podržanim formatima datuma | Pass |
| S8-UI-028 | Frontend/UI testiranje | Type-aware validacione poruke za numerička polja | Unijeti iznos sa valutom ili dodatnim tekstom | UI prikazuje da vrijednost mora biti numerička bez currency simbola ili dodatnog teksta | Korisniku je prikazana razumljiva poruka za numerička polja | Pass |
| S8-UI-029 | Frontend/UI testiranje | Responsivnost novih auth i document-processing ekrana | Testirati registration, login, upload, listu i detalje na manjoj širini ekrana | Stranice ostaju čitljive i upotrebljive | UI ostaje pregledan i funkcionalan na manjim ekranima | Pass |
| S8-UI-030 | Regresiono testiranje | Postojeći upload/extraction UI nakon Sprint 8 izmjena | Testirati upload, document list, detail, run extraction, refresh fields, edit field i confirm extraction | Funkcionalnosti iz prethodnih sprintova i dalje rade | Postojeći UI flow nije pokvaren Sprint 8 izmjenama | Pass |

---

## 8.4 Manualno API testiranje kroz Swagger/Postman

| ID testa | Vrsta testiranja | Alat | Endpoint / funkcionalnost | Koraci | Očekivani rezultat | Stvarni rezultat | Status |
|---|---|---|---|---|---|---|---|
| S8-API-001 | Backend/API testiranje | Swagger/Postman | Registracija firme | Poslati validan registration request | Kreira se firma i prvi admin korisnik | Firma i admin korisnik su uspješno kreirani | Pass |
| S8-API-002 | Validaciono testiranje | Swagger/Postman | Registracija sa duplikatnim emailom | Ponoviti registration request sa istim emailom firme | Vraća se validaciona greška | API je vratio grešku za duplikatni email | Pass |
| S8-API-003 | Backend/API testiranje | Swagger/Postman | Login | Poslati validne kredencijale | Korisnik dobija pristup aplikaciji/token/sesiju | Login je uspješno izvršen | Pass |
| S8-API-004 | Validaciono testiranje | Swagger/Postman | Login sa nevalidnim podacima | Poslati pogrešnu lozinku | Vraća se kontrolisana auth greška | Neuspješan login je pravilno odbijen | Pass |
| S8-API-005 | Backend/API testiranje | Swagger/Postman | Logout | Pozvati logout endpoint nakon prijave | Sesija/token se završava | Logout je uspješno izvršen | Pass |
| S8-API-006 | Backend/API testiranje | Swagger/Postman | Kreiranje korisnika firme | Admin šalje zahtjev za novog korisnika | Korisnik se kreira unutar firme admina | Korisnik je kreiran i povezan sa firmom | Pass |
| S8-API-007 | Backend/API testiranje | Swagger/Postman | Dodjela/promjena role korisnika | Poslati zahtjev za dodjelu ili promjenu role | Rola se ažurira | Rola je uspješno ažurirana | Pass |
| S8-API-008 | Backend/API testiranje | Swagger/Postman | Pregled korisnika firme | Pozvati endpoint za listu korisnika | Vraćaju se korisnici prijavljene firme | API vraća samo korisnike odgovarajuće firme | Pass |
| S8-API-009 | Backend/API testiranje | Swagger/Postman | Dashboard | Pozvati dashboard endpoint kao autorizovan korisnik | Vraćaju se osnovne statistike sistema/firme | Dashboard podaci su vraćeni | Pass |
| S8-API-010 | Backend/API testiranje | Swagger/Postman | Upload `RECEIPT` dokumenta | Poslati multipart upload sa `documentType = RECEIPT` | Dokument se kreira sa tipom `RECEIPT` | Receipt dokument je kreiran putem API-ja | Pass |
| S8-API-011 | Backend/API testiranje | Swagger/Postman | Upload `BANK_STATEMENT` dokumenta | Poslati multipart upload sa `documentType = BANK_STATEMENT` | Dokument se kreira sa tipom `BANK_STATEMENT` | Bank statement dokument je kreiran putem API-ja | Pass |
| S8-API-012 | Backend/API testiranje | Swagger/Postman | Upload `FORM` dokumenta | Poslati multipart upload sa `documentType = FORM` | Dokument se kreira sa tipom `FORM` | Form dokument je kreiran putem API-ja | Pass |
| S8-API-013 | Backend/API testiranje | Swagger/Postman | Upload `OTHER` dokumenta | Poslati multipart upload sa `documentType = OTHER` | Dokument se kreira kao kandidat za auto-classification | OTHER dokument je kreiran putem API-ja | Pass |
| S8-API-014 | Validaciono testiranje | Swagger/Postman | Upload sa `UNKNOWN` tipom | Poslati upload sa `documentType = UNKNOWN` | API vraća `DOCUMENT_TYPE_INVALID` | Upload sa `UNKNOWN` tipom je odbijen | Pass |
| S8-API-015 | Integration testiranje | Swagger/Postman | Extraction direktno označenog receipt dokumenta | Pokrenuti `POST /api/documents/{documentId}/extraction` za `RECEIPT` | Extraction prolazi receipt procesorom | Receipt extraction je uspješno izvršen | Pass |
| S8-API-016 | Integration testiranje | Swagger/Postman | Extraction direktno označenog bank statement dokumenta | Pokrenuti extraction za `BANK_STATEMENT` | Extraction prolazi bank statement procesorom | Bank statement extraction je uspješno izvršen | Pass |
| S8-API-017 | Integration testiranje | Swagger/Postman | Extraction direktno označenog form dokumenta | Pokrenuti extraction za `FORM` | Extraction prolazi form procesorom | Form extraction je uspješno izvršen | Pass |
| S8-API-018 | Integration testiranje | Swagger/Postman | Auto-classification `OTHER` dokumenta | Pokrenuti extraction za dokument uploadan kao `OTHER` | Classifier se pokreće prije parsera i sistem nastavlja prema detektovanom tipu ako je confidence dovoljan | Auto-classification flow je uspješno izvršen | Pass |
| S8-API-019 | Backend/API testiranje | Swagger/Postman | Classification review required response | Pokrenuti extraction za `OTHER` dokument koji classifier ne može sigurno klasifikovati | API vraća `409 DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED` | API je vratio očekivani 409 review response | Pass |
| S8-API-020 | Backend/API testiranje | Swagger/Postman | Manual classification confirm | Pozvati `PATCH /api/documents/{id}/classification` sa podržanim finalnim tipom | Dokument dobija finalni tip i vraća se u `UPLOADED` | Manual classification je uspješno potvrđena | Pass |
| S8-API-021 | Validaciono testiranje | Swagger/Postman | Manual classification sa `OTHER` ili nepodržanim tipom | Poslati `OTHER`, `UNKNOWN` ili `CONTRACT` kao finalni tip | API vraća validacionu grešku | Nevalidni finalni tipovi su odbijeni | Pass |
| S8-API-022 | Backend/API testiranje | Swagger/Postman | Confirm extraction za receipt | Nakon receipt extractiona pozvati confirm endpoint | Dokument prelazi u `READY_FOR_APPROVAL` ako su required/date alias polja validna | Receipt confirm je uspješno izvršen | Pass |
| S8-API-023 | Backend/API testiranje | Swagger/Postman | Confirm extraction za bank statement | Nakon bank statement extractiona pozvati confirm endpoint | Dokument prelazi u `READY_FOR_APPROVAL` ako su strukturna polja validna | Bank statement confirm je uspješno izvršen | Pass |
| S8-API-024 | Backend/API testiranje | Swagger/Postman | Confirm extraction za form | Nakon form extractiona pozvati confirm endpoint | Dokument prelazi u `READY_FOR_APPROVAL` bez invoice-specific required blokada | Form confirm je uspješno izvršen | Pass |
| S8-API-025 | Regresiono testiranje | Swagger/Postman | Invoice upload/extraction/edit/confirm nakon Sprint 8 izmjena | Izvršiti postojeći invoice flow iz prethodnih sprintova | Postojeći flow i dalje radi | Invoice flow je prošao bez regresije | Pass |

---

## 8.5 End-to-end i regresiono testiranje

| ID testa | Vrsta testiranja | Scenario | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S8-E2E-001 | End-to-end testiranje | Kompletan organizacijski onboarding flow | Registrovati firmu, kreirati admin nalog, prijaviti se kao admin | Firma i admin nalog su kreirani, admin se može prijaviti i pristupiti sistemu | Onboarding flow je uspješno izvršen | Pass |
| S8-E2E-002 | End-to-end testiranje | Admin kreira korisnika i dodjeljuje rolu | Prijaviti se kao admin, kreirati korisnika, dodijeliti rolu, provjeriti prikaz u listi | Korisnik je vidljiv u user management listi sa ispravnom rolom | Korisnik je kreiran i rola je prikazana | Pass |
| S8-E2E-003 | End-to-end testiranje | Login/logout flow za kreiranog korisnika | Kreirani korisnik se prijavi, zatim izvrši logout | Korisnik može pristupiti dozvoljenim dijelovima i nakon logouta gubi pristup | Login/logout flow radi očekivano | Pass |
| S8-E2E-004 | End-to-end testiranje | Role-based UI i API ponašanje | Prijaviti se sa različitim rolama i pokušati dozvoljene/nedozvoljene akcije | Korisnik vidi i izvršava samo akcije koje odgovaraju njegovoj roli | Role-based ponašanje je potvrđeno kroz UI i API | Pass |
| S8-E2E-005 | End-to-end testiranje | Multi-tenant izolacija dokumenata | Kreirati dokumente u dvije firme i pokušati pristupiti dokumentu druge firme | Korisnik vidi samo dokumente svoje firme | Podaci su izolovani po firmi | Pass |
| S8-E2E-006 | End-to-end testiranje | Multi-tenant izolacija korisnika | Admin jedne firme pregleda korisnike nakon što postoje korisnici druge firme | Lista prikazuje samo korisnike njegove firme | Korisnici drugih firmi nisu prikazani | Pass |
| S8-E2E-007 | End-to-end testiranje | Upload i extraction za receipt dokument | Uploadati receipt, pokrenuti extraction, pregledati fields i potvrditi extraction | Dokument prolazi receipt parser flow i završava u `READY_FOR_APPROVAL` | Receipt flow je uspješno izvršen | Pass |
| S8-E2E-008 | End-to-end testiranje | Upload i extraction za bank statement dokument | Uploadati bank statement, pokrenuti extraction, pregledati fields i potvrditi extraction | Dokument prolazi bank statement parser flow i završava u `READY_FOR_APPROVAL` | Bank statement flow je uspješno izvršen | Pass |
| S8-E2E-009 | End-to-end testiranje | Upload i extraction za form dokument | Uploadati form, pokrenuti extraction, pregledati fields i potvrditi extraction | Dokument prolazi form parser flow i završava u `READY_FOR_APPROVAL` bez invoice-specific blokada | Form flow je uspješno izvršen | Pass |
| S8-E2E-010 | End-to-end testiranje | Auto-classification visokog confidence-a | Uploadati dokument kao `OTHER`, classifier prepozna podržani tip sa dovoljnom sigurnošću, extraction se nastavlja odgovarajućim parserom | Korisnik vidi finalni tip, extraction rezultat i može nastaviti review/confirm flow | Auto-classification flow je uspješno izvršen | Pass |
| S8-E2E-011 | End-to-end testiranje | Auto-classification sa manual review-om | Uploadati dokument kao `OTHER`, classifier nije siguran, dokument prelazi u `NEEDS_CLASSIFICATION_REVIEW`, korisnik ručno potvrdi tip i ponovo pokrene extraction | Prvi extraction je blokiran uz review poruku; nakon ručne potvrde extraction prolazi | Manual classification review flow je uspješno izvršen | Pass |
| S8-E2E-012 | End-to-end testiranje | Type-aware validacija blokira nevalidan receipt | Pokrenuti receipt extraction bez validnog date aliasa i pokušati confirm | Confirm je blokiran, korisnik dobija validacionu poruku, status ostaje `EXTRACTED` | Receipt confirm je pravilno blokiran | Pass |
| S8-E2E-013 | End-to-end testiranje | Type-aware validacija blokira nevalidan bank statement | Pokrenuti bank statement extraction bez identity/activity strukture ili sa nevalidnim numeričkim formatom | Confirm je blokiran uz odgovarajuću validacionu poruku | Bank statement confirm je pravilno blokiran | Pass |
| S8-E2E-014 | End-to-end testiranje | Form dokument ne blokiraju invoice required pravila | Pokrenuti form extraction i confirmati bez invoice-specific polja | Confirm prolazi jer form nema stroga invoice required pravila | Form confirm prolazi bez pogrešne invoice validacije | Pass |
| S8-E2E-015 | Regresiono testiranje | Upload flow iz Sprinta 5 nakon auth/multi-tenant izmjena | Prijavljeni korisnik uploaduje PDF/PNG/JPG dokument i provjerava listu/detalje/download | Upload, lista, detalji i download i dalje rade | Sprint 5 flow nije pokvaren | Pass |
| S8-E2E-016 | Regresiono testiranje | OCR/extraction flow iz Sprinta 6 nakon novih procesora | Pokrenuti extraction nad invoice dokumentom i provjeriti rezultat | Postojeći invoice OCR flow i dalje radi | Sprint 6 extraction flow nije pokvaren | Pass |
| S8-E2E-017 | Regresiono testiranje | Edit/confirm flow iz Sprinta 7 nakon type-aware validacije | Editovati extraction field, popuniti placeholder, pregledati low-confidence polja i potvrditi extraction | Existing edit/confirm flow i dalje radi za invoice dokumente | Sprint 7 edit/confirm flow nije pokvaren | Pass |
| S8-E2E-018 | Regresiono testiranje | Error handling nakon Sprint 8 izmjena | Izazvati auth, upload, extraction, classification i validation greške | UI i API vraćaju kontrolisane poruke bez rušenja aplikacije | Error handling radi konzistentno kroz nove i stare tokove | Pass |

---

## 8.6 Deployment smoke testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S8-DEP-001 | Deployment smoke testiranje | Lokalno/Server | Pokretanje baze nakon Sprint 8 izmjena | Pokrenuti PostgreSQL container i provjeriti schema update/migraciju | Baza se pokreće i podržava nove enum/check vrijednosti za document type i status | Baza se uspješno pokrenula i prihvata nove tipove/status dokumenta | Pass |
| S8-DEP-002 | Deployment smoke testiranje | Lokalno/Server | Pokretanje backend containera nakon auth i document-processing izmjena | Pokrenuti backend container | Backend se pokreće bez greške | Backend container je uspješno pokrenut | Pass |
| S8-DEP-003 | Deployment smoke testiranje | Lokalno/Server | Pokretanje frontend containera nakon Sprint 8 UI izmjena | Pokrenuti frontend container | Frontend je dostupan u browseru | Frontend container je uspješno pokrenut i aplikacija je dostupna | Pass |
| S8-DEP-004 | Deployment smoke testiranje | Server | Provjera auth konfiguracije | Otvoriti deployanu aplikaciju, testirati login/logout i pristup zaštićenim rutama | Auth flow radi u deployanom okruženju | Login/logout i route protection rade na serveru | Pass |
| S8-DEP-005 | Deployment smoke testiranje | Server | Provjera registration flow-a | Registrovati testnu firmu i admin korisnika u deployanom okruženju | Firma i admin nalog se kreiraju bez server grešaka | Registration flow radi na serveru | Pass |
| S8-DEP-006 | Deployment smoke testiranje | Server | Provjera user management flow-a | Kao admin kreirati korisnika, dodijeliti rolu i provjeriti listu | User management funkcionalnosti rade u deployanom okruženju | Korisnik i rola su uspješno kreirani/prikazani | Pass |
| S8-DEP-007 | Deployment smoke testiranje | Server | Provjera multi-tenant izolacije u deployanom okruženju | Testirati pristup dokumentima/korisnicima iz različitih firmi | Podaci su izolovani po firmi | Korisnik vidi samo podatke svoje firme | Pass |
| S8-DEP-008 | Deployment smoke testiranje | Server | Provjera novih Document AI env varijabli | Provjeriti da backend container ima konfiguraciju za invoice, receipt, bank statement, form i classifier processor ID | Svi potrebni processor ID-jevi su dostupni aplikaciji | Backend prepoznaje sve nove processor ID konfiguracije | Pass |
| S8-DEP-009 | Deployment smoke testiranje | Server | Direct extraction za novi tip dokumenta | Uploadati receipt/bank/form dokument i pokrenuti extraction | Backend koristi odgovarajući processor i vraća rezultat ili kontrolisanu OCR grešku | Novi processor routing radi u deployanom okruženju | Pass |
| S8-DEP-010 | Deployment smoke testiranje | Server | Auto-classification flow | Uploadati dokument kao Other/Auto classify i pokrenuti extraction | Classifier se poziva i sistem nastavlja extraction ili traži manual review | Auto-classification flow radi u deployanom okruženju | Pass |
| S8-DEP-011 | Deployment smoke testiranje | Server | Manual classification review flow | Izazvati `NEEDS_CLASSIFICATION_REVIEW`, ručno potvrditi tip i ponovo pokrenuti extraction | Dokument se vraća u `UPLOADED`, zatim se extraction može pokrenuti | Manual classification flow radi na serveru | Pass |
| S8-DEP-012 | Deployment smoke testiranje | Server | Type-aware confirm validation | Potvrditi receipt, bank statement i form extraction flow u deployanom okruženju | Validacija se primjenjuje prema tipu dokumenta | Type-aware validacija radi na serveru | Pass |
| S8-DEP-013 | Deployment smoke testiranje | Server | Regresiona provjera upload storage-a | Uploadati dokument nakon Sprint 8 deploya i provjeriti da se fajl sprema na server storage | Fajl se sprema na očekivanu lokaciju | Upload storage i dalje radi nakon Sprint 8 izmjena | Pass |
| S8-DEP-014 | Deployment smoke testiranje | Server | Regresiona provjera extraction/edit/confirm flow-a | Pokrenuti postojeći invoice flow iz prethodnih sprintova | Stari extraction/edit/confirm flow i dalje radi | Nije pronađena regresija u postojećem invoice flow-u | Pass |
| S8-DEP-015 | Deployment smoke testiranje | Server | Provjera frontend-backend komunikacije nakon deploya | Kroz UI izvršiti registration/login/upload/extraction/manual classification akcije | Frontend uspješno komunicira sa backendom za nove i stare funkcionalnosti | FE-BE komunikacija je potvrđena u deployanom okruženju | Pass |

---

## Zaključak testiranja

Tokom Sprinta 8 testirane su funkcionalnosti vezane za autentifikaciju, autorizaciju, organizacijski model, korisnike i role, multi-tenant izolaciju, dashboard i prošireni tok obrade dokumenata.

Zaključak:

- Testirana je registracija firme i kreiranje prvog administratorskog naloga.
- Testirani su login, logout i zaštita zaštićenih ruta/endpointa.
- Testirano je kreiranje korisnika unutar firme, dodjela i promjena rola, pregled korisnika i reset lozinke.
- Testirana je role-based kontrola pristupa na UI i backend/API nivou.
- Testirana je multi-tenant izolacija korisnika i dokumenata po firmi.
- Testirani su novi tipovi dokumenata: `INVOICE`, `RECEIPT`, `BANK_STATEMENT`, `FORM` i `OTHER`.
- Testirano je da se interni/nepodržani tipovi kao `UNKNOWN` ili nepoznati tipovi ne mogu koristiti kroz javni upload/manual classification flow.
- Testiran je routing ekstrakcije na odgovarajući Document AI processor prema tipu dokumenta.
- Testiran je auto-classification flow za dokumente uploadane kao `OTHER`.
- Testirani su scenariji u kojima classifier prepoznaje podržan tip, vraća `OTHER`, ima nizak confidence ili dođe do greške classifier procesora.
- Testiran je status `NEEDS_CLASSIFICATION_REVIEW` i ručna potvrda tipa dokumenta.
- Testirana je type-aware validacija ekstraktovanih polja za invoice, receipt, bank statement i form dokumente.
- Testirano je da `FORM` dokumenti nisu pogrešno blokirani invoice-specific required pravilima.
- Testiran je frontend prikaz novih tipova dokumenata, classification metadata, review statusa i validacionih poruka.
- Izvršeno je manualno API testiranje kroz Swagger/Postman za auth, user management, document upload, extraction, classification i confirm flow.
- Izvršeno je end-to-end testiranje kompletnog toka od registracije firme do obrade dokumenata.
- Izvršeno je regresiono testiranje funkcionalnosti iz Sprintova 5, 6 i 7.
- Izvršeno je deployment smoke testiranje backend/frontend containera, baze, auth konfiguracije, novih processor ID konfiguracija i kompletnih korisničkih tokova u deployanom okruženju.
- Svi evidentirani testovi za Sprint 8 imaju status `Pass`. 

---

# Sprint 9 – Testiranje workflow foundation, status history, audit i task assignment funkcionalnosti

## 9.1 Funkcionalnosti koje se testiraju

U Sprintu 9 fokus testiranja je na postavljanju workflow osnove i prvim poslovnim workflow funkcionalnostima nad dokumentima.

Testirane funkcionalnosti uključuju:

- proširenje statusa dokumenta novim statusom `NEEDS_CORRECTION`,
- dodavanje workflow foundation modela: status history, komentari, taskovi i audit log,
- centralizovano mijenjanje statusa kroz `DocumentStatusTransitionService`,
- čuvanje historije statusa dokumenta,
- dodavanje i prikaz komentara na dokumentu,
- brisanje dokumenta sa povezanim workflow zapisima,
- role-based permission provjere kroz `WorkflowPermissionService`,
- audit log za bitne korisničke/sistemske akcije,
- prikaz audit loga samo Admin/Manager korisnicima,
- task assignment za dokumente,
- My Tasks prikaz za korisnika kojem je zadatak dodijeljen,
- start, complete i cancel task akcije,
- ograničenje poslovnih akcija kada postoji aktivan task dodijeljen drugom korisniku,
- frontend prikaz status history, komentara, audit loga, task assignment forme, My Tasks stranice i active task informacija,
- regresionu provjeru postojećeg upload/extraction/edit/confirm flow-a nakon dodavanja workflow sloja,
- osnovnu CI/build provjeru backend i frontend aplikacije.

---

## 9.2 Backend/API i integracijsko testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Ulaz / koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S9-BE-001 | Integration testiranje | Persistovanje workflow foundation entiteta | Kreirati i persistovati `StatusHistoryEntity`, `CommentEntity`, `TaskEntity`, `NotificationEntity` i `AuditLogEntity` | Novi workflow entiteti se uspješno spremaju u bazu | Svi workflow foundation entiteti su uspješno spremljeni u bazu | Pass |
| S9-BE-002 | Integration testiranje | Inicijalni status history zapis | Uploadati dokument i provjeriti `status_history` tabelu | Kreira se inicijalni history zapis sa `oldStatus = null`, `newStatus = UPLOADED` i akcijom `DOCUMENT_UPLOADED` | Inicijalni status history zapis je kreiran nakon upload-a dokumenta | Pass |
| S9-BE-003 | Integration testiranje | Promjena statusa kroz `DocumentStatusTransitionService` | Promijeniti status dokumenta iz `UPLOADED` u `NEEDS_CORRECTION` | Status dokumenta se mijenja i kreira se odgovarajući history zapis | Status je promijenjen, a history zapis sadrži stari status, novi status, akciju i korisnika | Pass |
| S9-BE-004 | Validaciono testiranje | Podrška za status `NEEDS_CORRECTION` | Kreirati ili ažurirati dokument sa statusom `NEEDS_CORRECTION` | Backend prihvata novi status i ne dolazi do enum/check constraint greške | Status `NEEDS_CORRECTION` je uspješno prihvaćen u backendu i bazi | Pass |
| S9-BE-005 | Integration testiranje | Proširenje extraction field modela | Kreirati extraction field sa `displayName` i `manual = true` | Backend i baza čuvaju nova polja bez greške | `displayName` i `manual` su uspješno spremljeni za extraction field | Pass |
| S9-BE-006 | Backend/API testiranje | Dohvat status history zapisa | Pozvati `GET /api/documents/{id}/status-history` za postojeći dokument | Backend vraća status history zapise dokumenta | Endpoint vraća listu history zapisa za dokument | Pass |
| S9-BE-007 | Backend/API testiranje | Status history redoslijed | Uploadati dokument, pokrenuti extraction, confirmati extraction i dohvatiti status history | History zapisi dolaze hronološki: `DOCUMENT_UPLOADED`, `EXTRACTION_COMPLETED`, `EXTRACTION_CONFIRMED` | Status history zapisi su vraćeni u hronološkom redoslijedu | Pass |
| S9-BE-008 | Backend/API testiranje | Zabrana brisanja status history kroz API | Pozvati `DELETE /api/documents/{id}/status-history` | Backend vraća `405 Method Not Allowed`, a history zapisi ostaju sačuvani | API ne dozvoljava brisanje status history zapisa | Pass |
| S9-BE-009 | Backend/API testiranje | Dohvat komentara dokumenta | Pozvati `GET /api/documents/{id}/comments` | Backend vraća komentare vezane za dokument | Endpoint vraća listu komentara dokumenta | Pass |
| S9-BE-010 | Backend/API testiranje | Dodavanje komentara na dokument | Pozvati `POST /api/documents/{id}/comments` sa validnim sadržajem | Komentar se sprema i vraća kroz API response | Komentar je uspješno spremljen i vraćen u response-u | Pass |
| S9-BE-011 | Validaciono testiranje | Odbijanje praznog komentara | Poslati komentar sa praznim stringom ili whitespace sadržajem | Backend vraća `400 Bad Request` i komentar se ne sprema | Prazan komentar je odbijen i nije spremljen u bazu | Pass |
| S9-BE-012 | Validaciono testiranje | Limit dužine komentara | Poslati komentar duži od dozvoljenog limita | Backend vraća validacionu grešku i komentar se ne sprema | Predug komentar je odbijen validacijom | Pass |
| S9-BE-013 | Backend/API testiranje | Komentari za nepostojeći dokument | Pozvati `GET` ili `POST` comments endpoint za nepostojeći dokument | Backend vraća `404 NOT_FOUND` | Backend je vratio kontrolisanu grešku za nepostojeći dokument | Pass |
| S9-BE-014 | Backend/API testiranje | Status history za nepostojeći dokument | Pozvati `GET /api/documents/{id}/status-history` sa nepostojećim ID-em | Backend vraća `404 NOT_FOUND` | Backend je vratio kontrolisanu grešku za nepostojeći dokument | Pass |
| S9-BE-015 | Integration testiranje | Brisanje dokumenta sa status history i komentarima | Dokument ima status history, komentar i extraction podatke; pozvati delete dokumenta | Dokument i svi povezani workflow/extraction zapisi se brišu bez FK greške | Dokument, status history, komentari i extraction podaci su obrisani bez greške | Pass |
| S9-BE-016 | Integration testiranje | Extraction failure kreira status history | Simulirati grešku OCR providera pri extraction procesu | Dokument prelazi u `PROCESSING_FAILED`, a history ima akciju `EXTRACTION_FAILED` | Greška ekstrakcije je evidentirana u status history zapisu | Pass |
| S9-BE-017 | Integration testiranje | Manual classification review kreira status history | Uploadati `OTHER` dokument, classifier vrati nesiguran rezultat | Dokument prelazi u `NEEDS_CLASSIFICATION_REVIEW`, history ima odgovarajući zapis i ne završava kao `PROCESSING_FAILED` | Classification review tok je ispravno evidentiran u status history | Pass |
| S9-BE-018 | Backend/API testiranje | Admin može dohvatiti audit log | Admin pozove `GET /api/documents/{id}/audit-log` | Backend vraća `200 OK` i audit zapise dokumenta | Admin je uspješno dohvatio audit log | Pass |
| S9-BE-019 | Backend/API testiranje | Manager može dohvatiti audit log | Manager pozove `GET /api/documents/{id}/audit-log` | Backend vraća `200 OK` i audit zapise dokumenta | Manager je uspješno dohvatio audit log | Pass |
| S9-BE-020 | Backend/API testiranje | Operator nema pristup audit logu | Operator pozove `GET /api/documents/{id}/audit-log` | Backend vraća `403 Forbidden` | Operatoru je zabranjen pristup audit logu | Pass |
| S9-BE-021 | Backend/API testiranje | Approver nema pristup audit logu | Approver pozove `GET /api/documents/{id}/audit-log` | Backend vraća `403 Forbidden` | Approveru je zabranjen pristup audit logu | Pass |
| S9-BE-022 | Integration testiranje | Multi-tenant zaštita audit loga | Admin iz druge firme pokuša dohvatiti audit log dokumenta prve firme | Backend vraća `403 Forbidden` ili `404 NOT_FOUND` prema postojećem patternu | Korisnik iz druge firme nije mogao pristupiti audit logu | Pass |
| S9-BE-023 | Integration testiranje | `AuditLogService.log(...)` kreira zapis | Pozvati `AuditLogService.log(...)`, zatim dohvatiti audit log endpointom | Kreirani audit zapis se vraća kroz API | Audit zapis je kreiran i vidljiv kroz audit endpoint | Pass |
| S9-BE-024 | Integration testiranje | Audit log za update extraction fielda | Izmijeniti extraction field vrijednost kroz postojeći endpoint | Backend sprema audit zapis sa akcijom `FIELD_UPDATED` | Update extraction fielda je evidentiran u audit logu | Pass |
| S9-BE-025 | Backend/API testiranje | Permission greške vraćaju 403 | Korisnik bez dozvole pokuša pristupiti zaštićenoj workflow akciji | Backend vraća `403 Forbidden`, ne `400 Bad Request` | Permission greške se vraćaju kao `403 Forbidden` | Pass |
| S9-BE-026 | Backend/API testiranje | Admin može dodijeliti task | Admin pozove `POST /api/documents/{id}/tasks/assign` sa validnim operatorom i task tipom | Task se kreira sa statusom `OPEN` | Task je uspješno kreiran i dodijeljen korisniku | Pass |
| S9-BE-027 | Integration testiranje | Assignment kreira notification i audit log | Dodijeliti task dokumentu | Sistem kreira task, notification zapis i audit log zapis | Nakon assignmenta kreirani su task, notifikacija i audit log | Pass |
| S9-BE-028 | Validaciono testiranje | Blokiranje duplog aktivnog taska | Pokušati kreirati drugi aktivni task istog tipa za isti dokument | Backend vraća validacionu grešku `TASK_DUPLICATE_ACTIVE` | Dupli aktivni task istog tipa je odbijen | Pass |
| S9-BE-029 | Validaciono testiranje | Assignee mora imati odgovarajuću rolu | Pokušati dodijeliti `APPROVAL` task operatoru ili `CORRECTION` task approveru | Backend vraća validacionu grešku za neispravnu rolu assignee korisnika | Task nije dodijeljen korisniku pogrešne role | Pass |
| S9-BE-030 | Validaciono testiranje | Assignee mora pripadati istoj firmi | Pokušati dodijeliti task korisniku iz druge firme | Backend vraća `404 NOT_FOUND` ili odgovarajuću company isolation grešku | Task nije dodijeljen korisniku iz druge firme | Pass |
| S9-BE-031 | Backend/API testiranje | Assigned korisnik vidi svoje taskove | Assigned operator pozove `GET /api/tasks/my` | Backend vraća samo taskove dodijeljene tom korisniku | Korisnik vidi svoj dodijeljeni task | Pass |
| S9-BE-032 | Backend/API testiranje | Assigned korisnik može startati task | Assigned operator pozove `PATCH /api/tasks/{id}/start` | Task prelazi iz `OPEN` u `IN_PROGRESS` | Task je uspješno prešao u `IN_PROGRESS` | Pass |
| S9-BE-033 | Backend/API testiranje | Assigned korisnik može kompletirati svoj task | Assigned operator pozove `PATCH /api/tasks/{id}/complete` | Task prelazi u `COMPLETED` i bilježi `completedByUserId` | Task je uspješno kompletiran od strane assigned korisnika | Pass |
| S9-BE-034 | Backend/API testiranje | Manager može vidjeti sve taskove firme | Manager pozove `GET /api/tasks` | Backend vraća taskove firme | Manager je uspješno dobio listu taskova firme | Pass |
| S9-BE-035 | Backend/API testiranje | Operator ne može vidjeti sve taskove firme | Operator pozove `GET /api/tasks` | Backend vraća `403 Forbidden` | Operatoru je zabranjen pristup listi svih taskova | Pass |
| S9-BE-036 | Backend/API testiranje | Manager može cancelovati aktivni task | Manager pozove `PATCH /api/tasks/{id}/cancel` | Task prelazi u `CANCELLED` | Manager je uspješno cancelovao aktivni task | Pass |
| S9-BE-037 | Backend/API testiranje | Operator ne može cancelovati task | Operator pokuša `PATCH /api/tasks/{id}/cancel` | Backend vraća `403 Forbidden` | Operatoru nije dozvoljeno cancelovanje taska | Pass |
| S9-BE-038 | Backend/API testiranje | Non-assignee ne može startati tuđi task | Drugi operator iz iste firme pokuša startati task koji mu nije dodijeljen | Backend vraća `403 Forbidden` | Non-assignee operator nije mogao startati tuđi task | Pass |
| S9-BE-039 | Backend/API testiranje | Non-assignee ne može kompletirati tuđi task | Drugi operator iz iste firme pokuša kompletirati task koji mu nije dodijeljen | Backend vraća `403 Forbidden` | Non-assignee operator nije mogao kompletirati tuđi task | Pass |
| S9-BE-040 | Integration testiranje | Aktivni `EXTRACTION` task blokira drugog operatora pri pokretanju ekstrakcije | Admin dodijeli `EXTRACTION` task operatoru A, operator B pokuša pokrenuti extraction | Backend vraća `403 Forbidden` | Operator koji nije assignee nije mogao pokrenuti extraction | Pass |
| S9-BE-041 | Integration testiranje | Assigned operator može pokrenuti extraction za svoj task | Admin dodijeli `EXTRACTION` task operatoru A, operator A pokrene extraction | Backend dozvoljava akciju | Assigned operator je uspješno pokrenuo extraction | Pass |
| S9-BE-042 | Integration testiranje | Aktivni `EXTRACTION` task blokira drugog operatora pri confirm akciji | Dokument je `EXTRACTED`, `EXTRACTION` task je assigned operatoru A, operator B pokuša confirm | Backend vraća `403 Forbidden` | Non-assignee operator nije mogao confirmati extraction | Pass |
| S9-BE-043 | Integration testiranje | Assigned operator može confirmati extraction za svoj task | Dokument je `EXTRACTED`, `EXTRACTION` task je assigned operatoru A, operator A potvrdi extraction | Backend vraća `200 OK`, dokument prelazi u `READY_FOR_APPROVAL` | Assigned operator je uspješno confirmovao extraction | Pass |
| S9-BE-044 | Integration testiranje | `CORRECTION` task se koristi u correction flow-u | Dokument je `NEEDS_CORRECTION`, `CORRECTION` task je assigned operatoru A | Reconfirm akcija dozvoljena je operatoru A, a zabranjena operatoru B | Correction task permission logika radi očekivano | Pass |
| S9-BE-045 | Regresiono testiranje | Free-for-all flow bez active taska | Dokument nema aktivan task, operator odgovarajuće role pokreće extraction/confirm | Akcija je dozvoljena prema role/status pravilima | Sistem i dalje podržava rad bez striktne dodjele zadatka | Pass |

---

## 9.3 Frontend/UI testiranje

| ID testa | Vrsta testiranja | Funkcionalnost | Koraci testiranja | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S9-UI-001 | Frontend/UI testiranje | Prikaz novog statusa `NEEDS_CORRECTION` | Otvoriti listu ili detalje dokumenta sa statusom `NEEDS_CORRECTION` | Status badge prikazuje čitljiv label `Needs Correction` | Status se prikazuje korisniku kroz odgovarajući badge | Pass |
| S9-UI-002 | Frontend/UI testiranje | Prikaz status history panela | Otvoriti document detail stranicu za dokument sa history zapisima | Korisnik vidi history panel/timeline sa akcijama i statusima | Status history panel je prikazan na detaljima dokumenta | Pass |
| S9-UI-003 | Frontend/UI testiranje | Redoslijed status history zapisa | Uploadati dokument, pokrenuti extraction i confirmati extraction | History zapisi su prikazani hronološki | Timeline prikazuje događaje u pravilnom redoslijedu | Pass |
| S9-UI-004 | Frontend/UI testiranje | Empty state za status history | Otvoriti stari dokument bez history zapisa | UI prikazuje odgovarajući empty state ili fallback | Stranica ne puca i prikazuje fallback/empty state | Pass |
| S9-UI-005 | Frontend/UI testiranje | Prikaz komentara na dokumentu | Otvoriti document detail stranicu i sekciju komentara | Korisnik vidi postojeće komentare dokumenta | Komentari su prikazani na document detail stranici | Pass |
| S9-UI-006 | Frontend/UI testiranje | Dodavanje validnog komentara | Unijeti komentar i kliknuti save/post | Komentar se prikazuje u listi, textarea se čisti, prikazuje se success poruka | Komentar je uspješno dodan i prikazan u UI-u | Pass |
| S9-UI-007 | Frontend/UI testiranje | Validacija praznog komentara | Pokušati dodati prazan ili whitespace komentar | UI/backend odbija unos i prikazuje korisniku grešku | Prazan komentar nije dodan i prikazana je greška | Pass |
| S9-UI-008 | Frontend/UI testiranje | Komentari ostaju nakon refresh-a | Dodati komentar i refreshovati document detail stranicu | Komentar ostaje vidljiv nakon ponovnog učitavanja | Komentar je trajno spremljen i prikazan nakon refresh-a | Pass |
| S9-UI-009 | Frontend/UI testiranje | Admin vidi audit log sekciju | Ulogovati se kao Admin i otvoriti document detail | Audit log sekcija/tab je vidljiva | Admin vidi audit log sekciju | Pass |
| S9-UI-010 | Frontend/UI testiranje | Manager vidi audit log sekciju | Ulogovati se kao Manager i otvoriti document detail | Audit log sekcija/tab je vidljiva | Manager vidi audit log sekciju | Pass |
| S9-UI-011 | Frontend/UI testiranje | Operator ne vidi audit log sekciju | Ulogovati se kao Operator i otvoriti document detail | Audit log sekcija nije prikazana | Operator ne vidi audit log na frontend strani | Pass |
| S9-UI-012 | Frontend/UI testiranje | Approver ne vidi audit log sekciju | Ulogovati se kao Approver i otvoriti document detail | Audit log sekcija nije prikazana | Approver ne vidi audit log na frontend strani | Pass |
| S9-UI-013 | Frontend/UI testiranje | User-friendly prikaz audit akcija | Izvršiti update extraction fielda i otvoriti audit log | Akcija se prikazuje kao čitljiv tekst, npr. `Field updated`, bez raw JSON prikaza korisniku | Audit log prikazuje čitljiv naziv akcije i detalje | Pass |
| S9-UI-014 | Frontend/UI testiranje | Prikaz korisnika u audit logu | Otvoriti audit log koji sadrži `FIELD_UPDATED` zapis | UI prikazuje `userFullName` korisnika koji je izvršio akciju | Audit log prikazuje ime korisnika koji je izvršio akciju | Pass |
| S9-UI-015 | Frontend/UI testiranje | Admin/Manager task assignment forma | Otvoriti document detail kao Admin/Manager | Vidljiva je forma/sekcija za dodjelu taska | Task assignment UI je prikazan Admin/Manager korisniku | Pass |
| S9-UI-016 | Frontend/UI testiranje | Uspješno dodjeljivanje taska kroz UI | Izabrati korisnika, task type i kliknuti assign | Task se kreira, prikazuje se success toastr i active task informacija | Task je uspješno dodijeljen kroz UI | Pass |
| S9-UI-017 | Frontend/UI testiranje | Duplicate task greška u UI-u | Pokušati dodijeliti drugi aktivni task istog tipa za isti dokument | Korisnik dobija jasnu error poruku | UI prikazuje grešku i ne kreira dupli task | Pass |
| S9-UI-018 | Frontend/UI testiranje | My Tasks stranica | Ulogovati se kao assigned operator i otvoriti `/tasks/my` | Korisnik vidi svoje dodijeljene taskove | My Tasks stranica prikazuje dodijeljene taskove | Pass |
| S9-UI-019 | Frontend/UI testiranje | Navigacija sa My Tasks na dokument | Kliknuti task na My Tasks stranici | Otvara se odgovarajući document detail | Klik na task vodi na odgovarajući dokument | Pass |
| S9-UI-020 | Frontend/UI testiranje | Start task kroz UI | Assigned korisnik klikne start task | Task status prelazi u `IN_PROGRESS` i UI se osvježava | Task je uspješno startan i status je ažuriran | Pass |
| S9-UI-021 | Frontend/UI testiranje | Complete task kroz UI | Assigned korisnik pokuša kompletirati task | Task se kompletira samo kada je poslovna akcija završena ili je UI spriječio prerano kompletiranje | Task completion ponašanje je usklađeno sa poslovnim flow-om | Pass |
| S9-UI-022 | Frontend/UI testiranje | Cancel task kao Admin/Manager | Admin/Manager canceluje aktivni task | Task prelazi u `CANCELLED`, a UI prikazuje ažurirano stanje | Task je uspješno cancelovan kroz UI | Pass |
| S9-UI-023 | Frontend/UI testiranje | Overdue task prikaz | Otvoriti task kojem je prošao due date | UI prikazuje oznaku ili warning da je task overdue | Task sa prošlim rokom je označen kao overdue | Pass |
| S9-UI-024 | Frontend/UI testiranje | Info banner za dokument assigned drugom korisniku | Operator otvori dokument koji je aktivno dodijeljen drugom operatoru | UI prikazuje info/warning banner da je dokument dodijeljen drugom korisniku | Operator odmah vidi da je dokument assigned drugom korisniku | Pass |
| S9-UI-025 | Frontend/UI testiranje | Existing extraction flow nakon task UI izmjena | Kao dozvoljeni operator pokrenuti extraction, editovati field i confirmati extraction | Postojeći extraction UI i dalje radi bez regresije | Extraction flow je uspješno izvršen nakon task UI izmjena | Pass |
| S9-UI-026 | Frontend/UI testiranje | Responsivnost workflow sekcija | Otvoriti document detail na manjoj širini ekrana | Status history, komentari, audit log i task sekcije ostaju čitljive | Workflow sekcije ostaju pregledne na manjim ekranima | Pass |

---

## 9.4 Manualno API testiranje kroz Swagger/Postman

| ID testa | Vrsta testiranja | Alat | Endpoint / funkcionalnost | Koraci | Očekivani rezultat | Stvarni rezultat | Status |
|---|---|---|---|---|---|---|---|
| S9-API-001 | Backend/API testiranje | Swagger/Postman | Dohvat status history | Pozvati `GET /api/documents/{id}/status-history` | API vraća status history zapise dokumenta | Status history zapisi su uspješno vraćeni | Pass |
| S9-API-002 | Backend/API testiranje | Swagger/Postman | Dohvat komentara | Pozvati `GET /api/documents/{id}/comments` | API vraća komentare dokumenta | Komentari su uspješno vraćeni | Pass |
| S9-API-003 | Backend/API testiranje | Swagger/Postman | Dodavanje komentara | Pozvati `POST /api/documents/{id}/comments` sa validnim sadržajem | Komentar se uspješno kreira | Komentar je kreiran putem API-ja | Pass |
| S9-API-004 | Validaciono testiranje | Swagger/Postman | Dodavanje praznog komentara | Poslati prazan/whitespace komentar | API vraća `400 Bad Request` | Prazan komentar je odbijen | Pass |
| S9-API-005 | Backend/API testiranje | Swagger/Postman | Dohvat audit loga kao Admin/Manager | Pozvati `GET /api/documents/{id}/audit-log` sa dozvoljenom rolom | API vraća audit log | Audit log je vraćen dozvoljenom korisniku | Pass |
| S9-API-006 | Backend/API testiranje | Swagger/Postman | Dohvat audit loga kao Operator/Approver | Pozvati audit endpoint sa nedozvoljenom rolom | API vraća `403 Forbidden` | Pristup audit logu je odbijen | Pass |
| S9-API-007 | Backend/API testiranje | Swagger/Postman | Dodjela taska | Pozvati `POST /api/documents/{id}/tasks/assign` | API kreira task i vraća task podatke | Task je uspješno kreiran putem API-ja | Pass |
| S9-API-008 | Backend/API testiranje | Swagger/Postman | Dohvat mojih taskova | Pozvati `GET /api/tasks/my` | API vraća taskove dodijeljene trenutnom korisniku | Vraćeni su taskovi prijavljenog korisnika | Pass |
| S9-API-009 | Backend/API testiranje | Swagger/Postman | Start taska | Pozvati `PATCH /api/tasks/{id}/start` kao assigned korisnik | API mijenja status taska u `IN_PROGRESS` | Task je uspješno startan putem API-ja | Pass |
| S9-API-010 | Backend/API testiranje | Swagger/Postman | Complete taska | Pozvati `PATCH /api/tasks/{id}/complete` kao assigned korisnik | API mijenja status taska u `COMPLETED` kada su ispunjeni uslovi | Task je uspješno kompletiran kada su uslovi ispunjeni | Pass |
| S9-API-011 | Backend/API testiranje | Swagger/Postman | Cancel taska | Pozvati `PATCH /api/tasks/{id}/cancel` kao Admin/Manager | API mijenja status taska u `CANCELLED` | Task je uspješno cancelovan | Pass |
| S9-API-012 | Validaciono testiranje | Swagger/Postman | Non-assignee workflow akcija | Korisnik koji nije assignee pokuša process/confirm za dokument assigned drugom korisniku | API vraća `403 Forbidden` | Backend je blokirao akciju korisnika koji nije assignee | Pass |

---

## 9.5 End-to-end i regresiono testiranje

| ID testa | Vrsta testiranja | Scenario | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|
| S9-E2E-001 | End-to-end testiranje | Upload → status history → comment | Uploadati dokument, otvoriti detalje, provjeriti status history i dodati komentar | Upload kreira initial history, komentar se sprema i ostaje nakon refresh-a | Dokument ima initial history zapis, a komentar je uspješno spremljen | Pass |
| S9-E2E-002 | End-to-end testiranje | Upload → extraction → confirm sa status history | Uploadati dokument, pokrenuti extraction i confirmati extraction | History prikazuje `DOCUMENT_UPLOADED`, `EXTRACTION_COMPLETED`, `EXTRACTION_CONFIRMED`; status prelazi u `READY_FOR_APPROVAL` | Workflow je uspješno evidentiran kroz status history | Pass |
| S9-E2E-003 | End-to-end testiranje | Edit extraction field → audit log | Pokrenuti extraction, editovati jedno polje, otvoriti audit log kao Admin/Manager | Audit log sadrži `FIELD_UPDATED` zapis sa korisnikom i detaljima | Update polja je vidljiv u audit logu | Pass |
| S9-E2E-004 | End-to-end testiranje | Task assignment → My Tasks → start | Admin/Manager dodijeli task operatoru, operator otvori My Tasks i starta task | Task je vidljiv operatoru i prelazi u `IN_PROGRESS` | Task assignment i start flow su uspješno izvršeni | Pass |
| S9-E2E-005 | End-to-end testiranje | Assigned extraction flow | Admin dodijeli `EXTRACTION` task operatoru, operator pokrene extraction i potvrdi dokument | Assigned operator može završiti extraction flow, dokument prelazi u `READY_FOR_APPROVAL` | Assigned operator je uspješno završio extraction flow | Pass |
| S9-E2E-006 | End-to-end testiranje | Non-assignee blokiran u assigned flow-u | Dokument ima aktivan task za operatora A, operator B pokuša raditi process/confirm | Backend vraća `403`, a UI prikazuje odgovarajuću informaciju/grešku | Non-assignee korisnik je blokiran i korisniku je prikazana poruka | Pass |
| S9-E2E-007 | End-to-end testiranje | Cancel task i novi assignment | Admin/Manager canceluje aktivni task, zatim dodjeljuje novi task istog tipa | Nakon cancel-a moguće je kreirati novi aktivni task | Cancel i novi assignment rade očekivano | Pass |
| S9-E2E-008 | Regresiono testiranje | Upload flow nakon workflow foundation izmjena | Testirati upload validnog dokumenta | Upload funkcionalnost iz prethodnih sprintova i dalje radi | Upload dokumenta radi bez regresije | Pass |
| S9-E2E-009 | Regresiono testiranje | Existing extraction/edit/confirm flow bez assigned taska | Dokument nema aktivan task; operator odgovarajuće role radi extraction/edit/confirm | Sistem i dalje podržava free-for-all flow prema roli/statusu | Extraction/edit/confirm radi bez obaveznog assignmenta | Pass |
| S9-E2E-010 | Regresiono testiranje | Classification review flow nakon status history refaktora | OTHER dokument ode u classification review, zatim se ručno potvrdi tip | Statusi i metadata ostaju ispravni, history se popunjava | Classification review flow radi bez regresije | Pass |
| S9-E2E-011 | Regresiono testiranje | Role-based UI nakon audit/task izmjena | Provjeriti Admin, Manager, Operator i Approver prikaz document detail stranice | Svaka rola vidi samo dozvoljene sekcije i akcije | Role-based UI prikaz radi očekivano | Pass |
| S9-E2E-012 | Regresiono testiranje | Delete dokumenta nakon workflow zapisa | Dokument ima history, komentar, extraction i task/audit podatke; obrisati dokument | Delete ne puca zbog FK constrainta i ne ostavlja orphan zapise | Dokument i povezani zapisi su obrisani bez FK greške | Pass |

---

## 9.6 CI i automatizovano build testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S9-CI-001 | Automatizovano smoke testiranje | GitHub Actions | Backend build i testovi na PR-u | Otvoriti PR prema `develop`; CI pokreće backend job | Backend build i svi Maven testovi prolaze | Backend build i testovi su prošli na CI runneru | Pass |
| S9-CI-002 | Automatizovano smoke testiranje | GitHub Actions | Frontend build na PR-u | Otvoriti PR prema `develop`; CI pokreće frontend job | Angular build prolazi bez greške | Frontend build je prošao na CI runneru | Pass |
| S9-CI-003 | Regresiono testiranje | GitHub Actions | Required status checks za develop | PR prema `develop` mora imati zelene backend/frontend checkove | PR koji ruši backend ili frontend build ne može biti normalno merge-an | Required status checks su konfigurisani za backend i frontend build | Pass |
| S9-CI-004 | Automatizovano smoke testiranje | Lokalno razvojno okruženje | Backend build lokalno | Pokrenuti `mvnw clean install` / `./mvnw clean install` | Backend se kompajlira i testovi prolaze | Backend build i testovi su lokalno prošli | Pass |
| S9-CI-005 | Automatizovano smoke testiranje | Lokalno razvojno okruženje | Frontend build lokalno | Pokrenuti `npm run build` u frontend folderu | Angular build prolazi | Frontend build je lokalno prošao | Pass |

---

## 9.7 Deployment smoke testiranje

| ID testa | Vrsta testiranja | Okruženje | Funkcionalnost | Koraci | Očekivani ishod | Stvarni ishod | Status |
|---|---|---|---|---|---|---|---|
| S9-DEP-001 | Deployment smoke testiranje | Lokalno Docker okruženje | Pokretanje baze nakon novih workflow tabela | Pokrenuti PostgreSQL container i backend aplikaciju | Nove tabele/kolone se kreiraju bez greške | Baza i backend su se uspješno pokrenuli sa novim workflow modelom | Pass |
| S9-DEP-002 | Deployment smoke testiranje | Lokalno/Server | Enum/check constraint za `NEEDS_CORRECTION` | Pokrenuti ručni SQL za ažuriranje document status constrainta ako je potreban | Baza prihvata novi status `NEEDS_CORRECTION` | Constraint je ažuriran i novi status je prihvaćen | Pass |
| S9-DEP-003 | Deployment smoke testiranje | Lokalno/Server | Pokretanje backend containera nakon Sprint 9 izmjena | Pokrenuti backend container | Backend se pokreće bez greške | Backend container se uspješno pokrenuo | Pass |
| S9-DEP-004 | Deployment smoke testiranje | Lokalno/Server | Pokretanje frontend containera nakon workflow UI izmjena | Pokrenuti frontend container | Frontend je dostupan u browseru | Frontend container se uspješno pokrenuo i aplikacija je dostupna | Pass |
| S9-DEP-005 | Deployment smoke testiranje | Lokalno/Server | FE-BE komunikacija za status history i komentare | Otvoriti document detail, dohvatiti history i dodati komentar | Frontend uspješno poziva backend endpoint-e | Status history i comments endpointi rade kroz UI | Pass |
| S9-DEP-006 | Deployment smoke testiranje | Lokalno/Server | FE-BE komunikacija za audit log | Otvoriti document detail kao Admin/Manager | Audit log endpoint se poziva i UI prikazuje podatke/empty state | Audit log sekcija radi u aplikaciji | Pass |
| S9-DEP-007 | Deployment smoke testiranje | Lokalno/Server | FE-BE komunikacija za task assignment | Dodijeliti task kroz UI i otvoriti My Tasks | Task se kreira i prikazuje assigned korisniku | Task assignment i My Tasks rade u aplikaciji | Pass |
| S9-DEP-008 | Deployment smoke testiranje | Lokalno/Server | Regresiona provjera upload/extraction flow-a | Uploadati dokument, pokrenuti extraction i confirm | Prethodni upload/extraction flow radi nakon Sprint 9 izmjena | Upload, extraction i confirm rade bez regresije | Pass |

---

## Zaključak testiranja

Tokom Sprinta 9, testirane su workflow foundation funkcionalnosti, status history i komentari, audit log i permission sloj, kao i task assignment i My Tasks flow.

Zaključak:

- Testirano je proširenje backend modela novim workflow entitetima i statusom `NEEDS_CORRECTION`.
- Testirano je centralizovano bilježenje status promjena kroz `DocumentStatusTransitionService`.
- Testirano je da upload, extraction, confirm, failure i classification review tokovi kreiraju odgovarajuće status history zapise.
- Testirano je dodavanje, dohvat, validacija i prikaz komentara na dokumentu.
- Testirano je brisanje dokumenta sa povezanim workflow i extraction zapisima bez FK grešaka.
- Testirano je da audit log mogu vidjeti samo Admin i Manager korisnici, dok Operator i Approver nemaju pristup.
- Testirano je da `AuditLogService.log(...)` kreira zapis i da se audit log popunjava za postojeću akciju update extraction fielda.
- Testirano je task assignment ponašanje: dodjela taska, duplicate task validacija, My Tasks prikaz, start, complete i cancel task.
- Testirano je da aktivni assignment ograničava poslovne akcije na assigned korisnika, dok sistem i dalje podržava free-for-all rad kada aktivni task ne postoji.
- Testirani su frontend prikazi za status history, komentare, audit log, task assignment, My Tasks, overdue oznake i banner za dokument dodijeljen drugom korisniku.
- Izvršeno je regresiono testiranje upload, extraction, edit, confirm i classification review tokova nakon uvođenja workflow sloja.
- Dodan je i provjeren basic CI koji na PR-ovima pokreće backend build/testove i frontend build.
- Svi evidentirani testovi za Sprint 9 imaju status `Pass`.