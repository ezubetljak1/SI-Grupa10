# Korisničko Uputstvo – Docflow
### Sistem za obradu i odobravanje dokumenata | ETF Sarajevo 2025/26 | Grupa 10

---

## 1. Kome je sistem namijenjen

Docflow je web aplikacija za digitalnu obradu poslovnih dokumenata. Sistem podržava upload dokumenata, automatsko prepoznavanje teksta (OCR) i klasifikaciju putem Google Document AI, pregled i korekciju izdvojenih podataka, workflow dodjele zadataka, tok odobravanja i generisanje strukturiranog XML izlaza.

Aplikacija je namijenjena kompanijama koje žele smanjiti ručni rad pri obradi dokumenata kao što su fakture, računi, bankovni izvodi i obrasci. Svaka kompanija ima vlastiti radni prostor (workspace) i vlastite korisnike sa različitim ulogama.

Sistem ne zahtijeva tehničko znanje. Sve akcije prate jasne korake, vizualne statusne oznake (badge) i poruke o uspjehu, upozorenju ili grešci.


---

## 2. Korisničke uloge

Docflow definiše četiri korisničke uloge unutar jedne kompanije:

| Uloga | Opis | Glavne mogucnosti |
|---|---|---|
| **Admin** | Administrator kompanije s potpunim pristupom | Sve mogućnosti sistema, plus upravljanje korisnicima i njihovim ulogama, pregled audit loga i dashboarda |
| **Manager** | Koordinator workflow-a | Pregled dashboarda i završenih dokumenata, dodjela zadataka, pregled audit loga; ne može sam mijenjati izdvojene podatke ni odobravati |
| **Operator** | Obrađivač dokumenata | Upload dokumenata, pokretanje ekstrakcije, pregled i korekcija izdvojenih polja, potvrda ekstrakcije |
| **Approver** | Osoba koja donosi odluku | Pregled potvrđene ekstrakcije, odobravanje, vraćanje na doradu ili odbijanje dokumenta |

> **Napomena:** Ne postoji javna registracija korisnika. Putem forme se registruje samo **kompanija**, čime se kreira prvi **Admin** nalog. Sve ostale korisnike i njihove uloge kreira isključivo Administrator kompanije sa stranice **Users**.

### 2.1 Šta koja uloga znači u aplikaciji

Aplikacija svakom korisniku na stranici **Profile** prikazuje opis vlastite uloge:

- **Admin** – Potpuni administrativni pristup kompaniji, uključujući upravljanje korisnicima i ulogama.
- **Manager** – Upravljanje workflow-om dokumenata, pregled aktivnosti radnog prostora i koordinacija korisnika.
- **Operator** – Upload dokumenata i pokretanje akcija obrade/ekstrakcije.
- **Approver** – Pregled izdvojenih podataka i odobravanje dokumenata.

---

## 3. Prijava i pristup sistemu

Docflow koristi **Keycloak** za autentifikaciju. Ne postoji zasebna login forma unutar aplikacije; prijava se odvija preko sigurne Keycloak stranice.

### 3.1 Prijava u sistem

1. Otvorite adresu aplikacije u pretraživaču (produkcijski URL: `https://docflow.page`).
2. Pošto pristup zahtijeva prijavu, sistem vas **automatski preusmjerava** na Keycloak stranicu za prijavu (na adresi `auth.docflow.page`).
3. U polje **Email** unesite svoju email adresu, a u polje **Password** lozinku.
4. Kliknite **"Sign in"**.

**Očekivani rezultat:** Sistem vas vraća u aplikaciju i preusmjerava na listu dokumenata (**Documents**). U gornjem desnom uglu prikazuje se vaše ime i vaša uloga.

![Keycloak stranica za prijavu](Slike%20ekrana/00_login.png)

### 3.2 Registracija kompanije

Novi radni prostor kreira se registracijom kompanije. Time se automatski kreira prvi administratorski nalog.

