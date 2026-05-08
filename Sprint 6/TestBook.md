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
