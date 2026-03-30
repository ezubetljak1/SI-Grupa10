# Product Vision

## Naziv projekta
**Sistem za AI asistirano prepoznavanje i obradu računa i ulaznih dokumenata**

## Problem koji sistem rješava

Kompanije svakodnevno zaprimaju i obrađuju veliki broj ulaznih dokumenata, poput računa i drugih poslovnih zapisa, pri čemu se značajan dio tog procesa i dalje obavlja ručno. Takav način rada usporava obradu, povećava mogućnost grešaka pri unosu i validaciji podataka te zahtijeva dodatno vrijeme i angažman zaposlenih.

Dodatni izazov predstavlja ograničena preglednost nad statusom dokumenta kroz različite faze obrade, od zaprimanja i provjere do odobravanja i knjiženja. Zbog toga postoji potreba za sistemom koji će automatizirati ključne korake obrade poslovnih dokumenata, smanjiti operativno opterećenje zaposlenih i omogućiti brži, pouzdaniji i transparentniji tok obrade.

## Ciljni korisnici

Ciljni korisnici sistema mogu se podijeliti na primarne i sekundarne korisnike.

### Primarni korisnici 
_Osobe koje direktno koriste sistem u svakodnevnom radu._

- **Računovođe**, koji pregledavaju izdvojene podatke, validiraju ih i pripremaju dokumente za dalje knjiženje
- **Administrativno osoblje**, koje unosi dokumente, provjerava osnovnu ispravnost podataka i prosljeđuje dokumente kroz definisani tok obrade
- **Menadžeri i odgovorne osobe**, koji odobravaju ili odbijaju dokumente i prate njihov status kroz proces

### Sekundarni korisnici / stakeholderi

_Osobe kojima je važno da sistem funkcioniše pouzdano, sigurno i transparentno._

- **Vlasnici kompanije**
- **Direktori i menadžment**
- **IT sektor kompanije**
- **Interni i eksterni revizori** 

Detaljniji pregled stakeholdera, njihovih uloga, interesa, očekivanja i nivoa uticaja prikazan je u dokumentu *Stakeholder Map*.

## Vrijednost sistema

Sistem donosi vrijednost organizaciji kroz automatizaciju obrade ulaznih računa i poslovnih dokumenata, čime se smanjuje potreba za ručnim unosom podataka i ubrzava obrada dokumentacije. Automatskim izdvajanjem ključnih informacija, klasifikacijom dokumenata i podrškom za provjeru ispravnosti unosa, sistem korisnicima olakšava svakodnevni rad i smanjuje vjerovatnoću ljudskih grešaka.

Pored operativnih koristi, sistem omogućava bolju preglednost nad statusom svakog dokumenta, jasnije praćenje odgovornosti u procesu i dosljedniju obradu podataka kroz standardizirane XML zapise. Na taj način organizacija dobija efikasniji, pouzdaniji i transparentniji proces upravljanja poslovnom dokumentacijom, uz bolju osnovu za dalju integraciju sa računovodstvenim i drugim poslovnim sistemima.

## Scope MVP verzije

MVP verzija sistema fokusirana je na implementaciju osnovnog end-to-end procesa obrade ulaznih dokumenata, od unosa do generisanja standardizovanog izlaza.

### 1. Unos i upravljanje dokumentima
Omogućen je upload PDF dokumenata i skeniranih dokumenata (JPG, PNG) putem web interfejsa.  
Sistem prikazuje listu dokumenata sa statusima obrade:
- Uploaded
- Processed
- Failed  

Dokumenti se pohranjuju u bazu podataka.

### 2. AI ekstrakcija podataka
Sistem koristi OCR za ekstrakciju ključnih podataka iz dokumenata, uključujući:
- dobavljač  
- datum računa  
- broj računa  
- ukupan iznos  
- iznos PDV-a  

U slučaju da podaci nisu prepoznati ili imaju nizak nivo pouzdanosti, označavaju se kao nepotpuni i prosljeđuju na ručnu provjeru.

### 3. Pregled, korekcija i validacija podataka
Korisniku je omogućen pregled ekstraktovanih podataka kroz UI, uz mogućnost ručne korekcije.  

Sistem vrši osnovnu validaciju:
- provjera formata datuma  
- provjera numeričkih vrijednosti  
- provjera prisustva obaveznih polja  

### 4. Generisanje izlaza
Na osnovu validiranih podataka sistem generiše standardizovani XML zapis.  
Korisniku je omogućeno preuzimanje generisanog XML fajla.

### 5. Osnovna autentifikacija korisnika
Sistem podržava:
- registraciju korisnika  
- prijavu   
- odjavu   

Bez naprednih rola i permisija.

### 6. Osnovna klasifikacija dokumenata
Sistem razlikuje dva tipa dokumenata:
- račun   
- ostalo  

## Šta ne ulazi u MVP

### 1. Napredna AI obrada
- napredna semantička analiza dokumenata  
- kompleksni modeli za visoku preciznost ekstrakcije  

### 2. Podrška za više tipova dokumenata
- ugovori  
- bankovni izvodi  
- narudžbenice i drugi poslovni dokumenti  

### 3. Integracija sa eksternim sistemima
- ERP sistemi  
- računovodstveni softveri  

### 4. Napredne funkcionalnosti i analitika
- napredno filtriranje i pretraga  
- sortiranje po kompleksnim kriterijima  
- dashboardi i izvještaji  
- batch obrada dokumenata  

### 5. Napredni workflow i upravljanje korisnicima
- kompleksne role i permisije  
- automatizovano knjiženje  

## Ključna ograničenja i pretpostavke

### Ograničenja

- Kvalitet automatske obrade zavisi od kvaliteta ulaznih dokumenata, odnosno od čitljivosti skenova i PDF fajlova.
- Tačnost izdvajanja podataka može varirati u zavisnosti od strukture i formata dokumenta.
- Integracija sa postojećim računovodstvenim i ERP sistemima može biti otežana zbog različitih formata podataka i ograničenih integracijskih mogućnosti.
- Sistem obrađuje osjetljive poslovne podatke, zbog čega su sigurnost, kontrola pristupa i usklađenost sa relevantnim pravilima i regulativom od posebnog značaja.
- Dostupni budžet i tehnički resursi mogu ograničiti izbor AI alata, infrastrukture i naprednih funkcionalnosti.
- Usvajanje sistema može biti otežano otporom korisnika prema promjeni postojećih poslovnih procesa.

### Pretpostavke

- Ulazni dokumenti će u većini slučajeva biti dovoljno kvalitetni za automatsku obradu.
- Korisnici posjeduju osnovne digitalne vještine i mogu koristiti sistem uz minimalnu obuku, ukoliko je interfejs dovoljno intuitivan.
- Kompanija je spremna prilagoditi dio postojećih procesa kako bi se sistem mogao efikasno koristiti.
- U početnoj fazi rada bit će potrebna dodatna korisnička podrška i ručna provjera dijela automatski izdvojenih podataka.
- AI model ili servis koji se koristi za izdvajanje podataka neće biti potpuno autonoman, već će rezultati zahtijevati korisničku potvrdu prije konačne obrade.