1. Na stranici za prijavu, na dnu, kliknite link **"New company? Register company"** (registracija je dostupna i kroz link **"Register company"** u aplikaciji bez prijave).
2. Popunite formu **Company details**:
   - **Company name** – naziv kompanije
   - **Company email** – službeni email kompanije
   - **Company address** – adresa kompanije
   - **Admin first name** / **Admin last name** – ime i prezime prvog administratora
   - **Admin email** – email administratora (na njega stiže poziv za postavljanje lozinke)
3. Kliknite **"Register company"**.

**Očekivani rezultat:** Pojavljuje se zelena potvrda *"... registered successfully."*. Sistem kreira kompaniju i administratorski nalog, te šalje email za postavljanje lozinke administratoru.


### 3.3 Prvo postavljanje lozinke

Novi korisnici (uključujući prvog administratora) ne dobijaju gotovu lozinku. Umjesto toga, sistem im šalje **email za postavljanje lozinke**.

1. Otvorite email koji je sistem poslao.
2. Kliknite na link za postavljanje lozinke.
3. Unesite i potvrdite novu lozinku.
4. Vratite se u aplikaciju i prijavite se (vidi 3.1).

> Dok korisnik ne postavi lozinku, njegov nalog ima status **Pending setup** na stranici **Users**.

### 3.4 Promjena lozinke i odjava

- **Promjena lozinke:** Otvorite **Profile** → kartica **Security** → kliknite **"Change password"**. Sistem vas preusmjerava na Keycloak gdje mijenjate lozinku.
- **Odjava:** U gornjem desnom uglu kliknite **Logout**.

---

## 4. Demo kredencijali

Docflow nema unaprijed ugrađene (hardcodirane) demo naloge. Nalozi se kreiraju kroz registraciju kompanije i kroz stranicu **Users**. Za demonstraciju smo popunili tabelu ispod stvarnim nalozima kreiranim na demo okruženju.

| Uloga | Email | Lozinka |
|---|---|---|
| Admin | `rqctnvmwgxzjhfqbaa@jbsze.com` | `Password123` |
| Manager | `kajerix372@okcpress.com` | `Password123` |
| Operator | `jelexej333@ameady.com` | `Password123` |
| Approver | `o43gbgg7xc@wshu.net` | `Password123` |

> Da biste vidjeli sve funkcionalnosti sistema, prijavite se redom sa svakim demo nalogom.

---

## 5. Opis glavnih ekrana

| Ekran | URL putanja | Ko ima pristup |
|---|---|---|
| Register company | `/register-company` | Svi (bez prijave) |
| Dashboard | `/dashboard` | Admin, Manager |
| Upload Document | `/documents/upload` | Admin, Operator |
| Documents (lista) | `/documents` | Svi prijavljeni |
| Document Details | `/documents/{id}` | Svi prijavljeni |
| My tasks | `/tasks/my` | Admin, Operator, Approver |
| Completed documents | `/review` | Admin, Manager |
| Company Users | `/company/users` | Admin |
| Profile | `/profile` | Svi prijavljeni |

Na lijevoj strani ekrana nalazi se **bočna navigacija (Sidebar)** sa logom *Docflow* i linkovima koji se prikazuju ovisno o ulozi: **Dashboard**, **Upload**, **Documents**, **Profile**, **My tasks**, **Completed**, **Users**.

U gornjem desnom uglu (topbar) nalazi se **dugme s inicijalima korisnika** gdje na klik možemo vidjeti nepročitane notifikacije i dugme **Logout**.

![Glavni izgled aplikacije sa sidebarom i topbarom](Slike%20ekrana/01_layout_sidebar.png)

---

### 5.1 Dashboard

Pregledna stranica za **Admina** i **Managera**. Prikazuje KPI kartice, zdravlje workflow-a (Workflow health), raspodjelu dokumenata po statusu (Documents by workflow status), opterećenje tima (Responsible users) i preporučene sljedeće akcije (What to do next).

![Dashboard](Slike%20ekrana/02_dashboard.png)

---

### 5.2 Upload Document

Stranica za upload dokumenta. Dostupna **Adminu** i **Operatoru**. Omogućava odabir fajla (drag & drop ili klik), unos opcionalnog naziva i odabir tipa dokumenta.

