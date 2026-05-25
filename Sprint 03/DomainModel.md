# Domain Model

## Uvod

U nastavku je prikazan domenski model sistema za obradu poslovnih dokumenata uz podršku OCR/AI obrade, validacije, odobravanja i generisanja XML izlaza.  
Model obuhvata glavne entitete sistema, njihove atribute, međusobne veze, kao i poslovna pravila važna za njegovo funkcionisanje.

## ERD prikaz

U nastavku je dat entity-relationship dijagram koji vizuelno prikazuje entitete, njihove ključne atribute i veze između njih.

![ERD dijagram](./SI_ERD.png)

---

## Glavni entiteti

| Naziv | Svrha |
|---|---|
| Company | Predstavlja firmu registrovanu u sistemu. |
| User | Predstavlja korisnika sistema koji pripada određenoj firmi i ima definisanu ulogu. |
| Role | Predstavlja ulogu korisnika u sistemu. |
| Document | Predstavlja dokument uploadovan u sistem koji prolazi kroz proces obrade, validacije i odobravanja. |
| Extraction | Predstavlja rezultat OCR/AI obrade dokumenta. |
| ExtractionField | Predstavlja pojedinačno izdvojeno polje iz rezultata ekstrakcije. |
| StatusHistory | Predstavlja historiju promjena statusa dokumenta. |
| Comment | Predstavlja komentar dodan na dokument radi komunikacije, pojašnjenja ili povratne informacije. |
| XmlOutput | Predstavlja generisani XML izlaz vezan za dokument. |
| AuditLog | Predstavlja evidenciju ključnih akcija izvršenih nad dokumentima u sistemu. |
| Notification | Predstavlja obavještenje koje sistem šalje korisniku u vezi sa dokumentom ili zadatkom. |
| Task | Predstavlja zadatak dodijeljen konkretnom korisniku u vezi sa obradom, validacijom ili odobravanjem dokumenta. |

---

## Detaljan opis entiteta

### Company

Predstavlja pravno lice (firmu) registrovanu u sistemu.

**Atributi:**
- `id: Number` – jedinstveni identifikator firme  
- `name: String` – naziv firme  
- `address: String` – adresa firme  
- `email: String` – kontakt email firme  
- `registration_date: Date` – datum registracije  

**Veze:**
- 1:N sa **User** 
- 1:N sa **Document**  

---

### Role

Predstavlja ulogu korisnika u sistemu. 

**Atributi:**
- `id: Number` – jedinstveni identifikator  
- `name: String` – naziv uloge 

**Veze:**
- 1:N sa **User**  

---

### User

Predstavlja korisnika sistema.

**Atributi:**
- `id: Number` – jedinstveni identifikator  
- `company_id: Number` – referenca na firmu kojoj korisnik pripada  
- `role_id: Number` – referenca na ulogu korisnika  
- `first_name: String` – ime korisnika  
- `last_name: String` – prezime korisnika  
- `email: String` – email adresa korisnika (koristi se za prijavu i komunikaciju)  
- `password_hash: String` – hashirana lozinka korisničkog naloga  
- `account_status: Enum` – status naloga 

**Veze:**
- N:1 sa **Company**  
- N:1 sa **Role**  
- 1:N sa **Document** 
- 1:N sa **Task** 
- 1:N sa **Comment**  
- 1:N sa **Notification**  
- 1:N sa **AuditLog**  
- 1:N sa **StatusHistory**  

---

### Document

Centralni entitet sistema koji predstavlja dokument.

**Atributi:**
- `id: Number` – jedinstveni identifikator dokumenta  
- `company_id: Number` – referenca na firmu kojoj dokument pripada  
- `created_by_user_id: Number` – referenca na korisnika koji je uploadovao dokument  
- `title: String` – naziv ili opis dokumenta  
- `file_type: String` – tip fajla 
- `document_type: Enum` – tip dokumenta   
- `storage_path: String` – putanja gdje je dokument fizički pohranjen  
- `upload_date: DateTime` – datum i vrijeme upload-a dokumenta  
- `file_size: Number` – veličina fajla u bajtima  
- `document_status: Enum` – trenutni status dokumenta u workflow-u  

**Veze:**
- N:1 sa **Company**  
- N:1 sa **User** (creator)  
- 1:1 sa **Extraction**  
- 1:N sa **StatusHistory**  
- 1:N sa **Comment**  
- 1:N sa **Notification**  
- 1:N sa **Task**  
- 1:N sa **AuditLog**  
- 1:1 sa **XmlOutput**  

---

### Extraction

Predstavlja rezultat OCR/AI obrade dokumenta.

**Atributi:**
- `id: Number` – jedinstveni identifikator ekstrakcije  
- `document_id: Number` – referenca na dokument nad kojim je izvršena ekstrakcija  
- `raw_json: Text` – sirovi JSON rezultat koji sadrži sve izdvojene podatke  
- `extraction_time: DateTime` – vrijeme kada je ekstrakcija izvršena  

**Veze:**
- 1:1 sa **Document**  
- 1:N sa **ExtractionField**  

---

