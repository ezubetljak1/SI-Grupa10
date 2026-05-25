# Initial Release Plan (IRP)

Initial Release Plan definiše planirane inkremente sistema kroz sprintove.  

Planirano je 5 inkremenata:
- **Inkrement 1** - Upravljanje dokumentima
- **Inkrement 2** - OCR i AI ekstrakcija
- **Inkrement 3** - Validacija i korekcija podataka
- **Inkrement 4** - Organizacioni model pristupa sistemu
- **Inkrement 5** - Efikasan rad i završna stabilizacija sistema

---

# Inkrement 1 – Upravljanje dokumentima

## Cilj inkrementa
Omogućiti korisniku da unosi, pregleda i upravlja dokumentima u sistemu.

Ovo je minimalna funkcionalna cjelina koja omogućava digitalizaciju dokumenata bez AI obrade.

## Glavne funkcionalnosti
- US-5.1, US-5.3 – Upload dokumenta  
- US-5.2 – Validacija fajla (tip, veličina)  
- US-5.4 – Pohrana dokumenta  
- US-5.5 – Lista dokumenata  
- US-5.6 – Pregled detalja dokumenta  

## Zavisnosti
- Osnovna backend i frontend arhitektura  
- Mehanizam za pohranu dokumenata

## Glavni rizici
- Problemi sa upload-om velikih fajlova  
- Neadekvatna validacija fajlova  
- Problemi sa pohranom dokumenata  

## Sprintovi realizacije
- Sprint 5  

---

# Inkrement 2 – OCR i AI ekstrakcija

## Cilj inkrementa
Automatizovati unos podataka iz dokumenata pomoću OCR/AI obrade.

Ovaj inkrement nadograđuje prvi i uvodi ključnu vrijednost sistema — automatsko prepoznavanje podataka.

## Glavne funkcionalnosti
- US-6.1, US-6.5 – OCR obrada dokumenta  
- US-6.3 – AI ekstrakcija podataka  
- US-6.2 – Prikaz ekstraktovanih podataka  
- US-6.4 – Klasifikacija dokumenta  

## Zavisnosti
- Inkrement 1 (dokumenti moraju postojati)  
- Eksterni OCR/AI servis  
- Stabilan API sloj  

## Glavni rizici
- Niska tačnost OCR/AI modela  
- Nedostupnost eksternih servisa  
- Nekonzistentni rezultati ekstrakcije  

## Sprintovi realizacije
- Sprint 6  

---

# Inkrement 3 – Validacija i korekcija podataka

## Cilj inkrementa
Omogućiti korisniku da pregleda, ispravi i validira automatski izdvojene podatke.

Ovo je ključni inkrement koji sistem čini pouzdanim i upotrebljivim u poslovanju.

## Glavne funkcionalnosti
- US-7.1 – Ručna korekcija podataka  
- US-7.2, US-7.3 – Validacija obaveznih polja i formata (datum, iznosi) 
- US-7.4 – Matematička validacija  
- US-7.5 – Spremanje validiranih podataka  

## Zavisnosti
- Inkrement 2 (postojanje ekstrakcije)  
- Definisana validaciona pravila  

## Glavni rizici
- Kompleksnost validacionih pravila  
- Loš UX za ručnu korekciju  
- Nedosljednost između AI i korisničkih izmjena  

## Sprintovi realizacije
- Sprint 7  

---

# Inkrement 4 – Organizacioni model pristupa sistemu

## Cilj inkrementa
Omogućiti rad sistema u realnom organizacijskom okruženju sa više korisnika, firmi i definisanim procesom odobravanja.

## Glavne funkcionalnosti

### Upravljanje korisnicima i firmama
- US-8.1, US-8.2 – Registracija firme i kreiranje admin naloga firme 
- US-8.3, US-8.4, US-8.6 – Prijava i odjava korisnika
- US-9.1, US-9.4 - Kreiranje korisničkih naloga i pregled korisnika unutar firme

### Role i autorizacija
- US-9.2 – Dodjela rola korisnicima  
- US-8.5, US-9.3 - Ograničenja na akcije po ulozi i pristup podataka vlastite organizacije

### Workflow dokumenta
- US-10.1 – Pregled statusa dokumenta  
- US-10.2, US-10.3 – Slanje dokumenata na odobravanje i njihov pregled  
- US-10.4, US-10.5 – Odobravanje i odbijanje dokumenta  
- US-10.6 – Historija statusa  

## Zavisnosti
- Svi prethodni inkrementi  
- Sigurnosni mehanizmi autentifikacije i autorizacije  
- Definisan workflow model  

## Glavni rizici
- Kompleksnost autorizacije  
- Pogrešno definisan workflow  

## Sprintovi realizacije
- Sprint 8  
- Sprint 9  
- Sprint 10  

---

# Inkrement 5 – Efikasan rad i završna stabilizacija sistema

## Cilj inkrementa
Unaprijediti sistem sa podrškom za praćenje aktivnosti, obavještavanje korisnika i finalizaciju obrade dokumenata.

Ovaj inkrement zaokružuje sistem u smislu transparentnosti, praćenja i operativne upotrebe u realnom okruženju.

## Glavne funkcionalnosti

### Finalizacija obrade dokumenta
- US-11.1, US-11.2 – Generisanje, pregled i preuzimanje XML izlaza 
- US-11.3 – Pohrana XML-a uz dokument i promjena statusa dokumenta
 
### Audit i pretraga
- US-11.4 – Pregled audit logova 
- US-11.5, US-12.1 – Pretraga i filtriranje dokumenata 
 
### Operativne dorade
- US-12.3, US-12.4 – Slanje notifikacija korisnicima  
- US-12.2 - Ostavljanje komentara na dokument
- US-12.5 - Reset lozinke
- US-12.6, US-12.7 - Dodatne operacije za menadžment i administraciju

## Zavisnosti
- Inkrement 4 (workflow mora postojati)  
- Definisani statusi dokumenta  
- Stabilan storage za XML fajlove  

## Glavni rizici
- Nekonzistentni audit zapisi  
- Kašnjenje ili neisporuka notifikacija  
- Greške u generisanju XML strukture  

## Sprintovi realizacije
- Sprint 11  
- Sprint 12  

---