- Podržani formati: **PDF, JPG, JPEG, PNG**. Maksimalna veličina: **10 MB**.
- Tipovi dokumenta (**Document type**): **Invoice**, **Receipt / Expense**, **Bank statement**, **Form**, **Other / Auto classify**.

![Upload dokumenta](Slike%20ekrana/03_upload.png)

> Ako odaberete **Other / Auto classify**, sistem prvo pokušava automatski prepoznati tip dokumenta. Ako pouzdanost klasifikacije bude niska, dokument će zahtijevati ručnu potvrdu tipa.

---

### 5.3 Documents (lista dokumenata)

Prikazuje sve dokumente kompanije sa pretragom i filterima (po nazivu, statusu, tipu, dodijeljenom korisniku i datumu kreiranja). Svaki red prikazuje naziv, tip, statusnu oznaku i velicinu.

Uz svaki dokument dostupne su akcije: **View details** (detalji), **Download** (preuzimanje) i **Delete** (brisanje, uz potvrdu).

![Lista dokumenata](Slike%20ekrana/04_documents_list.png)

---

### 5.4 Document Details (detalji dokumenta)

Centralni ekran za rad sa jednim dokumentom. Sadržaj se mijenja ovisno o statusu dokumenta i ulozi korisnika. Stranica moze sadržavati sljedeće sekcije:

- **Document information** – status, tip, veličina, datum uploada; baner aktivnog zadatka; rezultat AI klasifikacije.
- **Task assignment** – dodjela zadatka (vidljivo Adminu/Manageru).
- **Approval decision** – odluka o odobravanju (kada je status *Ready for Approval*).
- **XML output** – generisanje, pregled i preuzimanje XML-a.
- **Document preview** – pregled PDF-a ili slike.
- **Classification review required** – potvrda tipa kada AI nije siguran.
- **Extracted fields** – izdvojena polja sa korekcijama.
- **Status history** – hronologija promjena statusa.
- **Audit log** – revizijski trag (Admin/Manager).
- **Comments** – komentari uz dokument.

![Detalji dokumenta](Slike%20ekrana/05_document_details.png)

---

### 5.5 Extracted fields (izdvojena polja)

Tabela automatski izdvojenih polja sa kolonama **Field**, **Value**, **Confidence** i **Review status**. Polja niske pouzdanosti i datumi koje treba provjeriti posebno su označeni (warning). Ručno dodana polja imaju oznaku **Manual**, a obavezna prazna polja oznaku **Required**.

Operator moze urediti vrijednost (ikona olovke), obrisati polje (ikona kante), dodati novo polje (**Add field**) i potvrditi ekstrakciju (**Confirm extraction**).

![Izdvojena polja](Slike%20ekrana/06_extracted_fields.png)

---

### 5.6 Approval decision (odluka o odobravanju)

Kada je dokument u statusu **Ready for Approval**, Approver (ili Admin) vidi panel **Approval decision** sa poljem za komentar i tri dugmeta: **Approve**, **Return for correction** i **Reject**.

![Approval panel](Slike%20ekrana/07_approval_decision.png)

---

### 5.7 XML output

Za odobren (**Approved**) dokument dostupna je sekcija **XML output**. Ovdje se generiše strukturirani XML (**Generate XML** / **Regenerate XML**), pregleda njegov sadržaj (**Show XML preview**), preuzima (**Download XML**) i finalizira obrada (**Complete processing**).

![XML output](Slike%20ekrana/08_xml_output.png)

---

### 5.8 My tasks (moji zadaci)

Lista zadataka dodijeljenih prijavljenom korisniku, sa tabovima **Open**, **Completed** i **Cancelled**. Svaki red prikazuje dokument, tip zadatka (**Extraction**, **Correction**, **Approval**), status i rok. Dugme **Open** vodi na detalje dokumenta, a **Start** započinje zadatak.

![Moji zadaci](Slike%20ekrana/09_my_tasks.png)
![Moji zadaci](Slike%20ekrana/09_my_tasks2.png)