### ExtractionField

Predstavlja pojedinačno izdvojeno polje.

**Atributi:**
- `id: Number` – jedinstveni identifikator polja  
- `extraction_id: Number` – referenca na ekstrakciju kojoj polje pripada  
- `field_name: String` – naziv polja  
- `value: Text` – vrijednost izdvojenog podatka  
- `confidence: Decimal` – nivo pouzdanosti ekstrakcije  
- `is_corrected: Boolean` – označava da li je vrijednost ručno ispravljena  

**Veze:**
- N:1 sa **Extraction**  

---

### StatusHistory

Predstavlja historiju promjena statusa dokumenta.

**Atributi:**
- `id: Number` – jedinstveni identifikator zapisa  
- `document_id: Number` – referenca na dokument  
- `old_status: Enum` – prethodni status dokumenta  
- `new_status: Enum` – novi status dokumenta  
- `changed_at: DateTime` – vrijeme promjene statusa  
- `user_id: Number` – korisnik koji je izvršio promjenu  

**Veze:**
- N:1 sa **Document**  
- N:1 sa **User**  

---

### Comment

Predstavlja komentar na dokument.

**Atributi:**
- `id: Number` – jedinstveni identifikator komentara  
- `document_id: Number` – referenca na dokument  
- `user_id: Number` – korisnik koji je ostavio komentar  
- `content: Text` – sadržaj komentara  
- `created_at: DateTime` – vrijeme kreiranja komentara  

**Veze:**
- N:1 sa **Document**  
- N:1 sa **User**  

---

### XmlOutput

Predstavlja generisani XML dokument.

**Atributi:**
- `id: Number` – jedinstveni identifikator XML izlaza  
- `document_id: Number` – referenca na dokument  
- `storage_path: String` – putanja gdje je XML fajl pohranjen  
- `generated_at: DateTime` – vrijeme generisanja XML-a  

**Veze:**
- 1:1 sa **Document**  

---

### AuditLog

Predstavlja zapis o akcijama u sistemu.

**Atributi:**
- `id: Number` – jedinstveni identifikator zapisa  
- `document_id: Number` – referenca na dokument nad kojim je akcija izvršena  
- `user_id: Number` – korisnik koji je izvršio akciju  
- `action: String` – naziv akcije   
- `timestamp: DateTime` – vrijeme izvršavanja akcije  
- `details: Text` – dodatni detalji o akciji  

**Veze:**
- N:1 sa **Document**  
- N:1 sa **User**  

---

### Notification

Predstavlja sistemska obavještenja.

**Atributi:**
- `id: Number` – jedinstveni identifikator notifikacije  
- `user_id: Number` – korisnik kojem je notifikacija poslana  
- `document_id: Number` – referenca na dokument na koji se notifikacija odnosi  
- `text: String` – sadržaj notifikacije  
- `type: String` – tip notifikacije  
- `is_read: Boolean` – označava da li je notifikacija pročitana  
- `created_at: DateTime` – vrijeme kreiranja notifikacije  

**Veze:**
- N:1 sa **User**  
- N:1 sa **Document**  

---

### Task

Predstavlja zadatak vezan za dokument.

**Atributi:**
- `id: Number` – jedinstveni identifikator zadatka  
- `document_id: Number` – referenca na dokument  
- `assigned_user_id: Number` – korisnik kojem je zadatak dodijeljen  
- `task_type: String` – tip zadatka  
- `status: String` – status zadatka 
- `due_date: DateTime` – rok za izvršenje zadatka  
- `created_at: DateTime` – vrijeme kreiranja zadatka  

**Veze:**
- N:1 sa **Document**  
- N:1 sa **User**  

---

## Enum vrijednosti

| Entitet | Atribut | Vrijednosti |
|---|---|---|
| User | account_status | ACTIVE, INACTIVE |
| Document | document_type | INVOICE, OTHER, UNKNOWN |
| Document | document_status | UPLOADED, PROCESSING_FAILED, EXTRACTED, UNDER_REVIEW, READY_FOR_APPROVAL, APPROVED, REJECTED, COMPLETED |
| StatusHistory | old_status, new_status | Vrijednosti odgovaraju enumeraciji document_status |

---

## Poslovna pravila važna za model

### 1. Organizacija i pristup
- Svaki korisnik pripada jednoj firmi  
- Korisnik vidi samo podatke svoje firme  
- Korisnik ima jednu ulogu  
- Firma mora imati admina  

### 2. Dokumenti i obrada
- Dokument pripada jednoj firmi i jednom korisniku  
- Obrada počinje nakon upload-a  
- Niska pouzdanost → ručna validacija  
- Jedan dokument → jedna ekstrakcija  
- Svako polje → jedna ekstrakcija  

### 3. Validacija i odobravanje
- Obavezna polja moraju biti validna  
- Samo ovlašten korisnik odobrava  
- Odbijanje mora imati razlog  
- XML samo za odobrene dokumente  

### 4. Praćenje
- Svaka promjena statusa se loguje  
- Sve akcije se auditiraju  
- Task je vezan za jednog usera i jedan dokument  