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