---

### 5.9 Completed documents

Stranica za **Admina** i **Managera** (link **Completed** u sidebaru). Prikazuje dokumente koji su prošli kroz workflow, sa dugmetom **Open** za pregled svakog zapisa.

![Zavrseni dokumenti](Slike%20ekrana/10_completed.png)

---

### 5.10 Company Users (korisnici kompanije)

Stranica za **Admina**. Prikazuje statistiku korisnika (Total users, Active, Pending setup, Admins), formu za kreiranje korisnika i direktorij korisnika sa pretragom. Admin može mijenjati ulogu korisnika i aktivirati/deaktivirati nalog.

![Korisnici kompanije](Slike%20ekrana/11_users.png)
![Korisnici kompanije](Slike%20ekrana/11_users2.png)

---

### 5.11 Profile i notifikacije

Stranica **Profile** prikazuje podatke o nalogu, dugme **Change password**, opis uloge i **Notification center** sa nepročitanim i pročitanim obavijestima. Notifikacije su dostupne i kroz **dugme s inicijalima korisnika** u topbaru.

![Profil i notifikacije](Slike%20ekrana/12_profile_notifications.png)

---

## 6. Korisničke akcije – korak po korak

### 6.1 Operator: Upload dokumenta

1. U sidebaru kliknite **"Upload"**.
2. Prevucite fajl u označeno polje ili kliknite da odaberete fajl (PDF, JPG, JPEG ili PNG, do 10 MB).
3. Opcionalno unesite **Document name**.
4. Odaberite **Document type** (npr. *Invoice*). Za nepoznat dokument odaberite *Other / Auto classify*.
5. Kliknite **"Upload document"**.

**Očekivani rezultat:** Pojavljuje se poruka *"Document uploaded successfully."* i kartica **Last uploaded document** sa statusom **Uploaded**. Dokument se pojavljuje u listi **Documents**.

---

### 6.2 Operator: Pokretanje ekstrakcije (OCR)

1. Otvorite dokument iz liste (**View details**).
2. U sekciji **Extracted fields** kliknite **"Run extraction"**.

**Očekivani rezultat:** Sistem pokreće OCR/AI obradu. Po završetku status postaje **Extracted** i prikazuje se tabela izdvojenih polja. Ako obrada ne uspije, status je **Processing Failed** (pokušajte ponovo dugmetom **"Retry extraction"**).

> Ako je dokument uploadovan kao *Other* i AI ne moze pouzdano odrediti tip, status postaje **Needs Classification Review** (vidi 6.3).

---

### 6.3 Operator: Potvrda tipa dokumenta (klasifikacijski review)

1. Otvorite dokument u statusu **Needs Classification Review**.
2. U sekciji **Classification review required** pogledajte AI prijedlog tipa i pouzdanost.
3. U polju **Confirm correct document type** odaberite tačan tip.
4. Kliknite **"Confirm document type"**.

**Očekivani rezultat:** Dokument se vraća u status **Uploaded** i ekstrakcija se može ponovo pokrenuti sa ispravnim tipom.

---

### 6.4 Operator: Pregled, korekcija i dodavanje polja

**Uređivanje vrijednosti:**
1. U tabeli **Extracted fields** uz polje kliknite ikonu olovke.
2. Unesite ispravnu vrijednost. Za datume koristite evropski format **DD.MM.YYYY** ili **DD/MM/YYYY**.
3. Potvrdite kvačicom ili odustanite.

**Dodavanje polja:**
1. Kliknite **"Add field"**.
2. Odaberite polje s liste ili **Custom field...** (uz unos naziva).
3. Unesite vrijednost i kliknite **"Add field"**.

**Brisanje polja:** Kliknite ikonu kante. Za obavezno polje vrijednost se briše, ali polje ostaje kao prazni placeholder (**Required**) koji treba popuniti prije potvrde.

> Polja niske pouzdanosti (**Confidence**) i datumi oznaceni sa **Review needed** treba provjeriti prije potvrde ekstrakcije.

---

### 6.5 Operator: Potvrda ekstrakcije

1. Provjerite sva polja, posebno ona označena upozorenjem.
2. Popunite eventualna obavezna prazna polja.
3. Kliknite **"Confirm extraction"** (ili **"Reconfirm extraction"** ako je dokument bio vraćen na doradu).

**Očekivani rezultat:** Status dokumenta postaje **Ready for Approval** i pojavljuje se poruka *"Extraction confirmed."*. Dokument je spreman za odluku Approvera.

---

### 6.6 Admin/Manager: Dodjela zadatka

1. Otvorite dokument (**View details**).
2. U sekciji **Task assignment** odaberite **Task type** (**Extraction**, **Correction** ili **Approval**).
3. Odaberite **Assignee** (osobu) i opcionalno **Due date** (rok).
4. Kliknite **"Assign task"**.

**Očekivani rezultat:** Kreira se zadatak sa statusom **Open**, dodijeljeni korisnik dobija notifikaciju, a zadatak se pojavljuje u njegovoj listi **My tasks**. Aktivni zadatak može se otkazati dugmetom **Cancel**.

---

### 6.7 Approver: Odluka o dokumentu

1. Otvorite dokument u statusu **Ready for Approval**.
2. Pregledajte potvrđenu ekstrakciju i, po potrebi, sadržaj dokumenta u **Document preview**.
3. U sekciji **Approval decision** unesite **Decision comment** i odaberite jednu od akcija:
   - **Approve** – odobravanje (status postaje **Approved**)
   - **Return for correction** – vraćanje Operatoru na doradu (status **Needs Correction**)
   - **Reject** – odbijanje dokumenta (status **Rejected**)

**Očekivani rezultat:** Status dokumenta se mijenja, bilježi se komentar i zapis u **Status history** i **Audit log**, a odgovornom korisniku se šalje notifikacija.

---

### 6.8 Operator: Dorada vraćenog dokumenta

1. Otvorite dokument u statusu **Needs Correction**.
2. U sekciji **Extracted fields** pročitajte zahtjev za korekciju u panelu **Correction requested**.
3. Ispravite ili dopunite polja (vidi 6.4).
4. Kliknite **"Reconfirm extraction"**.

**Očekivani rezultat:** Status se ponovo postavlja na **Ready for Approval** i dokument se vraća Approveru.

---

### 6.9 Generisanje XML-a i zavrsetak obrade

1. Otvorite **Approved** dokument.
2. U sekciji **XML output** kliknite **"Generate XML"** (ili **"Regenerate XML"** za ponovno generisanje).
3. Pogledajte sadržaj dugmetom **"Show XML preview"** i preuzmite ga dugmetom **"Download XML"**.
4. Kada je sve u redu, kliknite **"Complete processing"** i potvrdite u dijalogu.

**Očekivani rezultat:** Status dokumenta postaje **Completed**. XML ostaje dostupan za pregled i preuzimanje, ali daljnje workflow akcije više nisu moguće.

---

### 6.10 Admin: Kreiranje korisnika i upravljanje ulogama

**Kreiranje korisnika:**
1. U sidebaru kliknite **"Users"**.
2. U sekciji **Create user** unesite **First name**, **Last name**, **Email** i odaberite **Role**.
3. Kliknite **"Add user"**.

**Očekivani rezultat:** Korisnik se kreira sa statusom **Pending setup** i automatski dobija email za postavljanje lozinke.

**Promjena uloge:** U direktoriju korisnika promijenite vrijednost u koloni **Role**.

**Aktivacija/deaktivacija:** Kliknite **"Deactivate"** (uz potvrdu) ili **"Activate"**. Deaktivirani korisnik ne može pristupiti sistemu dok ga Admin ponovo ne aktivira.

> Korisnik ne može mijenjati vlastitu ulogu ni status (te akcije su onemogućene za vlastiti nalog).

---

### 6.11 Komentari uz dokument

1. Otvorite dokument i pomjerite se do sekcije **Comments**.
2. U polje **"Add a general comment"** unesite tekst.
3. Kliknite **"Add comment"**.

**Očekivani rezultat:** Komentar se pojavljuje u listi komentara dokumenta i vidljiv je ostalim korisnicima.

---

## 7. Workflow dokumenta (životni ciklus)

Tipičan tok dokumenta kroz sistem:

```
Upload  →  Run extraction  →  (Needs Classification Review)  →  Extracted
   →  pregled / korekcija polja  →  Confirm extraction  →  Ready for Approval
   →  Approve / Return for correction / Reject
   →  (Approved)  →  Generate XML  →  Download / preview  →  Complete processing  →  Completed
```

Pregled statusa dokumenta:

| Status | Znacenje |
|---|---|
| **Uploaded** | Dokument je uploadovan, čeka ekstrakciju. |
| **Processing Failed** | OCR/ekstrakcija nije uspjela; pokušati ponovo. |
| **Extracted** | Polja su izdvojena, čeka pregled i potvrdu. |
| **Needs Classification Review** | AI nije pouzdano odredio tip; potrebna ručna potvrda. |
| **Ready for Approval** | Ekstrakcija potvrđena, čeka odluku Approvera. |
| **Needs Correction** | Approver vratio dokument na doradu. |
| **Approved** | Dokument odobren; može se generisati XML. |
| **Rejected** | Dokument odbijen. |
| **Completed** | Obrada završena; XML finaliziran. |

---

## 8. Notifikacijski sistem

Docflow šalje **in-app notifikacije** za ključne događaje u workflow-u:

- Dodjela zadatka korisniku (**Document assigned**)
- Dokument spreman za odobravanje (**Ready for approval**)
- Dokument vraćen na doradu (**Returned for correction**)
- Dokument odbijen (**Rejected**)
- Dokument odobren (**Approved**)

Notifikacije su dostupne na tri mjesta:
- **Dugme u topbaru** – brzi pregled posljednjih notifikacija i brojač nepročitanih.
- **Profile → Notification center** – pregled svih obavijesti, podijeljenih na nepročitane i pročitane.
- Klik na notifikaciju vodi na relevantnu stranicu (dokument ili **My tasks**) i označava je kao pročitanu.

Dugme **"Mark all as read"** označava sve notifikacije kao pročitane.

> **Email podsjetnik:** Sistem automatski šalje email podsjetnik za nepročitane notifikacije starije od **24 sata**. Email je grupisani digest, a ne zamjena za in-app notifikacije.

---

## 9. Ograničenja sistema

### Šta korisnici ne mogu raditi

**Operator ne može:**
- Odobravati, vraćati na doradu ni odbijati dokumente
- Dodjeljivati zadatke drugim korisnicima
- Pristupiti stranicama **Dashboard**, **Completed** i **Users**

**Approver ne može:**
- Uploadovati dokumente ni mijenjati izdvojena polja
- Dodjeljivati zadatke
- Pristupiti stranici **Users**

**Manager ne može:**
- Sam mijenjati izdvojena polja ni donositi odluku o odobravanju
- Pristupiti stranici **Users** (upravljanje korisnicima je isključivo Admin pravo)

**Svi korisnici ne mogu:**
- Mijenjati vlastitu ulogu ili status naloga
- Pristupiti stranicama za koje njihova uloga nema pravo (sistem provjerava pristup)
- Izvršavati workflow akciju na zadatku koji je dodijeljen drugom korisniku

### Tehnička ograničenja

- Podržani formati za upload su **PDF, JPG, JPEG i PNG**; maksimalna veličina fajla je **10 MB**.
- Tačnost OCR/ekstrakcije zavisi od kvaliteta dokumenta i vanjskog servisa (Google Document AI); izdvojena polja uvijek treba provjeriti prije potvrde.
- Email isporuka (podsjetnici i poziv za postavljanje lozinke) zavisi od vanjskog SMTP providera.
- Reset lozinke i prvo postavljanje lozinke odvijaju se preko Keycloak email linka.
- Regenerisanje XML-a zamjenjuje prethodni aktivni XML; puna historija verzija nije podržana.
